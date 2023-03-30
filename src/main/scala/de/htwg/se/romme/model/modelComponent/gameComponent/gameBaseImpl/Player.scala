package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Card
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Deck

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Table
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.PlayerHands
import com.google.inject.Inject
import de.htwg.se.romme.util.Util

import scala.collection.IterableOnce.iterableOnceExtensionMethods

case class Player(name: String, hands: PlayerHands, table: Table) {
  def getName: String = name

  def pickUpGraveYard: (Player,Table) = {
    val (pickedUpCard, newTable) = table.grabGraveYard()
    if (pickedUpCard.isDefined) {
      return (copy(name, hands = PlayerHands(newTable, hands.cardsOnHand ::: List(pickedUpCard.get), hands.outside), table = newTable), newTable)
    }
    (copy(name, hands, table = newTable), newTable)
  }

  def pickUpACard(deck: Deck): (Player, Deck) = {
    val (card, d) = deck.drawFromDeck()
    (copy(name, hands = PlayerHands(table, hands.cardsOnHand ::: List(card), hands.outside), table), d)
  }

  def dropASpecificCard(index: Integer): (Player, Table) = {
    val (newHands,newTable) = hands.dropASingleCard(index)
    (copy(name, hands = newHands, table = newTable), newTable)
  }

  def addCard(idxCard: Integer, idxlist: Integer): Player = {
        val tmpTableList: List[Card] = List()
        //tmpTableList.addAll(table.droppedCardsList(idxlist))
        if ((tmpTableList(0).placeInList.get != tmpTableList(1).placeInList.get && !(tmpTableList(0).getSuit.equals("Joker")) && !(tmpTableList(1).getSuit.equals("Joker"))) || tmpTableList(0).getSuit.equals("Joker") || tmpTableList(1).getSuit.equals("Joker")) // nach order sortiert
            val card: Card = hands.cardsOnHand(idxCard)
            tmpTableList :+ List(card)
            var list: List[Card] = List()
            list = tmpTableList.sortBy(_.placeInList)
            list = lookForGaps(list)
            if(list.isEmpty)
                print("error list has gaps !")
                return this
            end if
            //hands.cardsOnHand.remove(idxCard)
            //table.droppedCardsList.insert(idxlist,list)
            //table.droppedCardsList.remove(idxlist + 1)
            return copy(name, hands, table)
        else // nach Suit gelegt
            if(tmpTableList.size == 4) // bei 4 karten kann man nichts mehr anlegen
                print("error its already full")
                return this
            end if
            val storeRanks: List[Integer] = List()
            tmpTableList :+ List(hands.cardsOnHand(idxCard))
            var hasJoker = false
            tmpTableList.map(card => storeRanks :+ List(card.placeInList.get))
            tmpTableList.foreach(card => {
              if (card.getCardName.equals("Joker",""))
                hasJoker = true
            })
            if(hasJoker) // es gibt Joker
                if(storeRanks.distinct.size > 2)
                    print("Fehler bei storeRanks mit Joker")
                    return this
                end if
            else
                if(storeRanks.distinct.size > 1)
                    print("Fehler bei storeRanks ohne Joker")
                    return this
                end if
            end if
            
            //hands.cardsOnHand.remove(idxCard)
            //table.droppedCardsList.insert(idxlist,tmpTableList)
            //table.droppedCardsList.remove(idxlist + 1)
            return copy(name, hands, table)
        end if
    }

  def firstSplitter(list: List[Card], splitter: Integer): Integer = {
    if (splitter == list(splitter).placeInList.get)
      firstSplitter(list, splitter + 1)
    splitter
  }

  def secondForLoop(list: List[Card], splitter: Integer, newList: List[Card]): List[Card] = {
    if (splitter <= list.size - 1)
      secondForLoop(list,splitter + 1, newList ::: List(list(splitter)))
    newList
  }

  def checkIfNextCardIsCorrect(list: List[Card], next: Integer): Boolean = {
    list match {
      case Nil => false
      case x :: Nil => {
        if (x.placeInList.get == next)
          true
        else
          false
      }
      case x :: tail => {
        if (x.placeInList.get == next) {
          if (x.placeInList.get == 12) {
            checkIfNextCardIsCorrect(tail, 0)
          } else {
            checkIfNextCardIsCorrect(tail, next + 1)
          }
        } else
          false
      }
    }
  }

  def lookForGaps(list: List[Card]): List[Card] = {
    val lowestCard = lookForLowestCard(list)
    if(lowestCard == 0 && checkForAce(list))
      val tmpSplitterSafer = firstSplitter(list, 0)
      val secondList: List[Card] = List()
      val newList: List[Card] = secondForLoop(list, tmpSplitterSafer, secondList)
      val thirdList = list.filter(_.placeInList.get < tmpSplitterSafer)
      val finalList = newList ::: thirdList
      if (checkIfNextCardIsCorrect(finalList, finalList(0).placeInList.get))
        return finalList
      else
        return finalList.empty
      end if
    else
      if (checkIfNextCardIsCorrect(list, list(0).placeInList.get))
        return list
      else
        return list.empty
  }

  def lookForLowestCard(list: List[Card]): Integer = {
    val low: List[Integer] = list.map(card => card.placeInList.get)
    low.min
  }

  def checkForAce(list: List[Card]): Boolean = {
    list.foreach(x => {
      if (x.placeInList.get == 12)
        return true
    })
    false
  }

  def takeJoker(idxlist: Integer, idxCard: Integer) : (Player, Table) = {
    val tmpTableList: List[Card] = table.droppedCardsList(idxlist)
    val tmpRank: List[Integer] = tmpTableList.filter(card => card.getSuit.equals("Joker") ).map(card => card.getValue)
    val storeJokerPlaceRank: List[Integer] = tmpTableList.filter(card => card.getSuit.equals("Joker") ).map(card => tmpTableList.indexOf(card))
    val storeNormalCardsRank: List[Integer] = tmpTableList.filter(card => card.getSuit.equals("Joker") ).map(card => tmpTableList.indexOf(card))

    val tmpSuit: List[String] = tmpTableList.filter(card => card.placeInList.get == 15).map(card => card.getSuit)
    val storeJokerPlaceSuit: List[Integer] = tmpTableList.filter(card => card.placeInList.get == 15 ).map(card => tmpTableList.indexOf(card))
    val storeNormalCardsSuit: List[Integer] = tmpTableList.filter(card => card.placeInList.get == 15 ).map(card => tmpTableList.indexOf(card))
    
    if (tmpSuit.distinct.size == tmpSuit.size && !tmpSuit.isEmpty) // Strategy 0 Suit
      val splittedList = storeJokerPlaceSuit
        .filter(place => hands.cardsOnHand(idxCard).getSuit.equals(tmpTableList(storeJokerPlaceSuit(place)).getSuit)
         && hands.cardsOnHand(idxCard).getValue == tmpTableList(storeNormalCardsSuit(0)).getValue)
        .map(place => tmpTableList.splitAt(place))
      val insertNewCard = splittedList(0).toList ::: List(hands.cardsOnHand(idxCard))
      val finishedTableList = insertNewCard ::: splittedList(1).toList.tail
      val removedCardFromHand = Util.listRemoveAt(hands.cardsOnHand, idxCard)
      val giveJokerToPlayerHand = removedCardFromHand ::: List(Card(4,0))
      val splittedTableList = table.droppedCardsList.splitAt(idxlist)
      val addNewListToTable = splittedTableList(0).toList ::: finishedTableList
      val finalTable = addNewListToTable ::: splittedList(1).toList.tail
      val finishedTable = Table(table.graveYard, table.droppedCardsList)
      return (copy(name, hands = PlayerHands(finishedTable, giveJokerToPlayerHand, hands.outside), table = finishedTable), finishedTable)
    else  // Strategy 1 Order
      println("nach else ")
      val splittedList= storeJokerPlaceRank
      .filter(place => hands.cardsOnHand(idxCard).placeInList.get == tmpTableList(storeJokerPlaceRank(place)).placeInList.get && hands.cardsOnHand(idxCard).getSuit.equals(tmpTableList(storeNormalCardsRank(0)).getSuit))
      .map(place => tmpTableList.splitAt(place))
      val insertNewCard = splittedList(0).toList ::: List(hands.cardsOnHand(idxCard))
      val finishedTableList = insertNewCard ::: splittedList(1).toList.tail
      val removedCardFromHand = Util.listRemoveAt(hands.cardsOnHand, idxCard)
      val giveJokerToPlayerHand = removedCardFromHand ::: List(Card(4,0))
      val splittedTableList = table.droppedCardsList.splitAt(idxlist)
      val addNewListToTable = splittedTableList(0).toList ::: finishedTableList
      val finalTable = addNewListToTable ::: splittedList(1).toList.tail
      val finishedTable = Table(table.graveYard, table.droppedCardsList)
      return (copy(name, hands = PlayerHands(finishedTable, giveJokerToPlayerHand, hands.outside), table = finishedTable), finishedTable)
    end if 
    (copy(name,hands,table), table)
  }

  def dropMultipleCards(list: List[Integer], decision: Integer, hasJoker: Boolean) : (Player, Table) = {
    val (didItWork, newHands, newTable) = hands.dropCardsOnTable(list, decision, hasJoker)
    if(didItWork)
      val finalHandsList = dropCardsFromHand(newHands.cardsOnHand, startingHandSize, list, 0, list.size)
      finalHandsList.map(card => println(card.getCardNameAsString))
      return (copy(name,hands = PlayerHands(newTable, finalHandsList, outside = true),table = newTable), newTable)
    end if 
    (copy(name, hands = PlayerHands(newTable, newHands.cardsOnHand, newHands.outside), table = newTable), newTable)

  }

  def dropCardsFromHand(playerCards: List[Card], startingSize: Integer, counter: List[Integer], turnCounter: Integer, startingSizeCounter: Integer) : List[Card] = {
    if (turnCounter != (startingSizeCounter))
      println(playerCards.size + " + " + startingSize)
      if ((playerCards.size - 1) == startingSize)
        println(turnCounter +": " + (counter(0) - turnCounter)  +"  "+ playerCards.size )
        return dropCardsFromHand((Util.listRemoveAt(playerCards, counter(0))), startingSize, counter.tail, turnCounter + 1, startingSizeCounter)
      else
        println(turnCounter +": " + (counter(0) - turnCounter) + "  "+ playerCards.size )
        for (card <- playerCards)
          print(card.getCardNameAsString + " ")
        return dropCardsFromHand((Util.listRemoveAt(playerCards, (counter(0) - turnCounter))), startingSize, counter.tail, turnCounter + 1, startingSizeCounter)
      end if
    for (card <- playerCards)
      print(card.getCardNameAsString + " ")  
    return playerCards
  }

  def sortPlayersCards : Player = {
        val newHands = hands.sortMyCards()
        copy(name,hands = newHands, table)
    }

    def victory: Boolean = hands.cardsOnHand.isEmpty

    def showCards: String = hands.showYourCards()

    def showTable: String = table.showPlacedCardsOnTable()

}
