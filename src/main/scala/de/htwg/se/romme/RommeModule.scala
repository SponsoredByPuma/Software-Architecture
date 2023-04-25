package de.htwg.se.romme

import com.google.inject.{AbstractModule, Guice, Inject}
import com.google.inject.TypeLiteral
import net.codingwell.scalaguice.ScalaModule

import model.gameComponent.GameInterface
import model.gameComponent.gameBaseImpl._

import de.htwg.se.romme.controller.controllerComponent.ControllerInterface
import de.htwg.se.romme.controller.controllerComponent.controllerBaseImpl._

import fileIOComponent.FileIOInterface
import fileIOComponent.fileIOXmlImpl.FileIO

import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker

class RommeModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ControllerInterface]).to(classOf[Controller])
    val deck = new Deck(List[CardInterface]())
    val table = new Table(Card(5, 0), List[List[CardInterface]]())
    val player = new Player("Player 1", List[CardInterface](), false)
    val player2 = new Player("Player 2", List[CardInterface](), false)
    bind(classOf[GameInterface]).toInstance(Game(table, List(player, player2), deck))
    bind(classOf[FileIOInterface]).toInstance(FileIO())
  }
}
