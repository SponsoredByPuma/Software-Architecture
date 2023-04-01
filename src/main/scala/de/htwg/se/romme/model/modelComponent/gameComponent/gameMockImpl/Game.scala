package de.htwg.se.romme.model.modelComponent.gameComponent.gameMockImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Table
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Player
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Deck

case class Game(
    table: Table,
    players: List[Player],
    deck: Deck
) extends GameInterface:

    

  def set(table: Table, players: List[Player], deck: Deck): Game =
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


