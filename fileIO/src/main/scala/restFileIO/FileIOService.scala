package restFileIO


import fileIOComponent.FileIOInterface
import fileIOComponent.fileIOJsonImpl.FileIO
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, *}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import scala.concurrent.ExecutionContext.Implicits.global
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future, Await}
import scala.util.{Failure, Success}
import concurrent.duration.DurationInt
import akka.protobufv3.internal.compiler.PluginProtos.CodeGeneratorResponse.File
import play.api.libs.json.*
import scala.concurrent._

class FileIOService() {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext
    val WAIT_TIME = 5.seconds
    val fileIO = FileIO()
    val RestUIPort = 8082
    val routes: String =
        """
            """.stripMargin
    val route: Route =
        concat(
            pathSingleSlash {
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, routes))
            },
            get {
                path("load") {
                    val game = fileIO.gameToJson(Await.result(fileIO.load, WAIT_TIME))
                    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, game.toString()))
                }
            },
            put {
                path("save") {
                    entity(as[String]) { data =>
                        complete {
                            Await.result(fileIO.save(fileIO.jsonToGame(data)), WAIT_TIME)
                            Future.successful(HttpEntity(ContentTypes.`text/html(UTF-8)`, "game successfully saved"))
                        }
                    }
                }
            }
        )

    def start(): Unit = {
        val binding = Http().newServerAt("localhost", RestUIPort).bind(route)
        binding.onComplete {
            case Success(binding) => {
                println(s"Romme FileIO at http://localhost:$RestUIPort/")
            }
            case Failure(exception) => {
                println(s"Romme FileIO failed to start: ${exception.getMessage}")
            }
        }
    }
}