package deckComponent

import scala.util.{Failure, Success, Try}
import romme.card.cardComponent.CardInterface

trait DeckInterface{

    val deckList: List[CardInterface]

    def createNewDeck(): DeckInterface
    def drawFromDeck(): Try[(CardInterface, DeckInterface)]

}