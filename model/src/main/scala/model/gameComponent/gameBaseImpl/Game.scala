package model.gameComponent.gameBaseImpl

import model.gameComponent.GameInterface
import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card

import com.google.inject.Inject

case class Game @Inject() (table: TableInterface, players: List[Player], deck: DeckInterface)
    extends GameInterface {

  val deckAPI = ModelDeckRequest()

  def set(table: TableInterface, players: List[Player], deck: DeckInterface): Game =
    copy(table, players, deck)

  def gameStart: Game = {
    val newDeck = deckAPI.createNewDeck()
    copy(table, players, deck = newDeck)
  }

  def drawCards(playerIdx: Integer): Game = {
    val player: Player = players(playerIdx)
    val (newPlayer, newDeck) = player.draw13Cards(deck, List[CardInterface]())
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

  def fillHand(playerIdx: Integer, fillUntil: Integer): Game = {
    val newPlayer = players(playerIdx).fillHand(fillUntil)
    copy(table, players = players.updated(playerIdx, newPlayer), deck)
  }
}
