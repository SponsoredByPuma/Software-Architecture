package fileIOComponent.tableComponent.tableBaseImpl

import fileIOComponent.tableComponent.TableInterface
import fileIOComponent.cardComponent.CardInterface
import fileIOComponent.cardComponent.cardBaseImpl.Card

case class Table(graveYard: CardInterface, droppedCardsList: List[List[CardInterface]]) extends TableInterface {
 
  def replaceGraveYard(card: CardInterface): Table = {
    copy(graveYard = card, droppedCardsList)
  }

  def placeCardsOnTable(cards: List[CardInterface]): Table = {
    val newDroppedCards: List[List[CardInterface]] = droppedCardsList ::: List(cards)
    copy(graveYard, droppedCardsList = newDroppedCards)
  }
  def showPlacedCardsOnTable(): String = {
    val stringAsList: List[String] = List()
    val droppedCardString = "GraveYard: " + this.graveYard.getCardName + "\n"
    val stringAsList2 = droppedCardsList.map(droppedCardsSets => droppedCardsSets.map(droppedCard => droppedCard.getCardNameAsString))
    stringAsList2.map(string => println(string))
    stringAsList.mkString(" ")
    droppedCardString
  }

  def grabGraveYard(): (Option[CardInterface], Table) = {
    if(graveYard.getCardName.equals("",""))
      return (None, copy(graveYard, droppedCardsList))
    end if
    val returnCard = graveYard
    (Some(returnCard), copy(Card(5, 13), droppedCardsList))
  }

  def addCardToList(list: List[CardInterface], idx: Integer): Table = {
    copy(graveYard, droppedCardsList = droppedCardsList.updated(idx, list))
  }
}