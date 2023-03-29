package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

case class Table(graveYard: Card, droppedCardsList: List[List[Card]]) {

  //val droppedCardsList: List[List[Card]] = List()

  //val graveYard = Card(5, 0)
  
  def replaceGraveYard(card: Card): Table = {
    copy(card, droppedCardsList)
  }

  def placeCardsOnTable(cards: List[Card]): Table = {
    val newDroppedCards: List[List[Card]] = droppedCardsList ::: List(cards)
    copy(graveYard, droppedCardsList = newDroppedCards)
  }
  def showPlacedCardsOnTable(): String = {
    val stringAsList: List[String] = List()
    stringAsList :+ List("GraveYard: " + this.graveYard.getCardName + "\n")
    droppedCardsList.map(droppedCardsSets => {
      stringAsList :+ List("\n")
      droppedCardsSets.map(droppedCard => {
        stringAsList :+ List(droppedCard.getCardNameAsString)
      })
    })
    stringAsList.mkString(" ")
  }

  def grabGraveYard(): (Option[Card], Table) = {
    if(graveYard.getCardName.equals("",""))
      return (None, copy(graveYard, droppedCardsList))
    end if
    val returnCard = graveYard
    (Some(returnCard), copy(Card(5, 13), droppedCardsList))
  }
}