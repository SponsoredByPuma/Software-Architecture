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

class DeckService() {

    implicit def start(): Unit = {
    val binding = Http().newServerAt("localhost", RestUIPort).bind(route)

        binding.onComplete {
            case Success(binding) => {
                println(s"Successfully started")
            }
            case Failure(exception) => {
                println(s"Start Failed: ${exception.getMessage}")
            }
        }
    }
    
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    val RestUIPort = 8081
    val routes: String =
    """
        """.stripMargin

    val route: Route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      get {
        path("createNewDeck") {
            val deck = deck.createNewDeck()
            val json = Json.obj("randomCards" -> vectorToJson(deck.deckList),
                                    "groesse" -> deck.deckList.size)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
        }
      },
      get {
        path("drawFromDeck") {
            val c = deck.drawFromDeck()
            c match {
                case Success((card: CardInterface, newDeck: DeckInterface)) => {
                    deck = newDeck
                    val json = Json.obj("randomCards" -> vectorToJson(deck.deckList),
                                        "groesse" -> deck.deckList.size,
                                        "drawedCard" -> card.getCardNameAsString)
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
    )

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