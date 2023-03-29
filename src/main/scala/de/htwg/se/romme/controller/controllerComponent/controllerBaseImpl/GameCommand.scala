package de.htwg.se.romme
package controller.controllerComponent.controllerBaseImpl

import de.htwg.se.romme.util.Command
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl._
import de.htwg.se.romme.model.modelComponent.gameComponent.GameInterface
import de.htwg.se.romme.controller.controllerComponent.ControllerInterface

class GameCommand(
    gaming: GameInterface,
    controller: de.htwg.se.romme.controller.controllerComponent.controllerBaseImpl.Controller
) extends Command {
  override def doStep: Unit = controller.game = controller.game.set(
    gaming.table,
    gaming.player,
    gaming.player2,
    gaming.deck
  )

  override def undoStep: Unit = {
    val t = new Table()
    var h = new PlayerHands(gaming.table, List[Card]())
    var d = new Deck(List[Card]())
    val c: Card = gaming.player.hands.playerOneHand.last
    h = gaming.player.hands
    //h.playerOneHand.remove(h.playerOneHand.size - 1)
    d = gaming.deck
    //c +=: d.deckList
    controller.game = controller.game.set(t, gaming.player, gaming.player2, d)
  }

  override def redoStep: Unit = {
    var t = new Table()
    var h = new PlayerHands(gaming.table, List[Card]())
    var d = new Deck(List[Card]())
    var c: Card = gaming.deck.deckList(0)
    d = gaming.deck
    //d.deckList.remove(0)
    h = gaming.player.hands
    h.playerOneHand :+ List(c)
    controller.game = controller.game.set(t, gaming.player, gaming.player2, d)
  }
}
