package de.htwg.se.romme.model.modelComponent.gameComponent

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Card
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Table
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Deck
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Player

trait GameInterface:

    val table: Table
    val players: List[Player]
    val deck: Deck

    def set(table: Table, players: List[Player], deck: Deck): GameInterface
    def gameStart: GameInterface
    def drawCards(playerIdx: Integer): GameInterface
    def pickUpGraveYard(playerIdx: Integer) : GameInterface
    def pickUpACard(playerIdx: Integer) : GameInterface
    def replaceCardOrder(stelle: List[Integer], values: List[String], playerIdx: Integer) : GameInterface
    def replaceCardSuit(stelle: List[Integer], values: List[String], playerIdx: Integer) : GameInterface
    def dropASpecificCard(index: Integer, playerIdx: Integer) : GameInterface
    def addCard(idxCard: Integer, idxlist: Integer, playerIdx: Integer) : GameInterface
    def takeJoker(idxlist: Integer, idxCard: Integer, playerIdx: Integer) : GameInterface
    def dropMultipleCards(list: List[Integer], dec: Integer, playerIdx: Integer, hasJoker: Boolean) : GameInterface
    def sortPlayersCards(playerIdx: Integer) : GameInterface
    def victory(playerIdx: Integer) : Boolean
    def showCards(playerIdx: Integer) : String
    def showTable: String
    
