package restCard

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

class CardService() {

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

    val RestUIPort = 8080
    val routes: String =
    """
        """.stripMargin

    val route: Route =
    concat(
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
      },
      post {
        path("getSuit") {
            parameter("card") { 
                (card) => {
                    val suit = card.getSuit
                    val json = Json.obj("suit" -> suit)
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
      get {
        path("getSuitNumber") {
            val suitNumber = card.getSuitNumber
            val json = Json.obj("suitNumber" -> JsNumber(BigDecimal(suitNumber)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
        }
      },
      get {
        path("getValue") {
            val value = card.getValue
            val json = Json.obj("value" -> JsNumber(BigDecimal(value)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
        }
      },
      get {
        path("getCardName") {
            val (suit, rank) = card.getCardName
            val json = Json.obj("suit" -> suit,
                                "rank" -> rank)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
        }
      },
      get {
        path("placeInList") {
            val placeInList = card.placeInList
            val json = Json.obj("placeInList" -> JsNumber(BigDecimal(placeInList.get)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
        }
      },
      get {
        path("getCardNameAsString") {
            val cardNameAsString = card.getCardNameAsString
            val json = Json.obj("cardNameAsString" -> cardNameAsString)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
        }
      },
      get {
        path("getRank") {
            val rank = card.getRank
            val json = Json.obj("rank" -> JsNumber(BigDecimal(rank)))
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
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