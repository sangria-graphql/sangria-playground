package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api.libs.json._
import play.api.mvc._

import sangria.execution.Executor
import sangria.parser.{SyntaxError, QueryParser}
import sangria.marshalling.playJson._

import models.{FriendsResolver, CharacterRepo, SchemaDefinition}
import sangria.renderer.SchemaRenderer

import scala.concurrent.Future
import scala.util.{Failure, Success}

class Application @Inject() (system: ActorSystem) extends Controller {
  import system.dispatcher

  def index = Action {
    Ok(views.html.index())
  }

  def graphiql = Action {
    Ok(views.html.graphiql())
  }

  val executor = Executor(
    schema = SchemaDefinition.StarWarsSchema,
    userContext = new CharacterRepo,
    deferredResolver = new FriendsResolver,
    maxQueryDepth = Some(10))

  def graphql(query: String, variables: Option[String], operation: Option[String]) =
    Action.async(executeQuery(query, variables map parseVariables, operation))

  def graphqlBody = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operation").asOpt[String]

    val variables = (request.body \ "variables").toOption.flatMap {
      case JsString(vars) => Some(parseVariables(vars))
      case obj: JsObject => Some(obj)
      case _ => None
    }

    executeQuery(query, variables, operation)
  }

  private def parseVariables(variables: String) =
    if (variables.trim == "") Json.obj() else Json.parse(variables).as[JsObject]

  private def executeQuery(query: String, variables: Option[JsObject], operation: Option[String]) =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) =>
        executor.execute(queryAst,
          operationName = operation,
          variables = variables getOrElse Json.obj()) map (Ok(_))

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

  def renderSchema = Action {
    Ok(SchemaRenderer.renderSchema(SchemaDefinition.StarWarsSchema))
  }
}
