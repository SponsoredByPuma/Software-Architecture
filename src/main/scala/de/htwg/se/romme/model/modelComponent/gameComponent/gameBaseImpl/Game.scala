package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface

import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface
import com.google.inject.Inject

case class Game @Inject() (table: Table,var player: Player, var player2: Player, deck: Deck) extends GameInterface {

  def set(table: Table, player: Player, player2: Player, deck: Deck): Game = copy(table, player, player2, deck)

  def gameStart: Game = {
    deck.createNewDeck()
    player.hands.draw13Cards(deck)
    player2.hands.draw13Cards(deck)
    copy(table, player, player2, deck)
  }

  def pickUpGraveYard(player1Turn: Boolean): Game = {
    if (player1Turn) {
      player = player.pickUpGraveYard
    } else {
      player2 = player2.pickUpGraveYard
    }
      copy(table, player, player2, deck)
  }

  def pickUpACard(player1Turn: Boolean): Game = {
    if (player1Turn) {
      player = player.pickUpACard(deck)
    } else {
      player2 = player2.pickUpACard(deck)
    }
      copy(table, player, player2, deck)
  }

  def replaceCardOrder(stelle: List[Integer], values: List[String], player1Turn: Boolean): Game = {
    if (player1Turn) {
      for (x <- 0 to stelle.size - 1)
        var  t21 = 1
        //player.hands.playerOneHand.insert(stelle(x), Joker().setValue(values(x)))
        //player.hands.playerOneHand.remove(stelle(x) + 1)
    } else {
    for (x <- 0 to stelle.size - 1)
      var  t21 = 1
      //player2.hands.playerOneHand.insert(stelle(x), Joker().setValue(values(x)))
      //player2.hands.playerOneHand.remove(stelle(x) + 1)
    }
    copy(table, player, player2, deck)
  }

  def replaceCardSuit(stelle: List[Integer], values: List[String], player1Turn: Boolean): Game = {
    if (player1Turn)
      for (x <- 0 to stelle.size - 1)
        var  t21 = 1
        //player.hands.playerOneHand.insert(stelle(x), Joker().setSuit(values(x)))
        //player.hands.playerOneHand.remove(stelle(x) + 1)
    else
      for (x <- 0 to stelle.size - 1) {
        var  t21 = 1
        //player2.hands.playerOneHand.insert(stelle(x), Joker().setSuit(values(x)))
        //player2.hands.playerOneHand.remove(stelle(x) + 1)

      }
    end if
    copy(table, player, player2, deck)
  }

  def dropASpecificCard(index: Integer, player1Turn: Boolean): Game = {
    if (player1Turn)
      player = player.dropASpecificCard(index)
    else
      player2 = player2.dropASpecificCard(index)
    end if
      copy(table, player, player2, deck)
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
      player = player.dropMultipleCards(list, dec, hasJoker)
    else
      player2 = player2.dropMultipleCards(list, dec, hasJoker)
    end if
    copy(table, player, player2, deck)
  }

  def sortPlayersCards(player1Turn: Boolean): Game = {
    if (player1Turn)
      player.sortPlayersCards
    else
      player2.sortPlayersCards
    end if
      copy(table, player, player2, deck)
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

    
