package de.htwg.se.romme
package controller.controllerComponent.controllerBaseImpl

import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl._
import controller.controllerComponent._
import dienste.UndoManager

import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker

import scala.swing.Publisher
import com.google.inject.Inject
import com.google.inject.Guice
import fileIOComponent.FileIOInterface

import akka.actor.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.javadsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*
import akka.http.scaladsl.model.headers.*
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer, SystemMaterializer}

import scala.concurrent.{ExecutionContextExecutor, Future}

case class Controller @Inject() (var game: GameInterface)
    extends ControllerInterface
    with Publisher {

  val injector = Guice.createInjector(new RommeModule)
  var playerState: PlayerState = PlayerOne

  private val undoManager = new UndoManager
  implicit val system: ActorSystem = ActorSystem()
  implicit val mat: Materializer = SystemMaterializer(system).materializer

  val fileIO = FileIOInterface()

  val fileIOUri = "http://localhost:8082/"


  def getRequest(path: String): Future[HttpResponse] = {
    val http = Http()
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = fileIOUri + path,
      entity = fileIO.gameToJson(game).toString()
    )
    http.singleRequest(request)
  }

  def gameStart: Unit = {
    game = game.gameStart
    game = game.drawCards(playerState.getPlayer)
    game = game.drawCards(playerState.changePlayer.getPlayer)
    publish(new showPlayerTable)
  }

  def getIntsOptions(index: Integer): Option[Integer] = {
    if (index > (game.players(playerState.getPlayer).hand.size - 1))
      return None
    else
      return Some(index)
  }

  def checkIfDropsAreCorrect(
      cards: List[Option[Integer]],
      finalList: List[Integer]
  ): List[Integer] = {
    for (card <- cards) yield card match {
      case Some(value) =>
        return checkIfDropsAreCorrect(cards.tail, finalList ::: List(value))
      case None => return List[Integer]()
    }
    return finalList
  }

  def checkForJoker(list: List[Integer]): List[Integer] = {
    val areAllIntsCorrect = list.map(index => getIntsOptions(index))
    val test = checkIfDropsAreCorrect(areAllIntsCorrect, List[Integer]())
    val returnValues: List[Integer] = test
      .filter(cardsPlace =>
        game
          .players(playerState.getPlayer)
          .hand(cardsPlace)
          .placeInList
          .get == 15
      )
      .map(cardPlace => cardPlace)
    for (value <- returnValues) {
      println(value)
    }
    returnValues
  }

  def replaceCardOrder(stelle: List[Integer], values: List[String]): Unit = {
    game = game.replaceCardOrder(stelle, values, playerState.getPlayer)
  }

  def replaceCardSuit(stelle: List[Integer], values: List[String]): Unit = {
    game = game.replaceCardSuit(stelle, values, playerState.getPlayer)
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

  def takeJoker(idxlist: Integer, idxCard: Integer): Unit = {
    game = game.takeJoker(idxlist, idxCard, playerState.getPlayer)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def dropMultipleCards(
      list: List[Integer],
      dec: Integer,
      hasJoker: Boolean
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

  def getCards: List[CardInterface] = {
    game.players(playerState.getPlayer).hand
  }

  def getCardsTable: List[List[CardInterface]] = {
    game.table.droppedCardsList
  }

  def getGraveyardCard: CardInterface = game.table.graveYard

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

  def fillHand(playerIdx: Integer, fillUntil: Integer): Unit = {
    game = game.fillHand(playerIdx, fillUntil)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def load: Unit = {
    game = fileIO.load
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def save: Unit = {
    val result = getRequest("save")
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }
}
