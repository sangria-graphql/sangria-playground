package models

import sangria.schema._

case class Weapon(
  id: String,
  name: String
)

object Weapon {
  val idArg = Argument(
    name = "id",
    argumentType = OptionInputType(StringType),
    description = "id of the weapon"
  )

  val weaponType: ObjectType[Repository, Weapon] = ObjectType(
    name = "Weapon",
    description = "A weapon in the Star Wars Trilogy",
    fieldsFn = () => fields[Repository, Weapon](
      Field(
        name = "id",
        fieldType = StringType,
        description = Some("The id of the weapon."),
        resolve = _.value.id
      ),
      Field(
        name = "name",
        fieldType = OptionType(StringType),
        description = Some("The name of the weapon."),
        resolve = _.value.name
      )
    )
  )
}



