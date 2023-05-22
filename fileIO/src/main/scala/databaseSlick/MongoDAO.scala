package databaseSlick
import cardComponent.CardInterface
import databaseSlick.DAOInterface

import scala.util.{Failure, Success, Try}
import concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}



import scala.util.Try
import scala.util.{Failure, Success, Try}
import concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

import org.mongodb.scala.model.*
import org.mongodb.scala.model.Aggregates.*
import org.mongodb.scala.model.Filters.*
import org.mongodb.scala.model.Sorts.*
import org.mongodb.scala.result.{DeleteResult, InsertOneResult, UpdateResult}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer, SingleObservable, result}
import org.mongodb.scala.documentToUntypedDocument
import com.mongodb.ConnectionString
import org.mongodb.scala.SingleObservableFuture

import model.gameComponent.GameInterface
import deckComponent.deckBaseImpl.Deck
import cardComponent.cardBaseImpl.Card
import model.gameComponent.gameBaseImpl.Game
import tableComponent.tableBaseImpl.Table
import model.gameComponent.gameBaseImpl.Player


val WAIT_TIME = 5.seconds
val WAIT_DB = 5000

class MongoDAO extends DAOInterface {

  private val host = "localhost"
  private val port = "27017"

  val databaseUrl = s"mongodb://$host:$port/?authSource=romme"

  private val mongoClient: MongoClient = MongoClient(databaseUrl)
  val database: MongoDatabase = mongoClient.getDatabase("romme")
  val gameCollection: MongoCollection[Document] = database.getCollection("software-architecture")

  override def save(game: GameInterface): Unit = {
    println("Saving Game in Mongo")
    val deckSize = game.deck.deckList.size
    val deckList = vectorToString(game.deck.deckList)
    val graveYardCard = cardToString(game.table.graveYard)
    val droppedCards: String =  game.table.droppedCardsList.map(vectorToString).mkString("+")   
    val playerOneName = game.players(0).name
    val playerOneAmount = game.players(0).hand.size
    val playerOneHand = vectorToString(game.players(0).hand)
    val playerTwoName = game.players(1).name
    val playerTwoAmount = game.players(1).hand.size
    val playerTwoHand = vectorToString(game.players(1).hand)
    val gameID = storeGame(
        deckSize,
        deckList,
        graveYardCard,
        droppedCards,
        playerOneName,
        playerOneAmount,
        playerOneHand,
        playerTwoName,
        playerTwoAmount,
        playerTwoHand
      )
    println(s"Game saved in MySQL with ID $gameID")
  }

  val suitForCard = Map(
    "Heart" -> 0,
    "Diamond" -> 1,
    "Club" -> 2,
    "Spades" -> 3,
    "Joker" -> 4,
    "" -> 5
  )

  val rankForCard = Map(
    "two" -> 0,
    "three" -> 1,
    "four" -> 2,
    "five" -> 3,
    "six" -> 4,
    "seven" -> 5,
    "eight" -> 6,
    "nine" -> 7,
    "ten" -> 8,
    "jack" -> 9,
    "queen" -> 10,
    "king" -> 11,
    "ace" -> 12
  )

  def getCard(s: String): CardInterface = {
    if (s.equals("(,)")) {
      return Card(5, 0)
    }
    val s1 = s.dropRight(1)
    val s2 = s1.substring(1)
    val s3 = s2.split(",")

    val suit = suitForCard.apply(s3(0))
    if (suit > 3) {
      val card = Card(suit, 0)
      return card
    } else {
      val card = Card(suit, rankForCard.apply(s3(1)))
      return card
    }
  }

  def createDeck(cardNames: List[String], deck: List[CardInterface], size: Int): List[CardInterface] = {
    if (size == deck.size) {
      deck
    } else {
      createDeck(cardNames.tail, deck ::: List(getCard(cardNames.head)), size)
    }
  }

  override def load(id: Option[Int]): Try[GameInterface] = {
    Try {
    val query = id match {
      case Some(gameId) => gameCollection.find(equal("id", gameId))
      case None => gameCollection.find().sort(Document("id" -> -1)).limit(1)
    }

      val game = Await.result(query.headOption(), WAIT_TIME)
      println("Loading the game from MySQl")

      val deckSize = game.get.getInteger("deckSize")
      val deckList = game.get.getString("deckList")
      val graveYardCard = game.get.getString("graveYardCard")
      val tableCards = game.get.getString("droppedCards")
      val playerOneName = game.get.getString("playerOneName")
      val playerOneAmount = game.get.getInteger("playerOneAmount")
      val playerOneHand = game.get.getString("playerOneHand")
      val playerTwoName = game.get.getString("playerTwoName")
      val playerTwoAmount = game.get.getInteger("playerTwoAmount")
      val playerTwoHand = game.get.getString("playerTwoHand")

      val splittedDeck = deckList.split("-").toList
      val finalDeckList = createDeck(splittedDeck, List[CardInterface](), deckSize)

      val finalGraveYard = getCard(graveYardCard)
      val splittedTableCards = if (tableCards.isEmpty) List.empty[String] else tableCards.split("\\+").toList
      val finalTableCards = splittedTableCards.map { list =>
        createDeck(list.split("-").toList, List[CardInterface](), list.split("-").toList.size)
      }

      

      val splittedPlayerOneHand = playerOneHand.split("-").toList
      val finalPlayerOneHand = createDeck(splittedPlayerOneHand, List[CardInterface](), playerOneAmount)

      val splittedPlayerTwoHand = playerTwoHand.split("-").toList
      val finalPlayerTwoHand = createDeck(splittedPlayerTwoHand, List[CardInterface](), playerTwoAmount)

      val player1 = Player(playerOneName, finalPlayerOneHand, false)
      val player2 = Player(playerTwoName, finalPlayerTwoHand, false)

      Game(Table(finalGraveYard, finalTableCards),List(player1, player2),Deck(finalDeckList))
    }
  }

  override def storeGame(deckSize: Int, deckList: String, graveYardCard: String,
                         droppedCards: String, playerOneName: String,
                         playerOneAmount: Int, playerOneHand: String,
                         playerTwoName: String, playerTwoAmount: Int, playerTwoHand: String): Int = {
    val gameDoc = Document(
      "deckSize" -> deckSize,
      "deckList" -> deckList,
      "graveYardCard" -> graveYardCard,
      "droppedCards" -> droppedCards,
      "playerOneName" -> playerOneName,
      "playerOneAmount" -> playerOneAmount,
      "playerOneHand" -> playerOneHand,
      "playerTwoName" -> playerTwoName,
      "playerTwoAmount" -> playerTwoAmount,
      "playerTwoHand" -> playerTwoHand
    )
    val insertObservable = gameCollection.insertOne(gameDoc)
    Await.result(insertObservable.toFuture(), WAIT_TIME)
    gameDoc.getInteger("_id")
  }

  override def deleteGame(id: Int): Try[Boolean] = {
    val deleteObservable = gameCollection.deleteOne(equal("id", id))
    val result = Await.result(deleteObservable.toFuture(), 5.seconds)
    if (result.getDeletedCount == 1) {
      Success(true)
    } else {
      Failure(new NoSuchElementException("No game found with the given ID"))
    }
  }

  def vectorToString(vec: List[CardInterface]): String = {
    var rtn = ""
    for (card <- vec) {
      rtn += cardToString(card) + "-"
    }
    rtn
  }

  def stringToVector(string: String): List[CardInterface] = {
    string.split("-").map(getCard).toList
  }

  def cardToString(card: CardInterface) = card.getCardNameAsString
}