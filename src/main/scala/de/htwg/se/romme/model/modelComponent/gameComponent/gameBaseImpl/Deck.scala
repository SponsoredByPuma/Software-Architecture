package de.htwg.se.romme.model.modelComponent.gameComponent
package gameBaseImpl

import scala.util.Random

case class Deck(deckList: List[Card]) {

  def createNewDeck(): Deck = {
    val suitNumbers: List[Integer] = List(0, 1, 2, 3, 4)
    val rankNumbers: List[Integer] =
      List(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    val tmpList = Random.shuffle(suitNumbers.flatMap(suit => {
      val count = suit match {
        case 0 => 12
        case 1 => 12
        case 2 => 12
        case 3 => 12
        case 4 => 3
      }
      (0 to (count - 1)).map(rank => Card(suit, rank))
    }))
    val tmpList1 = Random.shuffle(suitNumbers.flatMap(suit => {
      val count = suit match {
        case 0 => 12
        case 1 => 12
        case 2 => 12
        case 3 => 12
        case 4 => 3
      }
      (0 to (count - 1)).map(rank => Card(suit, rank))
    }))
    val finalList = tmpList ++ tmpList
    copy(deckList = finalList)
  }

  def drawFromDeck(): (Card, Deck) = {
    val random = new scala.util.Random
    val tmp = random.nextInt(deckList.size - 1)
    val returnCard = deckList(tmp)
    val (first, second) = deckList.splitAt(tmp)
    val finalList = first ++ second.tail
    println(deckList.size)
    (returnCard, copy(deckList = finalList))
  }

}
