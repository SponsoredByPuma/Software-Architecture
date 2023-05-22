package databaseSlick
import cardComponent.CardInterface
import databaseSlick.DAOInterface
import databaseSlick.sqlTables.GameTable

import java.sql.SQLNonTransientException
import slick.lifted.TableQuery
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*

import scala.util.{Failure, Success, Try}
import concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}



import slick.lifted.TableQuery
import scala.util.Try
import java.sql.SQLNonTransientException
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api.*
import scala.util.{Failure, Success, Try}
import concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

import model.gameComponent.GameInterface
import deckComponent.deckBaseImpl.Deck
import cardComponent.cardBaseImpl.Card
import model.gameComponent.gameBaseImpl.Game
import tableComponent.tableBaseImpl.Table
import model.gameComponent.gameBaseImpl.Player


val WAIT_TIME = 5.seconds
val WAIT_DB = 5000

class MongoDAO extends DAOInterface {

  private val database_pw = "mongo"
  private val database_username = "root"
  private val host = "localhost"
  private val port = "27017"

  val databaseUrl = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"

  private val mongoClient: MongoClient = MongoClient(databaseUrl)
  val database: MongoDatabase = mongoClient.getDatabase("romme")
  val gameCollection: MongoCollection[Document] = database.getCollection("games")

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
      val query = id.map(id => gameTable.filter(_.id === id))
        .getOrElse(gameTable.filter(_.id === gameTable.map(_.id).max))

      val game = Await.result(database.run(query.result), WAIT_TIME)
      println("Loading the game from MySQl")

      val deckSize = game.head._2
      val deckList = game.head._3
      val graveYardCard = game.head._4
      val tableCards = game.head._5
      val playerOneName = game.head._6
      val playerOneAmount = game.head._7
      val playerOneHand = game.head._8
      val playerTwoName = game.head._9
      val playerTwoAmount = game.head._10
      val playerTwoHand = game.head._11

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
    val game = (0, deckSize, deckList, graveYardCard, droppedCards, playerOneName, playerOneAmount,
                playerOneHand, playerTwoName, playerTwoAmount, playerTwoHand)
    val query = gameTable returning gameTable.map(_.id)
    val action = query += game
    val result = database.run(action)
    Await.result(result, WAIT_TIME)
  }

  override def deleteGame(id: Int): Try[Boolean] = Try {
    Await.result(database.run(gameTable.filter(_.id === id).delete), WAIT_TIME)
    true
  }

  def vectorToString(vec: List[CardInterface]): String = {
    var rtn = ""
    for (card <- vec) {
      rtn += cardToString(card) + "-"
    }
    rtn
    /*
    for {
        i <- vec
      } yield {
        cardToString(i)
      }
    */
  }

  def stringToVector(string: String): List[CardInterface] = {
    string.split("-").map(getCard).toList
  }

  def cardToString(card: CardInterface) = card.getCardNameAsString
}