package de.htwg.se.romme.model.modelComponent.gameComponent
package gameBaseImpl

import scala.collection.mutable.ListBuffer

case class Deck() {

  var deckList: ListBuffer[Card] = ListBuffer()

  def createNewDeck(): ListBuffer[Card] = {
    val suitNumbers: List[Integer] = List(0,1,2,3)
    val rankNumbers: List[Integer] = List(0,1,2,3,4,5,6,7,8,9,10,11,12)
    suitNumbers.foreach(suit => {
      rankNumbers.foreach(rank => {
        val c = Card(suit, rank)
        deckList.addOne(c)
        deckList.addOne(c)
      })
    })
    val jokerCard = Card(4, 0)
    deckList.addOne(jokerCard)
    deckList.addOne(jokerCard)
    deckList.addOne(jokerCard)
    deckList.addOne(jokerCard)
    deckList.addOne(jokerCard)
    deckList.addOne(jokerCard)
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
