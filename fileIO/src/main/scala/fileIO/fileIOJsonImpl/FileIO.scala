package fileIOJsonImpl

import play.api.libs.json._
import java.io._
import scala.io.Source
import scala.collection.mutable.ListBuffer

import fileIO.FileIOInterface
import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl.Player
import model.gameComponent.gameBaseImpl.Game

import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker

class FileIO extends FileIOInterface {

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
    if(s.equals("(,)"))
      return Card(5,0)
    end if
    val s1 = s.dropRight(1)
    val s2 = s1.substring(1)
    val s3 = s2.split(",")

    val zuit = suitForCard.apply(s3(0))
    if(zuit > 3)
      val car = Card(zuit,0)
      car
    else
      val car = Card(zuit, rankForCard.apply(s3(1)))
      car
    end if
  }

  override def load: GameInterface = {
    val source: String = Source.fromFile("game.json").getLines.mkString
    val json: JsValue = Json.parse(source)

    val d = (json \ "game" \ "Deck").get
    val groesse = (d \ "groesse").get.as[Int]
    val d1k: List[CardInterface] =
      (for (i <- 0 until groesse) yield {
        getCard( (d \\ "cardName")(i).as[String])
      }).toList

    val d1d: List[CardInterface] = List()
    d1d :+ List(d1k)

    for(x <- d1d)
      print(x.getCardName)
    val dekk: DeckInterface = Deck(List[CardInterface]())
    dekk.deckList :+ List(d1d)

    val t = (json \ "game" \ "Table").get
    val grav = (t \ "friedhof" \ "cardName").get.as[String]
    val yard = getCard(grav)
    print(yard.getCardName)
    val tAnzahl = (t \ "droppedCardsAnzahl").get.as[Int]
    val test = (t \ "droppedCards").get
    var count = 0
    var count2 = 0
    val tk: List[List[CardInterface]] = {
      (for (i <-0 until tAnzahl) yield {
        val str = "size" + i.toString
        val big = (t \\ str)
        val big2 = big.toList
        val big3 = big2(0).as[Int]
        count = count + big3
        val ll: List[CardInterface] = List()
        (for (x <- count2 until count) yield {
          println(x)
          ll :+ List(getCard( (test \\ "cardName")(x).as[String]))
        })
        count2 = count2 + big3
        ll
      }).toList
    }
    val ts: List[List[CardInterface]] = List()
    for(x <- 0 until tk.size) yield {
      val tmpL: List[CardInterface] = List()
      for(y <- tk(x)) yield {
        tmpL :+ List(y)
      }
      ts :+ List(tmpL)
    }

    val teible: TableInterface = Table(Card(5, 0), List[List[CardInterface]]())
    //teible.graveYard = yard
    teible.droppedCardsList:+ List(ts)

    val p1 = (json \ "game" \ "player1").get
    val p1n = (p1 \ "name").get.as[String]
    val p1Anzahl = (p1 \ "anzahl").get.as[Int]
    val p1k: List[CardInterface] =
      (for (i <- 0 until p1Anzahl) yield {
        getCard( (p1 \\ "cardName")(i).as[String])
      }).toList
    val p1c: List[CardInterface] = List()
    val p1Hand: List[CardInterface] = p1c ::: p1k
    val player1: Player = Player("Player 1", p1Hand, false)

    val p2 = (json \ "game" \ "player2").get
    val p2n = (p2 \ "name").get.as[String]
    val p2Anzahl = (p2 \ "anzahl").get.as[Int]
    val p2k: List[CardInterface] =
      (for (i <- 0 until p2Anzahl) yield {
        getCard( (p2 \\ "cardName")(i).as[String])
      }).toList
    val p2c: List[CardInterface] = List[CardInterface]()
    val p2Hand: List[CardInterface] = p2c ::: p2k
    val player2: Player = Player("Player 2", p2Hand, false)
    val game: Game = Game(teible, List(player1, player2),dekk)
    game
  }

  override def save(game: GameInterface): Unit = {
    val pw = new PrintWriter(new File("game.json"))
    pw.write(Json.prettyPrint(gameToJson(game)))
    pw.close
  }

  def gameToJson(game: GameInterface) = {
    Json.obj(
      "game" -> Json.obj(
        "player1" -> Json.obj(
          "name" -> game.players(0).name,
          "karten" -> vectorToJson(game.players(0).hand),
          "anzahl" ->game.players(0).hand.size
        ),
        "player2" -> Json.obj(
          "name" -> game.players(1).name,
          "karten" -> vectorToJson(game.players(1).hand),
          "anzahl" -> game.players(1).hand.size
        ),
        "Table" -> Json.obj(
          "droppedCards" -> (for (x <- 0 until game.table.droppedCardsList.size) yield Json.obj(
            "new List" -> vectorToJson(game.table.droppedCardsList(x)),
            "size" + x.toString  -> game.table.droppedCardsList(x).size)),
          "droppedCardsAnzahl" -> game.table.droppedCardsList.size,
          "friedhof" -> karteToJson(game.table.graveYard)
        ),
        "Deck" -> Json.obj(
          "randomCards" -> vectorToJson(game.deck.deckList),
          "groesse" -> game.deck.deckList.size
        )
      )
    )
  }

  def listListToJson(entryList: List[List[CardInterface]]): JsValue = {
    Json.toJson(
      for {
        i <- entryList
      } yield {
        vectorToJson(i)
      }
    )
  }

  def karteToJson(kaertle: CardInterface) =
    Json.toJson(
      Json.obj(
        "cardName" -> kaertle.getCardNameAsString
      )
    )

  def vectorToJson(vec: List[CardInterface]) =
    Json.toJson(
      for {
        i <- vec
      } yield {
        Json.obj(
          "cardName" -> i.getCardNameAsString
        )
      }
    )

}

