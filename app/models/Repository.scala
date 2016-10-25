package models

class Repository {
  import models.Data._

  def getHero(episode: Option[Episode]) = {
    episode.flatMap(_ => getHuman(Some("1000")).headOption).getOrElse(droids.last)
  }

  def getHuman(id: Option[String]) = id match {
    case Some(x) => humans.find(_.id == x).toSeq
    case None => humans
  }

  def getDroid(id: Option[String]) = id match {
    case Some(x) => droids.find(_.id == x).toSeq
    case None => droids
  }

  def getWeapon(id: Option[String] = None) = id match {
    case Some(x) => weapons.find(_.id == x).toSeq
    case None => weapons
  }
}
