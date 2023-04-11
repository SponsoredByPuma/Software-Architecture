package de.htwg.se.romme
package controller.controllerComponent.controllerBaseImpl

import dienste.*
import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl._
import de.htwg.se.romme.controller.controllerComponent.ControllerInterface

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
    val t = new Table(Card(5, 0), List[List[Card]]())
    var d = new Deck(List[Card]())
    val c: Card = gaming.players(0).hand.last
    val h = gaming.players(0).hand
    //h.cardsOnHand.remove(h.cardsOnHand.size - 1)
    d = gaming.deck
    //c +=: d.deckList
    controller.game = controller.game.set(t, gaming.players, d)
  }

  override def redoStep: Unit = {
    var t = new Table(Card(5, 0), List[List[Card]]())
    var d = new Deck(List[Card]())
    var c: Card = gaming.deck.deckList(0)
    d = gaming.deck
    //d.deckList.remove(0)
    val h = gaming.players(0).hand ::: List(c)
    controller.game = controller.game.set(t, gaming.players, d)
  }
}
