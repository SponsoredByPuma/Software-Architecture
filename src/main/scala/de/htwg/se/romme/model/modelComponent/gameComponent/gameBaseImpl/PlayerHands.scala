package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.dropsComponent.dropsBaseImpl._
import de.htwg.se.romme.util.Util


case class PlayerHands(table: Table, cardsOnHand: List[Card], outside: Boolean) {
  def draw13Cards(d: Deck, exisitingCards: List[Card]): (PlayerHands, Deck) = {
    if (exisitingCards.size < 12) {
      val (retCard, retDeck) = d.drawFromDeck()
      val tmpList: List[Card] = List(retCard)
      val finalList = tmpList ++ exisitingCards
      val (newHand, newDeck) = draw13Cards(retDeck, finalList)
      (copy(table, cardsOnHand = newHand.cardsOnHand), newDeck)
    } else {
      val (retCard, retDeck) = d.drawFromDeck()
      val tmpList: List[Card] = List(retCard)
      val finalList = tmpList ++ exisitingCards
      (copy(table, cardsOnHand = finalList), retDeck)
    }
  }

  def dropASingleCard(index: Integer): (PlayerHands, Table) = {
    val newTable = table.replaceGraveYard(cardsOnHand(index))
    val newCardsOnHand = Util.listRemoveAt(cardsOnHand, index)
    (copy(table = newTable, cardsOnHand = newCardsOnHand, outside), newTable)
  }

  def sortMyCards(): PlayerHands = {
    val heart = cardsOnHand.filter(card => card.getSuit.equals("Heart")).map(card => Card(0, card.getRank)).sortBy(_.placeInList.get)
    val club = cardsOnHand.filter(card => card.getSuit.equals("Club")).map(card => Card(2, card.getRank)).sortBy(_.placeInList.get)
    val diamond = cardsOnHand.filter(card => card.getSuit.equals("Diamond")).map(card => Card(1, card.getRank)).sortBy(_.placeInList.get)
    val spades = cardsOnHand.filter(card => card.getSuit.equals("Spades")).map(card => Card(3, card.getRank)).sortBy(_.placeInList.get)
    val joker = cardsOnHand.filter(card => card.getSuit.equals("Joker")).map(card => Card(4,0))
    val tmp = heart ::: club
    val tmp2 = tmp ::: diamond
    val tmp3 = tmp2 ::: spades
    val finalHandCards = tmp3 ::: joker
    copy(table, cardsOnHand = finalHandCards, outside)
  }

  def getSuitNumber(suitString: String): Integer = {
    suitString match {
      case "Heart" => return 0
      case "Diamond" => return 1
      case "Club" => return 2
      case "Spades" => return 3
      case "Joker" => return 4
    }
  }

  def giveJokersRealValues(list: List[Card], jokersInList: List[Integer], jokerPlaces: List[Int], counter: Integer, startingSizeJoker: Integer, isSuit: Integer): List[Card] = {
    if (counter != startingSizeJoker)
      val splitCards = list.splitAt(jokersInList.head).toList
      if (isSuit == 0)
        val test = Joker().setSuit(cardsOnHand(jokerPlaces.head).getSuit)
        val cardsWithCorrectJoker = splitCards(0) ::: List(test)
        if (splitCards(1).size <= 1)
          return giveJokersRealValues(cardsWithCorrectJoker, jokersInList.tail, jokerPlaces.tail, counter + 1, startingSizeJoker, isSuit)
        else
          val mergeList = cardsWithCorrectJoker ::: splitCards(1).tail
          return giveJokersRealValues(mergeList, jokersInList.tail, jokerPlaces.tail, counter + 1, startingSizeJoker, isSuit)
        end if
      else
        val test = Joker().setRank(cardsOnHand(jokerPlaces.head).getRank)
        val cardsWithCorrectJoker = splitCards(0) ::: List(test)
        if (splitCards(1).size <= 1)
          return giveJokersRealValues(cardsWithCorrectJoker, jokersInList.tail, jokerPlaces.tail, counter + 1, startingSizeJoker, isSuit)
        else
          val mergeList = cardsWithCorrectJoker ::: splitCards(1).tail
          return giveJokersRealValues(mergeList, jokersInList.tail, jokerPlaces.tail, counter + 1, startingSizeJoker, isSuit)
        end if
      end if
    end if
    return list
  }

  def dropCardsOnTable(index: List[Integer], decision: Integer, hasJoker: Boolean): (Boolean, PlayerHands, Table) = {
    val drop = Drops
    var sum = 0
    val jokerPlaces = cardsOnHand.zipWithIndex.filter(pair => pair._1.getCardNameAsString.equals("(Joker, )")).map(pair => pair._2)
    val droppingCards: List[Card] = index.map(card => Card(getSuitNumber(cardsOnHand(card).getSuit), cardsOnHand(card).getRank))
    val idxJokers: List[Integer] = index.filter(idx => jokerPlaces.contains(idx)).map(idx => index.indexOf(idx))
    val finalDroppingCards = giveJokersRealValues(droppingCards, idxJokers, jokerPlaces, 0, idxJokers.size, decision)
    if(outside == false)
      val newDroppingCards = drop.execute(finalDroppingCards, decision, hasJoker)
      if (decision == 0)
        val count = newDroppingCards.count(card =>true) - 1
        sum = newDroppingCards.size * newDroppingCards(count).getValue
      else
        newDroppingCards.foreach(card => {
          sum = summe(sum)(card.getValue) // curry
        })
      end if
      if (sum < 40)
        println("The Sum is below 40")
        return (false, copy(table, cardsOnHand, outside), table)
      end if
      val newTable = table.placeCardsOnTable(newDroppingCards)
      return (true, copy(table = newTable, cardsOnHand, outside = true), newTable)
    else
      val newDroppingCards = drop.execute(finalDroppingCards, decision, hasJoker)
      if(newDroppingCards.isEmpty)
        println("Your Cards are Empty => There is a mistake")
        return (false, copy(table, cardsOnHand, outside), table)
      end if
      println("The Cards were placed on the table")
      val newTable = table.placeCardsOnTable(newDroppingCards)
      return (true, copy(table = newTable, cardsOnHand, outside), newTable)
    end if
  }

  def summe(c:Int) = (x: Int)=> x + c
  
  def showYourCards(): String = {
    val s : List[String] = cardsOnHand.map(card => card.getCardNameAsString)
    s.mkString(" ")
  }
}