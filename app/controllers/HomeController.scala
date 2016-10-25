package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import models._
import models.schema.SchemaDefinition
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import sangria.execution.deferred.DeferredResolver
import sangria.execution.{ ErrorWithResolver, Executor, QueryAnalysisError }
import sangria.marshalling.playJson._
import sangria.parser.{ QueryParser, SyntaxError }
import sangria.renderer.SchemaRenderer

import scala.concurrent.Future
import scala.util.{ Failure, Success }

class HomeController @Inject() (system: ActorSystem, config: Configuration) extends Controller {
  import system.dispatcher

  private[this] val defaultGraphQLUrl = config.getString("defaultGraphQLUrl").getOrElse(s"http://localhost:${config.getInt("http.port").getOrElse(9000)}/graphql")

  def index = Action {
    Ok(views.html.graphiql())
  }

  def graphql(query: String, variables: Option[String], operation: Option[String]) = Action.async {
    executeQuery(query, variables map parseVariables, operation)
  }

  def graphqlBody = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]

    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) => Some(parseVariables(vars))
      case obj: JsObject => Some(obj)
      case _ => None
    }

    executeQuery(query, variables, operation)
  }

  private[this] def parseVariables(variables: String) = if (variables.trim == "" || variables.trim == "null") {
    Json.obj()
  } else {
    Json.parse(variables).as[JsObject]
  }

  private[this] def executeQuery(query: String, variables: Option[JsObject], operation: Option[String]) = QueryParser.parse(query) match {
    case Success(queryAst) => Executor.execute(
      schema = SchemaDefinition.schema,
      queryAst = queryAst,
      userContext = new Repository,
      operationName = operation,
      variables = variables getOrElse Json.obj(),
      deferredResolver = DeferredResolver.fetchers(Character.fetcher),
      maxQueryDepth = Some(10)
    ).map(Ok(_)).recover {
      case error: QueryAnalysisError => BadRequest(error.resolveError)
      case error: ErrorWithResolver => InternalServerError(error.resolveError)
    }

    case Failure(error: SyntaxError) => Future.successful(BadRequest(Json.obj(
      "syntaxError" -> error.getMessage,
      "locations" -> Json.arr(Json.obj(
        "line" -> error.originalError.position.line,
        "column" -> error.originalError.position.column)
      )
    )))

    case Failure(error) => throw error
  }

  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(SchemaDefinition.schema))
  }
}
