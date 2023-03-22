package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import scala.collection.mutable.ListBuffer

case class Table() {

  var droppedCardsList: ListBuffer[ListBuffer[Card]] = new ListBuffer()

  var graveYard = Card(5, 0)
  
  def replaceGraveYard(card: Card): Unit = graveYard = card

  def placeCardsOnTable(cards: ListBuffer[Card]): Unit = droppedCardsList.append(cards)

  def showPlacedCardsOnTable(): String = {
    val stringAsList: ListBuffer[String] = new ListBuffer()
    stringAsList.addOne("GraveYard: " + this.graveYard.getCardName + "\n")
    droppedCardsList.map(droppedCardsSets => {
      stringAsList.addOne("\n")
      droppedCardsSets.map(droppedCard => {
        stringAsList.addOne(droppedCard.getCardNameAsString)
      })
    })
    stringAsList.mkString(" ")
  }

  def grabGraveYard(): Option[Card] = {
    if(graveYard.getCardName.equals("",""))
      None
    end if
    val returnCard = graveYard // safe the graveYard Card
    graveYard = Card(5, 13) // delete the graveYard
    Some(returnCard) // return the Card
  }
}