package models

object Data {
  val humans = List(
    Human(
      id = "1000",
      name = Some("Luke Skywalker"),
      friends = List("1002", "1003", "2000", "2001"),
      appearsIn = List(Episode.NewHope, Episode.Empire, Episode.Jedi),
      homePlanet = Some("Tatooine")
    ),
    Human(
      id = "1001",
      name = Some("Darth Vader"),
      friends = List("1004"),
      appearsIn = List(Episode.NewHope, Episode.Empire, Episode.Jedi),
      homePlanet = Some("Tatooine")
    ),
    Human(
      id = "1002",
      name = Some("Han Solo"),
      friends = List("1000", "1003", "2001"),
      appearsIn = List(Episode.NewHope, Episode.Empire, Episode.Jedi),
      homePlanet = None
    ),
    Human(
      id = "1003",
      name = Some("Leia Organa"),
      friends = List("1000", "1002", "2000", "2001"),
      appearsIn = List(Episode.NewHope, Episode.Empire, Episode.Jedi),
      homePlanet = Some("Alderaan")
    ),
    Human(
      id = "1004",
      name = Some("Wilhuff Tarkin"),
      friends = List("1001"),
      appearsIn = List(Episode.NewHope, Episode.Empire, Episode.Jedi),
      homePlanet = None
    )
  )

  val droids = List(
    Droid(
      id = "2000",
      name = Some("C-3PO"),
      friends = List("1000", "1002", "1003", "2001"),
      appearsIn = List(Episode.NewHope, Episode.Empire, Episode.Jedi),
      primaryFunction = Some("Protocol")
    ),
    Droid(
      id = "2001",
      name = Some("R2-D2"),
      friends = List("1000", "1002", "1003"),
      appearsIn = List(Episode.NewHope, Episode.Empire, Episode.Jedi),
      primaryFunction = Some("Astromech")
    )
  )

  val weapons = List(
    Weapon(id = "blaster", name = "Blaster"),
    Weapon(id = "lightsaber", name = "Lightsaber"),
    Weapon(id = "lightning", name = "Force Lightning")
  )
}
