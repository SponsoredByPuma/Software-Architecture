package de.htwg.se.romme
package controller.controllerComponent.controllerBaseImpl

import dienste.*
import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl._
import de.htwg.se.romme.controller.controllerComponent.ControllerInterface

import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker

class GameCommand(
    gaming: GameInterface,
    controller: de.htwg.se.romme.controller.controllerComponent.controllerBaseImpl.Controller
) extends Command {
  override def doStep: Unit = controller.game = controller.game.set(
    gaming.table,
    gaming.players,
    gaming.deck
  )

  override def undoStep: Unit = {
    val t = new Table(Card(5, 0), List[List[CardInterface]]())
    var d: DeckInterface = new Deck(List[CardInterface]())
    val c: CardInterface = gaming.players(0).hand.last
    val h = gaming.players(0).hand
    //h.cardsOnHand.remove(h.cardsOnHand.size - 1)
    d = gaming.deck
    //c +=: d.deckList
    controller.game = controller.game.set(t, gaming.players, d)
  }

  override def redoStep: Unit = {
    var t = new Table(Card(5, 0), List[List[CardInterface]]())
    var d: DeckInterface = new Deck(List[CardInterface]())
    var c: CardInterface = gaming.deck.deckList(0)
    d = gaming.deck
    //d.deckList.remove(0)
    val h = gaming.players(0).hand ::: List(c)
    controller.game = controller.game.set(t, gaming.players, d)
  }
}
