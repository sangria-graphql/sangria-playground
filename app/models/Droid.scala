package models

import sangria.schema._

import scala.concurrent.Future

case class Droid(
  id: String,
  name: Option[String],
  friends: List[String],
  appearsIn: List[Episode],
  primaryFunction: Option[String]
) extends Character

object Droid {
  val droidType = ObjectType(
    name = "Droid",
    "A mechanical creature in the Star Wars universe.",
    interfaces[Repository, Droid](Character.interfaceType),
    fields[Repository, Droid](
      Field(
        name = "id",
        StringType,
        Some("The id of the droid."),
        tags = ProjectionName("_id") :: Nil,
        resolve = _.value.id
      ),
      Field(
        name = "name",
        OptionType(StringType),
        Some("The name of the droid."),
        resolve = ctx => Future.successful(ctx.value.name)
      ),
      Field(
        name = "friends",
        ListType(Character.interfaceType),
        Some("The friends of the droid, or an empty list if they have none."),
        resolve = ctx => Character.fetcher.deferSeqOpt(ctx.value.friends)
      ),
      Field(
        name = "appearsIn",
        OptionType(ListType(OptionType(Episode.enum))),
        Some("Which movies they appear in."),
        resolve = _.value.appearsIn map (e => Some(e))
      ),
      Field(
        name = "primaryFunction",
        OptionType(StringType),
        Some("The primary function of the droid."),
        resolve = _.value.primaryFunction
      )
    )
  )
}
