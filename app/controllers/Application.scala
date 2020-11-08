package controllers

import javax.inject.Inject
import akka.actor.ActorSystem
import play.api.libs.json._
import play.api.mvc._
import play.api.Configuration
import sangria.execution._
import sangria.parser.{QueryParser, SyntaxError}
import sangria.marshalling.playJson._
import models.{CharacterRepo, SchemaDefinition}
import sangria.execution.deferred.DeferredResolver
import sangria.renderer.SchemaRenderer
import sangria.slowlog.SlowLog

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Application @Inject() (system: ActorSystem, config: Configuration) extends InjectedController {
  import system.dispatcher

  val googleAnalyticsCode = config.getOptional[String]("gaCode")
  val defaultGraphQLUrl = config.getOptional[String]("defaultGraphQLUrl").getOrElse(s"http://localhost:${config.getOptional[Int]("http.port").getOrElse(9000)}/graphql")

  def index = Action {
    Ok(views.html.index(googleAnalyticsCode,defaultGraphQLUrl))
  }

  def playground = Action {
    Ok(views.html.playground(googleAnalyticsCode))
  }

  def graphql(query: String, variables: Option[String], operation: Option[String]) = Action.async { request =>
    executeQuery(query, variables map parseVariables, operation, isTracingEnabled(request))
  }

  def graphqlBody = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]

    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) => Some(parseVariables(vars))
      case obj: JsObject => Some(obj)
      case _ => None
    }

    executeQuery(query, variables, operation, isTracingEnabled(request))
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "" || variables.trim == "null") Json.obj() else Json.parse(variables).as[JsObject]

  private def executeQuery(query: String, variables: Option[JsObject], operation: Option[String], tracing: Boolean) =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) =>
        Executor.execute(SchemaDefinition.StarWarsSchema, queryAst, new CharacterRepo,
            operationName = operation,
            variables = variables getOrElse Json.obj(),
            deferredResolver = DeferredResolver.fetchers(SchemaDefinition.characters),
            exceptionHandler = exceptionHandler,
            queryReducers = List(
              QueryReducer.rejectMaxDepth[CharacterRepo](15),
              QueryReducer.rejectComplexQueries[CharacterRepo](4000, (_, _) => TooComplexQueryError)),
            middleware = if (tracing) SlowLog.apolloTracing :: Nil else Nil)
          .map(Ok(_))
          .recover {
            case error: QueryAnalysisError => BadRequest(error.resolveError)
            case error: ErrorWithResolver => InternalServerError(error.resolveError)
          }

      // can't parse GraphQL query, return error
      case Failure(error: SyntaxError) =>
        Future.successful(BadRequest(Json.obj(
          "syntaxError" -> error.getMessage,
          "locations" -> Json.arr(Json.obj(
            "line" -> error.originalError.position.line,
            "column" -> error.originalError.position.column)))))

      case Failure(error) =>
        throw error
    }

  def isTracingEnabled(request: Request[_]) = request.headers.get("X-Apollo-Tracing").isDefined

  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(SchemaDefinition.StarWarsSchema))
  }

  lazy val exceptionHandler = ExceptionHandler {
    case (_, error @ TooComplexQueryError) => HandledException(error.getMessage)
    case (_, error @ MaxQueryDepthReachedError(_)) => HandledException(error.getMessage)
  }

  case object TooComplexQueryError extends Exception("Query is too expensive.")
}
