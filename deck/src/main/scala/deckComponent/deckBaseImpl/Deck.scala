package deckComponent.deckBaseImpl

import scala.util.Random
import scala.util.{Failure, Success, Try}
import romme.card.cardComponent.CardInterface
import romme.card.cardComponent.cardBaseImpl.Card
import deckComponent.DeckInterface

case class Deck(deckList: List[CardInterface]) extends DeckInterface{

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
      (0 to (count)).map(rank => Card(suit, rank))
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
    val finalList = tmpList ::: tmpList
    copy(deckList = finalList)
  }

  def drawFromDeck(): Try[(CardInterface, Deck)] = {
   if (!this.deckList.isEmpty) {
      val random = new scala.util.Random
      if (this.deckList.size != 1) {
        val tmp = random.nextInt(deckList.size - 1)
        val returnCard = deckList(tmp)
        val (first, second) = deckList.splitAt(tmp)
        val finalList = first ::: second.tail
        println(deckList.size)
        Success(returnCard, copy(deckList = finalList))
      } else {
        val returnCard = deckList(0)
        val (first, second) = deckList.splitAt(0)
        val finalList = first ::: second.tail
        println(deckList.size)
        return Success(returnCard, copy(deckList = finalList))
      }
    } else {
      Failure(exception = new Throwable("Deck is Empty"))
    }
  }
}
