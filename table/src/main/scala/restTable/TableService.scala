package restTable

import akka.http.scaladsl.server.PathMatcher
import akka.http.scaladsl.server.PathMatcher1
import akka.http.scaladsl.server.util.Tuple._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import play.api.libs.json._
import com.typesafe.config.ConfigFactory

import scala.io.StdIn
import scala.concurrent.ExecutionContext.Implicits.global

import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table

object TableService {
    val config = ConfigFactory.load()
    val port = config.getInt("port.table")
    private var server: Option[Http.ServerBinding] = None
    given system: ActorSystem = ActorSystem("TableService")
    var table: TableInterface = new Table(Card(5, 0), List[List[CardInterface]]())
    @main def main = {
        val Card: PathMatcher[CardInterface] = {
                    val suit = "0" | "1" | "2" | "3" | "4" | "5" 
                    val rank = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" | "10" | "11" | "12" | "13"
                    implicit val cardTuple: akka.http.scaladsl.server.util.Tuple[cardComponent.CardInterface] = akka.http.scaladsl.server.util.Tuple1(_)
                    (suit ~ rank).tmap { case (s, r) =>
                     cardComponent.cardBaseImpl.Card(s.asInstanceOf[String].toInt, r.asInstanceOf[String].toInt)
                     }
                }
        val CardList: PathMatcher1[List[CardInterface]] = {
            segment.map { s =>
                // some logic to convert the string s to a list of cards
                // for example, you can split the string by commas and then use your Card path matcher on each substring
                s.split(",").map { c =>
                Card.unapply(c).get // use your Card path matcher to extract a card from each substring
                }.toList // convert the array of cards to a list
            }
        }
        val route = path("replaceGraveYard" / Card) {
            card =>
            get {
                
                table = table.replaceGraveYard(card)
                val json = Json.obj(
                    "droppedCards" -> (for (x <- 0 until table.droppedCardsList.size) yield Json.obj(
                        "new List" -> vectorToJson(table.droppedCardsList(x)),
                        "size" + x.toString  -> table.droppedCardsList(x).size)),
                    "droppedCardsAnzahl" -> table.droppedCardsList.size,
                    "friedhof" -> karteToJson(table.graveYard)
                    )
                complete(json.toString())
            }
        } ~
        path("placeCardsOnTable" / repeat(Card, separator = Slash)) {
            cards =>
            get {
                table = table.placeCardsOnTable(cards)
                val json = Json.obj(
                    "droppedCards" -> (for (x <- 0 until table.droppedCardsList.size) yield Json.obj(
                        "new List" -> vectorToJson(table.droppedCardsList(x)),
                        "size" + x.toString  -> table.droppedCardsList(x).size)),
                    "droppedCardsAnzahl" -> table.droppedCardsList.size,
                    "friedhof" -> karteToJson(table.graveYard)
                    )
                complete(json.toString())
            }
        } ~
        path("grabGraveYard") {
            get {
                val (pickedUpCard, newTable) = table.grabGraveYard()
                table = newTable
                val json = Json.obj(
                    "droppedCards" -> (for (x <- 0 until table.droppedCardsList.size) yield Json.obj(
                        "new List" -> vectorToJson(table.droppedCardsList(x)),
                        "size" + x.toString  -> table.droppedCardsList(x).size)),
                    "droppedCardsAnzahl" -> table.droppedCardsList.size,
                    "friedhof" -> karteToJson(table.graveYard),
                    "pickedUpCard" -> karteToJson(pickedUpCard.get)
                    )
                complete(json.toString())
            }
        } ~
        path("addCardToList" / CardList / IntNumber) {
            (list, idx) =>
            get {
                table = table.addCardToList(list, idx)
                val json = Json.obj(
                    "droppedCards" -> (for (x <- 0 until table.droppedCardsList.size) yield Json.obj(
                        "new List" -> vectorToJson(table.droppedCardsList(x)),
                        "size" + x.toString  -> table.droppedCardsList(x).size)),
                    "droppedCardsAnzahl" -> table.droppedCardsList.size,
                    "friedhof" -> karteToJson(table.graveYard)
                    )
                complete(json.toString())
            }
        } ~
        path(config.getString("route.shutdown")) {
            get {
                shutdown()
                complete("Server shutting down...")
            }
        }
        val server = Some(Http().newServerAt("localhost", port).bind(route))
        server.get.map { _ => 
            println("Server online at http://localhost:" + port)
        }  recover { case ex => 
            println(s"Server could not start: ${ex.getMessage}")
        }
    }

    def shutdown(): Unit = {
        println("Server shutting down...")
        system.terminate()
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