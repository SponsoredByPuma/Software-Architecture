package de.htwg.se.romme
package controller.controllerComponent.controllerBaseImpl

import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl.*
import controller.controllerComponent.*
import dienste.UndoManager
import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker
import scala.util.{Try, Success, Failure}

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
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCode}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.model.Uri
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import akka.http.scaladsl.model.Uri
import databaseSlick._

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

  val testDAO = new SlickDAO

  val mongo = new MongoDAO

  val fileIOUri = "http://localhost:8082/"


  def putRequest(path: String): Future[HttpResponse] = {
    val http = Http()
    val request = HttpRequest(
      method = HttpMethods.PUT,
      uri = fileIOUri + path,
      entity = fileIO.gameToJson(game).toString()
    )
    http.singleRequest(request)
  }

  def getRequest(path: String): Future[HttpResponse] = {
    val http = Http()
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = fileIOUri + path
    )
    http.singleRequest(request)
  }

  def gameStart: Unit = {
    game = game.gameStart
    game = game.drawCards(playerState.getPlayer)
    game = game.drawCards(playerState.changePlayer.getPlayer)

    testDAO.save(game)
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
    val result = getRequest("load")
    var resJSON = ""
    val res = result.flatMap { response =>
      response.status match {
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String].map { jsonStr =>
            resJSON = jsonStr
          }
        case _ =>
          Future.failed(new RuntimeException(s"HTTP request failed with status ${response.status} and entity ${response.entity}"))
      }
    }
    Await.result(res, 10.seconds)
    //game = fileIO.jsonToGame(resJSON)
   /*game = testDAO.load(None) match {
     case Success(result) => result
     case Failure(exception) => throw exception
   }*/
    game = Await.result(mongo.load(None), 5.seconds) match {
      case Success(result) => result
      case Failure(exception) => throw exception
    }
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }

  def save: Unit = {
    val result = putRequest("save")
    val res = result.flatMap { response =>
            response.status match {
                case StatusCodes.OK =>
                Unmarshal(response.entity).to[String].map { jsonStr =>
                  println(s"Request completed successfully with status ${response.status} and content:\n${entity}")
                }
                case _ =>
                Future.failed(new RuntimeException(s"Failed : ${response.status} ${response.entity}"))
            }
        }
    Await.result(res, 10.seconds)
    //testDAO.save(game)
    Await.result(mongo.save(game), 5.seconds)
    publish(new showPlayerCards)
    publish(new showPlayerTable)
  }
}
