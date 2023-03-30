package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface
import com.google.inject.Inject

case class Game @Inject() (table: Table,var player: Player, var player2: Player, deck: Deck) extends GameInterface {

  def set(table: Table, player: Player, player2: Player, deck: Deck): Game = copy(table, player, player2, deck)

  def gameStart: Game = {
    val newDeck = deck.createNewDeck()
    copy(table, player, player2, deck = newDeck)
  }

  def drawCards1: Game = {
    val playerHands1 = PlayerHands(table, List[Card](), false)
    val (newHand, newDeck) = playerHands1.draw13Cards(deck, List[Card]())
    val player1 = Player(player.name, newHand, table)
    copy(table, player = player1, player2, deck = newDeck)
  }

  def drawCards2: Game = {
    val playerHands2 = PlayerHands(table, List[Card](), false)
    val p2Hands = playerHands2.draw13Cards(deck, List[Card]())
    val player2New = Player(player2.name, p2Hands(0), table)
    copy(table, player, player2 = player2New, deck = p2Hands(1))
  }

  def pickUpGraveYard(player1Turn: Boolean): Game = {
    if (player1Turn)
      val (newPlayer, newTable) = player.pickUpGraveYard
      copy(table = newTable, player = newPlayer, player2 = Player(player2.name, player2.hands, newTable), deck)
    else 
      val (newPlayer, newTable) = player2.pickUpGraveYard
      copy(table = newTable, player = Player(player.name, player.hands, newTable), player2 = newPlayer, deck)
  }

  def pickUpACard(player1Turn: Boolean): Game = {
    if (player1Turn)
      val (newPlayer, newDeck) = player.pickUpACard(deck)
      copy(table, player = newPlayer, player2, deck = newDeck)
    else 
      val (newPlayer, newDeck) = player2.pickUpACard(deck)
      copy(table, player, player2 = newPlayer, deck = newDeck)
    
  }

  def replaceCards(turnCounter: Integer, stellenStartingSize: Integer, stellen: List[Integer], values: List[String], playerCards: List[Card], isSuit: Boolean): List[Card] = {
    if (turnCounter != stellenStartingSize)
      val splitCards = playerCards.splitAt(stellen.head).toList
      if (isSuit)
        val firstCardsWithJoker: List[Card] = splitCards(0).toList ::: List(Joker().setSuit(values.head))
        val finalPlayerCards: List[Card] = firstCardsWithJoker ::: splitCards(1).toList.tail
        return replaceCards(turnCounter + 1, stellenStartingSize, stellen.tail, values.tail, finalPlayerCards, isSuit)
      else
        val firstCardsWithJoker: List[Card] = splitCards(0).toList ::: List(Joker().setValue(values.head))
        val finalPlayerCards: List[Card] = firstCardsWithJoker ::: splitCards(1).toList.tail
        return replaceCards(turnCounter + 1, stellenStartingSize, stellen.tail, values.tail, finalPlayerCards, isSuit)
      end if
    else
      return playerCards
  }

  def replaceCardOrder(stellen: List[Integer], values: List[String], player1Turn: Boolean): Game = {
    if (player1Turn) 
      val newPlayerCards = replaceCards(0, stellen.size, stellen, values, player.hands.cardsOnHand, false)
      copy(table, player = Player(player.name, PlayerHands(table, newPlayerCards, player.hands.outside), table), player2, deck)
    else 
      val newPlayerCards = replaceCards(0, stellen.size, stellen, values, player2.hands.cardsOnHand, false)
      copy(table, player, player2 = Player(player2.name, PlayerHands(table, newPlayerCards, player.hands.outside), table), deck)
  }

  def replaceCardSuit(stellen: List[Integer], values: List[String], player1Turn: Boolean): Game = {
    if (player1Turn) 
      val newPlayerCards = replaceCards(0, stellen.size, stellen, values, player.hands.cardsOnHand, false)
      copy(table, player = Player(player.name, PlayerHands(table, newPlayerCards, player2.hands.outside), table), player2, deck)
    else 
      val newPlayerCards = replaceCards(0, stellen.size, stellen, values, player2.hands.cardsOnHand, false)
      copy(table, player, player2 = Player(player2.name, PlayerHands(table, newPlayerCards, player2.hands.outside), table), deck)
  }

  def dropASpecificCard(index: Integer, player1Turn: Boolean): Game = {
    if (player1Turn)
      val (newPlayer, newTable) = player.dropASpecificCard(index)
      return copy(table = newTable, player = newPlayer, player2 = Player(player2.name, player2.hands, newTable), deck)
    else
      val (newPlayer, newTable) = player2.dropASpecificCard(index)
      return copy(table = newTable, player = Player(player.name, player.hands, newTable), player2 = newPlayer, deck)
    end if
  }

  def addCard(idxCard: Integer, idxlist: Integer, player1Turn: Boolean): Game = {
    if (player1Turn)
      player = player.addCard(idxCard, idxlist)
    else
      player2 = player2.addCard(idxCard, idxlist)
    end if
      copy(table, player, player2, deck)
  }

  def takeJoker(idxlist: Integer, idxCard: Integer, player1Turn: Boolean): Game = {
    if (player1Turn)
      val (newPlayer, newTable) = player.takeJoker(idxlist, idxCard)
      val newPlayerHands = PlayerHands(newTable, player2.hands.cardsOnHand, player2.hands.outside)
      copy(table = newTable, player = newPlayer, player2 = Player(player2.name, newPlayerHands, newTable),deck)
    else
      val (newPlayer, newTable) = player2.takeJoker(idxlist, idxCard)
      val newPlayerHands = PlayerHands(newTable, player.hands.cardsOnHand, player.hands.outside)
      copy(table = newTable, player = Player(player.name, newPlayerHands, newTable), player2 = newPlayer, deck)
  }

  def dropMultipleCards(list: List[Integer], dec: Integer, player1Turn: Boolean, hasJoker: Boolean): Game = {
    if (player1Turn)
      val (newPlayer, newTable) = player.dropMultipleCards(list, dec, hasJoker)
      val newPlayerHands = PlayerHands(newTable, player2.hands.cardsOnHand, player2.hands.outside)
      return copy(table = newTable, player = newPlayer, player2 = Player(player2.name, newPlayerHands, newTable), deck)
    else
      val (newPlayer, newTable) = player2.dropMultipleCards(list, dec, hasJoker)
      val newPlayerHands = PlayerHands(newTable, player.hands.cardsOnHand, player.hands.outside)
      return copy(table = newTable, player = Player(player.name, newPlayerHands, newTable), player2 = newPlayer, deck)
  }

  def sortPlayersCards(player1Turn: Boolean): Game = {
    if (player1Turn)
      val newPlayer = player.sortPlayersCards
      copy(table, player = newPlayer, player2, deck)
    else
      val newPlayer = player2.sortPlayersCards
      copy(table, player, player2 = newPlayer, deck)
  }

  def victory(player1Turn: Boolean): Boolean = {
    if (player1Turn)
      player.victory
    else
      player2.victory
    end if
  }

  def showCards(player1Turn: Boolean): String = {
    if (player1Turn)
      player.showCards
    else
      player2.showCards
    end if
  }

  def showTable: String = player.showTable
}

    
