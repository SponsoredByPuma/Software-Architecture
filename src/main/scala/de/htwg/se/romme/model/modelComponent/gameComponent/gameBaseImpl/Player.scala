package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Card
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Deck

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Table
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.PlayerHands
import com.google.inject.Inject

import scala.collection.IterableOnce.iterableOnceExtensionMethods

case class Player(name: String, hands: PlayerHands, table: Table) {
  def getName: String = name

  def pickUpGraveYard: Player = {
    val d = table.grabGraveYard()
    if (d.isDefined) {
      hands.cardsOnHand :+ List(d.get)
    }
    copy(name, hands, table)
  }

  def pickUpACard(deck: Deck): (Player, Deck) = {
    val (card, d) = deck.drawFromDeck()
    (copy(name, hands = PlayerHands(table, hands.cardsOnHand ::: List(card)), table), d)
  }

  def dropASpecificCard(index: Integer): Player = {
    hands.dropASingleCard(index)
    copy(name, hands, table)
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
      newList :+ List(list(splitter))
      secondForLoop(list,splitter + 1, newList)
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
    if(lowestCard == 0 && checkForAce(list)) // if there is an ace and a two in the order the ace and two need to be flexible
      val tmpSplitterSafer = firstSplitter(list, 0) - 1
      val secondList: List[Card] = List()
      val newList: List[Card] = List()
      //newList.addAll(secondForLoop(list, tmpSplitterSafer, secondList)) // füge erst die Bube,Dame, König, Ass hinzu
      val thirdList = list.filter(_.placeInList.get <= tmpSplitterSafer)
      //newList.addAll(thirdList) // danach die 2,3,4,5...
      if (checkIfNextCardIsCorrect(newList.toList, newList(0).placeInList.get))
        return newList
      else
        return newList.empty
      end if
    else
      if (checkIfNextCardIsCorrect(list.toList, list(0).placeInList.get))
        return list
      else
        return list.empty
  }

  def lookForLowestCard(list: List[Card]): Integer = {
    val low: List[Integer] = List()
    list.foreach(card => low :+ List(card.placeInList.get))
    low.min
  }

  def checkForAce(list: List[Card]): Boolean = {
    list.foreach(x => {
      if (x.placeInList.get == 12)
        return true
    })
    false
  }

  def takeJoker(idxlist: Integer, idxCard: Integer) : Player = {
    val tmpTableList: List[Card] = List()
    //tmpTableList.addAll(table.droppedCardsList(idxlist))
    val tmpSuit: List[String] = List()
    val tmpRank: List[Integer] = List()
    val storeJokerPlace: List[Integer] = List()
    val storeNormalCards: List[Integer] = List()
    tmpTableList.filter(card => card.getSuit.equals("Joker") ).map(card => {
      tmpRank :+ List(card.getValue)
      storeNormalCards :+ List(tmpTableList.indexOf(card))
      storeJokerPlace :+ List(tmpTableList.indexOf(card))
    })
    tmpTableList.filter(card => card.placeInList.get == 15).map(card => {
      tmpSuit :+ List(card.getSuit)
      storeNormalCards :+ List(tmpTableList.indexOf(card))
      storeJokerPlace :+ List(tmpTableList.indexOf(card))
    })
    
    if (tmpSuit.distinct.size == tmpSuit.size && !tmpSuit.isEmpty) // Strategy 0 Suit
      storeJokerPlace.foreach(place => {
        if (hands.cardsOnHand(idxCard).getSuit.equals(tmpTableList(storeJokerPlace(place)).getSuit) && hands.cardsOnHand(idxCard).getValue == tmpTableList(storeNormalCards(0)).getValue) // schaue ob deine Card auch der gewünschte Suit hat
          //tmpTableList.insert(storeJokerPlace(place), hands.cardsOnHand(idxCard)) // füge deine Karte ein
          //tmpTableList.remove(storeJokerPlace(place) + 1) // remove den Joker
          //hands.cardsOnHand.remove(idxCard) // remove deine Karte von der Hand
          hands.cardsOnHand :+ List(Card(4,0)) // gebe dir einen Joker auf die hand
          //table.droppedCardsList.insert(idxlist,tmpTableList)// füge die neue Liste auf dem Tisch ein
          //table.droppedCardsList.remove(idxlist + 1) // lösche die Alte Liste
          println("im Strategy Suit 0 ")
          return copy(name,hands,table)
      })
    else  // Strategy 1 Order
      println("nach else ")
      storeJokerPlace.foreach(place => {
        if(hands.cardsOnHand(idxCard).placeInList.get == tmpTableList(storeJokerPlace(place)).placeInList.get && hands.cardsOnHand(idxCard).getSuit.equals(tmpTableList(storeNormalCards(0)).getSuit)) // schaue ob deine Card auch der gewünschte Value hat
         // tmpTableList.insert(storeJokerPlace(place), hands.cardsOnHand(idxCard)) // füge deine Karte ein
          //tmpTableList.remove(storeJokerPlace(place) + 1) // remove den Joker
          //hands.cardsOnHand.remove(idxCard) // remove deine Karte von der Hand
          hands.cardsOnHand :+ List(Card(4,0)) // gebe dir einen Joker auf die hand
          //table.droppedCardsList.insert(idxlist,tmpTableList) // füge die neue Liste auf dem Tisch ein
          //table.droppedCardsList.remove(idxlist + 1) // lösche die Alte Liste
        return copy(name,hands,table)
      })
    copy(name,hands,table)
  }

  def dropMultipleCards(list: List[Integer], decision: Integer, hasJoker: Boolean) : Player = {
    if(hands.dropCardsOnTable(list, decision, hasJoker))
      list.sorted
      val startingHandSize = hands.cardsOnHand.size - 1
      list.foreach(counter => {
        if (startingHandSize == hands.cardsOnHand.size - 1) {
          val dof = 1
        }
        //hands.cardsOnHand.remove(counter)
        else
          val diff = startingHandSize - (hands.cardsOnHand.size - 1)
          //hands.cardsOnHand.remove((counter - diff))
        end if
      })
    copy(name,hands,table)
  }

  def sortPlayersCards : Player = {
        val newHands = hands.sortMyCards()
        copy(name,hands = newHands, table)
    }

    def victory: Boolean = hands.cardsOnHand.isEmpty

    def showCards: String = hands.showYourCards()

    def showTable: String = table.showPlacedCardsOnTable()

}
