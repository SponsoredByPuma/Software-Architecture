package restDeck

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import play.api.libs.json._
import com.typesafe.config.ConfigFactory

import scala.io.StdIn
import scala.concurrent.ExecutionContext.Implicits.global

import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker

import scala.util.{Failure, Success, Try}

object DeckService {
    val config = ConfigFactory.load()
    val port = config.getInt("port.deck")
    private var server: Option[Http.ServerBinding] = None
    given system: ActorSystem = ActorSystem("DeckService")
    @main def main = {
        var deck: DeckInterface = new Deck(List[CardInterface]()) // hier wahrscheinlich noch eine List[CardInterface] dazu
        val route = path("createNewDeck") {
            get {
                deck = deck.createNewDeck()
                val json = Json.obj("randomCards" -> vectorToJson(deck.deckList),
                                    "groesse" -> deck.deckList.size)
                complete(json.toString())
            }
        } ~
        path("drawFromDeck") {
            get {
                val c = deck.drawFromDeck()
                c match {
                    case Success((card: CardInterface, newDeck: DeckInterface)) => {
                        deck = newDeck
                        val json = Json.obj("randomCards" -> vectorToJson(deck.deckList),
                                            "groesse" -> deck.deckList.size,
                                            "drawedCard" -> card.getCardNameAsString)
                        complete(json.toString())
                    }
                }
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