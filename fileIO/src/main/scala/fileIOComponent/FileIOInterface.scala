package fileIOComponent
import play.api.libs.json._
import model.gameComponent.GameInterface
//import fileIOXmlImpl.FileIO
import fileIOJsonImpl.FileIO

trait FileIOInterface:
    def load: GameInterface
    def save(game: GameInterface): Unit
    def gameToJson(game: GameInterface): JsValue
    def jsonToGame(game: String): GameInterface

object FileIOInterface {
    def apply(): FileIOInterface = fileIOJsonImpl.FileIO() // fileIOJsonImpl.FileIO()  fileIOXmlImpl.FileIO()
}