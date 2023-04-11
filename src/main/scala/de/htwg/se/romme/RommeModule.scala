package de.htwg.se.romme

import com.google.inject.{AbstractModule, Guice, Inject}
import com.google.inject.TypeLiteral
import net.codingwell.scalaguice.ScalaModule

import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl._

import de.htwg.se.romme.controller.controllerComponent.ControllerInterface
import de.htwg.se.romme.controller.controllerComponent.controllerBaseImpl._

import fileIO.FileIOInterface
import fileIO.fileIOXmlImpl.FileIO

class RommeModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    val deck = new Deck(List[Card]())
    val table = new Table(Card(5, 0), List[List[Card]]())
    val player = new Player("Player 1", List[Card](), false)
    val player2 = new Player("Player 2", List[Card](), false)
    bind(classOf[GameInterface]).toInstance(Game(table, List(player, player2), deck))
    bind(classOf[FileIOInterface]).toInstance(FileIO())
  }
}
