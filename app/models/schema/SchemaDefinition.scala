package models.schema

import models._
import sangria.schema._

object SchemaDefinition {
  val query = ObjectType(
    name = "Query",
    fields[Repository, Unit](
      Field(
        name = "weapon",
        fieldType = ListType(Weapon.weaponType),
        arguments = Weapon.idArg :: Nil,
        resolve = c => c.ctx.getWeapon(c.arg(Weapon.idArg))
      ),
      Field(
        name = "hero",
        fieldType = Character.interfaceType,
        arguments = Episode.episodeArg :: Nil,
        deprecationReason = Some("Use `human` or `droid` fields instead"),
        resolve = c => c.ctx.getHero(c.arg(Episode.episodeArg))
      ),
      Field(
        name = "human",
        fieldType = ListType(Human.humanType),
        arguments = Character.idArg :: Nil,
        resolve = c => c.ctx.getHuman(c.arg(Character.idArg))
      ),
      Field(
        name = "droid",
        fieldType = ListType(Droid.droidType),
        arguments = Character.idArg :: Nil,
        resolve = c => c.ctx.getDroid(c.arg(Character.idArg))
      )
    )
  )

  val schema = Schema(query)
}
