package restCard

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


    def makeRealCard(cardName: String): CardInterface = {
        var cardNameWithOutBrackets = cardName.substring(1, cardName.length() - 1)
        var cardArray = cardNameWithOutBrackets.split(",")
        var suit = 0
        cardArray(0) match {
            case "Heart" =>
                suit = 0
            case "Diamond" =>
                suit = 1
            case "Club"=>
                suit = 2
            case "Spades"=>
                suit = 3
            case "Joker"=>
                suit = 4
            case ""=>
                suit = 5
        }
        var rank = 0
        cardArray(1) match {
            case "two"=>
                rank = 0
            case "three"=>
                rank = 1
            case "four"=>
                rank = 2
            case "five"=>
                rank = 3
            case "six"=>
                rank = 4
            case "seven"=>
                rank = 5
            case "eight"=>
                rank = 6
            case "nine"=>
                rank = 7
            case "ten"=>
                rank = 8
            case "jack"=>
                rank = 9
            case "queen"=>
                rank = 10
            case "king"=>
                rank = 11
            case "ace"=>
                rank = 12
        }
        return Card(suit, rank)
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
                    val realCard: CardInterface = makeRealCard(card)
                    val suit = realCard.getSuit
                    val json = Json.obj("suit" -> suit)
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
      post {
        path("getSuitNumber") {
            parameter("card") {
                (card) => {
                    val realCard: CardInterface = makeRealCard(card)
                    val suitNumber = realCard.getSuitNumber
                    val json = Json.obj("suitNumber" -> JsNumber(BigDecimal(suitNumber)))
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
      post {
        path("getValue") {
            parameter("card") {
                (card) => {
                    val realCard: CardInterface = makeRealCard(card)
                    val value = realCard.getValue
                    val json = Json.obj("value" -> JsNumber(BigDecimal(value)))
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
      post {
        path("getCardName") {
            parameter("card") {
                (card) => {
                    val realCard: CardInterface = makeRealCard(card)
                    val (suit, rank) = realCard.getCardName
                    val json = Json.obj("suit" -> suit,
                                        "rank" -> rank)
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
      post {
        path("placeInList") {
            parameter("card") {
                (card) => {
                    val realCard: CardInterface = makeRealCard(card)
                    val placeInList = realCard.placeInList
                    val json = Json.obj("placeInList" -> JsNumber(BigDecimal(placeInList.get)))
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
      post {
        path("getCardNameAsString") {
            parameter("card") {
                (card) => {
                    val realCard: CardInterface = makeRealCard(card)
                    val cardNameAsString = realCard.getCardNameAsString
                    val json = Json.obj("cardNameAsString" -> cardNameAsString)
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },
      post {
        path("getRank") {
            parameter("card") {
                (card) => {
                    val realCard: CardInterface = makeRealCard(card)
                    val rank = realCard.getRank
                    val json = Json.obj("rank" -> JsNumber(BigDecimal(rank)))
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,json.toString()))
                }
            }
        }
      },

    )
}