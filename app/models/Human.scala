package models

import sangria.schema._

case class Human(
  id: String,
  name: Option[String],
  friends: List[String],
  appearsIn: List[Episode],
  homePlanet: Option[String]
) extends Character

object Human {
  val humanType = ObjectType(
    name = "Human",
    description = "A humanoid creature in the Star Wars universe.",
    interfaces = interfaces[Repository, Human](Character.interfaceType),
    fields = fields[Repository, Human](
      Field(
        name = "id",
        fieldType = StringType,
        description = Some("The id of the human."),
        resolve = _.value.id
      ),
      Field(
        name = "name",
        fieldType = OptionType(StringType),
        description = Some("The name of the human."),
        resolve = _.value.name
      ),
      Field(
        name = "friends",
        fieldType = ListType(Character.interfaceType),
        description = Some("The friends of the human, or an empty list if they have none."),
        resolve = ctx => Character.fetcher.deferSeqOpt(ctx.value.friends)
      ),
      Field(
        name = "appearsIn",
        fieldType = OptionType(ListType(OptionType(Episode.enum))),
        description = Some("Which movies they appear in."),
        resolve = _.value.appearsIn map (e => Some(e))
      ),
      Field(
        name = "homePlanet",
        fieldType = OptionType(StringType),
        description = Some("The home planet of the human, or null if unknown."),
        resolve = _.value.homePlanet
      )
    )
  )
}

