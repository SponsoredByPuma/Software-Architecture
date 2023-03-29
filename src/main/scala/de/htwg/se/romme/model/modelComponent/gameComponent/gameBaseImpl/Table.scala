package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

case class Table() {

  val droppedCardsList: List[List[Card]] = List()

  var graveYard = Card(5, 0)
  
  def replaceGraveYard(card: Card): Unit = graveYard = card

  def placeCardsOnTable(cards: List[Card]): Unit = droppedCardsList :+ List(cards)

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

  def grabGraveYard(): Option[Card] = {
    if(graveYard.getCardName.equals("",""))
      return None
    end if
    val returnCard = graveYard
    graveYard = Card(5, 13)
    Some(returnCard)
  }
}