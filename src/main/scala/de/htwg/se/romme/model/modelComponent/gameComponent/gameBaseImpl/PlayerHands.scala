package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import de.htwg.se.romme.model.modelComponent.dropsComponent.dropsBaseImpl._


case class PlayerHands(table: Table, playerOneHand: List[Card]) {
  var outside = false

  def draw13Cards(d: Deck, exisitingCards: List[Card]): (PlayerHands, Deck) = {
    if (exisitingCards.size < 13) {
      val x = List(d.drawFromDeck())
      val tmpList: List[Card] = List(x(0)(0))
      val finalList = tmpList ++ exisitingCards
      draw13Cards(x(0)(1), finalList)
    }
    (copy(table, playerOneHand = exisitingCards), d)
  }

  def dropASingleCard(index: Integer): Unit = {
    table.replaceGraveYard(playerOneHand(index))
    //playerOneHand.remove(index)
  }

  def sortMyCards(): Unit = {
    val heart: List[Card] = List()
    val club: List[Card] = List()
    val diamond: List[Card] = List()
    val spades: List[Card] = List()
    val joker: List[Card] = List()

    playerOneHand.map(card => {
      card.getSuit match {
        case "Heart"   => heart :+ List(card)
        case "Club"    => club :+ List(card)
        case "Diamond" => diamond :+ List(card)
        case "Spades"  => spades :+ List(card)
        case "Joker"   => joker :+ List(card)
      }
    })
    // sort all the list by its ranks
    heart.sortBy(_.placeInList.get)
    club.sortBy(_.placeInList.get)
    diamond.sortBy(_.placeInList.get)
    spades.sortBy(_.placeInList.get)

    playerOneHand.empty // empty the playerHand

    //playerOneHand.addAll(heart)
    //playerOneHand.addAll(diamond)
    //playerOneHand.addAll(spades)
    //playerOneHand.addAll(club)
    //playerOneHand.addAll(joker)
  }

  def dropCardsOnTable(index: List[Integer], decision: Integer, hasJoker: Boolean): Boolean = {
    val drop = Drops
    val droppingCards: List[Card] = List()
    var sum = 0

    index.map(card => droppingCards :+ List(playerOneHand(card))) // adds the element of your hand at the index

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
    val s: List[String] = List()
    playerOneHand.map(card => s :+ List(card.getCardNameAsString))
    s.mkString(" ")
  }
}