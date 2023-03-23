package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Card
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Deck

import scala.collection.mutable.ListBuffer
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Table
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.PlayerHands
import com.google.inject.Inject

import scala.collection.IterableOnce.iterableOnceExtensionMethods

case class Player(name: String, hands: PlayerHands, table: Table) {
  def getName: String = name

  def pickUpGraveYard: Player = {
    val d = table.grabGraveYard()
    hands.playerOneHand.addOne(d.get)
    copy(name, hands, table)
  }

  def pickUpACard(deck: Deck): Player = {
    val d = deck.drawFromDeck()
    hands.playerOneHand.addOne(d)
    copy(name, hands, table)
  }

  def dropASpecificCard(index: Integer): Player = {
    hands.dropASingleCard(index)
    copy(name, hands, table)
  }

  def addCard(idxCard: Integer, idxlist: Integer): Player = {
        val tmpTableList: ListBuffer[Card] = ListBuffer()
        tmpTableList.addAll(table.droppedCardsList(idxlist))
        if ((tmpTableList(0).placeInList.get != tmpTableList(1).placeInList.get && !(tmpTableList(0).getSuit.equals("Joker")) && !(tmpTableList(1).getSuit.equals("Joker"))) || tmpTableList(0).getSuit.equals("Joker") || tmpTableList(1).getSuit.equals("Joker")) // nach order sortiert
            val card: Card = hands.playerOneHand(idxCard)
            tmpTableList.addOne(card)
            var list: ListBuffer[Card] = ListBuffer()
            list = tmpTableList.sortBy(_.placeInList)
            list = lookForGaps(list)
            if(list.isEmpty)
                print("error list has gaps !")
                return this
            end if
            hands.playerOneHand.remove(idxCard)
            table.droppedCardsList.insert(idxlist,list)
            table.droppedCardsList.remove(idxlist + 1)
            return copy(name, hands, table)
        else // nach Suit gelegt
            if(tmpTableList.size == 4) // bei 4 karten kann man nichts mehr anlegen
                print("error its already full")
                return this
            end if
            val storeRanks: ListBuffer[Integer] = ListBuffer()
            tmpTableList.addOne(hands.playerOneHand(idxCard))
            var hasJoker = false
            tmpTableList.map(card => storeRanks.addOne(card.placeInList.get))
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
            
            hands.playerOneHand.remove(idxCard)
            table.droppedCardsList.insert(idxlist,tmpTableList)
            table.droppedCardsList.remove(idxlist + 1)
            return copy(name, hands, table)
        end if
    }

  def firstSplitter(list: ListBuffer[Card], splitter: Integer): Integer = {
    if (splitter == list(splitter).placeInList.get)
      firstSplitter(list, splitter + 1)
    splitter
  }

  def secondForLoop(list: ListBuffer[Card], splitter: Integer, newList: ListBuffer[Card]): ListBuffer[Card] = {
    if (splitter <= list.size - 1)
      newList.addOne(list(splitter))
      secondForLoop(list,splitter + 1, newList)
    newList
  }

  def checkIfNextCardIsCorrect(list: List[Card], next: Integer): List[Card] = {
    list match {
      case Nil => list.empty
      case x :: Nil => {
        if (x.placeInList.get == next)
          list
        else
          list.empty
      }
      case x :: tail => {
        if (x.placeInList.get == next) {
          if (x.placeInList.get == 12)
            checkIfNextCardIsCorrect(list, 0)
          else
            checkIfNextCardIsCorrect(list, next + 1)
        } else
          list.empty
      }

    }
  }

  def lookForGaps(list: ListBuffer[Card]): ListBuffer[Card] = {
    val lowestCard = lookForLowestCard(list)
    if(lowestCard == 0 && checkForAce(list)) // if there is an ace and a two in the order the ace and two need to be flexible
      val tmpSplitterSafer = firstSplitter(list, 0) - 1
      val secondList: ListBuffer[Card] = ListBuffer()
      val newList: ListBuffer[Card] = ListBuffer()
      newList.addAll(secondForLoop(list, firstSplitter(list, 0), secondList)) // füge erst die Bube,Dame, König, Ass hinzu
      val thirdList = list.filter(_.placeInList.get <= tmpSplitterSafer)
      newList.addAll(thirdList) // danach die 2,3,4,5...
      checkIfNextCardIsCorrect(newList.toList, newList(0).placeInList.get).to(ListBuffer)
    else
      checkIfNextCardIsCorrect(list.toList, list(0).placeInList.get).to(ListBuffer)
  }

  def lookForLowestCard(list: ListBuffer[Card]): Integer = {
    val low: ListBuffer[Integer] = ListBuffer()
    list.foreach(card => low.addOne(card.placeInList.get))
    low.min
  }

  def checkForAce(list: ListBuffer[Card]): Boolean = {
    list.foreach(x => {
      if (x.placeInList.get == 12)
        return true
    })
    false
  }

  def takeJoker(idxlist: Integer, idxCard: Integer) : Player = {
    val tmpTableList: ListBuffer[Card] = ListBuffer()
    tmpTableList.addAll(table.droppedCardsList(idxlist))
    val tmpSuit: ListBuffer[String] = ListBuffer()
    val tmpRank: ListBuffer[Integer] = ListBuffer()
    val storeJokerPlace: ListBuffer[Integer] = ListBuffer()
    val storeNormalCards: ListBuffer[Integer] = ListBuffer()
    tmpTableList.filter(card => card.getSuit.equals("Joker") ).map(card => {
      tmpRank.addOne(card.getValue)
      storeNormalCards.addOne(tmpTableList.indexOf(card))
      storeJokerPlace.addOne(tmpTableList.indexOf(card))
    })
    tmpTableList.filter(card => card.placeInList.get == 15).map(card => {
      tmpSuit.addOne(card.getSuit)
      storeNormalCards.addOne(tmpTableList.indexOf(card))
      storeJokerPlace.addOne(tmpTableList.indexOf(card))
    })
    
    if (tmpSuit.distinct.size == tmpSuit.size && !tmpSuit.isEmpty) // Strategy 0 Suit
      storeJokerPlace.foreach(place => {
        if (hands.playerOneHand(idxCard).getSuit.equals(tmpTableList(storeJokerPlace(place)).getSuit) && hands.playerOneHand(idxCard).getValue == tmpTableList(storeNormalCards(0)).getValue) // schaue ob deine Card auch der gewünschte Suit hat
          tmpTableList.insert(storeJokerPlace(place), hands.playerOneHand(idxCard)) // füge deine Karte ein
          tmpTableList.remove(storeJokerPlace(place) + 1) // remove den Joker
          hands.playerOneHand.remove(idxCard) // remove deine Karte von der Hand
          hands.playerOneHand.addOne(Card(4,0)) // gebe dir einen Joker auf die hand
          table.droppedCardsList.insert(idxlist,tmpTableList)// füge die neue Liste auf dem Tisch ein
          table.droppedCardsList.remove(idxlist + 1) // lösche die Alte Liste
          println("im Strategy Suit 0 ")
          return copy(name,hands,table)
      })
    else  // Strategy 1 Order
      println("nach else ")
      storeJokerPlace.foreach(place => {
        if(hands.playerOneHand(idxCard).placeInList.get == tmpTableList(storeJokerPlace(place)).placeInList.get && hands.playerOneHand(idxCard).getSuit.equals(tmpTableList(storeNormalCards(0)).getSuit)) // schaue ob deine Card auch der gewünschte Value hat
          tmpTableList.insert(storeJokerPlace(place), hands.playerOneHand(idxCard)) // füge deine Karte ein
          tmpTableList.remove(storeJokerPlace(place) + 1) // remove den Joker
          hands.playerOneHand.remove(idxCard) // remove deine Karte von der Hand
          hands.playerOneHand.addOne(Card(4,0)) // gebe dir einen Joker auf die hand
          table.droppedCardsList.insert(idxlist,tmpTableList) // füge die neue Liste auf dem Tisch ein
          table.droppedCardsList.remove(idxlist + 1) // lösche die Alte Liste
        return copy(name,hands,table)
      })
    copy(name,hands,table)
  }

  def dropMultipleCards(list: ListBuffer[Integer], dec: Integer, hasJoker: Boolean) : Player = {
    if(hands.dropCardsOnTable(list, dec, hasJoker))
      list.sorted // sortiere die Liste
      list.map()
      for (counter <- 0 to list.size - 1) // gehe die Liste durch
      // falls die Zahl 0 < 12 ist müssen die restlichen Cards um 1 verringert werden, da bei remove eins weggenommen wird
        if (list(counter) < hands.playerOneHand.size - 1)
            for(counter <- counter + 1 to list.size - 1) // go through the next inputs
                list(counter) = list(counter) - 1 // decrement the next input for one
        end if
        hands.playerOneHand.remove(list(counter)) // remove the Card
    end if
    copy(name,hands,table)
  }

  def sortPlayersCards : Player = {
        hands.sortMyCards()
        copy(name,hands,table)
    }

    def victory: Boolean = hands.playerOneHand.isEmpty

    def showCards: String = hands.showYourCards()

    def showTable: String = table.showPlacedCardsOnTable()

}
