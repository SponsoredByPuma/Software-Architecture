package databaseSlick

import model.gameComponent.GameInterface

import scala.util.Try
import scala.concurrent.Future

trait DAOInterface {

  def save(game: GameInterface): Future[Unit]

  def load(id: Option[Int]) : Future[Try[GameInterface]]

  def storeGame(deckSize: Int, deckList: String, graveYardCard: String,
                droppedCards: String, playerOneName: String,
                playerOneAmount: Int, playerOneHand: String,
                playerTwoName: String, playerTwoAmount: Int, playerTwoHand: String): Future[Int]

  def deleteGame(id: Int): Future[Try[Boolean]]

}
