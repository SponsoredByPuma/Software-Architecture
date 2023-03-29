package de.htwg.se.romme
package controller.controllerComponent.controllerBaseImpl

import model.modelComponent.gameComponent.GameInterface
import model.modelComponent.gameComponent.gameBaseImpl._
import controller.controllerComponent._
import de.htwg.se.romme.util.UndoManager

import scala.swing.Publisher
import com.google.inject.Inject
import com.google.inject.Guice
//import de.htwg.se.romme.model.modelComponent.fileIOComponent.FileIOInterface

case class Controller @Inject() (var game: GameInterface) extends ControllerInterface with Publisher{

  val injector = Guice.createInjector(new RommeModule)

  private val undoManager = new UndoManager

  //val fileIO = FileIOInterface()
  
  var player1Turn: Boolean = true

  def gameStart: Unit = {
    game = game.gameStart
    game = game.drawCards1
    game = game.drawCards2
    publish(new showPlayerTable)
  }

  def checkForJoker(list: List[Integer]): List[Integer] = {
    val returnValues: List[Integer] = List()
    if(player1Turn) {
      list.filter(cardsPlace => game.player.hands.playerOneHand(cardsPlace).placeInList.get == 15)
        .map(cardPlace => returnValues :+ list(cardPlace))
      returnValues
    } else {
      list.filter(cardsPlace => game.player2.hands.playerOneHand(cardsPlace).placeInList.get == 15)
        .map(cardPlace => returnValues :+ list(cardPlace))
      returnValues
    }
  }

  def replaceCardOrder(stelle: List[Integer], values: List[String]) : Unit = {
      game = game.replaceCardOrder(stelle,values,player1Turn)
  }

  def replaceCardSuit(stelle: List[Integer], values: List[String]) : Unit = {
    game = game.replaceCardSuit(stelle,values,player1Turn)
  }

  def switch: Unit = {
    player1Turn = !player1Turn
    publish(new showPlayerCards)
  }

  def playersTurn: Boolean = {
    this.player1Turn
  }

  def pickUpGraveYard: Unit = {
    game = game.pickUpGraveYard(player1Turn)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def pickUpACard: Unit = {
    game = game.pickUpACard(player1Turn)
    //undoManager.doStep(new GameCommand(game, this))
    publish(new showPlayerCards)
  }

  def dropASpecificCard(index: Integer): Unit = {
    game = game.dropASpecificCard(index, player1Turn)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def takeJoker(
      idxlist: Integer,
      idxCard: Integer): Unit = {
    game = game.takeJoker(idxlist, idxCard, player1Turn)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      hasJoker:Boolean
  ): Unit = {
    game = game.dropMultipleCards(list, dec, player1Turn, hasJoker)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def sortPlayersCards: Unit = {
    game = game.sortPlayersCards(player1Turn)
    publish(new showPlayerCards)
  }

  def victory: Boolean = {
    game.victory(player1Turn)
  }

  def showCards: String = {
    if(player1Turn)
      "PLAYER 1: " + game.showCards(player1Turn)
    else
     "PLAYER 2: " + game.showCards(player1Turn)
  }

  def getCards: List[Card] = {
    if(player1Turn)
      game.player.hands.playerOneHand
    else
      game.player2.hands.playerOneHand
    end if
  }

  def getCardsTable: List[List[Card]] = {
    game.table.droppedCardsList
  }

  def getGraveyardCard: Card = game.table.graveYard

  def showTable: String = {
    game.showTable
  }

  def undo: Unit = {
    //undoManager.undoStep
    print(game.deck.deckList.size)
  }
  def redo: Unit = {
    //undoManager.redoStep
    print(game.deck.deckList.size)
  }


  def addCard(
      idxCard: Integer,
      idxlist: Integer
  ): Unit = {
    game = game.addCard(idxCard, idxlist, player1Turn)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def load: Unit = {
    //game = fileIO.load
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def save: Unit = {
   // fileIO.save(game)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }
}
