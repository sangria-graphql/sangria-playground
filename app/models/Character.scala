package models

import sangria.execution.deferred.{ Fetcher, HasId }
import sangria.schema._

import scala.concurrent.Future

trait Character {
  def id: String
  def name: Option[String]
  def friends: List[String]
  def appearsIn: List[Episode]
}

object Character {
  val idArg = Argument(
    name = "id",
    argumentType = OptionInputType(StringType),
    description = "id of the character"
  )

  val fetcher = Fetcher.caching( (ctx: Repository, ids: Seq[String]) =>
    Future.successful(ids.flatMap(id => ctx.getHuman(Some(id)).headOption.orElse(ctx.getDroid(Some(id)).headOption))))(HasId(_.id)
  )

  val interfaceType: InterfaceType[Repository, Character] = InterfaceType(
    name = "Character",
    description = "A character in the Star Wars Trilogy",
    fieldsFn = () => fields[Repository, Character](
      Field(
        name = "id",
        fieldType = StringType,
        description = Some("The id of the character."),
        resolve = _.value.id
      ),
      Field(
        name = "name",
        fieldType = OptionType(StringType),
        description = Some("The name of the character."),
        resolve = _.value.name
      ),
      Field(
        name = "friends",
        fieldType = ListType(interfaceType),
        description = Some("The friends of the character, or an empty list."),
        resolve = ctx => fetcher.deferSeqOpt(ctx.value.friends)
      ),
      Field(
        name = "appearsIn",
        fieldType = OptionType(ListType(OptionType(Episode.enum))),
        description = Some("Which movies they appear in."),
        resolve = x => x.value.appearsIn.map(e => Some(e))
      )
    )
  )
}
