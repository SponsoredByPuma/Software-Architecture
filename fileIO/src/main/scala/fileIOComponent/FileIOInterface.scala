package fileIOComponent
import modelComponent.gameComponent._
import modelComponent.gameComponent.gameBaseImpl.Card
import scala.collection.mutable.ListBuffer
import fileIOComponent.fileIOXmlImpl.FileIO
import fileIOComponent.fileIOJsonImpl.FileIO

trait FileIOInterface:
    def load: GameInterface
    def save(game: GameInterface): Unit

object FileIOInterface {
    def apply(): FileIOInterface = fileIOJsonImpl.FileIO() // fileIOJsonImpl.FileIO()  fileIOXmlImpl.FileIO()
}