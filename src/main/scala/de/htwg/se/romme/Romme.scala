package de.htwg.se.romme

import controllerComponent.controllerBaseImpl.Controller
import modelComponent.gameComponent.GameInterface
import modelComponent.gameComponent.gameBaseImpl._
import viewComponent.gui.SwingGui
import viewComponent.Tui
import scala.io.StdIn.readLine

import com.google.inject.Guice
import controllerComponent.ControllerInterface

object Romme {
  val injector = Guice.createInjector(new RommeModule)
  val controller = injector.getInstance(classOf[ControllerInterface])
  val tui = new Tui(controller)
  val gui = new SwingGui(controller)
  def main(args: Array[String]): Unit = {
    var input: String = ""
    input = readLine()
    while (input != "quit") {
      tui.processInputReadLine(input)
      input = readLine()
    }
  }
}
