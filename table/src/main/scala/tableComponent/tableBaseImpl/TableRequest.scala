package tableComponent.tableBaseImpl

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

import tableComponent.tableBaseImpl.Client

class TableRequest {

    implicit val system: ActorSystem = ActorSystem()
    implicit val mat: Materializer = SystemMaterializer(system).materializer

    val webClientCard = new Client("http://localhost:8080/")

    var cardName = ""

    def waitRefreshcardNameAsString(result: Future[HttpResponse]) = {
        val res = result.flatMap { response =>
            response.status match {
                case StatusCodes.OK =>
                Unmarshal(response.entity).to[String].map { jsonStr =>
                    this.cardName = jsonStr
                }
                case _ =>
                Future.failed(new RuntimeException(s"Failed : ${response.status} ${response.entity}"))
            }
        }
        Await.result(res, 10.seconds)
    }

    def cardNameAsString(card: String): String = {
        val endPoint = s"getCardNameAsString?card=$card"
        println(endPoint)
        val postResponse = webClientCard.getRequest(endPoint)
        waitRefreshcardNameAsString(postResponse)
        this.cardName
    }
}