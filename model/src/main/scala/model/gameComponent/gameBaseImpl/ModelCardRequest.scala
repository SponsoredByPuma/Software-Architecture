package model.gameComponent.gameBaseImpl

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCode}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.http.javadsl.model.StatusCodes
import akka.http.javadsl.model.Uri
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import akka.actor.ActorSystem
import akka.stream.{Materializer, SystemMaterializer}
import akka.http.scaladsl.model.Uri

import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card

class ModelCardRequest {

    implicit val system: ActorSystem = ActorSystem()
    implicit val mat: Materializer = SystemMaterializer(system).materializer

    val port: String = sys.env.getOrElse("CARD_SERVICE_PORT", "8080")
    val host: String = sys.env.getOrElse("CARD_SERVICE_HOST", "romme-card-service")

    val webClientCard = new Client(s"http://$host:$port/")

    var suitName = ""
    var suitNumber = 0
    var value = 0
    var rank = 0
    var placeInList = 0

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

    val rankList: List[String] = List(
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
    "ten",
    "jack",
    "queen",
    "king",
    "ace"
  )

    //
    // get Methods
    //

    def getCard(s: String): CardInterface = {
        if(s.equals("(,)")) {
            return Card(5,0)
        }
        val s1 = s.dropRight(1)
        val s2 = s1.substring(1)
        val s3 = s2.split(",")

        val suit = suitForCard.apply(s3(0))
        if(suit > 3) {
            val card = Card(suit,0)
            return card
        } else {
            val card = Card(suit, rankForCard.apply(s3(1)))
            return card
        }
    }


    def getSuit(cardName: String): String = {
        val endPoint = s"getSuit?card=$cardName"
        val postResponse = webClientCard.getRequest(endPoint)
        getSuitFromHTTP(postResponse)
        return this.suitName
    }

    def getSuitNumber(cardName: String): Integer = {
        val endpoint = s"getSuitNumber?card=$cardName"
        val postResponse = webClientCard.getRequest(endpoint)
        getSuitNumberFromHTTP(postResponse)
        return this.suitNumber
    }

    def getValue(cardName: String): Integer = {
        val endpoint = s"getValue?card=$cardName"
        val postResponse = webClientCard.getRequest(endpoint)
        getValueFromHTTP(postResponse)
        return this.value
    }

    def getRank(cardName: String): Integer = {
        val endpoint = s"getRank?card=$cardName"
        val postResponse = webClientCard.getRequest(endpoint)
        getRankFromHTTP(postResponse)
        return this.rank
    }

    def getCardName(cardName: String): (String, String) = {
        val suidEndpoint = s"getSuit?card=$cardName"
        val suidPostResponse = webClientCard.getRequest(suidEndpoint)
        getSuitFromHTTP(suidPostResponse)
        val rankEndpoint = s"getRank?card=$cardName"
        val rankPostResponse = webClientCard.getRequest(rankEndpoint)
        getRankFromHTTP(rankPostResponse)
        val rankString = rankList(this.rank)
        return (this.suitName, rankString)
    }

    def getPlaceInList(cardName: String): Option[Integer] = {
        val endpoint = s"placeInList?card=$cardName"
        val postResponse = webClientCard.getRequest(endpoint)
        getPlaceInListFromHTTP(postResponse)
        return Some(this.placeInList)
    }

    //
    // getFromHTTP Methods
    //

    def getSuitFromHTTP(result: Future[HttpResponse]): Unit = {
        val res = result.flatMap { response =>
            response.status match {
                case StatusCodes.OK =>
                Unmarshal(response.entity).to[String].map { jsonStr =>
                    val array = jsonStr.split(":")
                    this.suitName = array(1).substring(1, array(1).length - 2)
                }
                case _ =>
                Future.failed(new RuntimeException(s"Failed : ${response.status} ${response.entity}"))
            }
        }
        Await.result(res, 10.seconds)
    }

    def getSuitNumberFromHTTP(result: Future[HttpResponse]): Unit = {
        val res = result.flatMap { response =>
            response.status match {
                case StatusCodes.OK =>
                Unmarshal(response.entity).to[String].map { jsonStr =>
                    val array = jsonStr.split(":")
                    this.suitNumber = array(1).substring(0, array(1).length - 1).toInt
                }
                case _ =>
                Future.failed(new RuntimeException(s"Failed : ${response.status} ${response.entity}"))
            }
        }
        Await.result(res, 10.seconds)
    }

    def getValueFromHTTP(result: Future[HttpResponse]): Unit = {
        val res = result.flatMap { response =>
            response.status match {
                case StatusCodes.OK =>
                Unmarshal(response.entity).to[String].map { jsonStr =>
                    val array = jsonStr.split(":")
                    this.value = array(1).substring(0, array(1).length - 1).toInt
                }
                case _ =>
                Future.failed(new RuntimeException(s"Failed : ${response.status} ${response.entity}"))
            }
        }
        Await.result(res, 10.seconds)
    }

    def getRankFromHTTP(result: Future[HttpResponse]): Unit = {
        val res = result.flatMap { response =>
            response.status match {
                case StatusCodes.OK =>
                Unmarshal(response.entity).to[String].map { jsonStr =>
                    val array = jsonStr.split(":")
                    this.rank = array(1).substring(0, array(1).length - 1).toInt
                }
                case _ =>
                Future.failed(new RuntimeException(s"Failed : ${response.status} ${response.entity}"))
            }
        }
        Await.result(res, 10.seconds)
    }

    def getPlaceInListFromHTTP(result: Future[HttpResponse]): Unit = {
        val res = result.flatMap { response =>
            response.status match {
                case StatusCodes.OK =>
                Unmarshal(response.entity).to[String].map { jsonStr =>
                    val array = jsonStr.split(":")
                    this.placeInList = array(1).substring(0, array(1).length - 1).toInt
                }
                case _ =>
                Future.failed(new RuntimeException(s"Failed : ${response.status} ${response.entity}"))
            }
        }
        Await.result(res, 10.seconds)
    }
}