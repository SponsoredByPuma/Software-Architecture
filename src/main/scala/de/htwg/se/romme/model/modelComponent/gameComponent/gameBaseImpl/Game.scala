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

  def replaceCardOrder(stelle: List[Integer], values: List[String], player1Turn: Boolean): Game = {
    if (player1Turn) {
      for (x <- 0 to stelle.size - 1)
        var  t21 = 1
        //player.hands.cardsOnHand.insert(stelle(x), Joker().setValue(values(x)))
        //player.hands.cardsOnHand.remove(stelle(x) + 1)
    } else {
    for (x <- 0 to stelle.size - 1)
      var  t21 = 1
      //player2.hands.cardsOnHand.insert(stelle(x), Joker().setValue(values(x)))
      //player2.hands.cardsOnHand.remove(stelle(x) + 1)
    }
    copy(table, player, player2, deck)
  }

  def replaceCardSuit(stelle: List[Integer], values: List[String], player1Turn: Boolean): Game = {
    if (player1Turn)
      for (x <- 0 to stelle.size - 1)
        var  t21 = 1
        //player.hands.cardsOnHand.insert(stelle(x), Joker().setSuit(values(x)))
        //player.hands.cardsOnHand.remove(stelle(x) + 1)
    else
      for (x <- 0 to stelle.size - 1) {
        var  t21 = 1
        //player2.hands.cardsOnHand.insert(stelle(x), Joker().setSuit(values(x)))
        //player2.hands.cardsOnHand.remove(stelle(x) + 1)

      }
    end if
    copy(table, player, player2, deck)
  }

  def dropASpecificCard(index: Integer, player1Turn: Boolean): Game = {
    if (player1Turn)
      val (newPlayer, newTable) = player.dropASpecificCard(index)
      return copy(table = newTable, player = newPlayer, player2 = Player(player2.name, player2.hands, newTable), deck)
    else
      val (newPlayer,newTable) = player2.dropASpecificCard(index)
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
      player = player.takeJoker(idxlist, idxCard)
    else
      player2 = player2.takeJoker(idxlist, idxCard)
    end if
      copy(table, player, player2, deck)
  }

  def dropMultipleCards(list: List[Integer], dec: Integer, player1Turn: Boolean, hasJoker: Boolean): Game = {
    if (player1Turn)
      val (newPlayer, newTable) = player.dropMultipleCards(list, dec, hasJoker)
      copy(table = newTable, player = newPlayer, player2 = Player(player2.name, player2.hands, newTable), deck)
    else
      val (newPlayer, newTable) = player2.dropMultipleCards(list, dec, hasJoker)
      copy(table = newTable, player = Player(player.name, player.hands, newTable), player2 = newPlayer, deck)
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

    
