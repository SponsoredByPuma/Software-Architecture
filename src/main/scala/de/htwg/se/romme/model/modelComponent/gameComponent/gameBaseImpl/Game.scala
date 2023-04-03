package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface
import com.google.inject.Inject

case class Game @Inject() (table: Table, players: List[Player], deck: Deck)
    extends GameInterface {

  def set(table: Table, players: List[Player], deck: Deck): Game =
    copy(table, players, deck)

  def gameStart: Game = {
    val newDeck = deck.createNewDeck()
    copy(table, players, deck = newDeck)
  }

  def drawCards(playerIdx: Integer): Game = {
    val player: Player = players(playerIdx)
    val (newPlayer, newDeck) = player.draw13Cards(deck, List[Card]())
    copy(table, players = players.updated(playerIdx, newPlayer), deck = newDeck)
  }

  def pickUpGraveYard(playerIdx: Integer): Game = {
    val (newPlayer, newTable) = players(playerIdx).pickUpGraveYard(table)
    copy(
      table = newTable,
      players = players.updated(playerIdx, newPlayer),
      deck
    )
  }

  def pickUpACard(playerIdx: Integer): Game = {
    val (newPlayer, newDeck) = players(playerIdx).pickUpACard(deck)
    copy(table, players = players.updated(playerIdx, newPlayer), deck = newDeck)
  }

  def replaceCardOrder(
      stellen: List[Integer],
      values: List[String],
      playerIdx: Integer
  ): Game = {
    val newPlayer = players(playerIdx).replaceCards(
      0,
      stellen.size,
      stellen,
      values,
      players(playerIdx).hand,
      false
    )
    copy(table, players = players.updated(playerIdx, newPlayer), deck)
  }

  def replaceCardSuit(
      stellen: List[Integer],
      values: List[String],
      playerIdx: Integer
  ): Game = {
    val newPlayer = players(playerIdx).replaceCards(
      0,
      stellen.size,
      stellen,
      values,
      players(playerIdx).hand,
      true
    )
    copy(table, players = players.updated(playerIdx, newPlayer), deck)
  }

  def dropASpecificCard(cardIdx: Integer, playerIdx: Integer): Game = {
    val (newPlayer, newTable) =
      players(playerIdx).dropASpecificCard(cardIdx, table)
    copy(
      table = newTable,
      players = players.updated(playerIdx, newPlayer),
      deck
    )
  }

  def addCard(idxCard: Integer, idxlist: Integer, playerIdx: Integer): Game = {
    val (newPlayer, newTable) =
      players(playerIdx).addCard(idxCard, idxlist, table)
    copy(
      table = newTable,
      players = players.updated(playerIdx, newPlayer),
      deck
    )

  }

  def takeJoker(
      idxlist: Integer,
      idxCard: Integer,
      playerIdx: Integer
  ): Game = {
    val (newPlayer, newTable) =
      players(playerIdx).takeJoker(idxlist, idxCard, table)
    copy(
      table = newTable,
      players = players.updated(playerIdx, newPlayer),
      deck
    )
  }

  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      playerIdx: Integer,
      hasJoker: Boolean
  ): Game = {
    val (newPlayer, newTable) =
      players(playerIdx).dropMultipleCards(list, dec, hasJoker, table)
    copy(
      table = newTable,
      players = players.updated(playerIdx, newPlayer),
      deck
    )
  }

  def sortPlayersCards(playerIdx: Integer): Game = {
    val newPlayer = players(playerIdx).sortPlayersCards
    copy(table, players = players.updated(playerIdx, newPlayer), deck)
  }

  def victory(playerIdx: Integer): Boolean = {
    players(playerIdx).victory
  }

  def showCards(playerIdx: Integer): String = {
    players(playerIdx).showCards
  }

  def showTable: String = table.showPlacedCardsOnTable()
}
