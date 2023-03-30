package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

case class Table(graveYard: Card, droppedCardsList: List[List[Card]]) {
 
  def replaceGraveYard(card: Card): Table = {
    copy(graveYard = card, droppedCardsList)
  }

  def placeCardsOnTable(cards: List[Card]): Table = {
    val newDroppedCards: List[List[Card]] = droppedCardsList ::: List(cards)
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

  def grabGraveYard(): (Option[Card], Table) = {
    if(graveYard.getCardName.equals("",""))
      return (None, copy(graveYard, droppedCardsList))
    end if
    val returnCard = graveYard
    (Some(returnCard), copy(Card(5, 13), droppedCardsList))
  }
}