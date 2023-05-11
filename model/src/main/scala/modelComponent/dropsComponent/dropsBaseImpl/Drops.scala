package modelComponent.dropsComponent.dropsBaseImpl

import modelComponent.gameComponent.gameBaseImpl.Card
import modelComponent.dropsComponent.DropsInterface

object Drops {

  abstract class Drops () extends DropsInterface {
    def strategy(numberOfStrategy: Integer): Integer

    def strategySameSuit: Integer

    def strategyOrder: Integer

    def execute(cards: List[Card], numberOfStrategy: Integer,hasJoker:Boolean): Integer

  }
  
  def execute(cards: List[Card], numberOfStrategy: Integer, hasJoker:Boolean): List[Card] =
    strategy(numberOfStrategy, cards,hasJoker)

  def strategy(numberOfStrategy: Integer, cards: List[Card], hasJoker: Boolean): List[Card] = {
    numberOfStrategy match {
      case 0 => strategySameSuit(cards,hasJoker)
      case 1 => strategyOrder(cards, hasJoker)
    }
  }

  def strategySameSuit(cards: List[Card], hasJoker: Boolean): List[Card] = {
    if(cards.size > 4 || cards.size < 3) // it can only be 4 cards at max and min 3 cards
      cards.empty
    end if
    val storeSuits: List[String] = cards.map(card => card.getSuit)
    if (storeSuits.distinct.size != storeSuits.size) // are the duplicates in the list ?
      println("Error! There are Duplicates in your Suites") 
      return cards.empty
    end if
    val storeRanks: List[Integer] = cards.map(card =>card.placeInList.get)
    if (hasJoker == false)
      if(storeRanks.distinct.size > 1) // if there is more than one rank in the list
        return cards.empty
    else
      if(storeRanks.distinct.size > 2) // if there is more than two ranks in the list
        return cards.empty
    end if
    cards
  }

  def strategyOrder(cards: List[Card], hasJoker:Boolean): List[Card] = {
    if (cards.size == 0)
      return cards.empty
    val suit = cards.filter(x => !x.getSuit.equals("Joker")).map(x => x.getSuit).last
    val newCards = cards.filter(card => card.getSuit.equals(suit) || card.getSuit.equals("Joker"))
    if (newCards.size != cards.size)
      return cards.empty
    val list = cards.sortBy(_.placeInList)
    val testedList = lookForGaps(list)
    if(testedList.isEmpty)
      println("Error in Strategy Order Function, List has Gaps in it.")
      return cards.empty
    end if
    testedList
  }

  def firstSplitter(list: List[Card], splitter: Integer): Integer = {
    if (splitter == list(splitter).placeInList.get)
      return firstSplitter(list, splitter + 1)
    return splitter
  }

  def secondForLoop(list: List[Card], splitter: Integer, newList: List[Card]): List[Card] = {
    if (splitter <= list.size - 1)
      return secondForLoop(list,splitter + 1, newList ::: List(list(splitter)))
    return newList
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
      val tmpSplitterSafer = firstSplitter(list, 0)
      val secondList: List[Card] = List()
      //newList.addAll(secondForLoop(list, tmpSplitterSafer, secondList)) // füge erst die Bube,Dame, König, Ass hinzu
      val newList: List[Card] = secondForLoop(list, tmpSplitterSafer, secondList)
      val thirdList = list.filter(_.placeInList.get < tmpSplitterSafer)
      //newList.addAll(thirdList) // danach die 2,3,4,5...
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
}