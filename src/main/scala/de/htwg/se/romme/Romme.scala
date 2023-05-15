package de.htwg.se.romme

import de.htwg.se.romme.controller.controllerComponent.controllerBaseImpl.Controller
import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl._
import deckComponent.DeckInterface
import de.htwg.se.romme.aview.gui.SwingGui
import de.htwg.se.romme.aview.Tui
import scala.io.StdIn.readLine
import restDeck.DeckService
import restCard.CardService
import restFileIO.FileIOService

import com.google.inject.Guice
import de.htwg.se.romme.controller.controllerComponent.ControllerInterface

object Romme {
  val injector = Guice.createInjector(new RommeModule)
  val controller = injector.getInstance(classOf[ControllerInterface])
  val tui = new Tui(controller)
  val gui = new SwingGui(controller)
  /*val deckAPI = DeckService(injector.getInstance(classOf[DeckInterface]))
  val cardAPI = CardService()
  deckAPI.start()
  cardAPI.start()
  val fileIOService = FileIOService()
  fileIOService.start()*/

  def main(args: Array[String]): Unit = {
    var input: String = ""
    input = readLine()
    while (input != "quit") {

      tui.processInputReadLine(input)
      input = readLine()
    }
  }
}
