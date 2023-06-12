package fileIOComponent
import play.api.libs.json._
import model.gameComponent.GameInterface
//import fileIOXmlImpl.FileIO
import fileIOJsonImpl.FileIO
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

trait FileIOInterface:
    def load: Future[GameInterface]
    def save(game: GameInterface): Future[Unit]
    def gameToJson(game: GameInterface): JsValue
    def jsonToGame(game: String): GameInterface

object FileIOInterface {
    def apply(): FileIOInterface = fileIOJsonImpl.FileIO() // fileIOJsonImpl.FileIO()  fileIOXmlImpl.FileIO()
}