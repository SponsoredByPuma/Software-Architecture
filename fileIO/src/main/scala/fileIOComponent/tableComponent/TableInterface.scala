package fileIOComponent.tableComponent

import fileIOComponent.cardComponent.CardInterface

trait TableInterface:

    val graveYard: CardInterface
    val droppedCardsList: List[List[CardInterface]]

    def replaceGraveYard(card: CardInterface): TableInterface
    def placeCardsOnTable(cards: List[CardInterface]): TableInterface
    def showPlacedCardsOnTable(): String
    def grabGraveYard(): (Option[CardInterface], TableInterface)
    def addCardToList(list: List[CardInterface], idx: Integer): TableInterface

    
