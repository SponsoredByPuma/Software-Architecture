package de.htwg.se.romme.model.modelComponent.gameComponent
package gameBaseImpl

case class Deck() {

  val deckList: List[Card] = List()

  def createNewDeck(): List[Card] = {
    val suitNumbers: List[Integer] = List(0, 1, 2, 3)
    val rankNumbers: List[Integer] =
      List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    val tmpList: List[Card] = List.empty
    suitNumbers.foreach(suit => {
      rankNumbers.foreach(rank => {
        val c = Card(suit, rank)
        tmpList :+ List(c)
        //copy(deckList = tmpList)
      })
    })
    val jokerCard = Card(4, 0)
    println("CreateNewDeck: " + tmpList.size)
    deckList
  }

  def drawFromDeck(): Card = {
    val random = new scala.util.Random
    val tmp = random.nextInt(deckList.size - 1)
    val tmpsafe = deckList(tmp)
    val (first, second) = deckList.splitAt(tmp)
    deckList.filter(card => card.getValue == 99999)
    deckList :+ first :+ second.tail
    println(deckList.size) // for testing pourpose can be removed later on
    tmpsafe // return the card
  }

}
