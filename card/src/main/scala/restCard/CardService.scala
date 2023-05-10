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

import card.cardComponent.CardInterface
import card.cardComponent.cardBaseImpl.Card

object CardService {
    val config = ConfigFactory.load()
    val port = config.getInt("port.card")
    private var server: Option[Http.ServerBinding] = None
    given system: ActorSystem = ActorSystem("CardService")
    var card: CardInterface = Card(2, 0)
    @main def main = {
        val route = path("getSuit") {
            get {
                val suit = card.getSuit
                val json = Json.obj("suit" -> suit)
                complete(json.toString())
            }
        } ~
        path("getSuitNumber") {
            get {
                val suitNumber = card.getSuitNumber
                val json = Json.obj("suitNumber" -> JsNumber(BigDecimal(suitNumber)))
                complete(json.toString())
            }
        } ~
        path("getValue") {
            get {
                val value = card.getValue
                val json = Json.obj("value" -> JsNumber(BigDecimal(value)))
                complete(json.toString())
            }
        } ~
        path("getCardName") {
            get {
                val (suit, rank) = card.getCardName
                val json = Json.obj("suit" -> suit,
                                    "rank" -> rank)
                complete(json.toString())
            }
        } ~
        path("placeInList") {
            get {
                val placeInList = card.placeInList
                val json = Json.obj("placeInList" -> JsNumber(BigDecimal(placeInList.get)))
                complete(json.toString())
            }
        } ~
        path("getCardNameAsString") {
            get {
                val cardNameAsString = card.getCardNameAsString
                val json = Json.obj("cardNameAsString" -> cardNameAsString)
                complete(json.toString())
            }
        } ~
        path("getRank") {
            get {
                val rank = card.getRank
                val json = Json.obj("rank" -> JsNumber(BigDecimal(rank)))
                complete(json.toString())
            }
        } ~
        path(config.getString("route.shutdown")) {
            get {
                shutdown()
                complete("Server shutting down...")
            }
        }
        val server = Some(Http().newServerAt("card", port).bind(route))
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