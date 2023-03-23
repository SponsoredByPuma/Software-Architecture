package de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl

import scala.collection.mutable.ListBuffer
import de.htwg.se.romme.model.modelComponent.dropsComponent.dropsBaseImpl._


case class PlayerHands(table: Table) {
  var playerOneHand: ListBuffer[Card] = new ListBuffer()
  var outside = false

  def draw13Cards(d: Deck): ListBuffer[Card] = {
    if (playerOneHand.size < 13) {
      playerOneHand.addOne(d.drawFromDeck())
      draw13Cards(d)
    }
    playerOneHand
  }

  def dropASingleCard(index: Integer): Unit = {
    table.replaceGraveYard(playerOneHand(index))
    playerOneHand.remove(index)
  }

  def sortMyCards(): Unit = {
    var heart: ListBuffer[Card] = new ListBuffer()
    var club: ListBuffer[Card] = new ListBuffer()
    var diamond: ListBuffer[Card] = new ListBuffer()
    var spades: ListBuffer[Card] = new ListBuffer()
    val joker: ListBuffer[Card] = new ListBuffer()

    playerOneHand.map(card => {
      card.getSuit match {
        case "Heart"   => heart.addOne(card)
        case "Club"    => club.addOne(card)
        case "Diamond" => diamond.addOne(card)
        case "Spades"  => spades.addOne(card)
        case "Joker"   => joker.addOne(card)
      }
    })
    // sort all the list by its ranks
    heart = heart.sortBy(_.placeInList.get)
    club = club.sortBy(_.placeInList.get)
    diamond = diamond.sortBy(_.placeInList.get)
    spades = spades.sortBy(_.placeInList.get)

    playerOneHand = playerOneHand.empty // empty the playerHand

    playerOneHand.addAll(heart)
    playerOneHand.addAll(diamond)
    playerOneHand.addAll(spades)
    playerOneHand.addAll(club)
    playerOneHand.addAll(joker)
  }

  def dropCardsOnTable(index: ListBuffer[Integer], dec: Integer, hasJoker: Boolean): Boolean = {
    val drop = Drops
    val droppingCards: ListBuffer[Card] = new ListBuffer()
    var sum = 0

    index.map(card => droppingCards.addOne(playerOneHand(card))) // adds the element of your hand at the index

    if(outside == false)
      val newDroppingCards = drop.execute(droppingCards,dec,hasJoker)
      if (dec == 0)
        val count = droppingCards.count(card => card.getValue.equals(2))
        sum = newDroppingCards.size * newDroppingCards(count).getValue
      else
        newDroppingCards.map(card => sum = sum + card.getValue)
      end if
      if (sum < 40)
        println("The Sum is below 40")
        return false
      end if
      table.placeCardsOnTable(newDroppingCards)
      outside = true
      true
    else
      val newDroppingCards = drop.execute(droppingCards, dec,hasJoker)
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
  
  def showYourCards(): String = {
    val s: ListBuffer[String] = new ListBuffer()
    playerOneHand.map(card => s.addOne(card.getCardNameAsString))
    s.mkString(" ")
  }
}