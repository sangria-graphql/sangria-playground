package models

import enumeratum._
import sangria.schema._
import sangria.macros.derive._

sealed abstract class Episode(val num: Int) extends EnumEntry

object Episode extends Enum[Episode]{
  case object NewHope extends Episode(4)
  case object Empire extends Episode(5)
  case object Jedi extends Episode(6)

  override val values = findValues

  val enum: EnumType[Episode] = deriveEnumType[Episode](
    EnumTypeName("Episode"),
    EnumTypeDescription("One of the films in the Star Wars Trilogy")
  )

  val episodeArg = Argument(
    name = "episode",
    argumentType = OptionInputType(enum),
    description = "If omitted, returns the hero of the whole saga. If provided, returns the hero of that particular episode."
  )
}



