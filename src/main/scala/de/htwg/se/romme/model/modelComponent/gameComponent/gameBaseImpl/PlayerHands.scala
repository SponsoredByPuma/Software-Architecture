package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.dropsComponent.dropsBaseImpl._
import de.htwg.se.romme.util.Util


case class PlayerHands(table: Table, cardsOnHand: List[Card]) {
  var outside = false

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
    (copy(table = newTable, cardsOnHand = newCardsOnHand), newTable)
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
    copy(table, cardsOnHand = finalHandCards)
  }

  def dropCardsOnTable(index: List[Integer], decision: Integer, hasJoker: Boolean): Boolean = {
    val drop = Drops
    val droppingCards: List[Card] = List()
    var sum = 0

    index.map(card => droppingCards :+ List(cardsOnHand(card))) // adds the element of your hand at the index

    if(outside == false)
      val newDroppingCards = drop.execute(droppingCards,decision,hasJoker)
      if (decision == 0)
        val count = droppingCards.count(card => card.getValue.equals(2))
        sum = newDroppingCards.size * newDroppingCards(count).getValue
      else
        newDroppingCards.foreach(card => {
          println("Card: " + card.getCardNameAsString)
          sum = summe(sum)(card.getValue) // curry
        })
      end if
      if (sum < 40)
        println("The Sum is below 40")
        return false
      end if
      table.placeCardsOnTable(newDroppingCards)
      outside = true
      true
    else
      val newDroppingCards = drop.execute(droppingCards, decision, hasJoker)
      if(newDroppingCards.isEmpty)
        println("Your Cards are Empty => There is a mistake")
        return false
      end if
      println("The Cards were placed on the table")
      table.placeCardsOnTable(newDroppingCards)
      true
    end if
    true
  }

  def summe(c:Int) = (x: Int)=> x + c
  
  def showYourCards(): String = {
    val s : List[String] = cardsOnHand.map(card => card.getCardNameAsString)
    s.mkString(" ")
  }
}