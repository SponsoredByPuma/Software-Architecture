package databaseSlick.sqlTables

//import spray.json._
import slick.jdbc.MySQLProfile.api.*

class GameTable(tag: Tag) extends Table[(Int,Int,String,String,String,String,Int,String,String,Int,String)](tag,"BOARD") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def deckSize = column[Int]("DECKSIZE")

  def deckList = column[String]("DECKLIST")

  def graveYardCard = column[String]("GRAVEYARDCARD")

  def tableCards = column[String]("TABLECARDS")

  def playerOneName = column[String]("PLAYERONENAME")

  def playerOneAmount = column[Int]("PLAYERONEAMOUNT")

  def playerOneHand = column[String]("PLAYERONEHAND")

  def playerTwoName = column[String]("PLAYERTWONAME")

  def playerTwoAmount = column[Int]("PLAYERTWOAMOUNT")

  def playerTwoHand = column[String]("PLAYERTWOHAND")

  override def * = (id,deckSize,deckList, graveYardCard, tableCards,
    playerOneName, playerOneAmount, playerOneHand, playerTwoName, playerTwoAmount, playerTwoHand)
}
