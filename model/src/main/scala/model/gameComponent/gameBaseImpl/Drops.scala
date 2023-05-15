package model.gameComponent.gameBaseImpl

import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import model.gameComponent.gameBaseImpl.Drops

object Drops {

  val cardAPI = ModelCardRequest()

  abstract class Drops () {
    def strategy(numberOfStrategy: Integer): Integer

    def strategySameSuit: Integer

    def strategyOrder: Integer

    def execute(cards: List[CardInterface], numberOfStrategy: Integer,hasJoker:Boolean): Integer

  }
  
  def execute(cards: List[CardInterface], numberOfStrategy: Integer, hasJoker:Boolean): List[CardInterface] =
    strategy(numberOfStrategy, cards,hasJoker)

  def strategy(numberOfStrategy: Integer, cards: List[CardInterface], hasJoker: Boolean): List[CardInterface] = {
    numberOfStrategy match {
      case 0 => strategySameSuit(cards,hasJoker)
      case 1 => strategyOrder(cards, hasJoker)
    }
  }

  def strategySameSuit(cards: List[CardInterface], hasJoker: Boolean): List[CardInterface] = {
    if(cards.size > 4 || cards.size < 3) // it can only be 4 cards at max and min 3 cards
      cards.empty
    end if
    val storeSuits: List[String] = cards.map(card => cardAPI.getSuit(card.getCardNameAsString))
    if (storeSuits.distinct.size != storeSuits.size) // are the duplicates in the list ?
      println("Error! There are Duplicates in your Suites") 
      return cards.empty
    end if
    val storeRanks: List[Integer] = cards.map(card =>cardAPI.getPlaceInList(card.getCardNameAsString).get)
    if (hasJoker == false)
      if(storeRanks.distinct.size > 1) // if there is more than one rank in the list
        return cards.empty
    else
      if(storeRanks.distinct.size > 2) // if there is more than two ranks in the list
        return cards.empty
    end if
    cards
  }

  def strategyOrder(cards: List[CardInterface], hasJoker :Boolean): List[CardInterface] = {
    if (cards.size == 0)
      return cards.empty
    val suit = cards.filter(x => !cardAPI.getSuit(x.getCardNameAsString).equals("Joker")).map(x => cardAPI.getSuit(x.getCardNameAsString)).last
    val newCards = cards.filter(card => cardAPI.getSuit(card.getCardNameAsString).equals(suit) || cardAPI.getSuit(card.getCardNameAsString).equals("Joker"))
    if (newCards.size != cards.size)
      return cards.empty
    val list = cards.sortBy(card => cardAPI.getPlaceInList(card.getCardNameAsString).get)
    println("list:")
    for (card <- list) {
      println(card.getCardName)
    }
    val filledCards = list.foldLeft((List.empty[CardInterface], Map.empty[Integer, CardInterface])) {
    case ((Nil, m), x) => (List(x), m + (cardAPI.getPlaceInList(x.getCardNameAsString).get -> x))
    case ((acc :+ last, m), x) =>
      val newCards = List.fill(cardAPI.getPlaceInList(x.getCardNameAsString).get - cardAPI.getPlaceInList(last.getCardNameAsString).get - 1)(m(cardAPI.getPlaceInList(last.getCardNameAsString).get)) ++ List(x)
      (acc ++ newCards, m + (cardAPI.getPlaceInList(x.getCardNameAsString).get -> x))
    }._1
    println("filledCards:")
    for (card <- newCards) {
      println(card.getCardName)
    }
    val testedList = lookForGaps(newCards)
    if(testedList.isEmpty)
      println("Error in Strategy Order Function, List has Gaps in it.")
      return cards.empty
    end if
    testedList
  }

  def firstSplitter(list: List[CardInterface], splitter: Integer): Integer = {
    if (splitter == cardAPI.getPlaceInList(list(splitter).getCardNameAsString).get)
      return firstSplitter(list, splitter + 1)
    return splitter
  }

  def secondForLoop(list: List[CardInterface], splitter: Integer, newList: List[CardInterface]): List[CardInterface] = {
    if (splitter <= list.size - 1)
      return secondForLoop(list,splitter + 1, newList ::: List(list(splitter)))
    return newList
  }

  def checkIfNextCardIsCorrect(list: List[CardInterface], next: Integer): Boolean = {
    list match {
      case Nil => false
      case x :: Nil => {
        if (cardAPI.getPlaceInList(x.getCardNameAsString).get == next || cardAPI.getPlaceInList(x.getCardNameAsString).get == 15)
          true
        else
          false
      }
      case x :: tail => {
        if (cardAPI.getPlaceInList(x.getCardNameAsString).get == next || cardAPI.getPlaceInList(x.getCardNameAsString).get == 15) {
          if (cardAPI.getPlaceInList(x.getCardNameAsString).get == 12) {
            checkIfNextCardIsCorrect(tail, 0)
          } else {
            checkIfNextCardIsCorrect(tail, next + 1)
          }
        } else
          false
      }
    }
  }

  def lookForGaps(list: List[CardInterface]): List[CardInterface] = {
    val lowestCard = lookForLowestCard(list)
    if(lowestCard == 0 && checkForAce(list)) // if there is an ace and a two in the order the ace and two need to be flexible
      val tmpSplitterSafer = firstSplitter(list, 0)
      val secondList: List[CardInterface] = List()
      //newList.addAll(secondForLoop(list, tmpSplitterSafer, secondList)) // füge erst die Bube,Dame, König, Ass hinzu
      val newList: List[CardInterface] = secondForLoop(list, tmpSplitterSafer, secondList)
      val thirdList = list.filter(card => cardAPI.getPlaceInList(card.getCardNameAsString).get < tmpSplitterSafer)
      //newList.addAll(thirdList) // danach die 2,3,4,5...
      val finalList = newList ::: thirdList
      if (checkIfNextCardIsCorrect(finalList, cardAPI.getPlaceInList(finalList(0).getCardNameAsString).get))
        return finalList
      else
        return finalList.empty
      end if
    else
      if (checkIfNextCardIsCorrect(list, cardAPI.getPlaceInList(list(0).getCardNameAsString).get))
        return list
      else
        return list.empty
  }

  def lookForLowestCard(list: List[CardInterface]): Integer = {
    val low: List[Integer] = list.map(card => cardAPI.getPlaceInList(card.getCardNameAsString).get)
    low.min
  }

  def checkForAce(list: List[CardInterface]): Boolean = {
    list.foreach(x => {
      if (cardAPI.getPlaceInList(x.getCardNameAsString).get == 12)
        return true
    })
    false
  }
}