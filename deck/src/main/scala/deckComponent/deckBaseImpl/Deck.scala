package deckComponent.deckBaseImpl

import scala.util.Random
import scala.util.{Failure, Success, Try}
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import deckComponent.DeckInterface

import scala.swing.Publisher
import com.google.inject.Inject
import com.google.inject.Guice

case class CardPrototype(suit: Int, rank: Int) {
  def cloneCard(): CardInterface = {
    Card(suit, rank)
  }
}

case class Deck @Inject()(deckList: List[CardInterface]) extends DeckInterface{
  

  def createNewDeck(): Deck = {
    val prototypes = for {
    suit <- 0 to 4
    rank <- 0 to 12
  } yield CardPrototype(suit, rank)

  val finalList = prototypes.flatMap { prototype =>
    val count = prototype.suit match {
      case 4 => 3
      case _ => 12
    }
    (0 to count).map(_ => prototype.cloneCard())
  }

  Deck(finalList.toList)
}
  def drawFromDeck(): Try[(CardInterface, Deck)] = {
   if (!this.deckList.isEmpty)
     val random = new scala.util.Random
      if (this.deckList.size != 1)
        val tmp = random.nextInt(deckList.size - 1)
        val returnCard = deckList(tmp)
        val (first, second) = deckList.splitAt(tmp)
        val finalList = first ::: second.tail
        Success(returnCard, copy(deckList = finalList))
      else {
        val returnCard = deckList(0)
        val (first, second) = deckList.splitAt(0)
        val finalList = first ::: second.tail
        return Success(returnCard, copy(deckList = finalList))
      }
    else
      Failure(exception = new Throwable("Deck is Empty"))
  }

}
