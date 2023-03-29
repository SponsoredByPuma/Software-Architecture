package de.htwg.se.romme.model.modelComponent.gameComponent.gameMockImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Table
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Player
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Deck

case class Game(
    table: Table,
    var player: Player,
    var player2: Player,
    deck: Deck
) extends GameInterface:

    

  def set(table: Table, player: Player, player2: Player, deck: Deck): Game =
    this
  def gameStart: Game = this
  def drawCards1: Game = this
  def drawCards2: Game = this
  def pickUpGraveYard(player1Turn: Boolean): Game = this
  def pickUpACard(player1Turn: Boolean): Game = this
  def replaceCardOrder(
      stelle: List[Integer],
      values: List[String],
      player1Turn: Boolean
  ): Game = this
  def replaceCardSuit(
      stelle: List[Integer],
      values: List[String],
      player1Turn: Boolean
  ): Game = this
  def dropASpecificCard(index: Integer, player1Turn: Boolean): Game = this
  def addCard(idxCard: Integer, idxlist: Integer, player1Turn: Boolean): Game =
    this
  def takeJoker(
      idxlist: Integer,
      idxCard: Integer,
      player1Turn: Boolean
  ): Game = this
  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      player1Turn: Boolean,
      hasJoker: Boolean
  ): Game = this
  def sortPlayersCards(player1Turn: Boolean): Game = this
  def victory(player1Turn: Boolean): Boolean = false
  def showCards(player1Turn: Boolean): String = ""
  def showTable: String = ""


