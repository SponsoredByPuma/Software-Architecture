package restDeck

import com.google.inject.name.Names
import com.google.inject.{Guice, Inject}

import scala.swing.Publisher
import scala.swing.event.Event
import net.codingwell.scalaguice.InjectorExtensions.*
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import play.api.libs.json.*

import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker

import scala.util.{Failure, Success, Try}

class DeckService(var deck: DeckInterface) {

    implicit def start(): Unit = {
    val binding = Http().newServerAt(RestUIHost, RestUIPort).bind(route)

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

    //val RestUIPort = 8081

    val RestUIPort: Int = sys.env.getOrElse("DECK_SERVICE_PORT", "8081").toInt
    val RestUIHost: String = sys.env.getOrElse("DECK_SERVICE_HOST", "romme-deck-service")

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
            deck = deck.createNewDeck()
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