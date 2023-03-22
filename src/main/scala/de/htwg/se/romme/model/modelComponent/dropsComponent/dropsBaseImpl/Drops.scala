package de.htwg.se.romme.model.modelComponent.dropsComponent.dropsBaseImpl

import scala.collection.mutable.ListBuffer
import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Card
import de.htwg.se.romme.model.modelComponent.dropsComponent.DropsInterface

object Drops {

  abstract class Drops () extends DropsInterface {
    def strategy(numberOfStrategy: Integer): Integer

    def strategySameSuit: Integer

    def strategyOrder: Integer

    def execute(cards: ListBuffer[Card], numberOfStrategy: Integer,hasJoker:Boolean): Integer

  }
  
  def execute(cards: ListBuffer[Card], numberOfStrategy: Integer,hasJoker:Boolean): ListBuffer[Card] =
    strategy(numberOfStrategy, cards,hasJoker)

  def strategy(numberOfStrategy: Integer, cards: ListBuffer[Card],hasJoker: Boolean): ListBuffer[Card] = {
    var list : ListBuffer[Card] = ListBuffer()
    numberOfStrategy match {
      case 0 => list = strategySameSuit(cards,hasJoker)
      case 1 => list = strategyOrder(cards, hasJoker)
    }
    list
  }

  def strategySameSuit(cards: ListBuffer[Card], hasJoker: Boolean): ListBuffer[Card] = {
    var tmpRank = 0
    var counter = 0

    if(cards.size > 4 || cards.size < 3) // it can only be 4 cards at max and min 3 cards
      cards.empty
    end if
  
    while (cards(counter).getSuit.equals("Joker"))
      counter = counter + 1
    tmpRank = cards(counter).placeInList.get
    println(tmpRank)
    val rankNumber = cards.filter(card => !card.getSuit.equals("Joker")).map(card => card.placeInList.get)
    println(rankNumber)

    val storeSuits: ListBuffer[String] = ListBuffer()
    for (card <- cards) // store all Suits in a list
      storeSuits.addOne(card.getSuit)  
    if (storeSuits.distinct.size != storeSuits.size) // are the duplicates in the list ?
      println("Bei storeSuits") 
      return cards.empty
    end if
    val storeRanks: ListBuffer[Integer] = ListBuffer()
    for (card <- cards)
      storeRanks.addOne(card.placeInList.get)
    if (hasJoker == false)
      if(storeRanks.distinct.size > 1) // if there is more than one rank in the list
        return cards.empty
    else
      if(storeRanks.distinct.size > 2) // if there is more than one rank in the list
        return cards.empty
    end if
    cards
  }

  def strategyOrder(cards: ListBuffer[Card],hasJoker:Boolean): ListBuffer[Card] = {
    var counter = 0
    val tmpList: ListBuffer[Integer] = ListBuffer()
    while(cards(counter).getSuit.equals("Joker")) // if the list starts with a joker you need to get the real Suit
        counter = counter + 1
    val suit = cards.filter(x => !x.getSuit.equals("Joker")).map(x => x.getSuit)
    print(suit)
    for (x <- 0 to (cards.size - 1)) // check if all cards are the same suit
      if (cards(x).getSuit.equals(cards(counter).getSuit) || cards(x).getSuit.equals("Joker")) // 
        if (cards(x).getSuit.equals("Joker"))
          tmpList.addOne(x)
        end if 
      else
        return cards.empty // the cards have different Suits so its wrong
      end if

    val list = cards.sortBy(_.placeInList)
    val testedList = lookForGaps(list)
    if(testedList.isEmpty)
      print("Error in Strategy Order Function, List has Gaps in it.")
      return cards.empty
    end if
    testedList
  }

  def lookForGaps(list: ListBuffer[Card]): ListBuffer[Card] = {

    val lowestCard = lookForLowestCard(list)

    if(lowestCard == 0 && checkForAce(list)) // if there is an ace and a two in the order the ace and two need to be flexible
      var splitter = 0
      while(splitter == list(splitter).placeInList.get) // solange die Reihenfolge noch passt erhöhe den counter
        splitter = splitter + 1
      val secondList: ListBuffer[Card] = ListBuffer()
      for (x <- splitter to list.size - 1) // adde alle Element nach der Lücke hinzu
        secondList.addOne(list(splitter))
        splitter = splitter + 1
      val newList: ListBuffer[Card] = ListBuffer()
      newList.addAll(secondList) // füge erst die Bube,Dame, König, Ass hinzu

      var thirdList: ListBuffer[Card] = ListBuffer() 
      thirdList = list.filter(_.placeInList.get < splitter)
      newList.addAll(thirdList) // danach die 2,3,4,5...

      var next = newList(0).placeInList.get

      for(x <- 0 until newList.size - 1)
        next = next + 1
        if(newList(x).placeInList.get == 12)
          next = 0
        end if
        if (next != newList(x + 1).placeInList.get)
          return newList.empty // return false
        end if
      newList // return true
    else
      var next = list(0).placeInList.get
      for (x <- 0 until (list.size - 1)) // until, since the last card has no next 
        next = next + 1 // increase next for 1
        if(list(x + 1).placeInList.get != next)
          return list.empty // return false
        end if
      list // return true
    end if
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
}