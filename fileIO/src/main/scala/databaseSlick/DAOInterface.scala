package databaseSlick

import model.gameComponent.GameInterface

import scala.util.Try

trait DAOInterface {

  def save(game: GameInterface): Unit

  def load(id: Option[Int]) : Try[GameInterface]

  def storeGame(deckSize: Int, deckList: String, graveYardCard: String,
                droppedCards: String, playerOneName: String,
                playerOneAmount: Int, playerOneHand: String,
                playerTwoName: String, playerTwoAmount: Int, playerTwoHand: String): Int

  def deleteGame(id: Int): Try[Boolean]

}
