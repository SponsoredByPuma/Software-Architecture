package model.gameComponent.gameBaseImpl

import model.gameComponent.gameBaseImpl.Drops
import com.google.inject.Inject
import dienste.Util
import deckComponent.DeckInterface
import deckComponent.deckBaseImpl.Deck
import tableComponent.TableInterface
import tableComponent.tableBaseImpl.Table
import cardComponent.CardInterface
import cardComponent.cardBaseImpl.Card
import cardComponent.cardBaseImpl.Joker
import model.gameComponent.gameBaseImpl.ModelDeckRequest

import scala.util.{Failure, Success, Try}

import scala.collection.IterableOnce.iterableOnceExtensionMethods

case class Player(name: String, hand: List[CardInterface], outside: Boolean) {
  val modelDeckRequest = ModelDeckRequest()
  def getName: String = name

  def pickUpGraveYard(table: TableInterface): (Player,TableInterface) = {
    val (pickedUpCard, newTable) = table.grabGraveYard()
    pickedUpCard match {
      case Some(value) => {
        val newHand = hand ::: List(value)
        return (copy(name, hand = newHand, outside), newTable)
      }
      case None => return (copy(name, hand, outside), newTable)
    }
  }

  def pickUpACard(deck: DeckInterface): (Player, DeckInterface) = {
    val c = modelDeckRequest.drawFromDeck()
    c match {
      case Success((card, newDeck)) => {
        val newHand = hand ::: List(card)
        (copy(name, hand = newHand, outside), newDeck)
      }
      case Failure(e) => {
        println(e.getMessage())
        return (copy(name, hand, outside), deck)
      }
    }
  }

  def dropASpecificCard(index: Integer, table: TableInterface): (Player, TableInterface) = {
    val newTable = table.replaceGraveYard(hand(index))
    val newHand = Util.listRemoveAt(hand, index)
    (copy(name, hand = newHand, outside), newTable)
  }

  def addCard(idxCard: Integer, idxlist: Integer, table: TableInterface): (Player, TableInterface) = {
        val tmpTableList: List[CardInterface] = table.droppedCardsList(idxlist)
        if ((tmpTableList(0).placeInList.get != tmpTableList(1).placeInList.get && !(tmpTableList(0).getSuit.equals("Joker")) && !(tmpTableList(1).getSuit.equals("Joker"))) || tmpTableList(0).getSuit.equals("Joker") || tmpTableList(1).getSuit.equals("Joker")) // nach order sortiert
            val card: CardInterface = hand(idxCard)
            val tmp_table_one = tmpTableList :::List(card)
            val tmp_table_two = tmp_table_one.sortBy(_.placeInList.get)
            val newTableList = lookForGaps(tmp_table_two)
            newTableList match {
              case None => {
                println("error list has gaps !")
                return (copy(), table)
              }
              case Some(value) => {
                val storeSuits = value.map(card => card.getSuit)
                val jokerAmount = value.filter(card => card.getCardNameAsString.equals("(Joker, )")).count(card => card.getCardNameAsString.equals("(Joker, )"))
                if (jokerAmount ==  0) // keine Jokers
                  if (storeSuits.distinct.size > 1)
                    println("The Suit of your Card is not correct !")
                    return (copy(), table)
                  end if
                else  // mit Jokers
                  if (storeSuits.distinct.size > 2)
                    println("You Card has a incorrect Suit !")
                    return (copy(), table)
                  end if
                end if 
                val newHand = Util.listRemoveAt(hand, idxCard) 
                val newTable = table.addCardToList(value, idxlist)
                return (copy(name, hand = newHand, outside), newTable)
              }
            }
        else // nach Suit gelegt
            if(tmpTableList.size == 4) // bei 4 karten kann man nichts mehr anlegen
                print("error its already full")
                return (copy(), table)
            end if
            val card: CardInterface = hand(idxCard)
            val tmp_table_list: List[CardInterface] = tmpTableList ::: List(card)     
            val jokerAmount = tmp_table_list.filter(card => card.getCardNameAsString.equals("(Joker, )")).count(card => card.getCardNameAsString.equals("(Joker, )")) 
            val newTableList = if (jokerAmount != 0) Drops.strategySameSuit(tmp_table_list, true) else Drops.strategySameSuit(tmp_table_list, false)
            val newTable = if (!newTableList.isEmpty) table.addCardToList(newTableList, idxlist) else table
            
            val newHand = if (!newTableList.isEmpty) Util.listRemoveAt(hand, idxCard) else hand
            return (copy(name, hand = newHand, outside), newTable)
        end if
    }

  def firstSplitter(list: List[CardInterface], splitter: Integer): Integer = {
    if (splitter == list(splitter).placeInList.get)
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

  def lookForGaps(list: List[CardInterface]): Option[List[CardInterface]] = {
    val lowestCard = lookForLowestCard(list)
    if(lowestCard == 0 && checkForAce(list))
      val tmpSplitterSafer = firstSplitter(list, 0)
      val secondList: List[CardInterface] = List()
      val newList: List[CardInterface] = secondForLoop(list, tmpSplitterSafer, secondList)
      val thirdList = list.filter(_.placeInList.get < tmpSplitterSafer)
      val finalList = newList ::: thirdList
      if (checkIfNextCardIsCorrect(finalList, finalList(0).placeInList.get))
        return Some(finalList)
      else
        return None
      end if
    else
      if (checkIfNextCardIsCorrect(list, list(0).placeInList.get))
        return Some(list)
      else
        return None
  }

  def lookForLowestCard(list: List[CardInterface]): Integer = {
    val low: List[Integer] = list.map(card => card.placeInList.get)
    low.min
  }

  def checkForAce(list: List[CardInterface]): Boolean = {
    list.foreach(x => {
      if (x.placeInList.get == 12)
        return true
    })
    false
  }

  def takeJoker(idxlist: Integer, idxCard: Integer, table: TableInterface) : (Player, TableInterface) = {
    val tmpTableList: List[CardInterface] = table.droppedCardsList(idxlist)
    val tmpRank: List[Integer] = tmpTableList.filter(card => card.getSuit.equals("Joker") ).map(card => card.getValue)
    val storeJokerPlaceRank: List[Integer] = tmpTableList.zipWithIndex.filter(card => card._1.getSuit.equals("Joker") ).map(card => card._2)
    val storeNormalCardsRank: List[Integer] = tmpTableList.zipWithIndex.filter(card => !card._1.getSuit.equals("Joker") ).map(card => card._2)

    val tmpSuit: List[String] = tmpTableList.filter(card => card.placeInList.get == 15).map(card => card.getSuit)
    val storeJokerPlaceSuit: List[Integer] = tmpTableList.zipWithIndex.filter(card => card._1.placeInList.get == 15 ).map(card => card._2)
    val storeNormalCardsSuit: List[Integer] = tmpTableList.zipWithIndex.filter(card => card._1.placeInList.get != 15 ).map(card => card._2)

    if (tmpSuit.distinct.size == tmpSuit.size && !tmpSuit.isEmpty) // Strategy 0 Suit
      val finalPlace = storeJokerPlaceSuit
        .filter(place => hand(idxCard).getSuit.equals(tmpTableList(place).getSuit)
         && hand(idxCard).getValue == tmpTableList(storeNormalCardsSuit(0)).getValue)
        .map(place => place)
      val updatedList = tmpTableList.updated(finalPlace.head, hand(idxCard))
      val removedCardFromHand = Util.listRemoveAt(hand, idxCard)
      val giveJokerToPlayerHand = removedCardFromHand ::: List(Card(4,0))
      val finalTable = table.droppedCardsList.updated(idxlist, updatedList)
      val finishedTable = Table(table.graveYard, finalTable)
      return (copy(name, hand = giveJokerToPlayerHand, outside), finishedTable)
    else  // Strategy 1 Order
      val finalPlace= storeJokerPlaceRank
      .filter(place => hand(idxCard).placeInList.get == tmpTableList(place).placeInList.get 
      && hand(idxCard).getSuit.equals(tmpTableList(storeNormalCardsRank(0)).getSuit))
      .map(place => place)
      val updatedList = tmpTableList.updated(finalPlace.head, hand(idxCard))
      val removedCardFromHand = Util.listRemoveAt(hand, idxCard)
      val giveJokerToPlayerHand = removedCardFromHand ::: List(Card(4,0))
      val finalTable = table.droppedCardsList.updated(idxlist, updatedList)
      val finishedTable = Table(table.graveYard, finalTable)
      return (copy(name, hand = giveJokerToPlayerHand, outside), finishedTable)
    end if 
    (copy(), table)
  }

  def dropMultipleCards(list: List[Integer], decision: Integer, hasJoker: Boolean, table : TableInterface) : (Player, TableInterface) = {
    val (didItWork, newPlayer, newTable) = dropCardsOnTable(list, decision, hasJoker, table)
    if(didItWork)
      val newHand = dropCardsFromHand(newPlayer.hand, newPlayer.hand.size, list, 0, list.size)
      newHand.map(card => println(card.getCardNameAsString))
      return (copy(name,hand = newHand, outside = true), newTable)
    end if 
    (copy(), newTable)

  }

  def dropCardsFromHand(playerCards: List[CardInterface], startingSize: Integer, counter: List[Integer], turnCounter: Integer, startingSizeCounter: Integer) : List[CardInterface] = {
    if (turnCounter != (startingSizeCounter))
      if ((playerCards.size - 1) == startingSize)
        return dropCardsFromHand((Util.listRemoveAt(playerCards, counter(0))), startingSize, counter.tail, turnCounter + 1, startingSizeCounter)
      else
        return dropCardsFromHand((Util.listRemoveAt(playerCards, (counter(0) - turnCounter))), startingSize, counter.tail, turnCounter + 1, startingSizeCounter)
      end if
    return playerCards
  }

  def draw13Cards(d: DeckInterface, exisitingCards: List[CardInterface]): (Player, DeckInterface) = {
    if (exisitingCards.size < 12) {
      val c = modelDeckRequest.drawFromDeck()
        c match {
        case Success((retCard, retDeck)) => {
          val tmpList: List[CardInterface] = List(retCard)
          val finalList = tmpList ::: exisitingCards
          val (newPlayer, newDeck) = draw13Cards(retDeck, finalList)
          return (copy(hand = newPlayer.hand), newDeck)
        }
        case Failure(e) => {
          println("Failure e")
          return (copy(name, hand, outside), d)
        }
      }
    } else {
      val c = modelDeckRequest.drawFromDeck()
      c match {
        case Success((retCard, retDeck)) => {
          val tmpList: List[CardInterface] = List(retCard)
          val finalList = tmpList ::: exisitingCards
          return (copy(hand = finalList ), retDeck)
        }
        case Failure(e) => {
          println("Failure e")
          return (copy(name, hand, outside), d)
        }
      }
    }
  }

  def replaceCards(turnCounter: Integer, stellenStartingSize: Integer, stellen: List[Integer], values: List[String], playerCards: List[CardInterface], isSuit: Boolean): Player = {
    if (turnCounter != stellenStartingSize)
      val splitCards = playerCards.splitAt(stellen.head).toList
      if (isSuit)
        val firstCardsWithJoker: List[CardInterface] = splitCards(0).toList ::: List(Joker().setSuit(values.head))
        val finalPlayerCards: List[CardInterface] = firstCardsWithJoker ::: splitCards(1).toList.tail
        return replaceCards(turnCounter + 1, stellenStartingSize, stellen.tail, values.tail, finalPlayerCards, isSuit)
      else
        val firstCardsWithJoker: List[CardInterface] = splitCards(0).toList ::: List(Joker().setValue(values.head))
        val finalPlayerCards: List[CardInterface] = firstCardsWithJoker ::: splitCards(1).toList.tail
        return replaceCards(turnCounter + 1, stellenStartingSize, stellen.tail, values.tail, finalPlayerCards, isSuit)
      end if
    else
      return copy(hand = playerCards)
  }

  def getDroppingCards(index: Integer): Option[CardInterface] = {
    if (index > (hand.size - 1))
      return None
    else
      return Some(Card(hand(index).getSuitNumber, hand(index).getRank))
  }

  def checkIfDropsAreCorrect(cards: List[Option[CardInterface]], finalList: List[CardInterface]): List[CardInterface] = {
    for (card <- cards) yield card match {
      case Some(value) => return checkIfDropsAreCorrect(cards.tail, finalList ::: List(value))
      case None => return List[CardInterface]()
    }
    return finalList
  }

  def dropCardsOnTable(index: List[Integer], decision: Integer, hasJoker: Boolean, table: TableInterface): (Boolean, Player, TableInterface) = {
    val drop = Drops
    var sum = 0
    val jokerPlaces = hand.zipWithIndex.filter(pair => pair._1.getCardNameAsString.equals("(Joker, )")).map(pair => pair._2)
    val cardsToBeDropped: List[Option[CardInterface]] = index.map(card => getDroppingCards(card))
    val droppingCards: List[CardInterface] = checkIfDropsAreCorrect(cardsToBeDropped, List[CardInterface]())
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
        return (false, copy(), table)
      end if
      val newTable = table.placeCardsOnTable(newDroppingCards)
      return (true, copy(outside = true), newTable)
    else
      val newDroppingCards = drop.execute(finalDroppingCards, decision, hasJoker)
      if(newDroppingCards.isEmpty)
        println("Your Cards are Empty => There is a mistake")
        return (false, copy(), table)
      end if
      println("The Cards were placed on the table")
      val newTable = table.placeCardsOnTable(newDroppingCards)
      return (true, copy(), newTable)
    end if
  }

  def summe(c:Int) = (x: Int)=> x + c

  def giveJokersRealValues(list: List[CardInterface], jokersInList: List[Integer], jokerPlaces: List[Int], counter: Integer, startingSizeJoker: Integer, isSuit: Integer): List[CardInterface] = {
    if (counter != startingSizeJoker)
      val splitCards = list.splitAt(jokersInList.head).toList
      if (isSuit == 0)
        val newJoker = Joker().setSuit(hand(jokerPlaces.head).getSuit)
        val cardsWithCorrectJoker = splitCards(0) ::: List(newJoker)
        if (splitCards(1).size <= 1)
          return giveJokersRealValues(cardsWithCorrectJoker, jokersInList.tail, jokerPlaces.tail, counter + 1, startingSizeJoker, isSuit)
        else
          val mergeList = cardsWithCorrectJoker ::: splitCards(1).tail
          return giveJokersRealValues(mergeList, jokersInList.tail, jokerPlaces.tail, counter + 1, startingSizeJoker, isSuit)
        end if
      else
        val newJoker = Joker().setRank(hand(jokerPlaces.head).getRank)
        val cardsWithCorrectJoker = splitCards(0) ::: List(newJoker)
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

  def sortPlayersCards : Player = {
    val heart = hand.filter(card => card.getSuit.equals("Heart")).map(card => Card(0, card.getRank)).sortBy(_.placeInList.get)
    val club = hand.filter(card => card.getSuit.equals("Club")).map(card => Card(2, card.getRank)).sortBy(_.placeInList.get)
    val diamond = hand.filter(card => card.getSuit.equals("Diamond")).map(card => Card(1, card.getRank)).sortBy(_.placeInList.get)
    val spades = hand.filter(card => card.getSuit.equals("Spades")).map(card => Card(3, card.getRank)).sortBy(_.placeInList.get)
    val joker = hand.filter(card => card.getSuit.equals("Joker")).map(card => Card(4,0))
    val add_heart_and_clubs = heart ::: club
    val add_diamonds = add_heart_and_clubs ::: diamond
    val add_spades = add_diamonds ::: spades
    val sortedHand = add_spades ::: joker
    copy(name, hand = sortedHand, outside)
  }

    def victory: Boolean = hand.isEmpty

    def showCards: String = {
      val s : List[String] = hand.map(card => card.getCardNameAsString)
      s.mkString(" ")
    }

    def fillHand(fillUntil: Integer): (Player) = {
      val index = 0
      val presentHand: List[Option[CardInterface]] = presentCards(index, fillUntil, List[Option[CardInterface]]())
      val newHand: List[CardInterface] = 
      for (card <- presentHand) yield card match {
        case Some(value) =>{
         value }
        case None => {
          Card(4, 4)
        } 
      }
      copy(hand = newHand)
      
    }


    def presentCards(idx: Integer, size: Integer, existingCards: List[Option[CardInterface]]): List[Option[CardInterface]] = {
      if (idx < size) {
        if (idx < hand.size) {
          val card: Option[CardInterface] = Some(hand(idx))
          val cards = existingCards ::: List(card)
          return presentCards((idx + 1), size, cards)
        } else {
          val card: Option[CardInterface] = None
          val cards = existingCards ::: List(card)
          return presentCards((idx + 1), size, cards)
        }
      }
      else {
        return existingCards
      }
    }
}
