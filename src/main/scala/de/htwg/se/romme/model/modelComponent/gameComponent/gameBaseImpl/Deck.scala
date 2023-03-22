package de.htwg.se.romme.model.modelComponent.gameComponent
package gameBaseImpl

import scala.collection.mutable.ListBuffer

case class Deck() {

  var deckList: ListBuffer[Card] = ListBuffer()

  def createNewDeck(): ListBuffer[Card] = {

    var suitCounter = 0
    var rankCounter = 0

    for (suitCounter <- 0 to 3) {
      for (rankCounter <- 0 to 12) {
        val c = Card(suitCounter, rankCounter)
        deckList.addOne(c)
        deckList.addOne(c)
      }
    }

    var jokerCounter = 0
    for (jokerCounter <- 1 to 6) {
      val jokerCard = Card(4, 0)
      deckList.addOne(jokerCard)
    }
    deckList
  }

  def drawFromDeck(): Card = {
    val random = new scala.util.Random // random generator
    // safe a random value between 0 and the size of the current deck - 1
    val tmp = random.nextInt(deckList.size - 1)
    // safe the card which will be drawn from the deck
    val tmpsafe = deckList(tmp)
    deckList.remove(tmp) // remove the card from the game
    println(deckList.size) // for testing pourpose can be removed later on
    tmpsafe // return the card
  }

}
