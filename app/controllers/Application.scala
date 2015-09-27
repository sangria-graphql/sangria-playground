package controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import play.api._
import play.api.libs.json._
import play.api.mvc._

import sangria.execution.Executor
import sangria.introspection.introspectionQuery
import sangria.parser.{SyntaxError, QueryParser}
import sangria.integration.playJson._

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
    maxQueryDepth = Some(7))

  def graphql(query: String, variables: Option[String], operation: Option[String]) =
    Action.async(executeQuery(query, variables, operation))

  def graphqlBody = Action.async(parse.json) { request =>
    val query = (request.body \ "query").as[String]
    val variables = (request.body \ "variables").asOpt[String]
    val operation = (request.body \ "operation").asOpt[String]

    executeQuery(query, variables, operation)
  }

  private def executeQuery(query: String, variables: Option[String], operation: Option[String]) =
    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) =>
        executor.execute(queryAst,
          operationName = operation,
          variables = variables map Json.parse getOrElse Json.obj()) map (Ok(_))

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

  def renderSchema = Action.async {
    executor.execute(introspectionQuery) map (res =>
      SchemaRenderer.renderSchema(res) map (Ok(_)) getOrElse
        InternalServerError("Can't render the schema!"))
  }
}
