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
  var playerState: PlayerState = PlayerOne

  private val undoManager = new UndoManager

  //val fileIO = FileIOInterface()

  def gameStart: Unit = {
    game = game.gameStart
    game = game.drawCards(playerState.getPlayer)
    game = game.drawCards(playerState.changePlayer.getPlayer)
    publish(new showPlayerTable)
  }

  def checkForJoker(list: List[Integer]): List[Integer] = {
    val returnValues: List[Integer] = list.filter(cardsPlace => game.players(playerState.getPlayer).hand(cardsPlace).placeInList.get == 15)
      .map(cardPlace => cardPlace)
    returnValues
  }

  def replaceCardOrder(stelle: List[Integer], values: List[String]) : Unit = {
      game = game.replaceCardOrder(stelle,values,playerState.getPlayer)
  }

  def replaceCardSuit(stelle: List[Integer], values: List[String]) : Unit = {
    game = game.replaceCardSuit(stelle,values,playerState.getPlayer)
  }

  def switch: Unit = {
    playerState = playerState.changePlayer
    publish(new showPlayerCards)
  }

  def pickUpGraveYard: Unit = {
    game = game.pickUpGraveYard(playerState.getPlayer)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def pickUpACard: Unit = {
    game = game.pickUpACard(playerState.getPlayer)
    //undoManager.doStep(new GameCommand(game, this))
    publish(new showPlayerCards)
  }

  def dropASpecificCard(cardIdx: Integer): Unit = {
    game = game.dropASpecificCard(cardIdx, playerState.getPlayer)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def takeJoker(
      idxlist: Integer,
      idxCard: Integer): Unit = {
    game = game.takeJoker(idxlist, idxCard, playerState.getPlayer)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      hasJoker:Boolean
  ): Unit = {
    game = game.dropMultipleCards(list, dec, playerState.getPlayer, hasJoker)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def sortPlayersCards: Unit = {
    game = game.sortPlayersCards(playerState.getPlayer)
    publish(new showPlayerCards)
  }

  def victory: Boolean = {
    game.victory(playerState.getPlayer)
  }

  def showCards: String = {
    playerState.name + game.showCards(playerState.getPlayer)
  }

  def getCards: List[Card] = {
    game.players(playerState.getPlayer).hand
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
    game = game.addCard(idxCard, idxlist, playerState.getPlayer)
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
