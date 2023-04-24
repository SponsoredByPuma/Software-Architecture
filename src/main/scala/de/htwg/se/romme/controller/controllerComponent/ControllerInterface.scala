package de.htwg.se.romme

package controller.controllerComponent

import dienste.Observable
import dienste.UndoManager
import scala.io.StdIn.readLine
import dienste.Observable
import model.gameComponent.GameInterface
import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker

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
  def getCards: List[CardInterface]
  def getCardsTable: List[List[CardInterface]]
  def getGraveyardCard: CardInterface
  def showTable: String
  def undo: Unit
  def redo: Unit
  def addCard(idxCard: Integer, idxlist: Integer): Unit
  def fillHand(playerIdx: Integer, fillUntil: Integer): Unit

  def load: Unit
  def save: Unit
}
