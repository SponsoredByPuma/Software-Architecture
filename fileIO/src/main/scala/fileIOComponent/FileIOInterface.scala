package fileIOComponent
import model.gameComponent.GameInterface
import fileIOXmlImpl.FileIO
import fileIOJsonImpl.FileIO

trait FileIOInterface{
    def load: GameInterface
    def save(game: GameInterface): Unit
}
object FileIOInterface {
    def apply(): FileIOInterface = fileIOJsonImpl.FileIO() // fileIOJsonImpl.FileIO()  fileIOXmlImpl.FileIO()
}