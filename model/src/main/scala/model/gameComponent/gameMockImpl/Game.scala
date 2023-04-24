package model.gameComponent.gameMockImpl

import model.gameComponent.GameInterface

import model.gameComponent.gameBaseImpl.Player
import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card

case class Game(
    table: TableInterface,
    players: List[Player],
    deck: DeckInterface
) extends GameInterface:

    

  def set(table: TableInterface, players: List[Player], deck: DeckInterface): Game =
    this
  def gameStart: Game = this
  def drawCards(playerIdx: Integer): Game = this
  def pickUpGraveYard(playerIdx: Integer): Game = this
  def pickUpACard(playerIdx: Integer): Game = this
  def replaceCardOrder(
      stelle: List[Integer],
      values: List[String],
      playerIdx: Integer
  ): Game = this
  def replaceCardSuit(
      stelle: List[Integer],
      values: List[String],
      playerIdx: Integer
  ): Game = this
  def dropASpecificCard(index: Integer, playerIdx: Integer): Game = this
  def addCard(idxCard: Integer, idxlist: Integer, playerIdx: Integer): Game =
    this
  def takeJoker(
      idxlist: Integer,
      idxCard: Integer,
      playerIdx: Integer
  ): Game = this
  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      playerIdx: Integer,
      hasJoker: Boolean
  ): Game = this
  def sortPlayersCards(playerIdx: Integer): Game = this
  def victory(playerIdx: Integer): Boolean = false
  def showCards(playerIdx: Integer): String = ""
  def showTable: String = ""
  def fillHand(playerIdx: Integer, fillUntil: Integer): Game = this


