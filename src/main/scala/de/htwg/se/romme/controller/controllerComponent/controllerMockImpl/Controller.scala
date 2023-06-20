package de.htwg.se.romme.controller.controllerComponent

import model.gameComponent.GameInterface
import de.htwg.se.romme.controller.controllerComponent.ControllerInterface
import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker


class Controller(var game: GameInterface) extends ControllerInterface {

  def playerState: PlayerState = PlayerOne
  def gameStart: Unit = {}
  def checkForJoker(list: List[Integer]): List[Integer] = {
    val t1: List[Integer] = List()
    t1
  }
  def replaceCardOrder(
      stelle: List[Integer],
      values: List[String]
  ): Unit = {}
  def replaceCardSuit(
      stelle: List[Integer],
      values: List[String]
  ): Unit = {}
  def switch: Unit = {}
  def playersTurn: Boolean = false
  def pickUpGraveYard: Unit = {}
  def pickUpACard: Unit = {}
  def dropASpecificCard(cardIdx: Integer): Unit = {}
  def takeJoker(idxlist: Integer, idxCard: Integer): Unit = {}
  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      hasJoker: Boolean
  ): Unit = {}
  def sortPlayersCards: Unit = {}
  def victory: Boolean = false
  def showCards: String = ""
  def getCards: List[CardInterface] = {
    val t1: List[CardInterface] = List()
    t1
  }
  def getCardsTable: List[List[CardInterface]] = {
    val t1: List[List[CardInterface]] = List()
    t1
  }
  def getGraveyardCard: CardInterface = {
    val t1 = Card(0, 0)
    t1
  }
  def showTable: String = { "" }
  def undo: Unit = {}
  def redo: Unit = {}
  def addCard(idxCard: Integer, idxlist: Integer): Unit = {}
  def fillHand(playerIdx: Integer, fillUntil: Integer): Unit = {}
  def load: Unit = {}
  def save: Unit = {}
  def delete: Unit = {}

}
