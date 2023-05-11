package controllerComponent

import _root_.de.htwg.se.romme.util.Observable
import de.htwg.se.romme.util.UndoManager
import scala.io.StdIn.readLine
import util.Observable
import scala.collection.mutable.ListBuffer
import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Card

import scala.swing.Publisher

trait ControllerInterface extends Publisher {
  var game: GameInterface
  def playerState: PlayerState
  def gameStart: Unit
  def checkForJoker(list: List[Integer]): List[Integer]
  def replaceCardOrder(
      stelle: List[Integer],
      values: List[String]
  ): Unit
  def replaceCardSuit(
      stelle: List[Integer],
      values: List[String]
  ): Unit
  def switch: Unit
  def pickUpGraveYard: Unit
  def pickUpACard: Unit
  def dropASpecificCard(cardIdx: Integer): Unit
  def takeJoker(idxlist: Integer, idxCard: Integer): Unit
  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      hasJoker: Boolean
  ): Unit
  def sortPlayersCards: Unit
  def victory: Boolean
  def showCards: String
  def getCards: List[Card]
  def getCardsTable: List[List[Card]]
  def getGraveyardCard: Card
  def showTable: String
  def undo: Unit
  def redo: Unit
  def addCard(idxCard: Integer, idxlist: Integer): Unit
  def fillHand(playerIdx: Integer, fillUntil: Integer): Unit

  def load: Unit
  def save: Unit
}
