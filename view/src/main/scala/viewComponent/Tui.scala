package viewComponent

import controllerComponent._
import controllerComponent.controllerBaseImpl._
import scala.io.StdIn.readLine
import scala.swing.Reactor

class Tui(controller: ControllerInterface) extends Reactor {
  listenTo(controller)
  
  def processInputReadLine(input: String): Unit = {
    input match {
      case "quit"    => System.exit(0)
      case "switch" => controller.switch
      case "new"     => controller.gameStart
      case "pick"    => controller.pickUpACard
      case "graveYard" => controller.pickUpGraveYard
      case "add" => 
        println("Which card would you like to add ?")
        val cardIndex = readLine().toInt
        println("Which Set would you like to expand ?")
        val listIndex = readLine().toInt
        controller.addCard(cardIndex,listIndex)
      case "undo" => controller.undo
      case "redo" => controller.redo
      case "drop"    => 
        println("Which card would you like to drop ?")
        val index = readLine().toInt
        controller.dropASpecificCard(index)
      case "dropM" => 
        var amount = 0
        if(controller.playerState.getPlayer == 0)
          {
            while(amount < 3 || amount >= controller.game.players(0).hand.size)
              print("How many Cards would you like to drop ?")
              amount = readLine.toInt
          }
        else
          {
            while(amount < 3 || amount >= controller.game.players(1).hand.size)
              print("How many Cards would you like to drop ?")
              amount = readLine.toInt
          }
        val list : List[Integer] = List()
        var scanner = ""
        while(amount > 0)
          println("Which Card would you like to drop ?")
          scanner = readLine
          list :+ list(scanner.toInt)
          amount = amount - 1
        println("Would you like to drop them by Suit(0) or by Order(1) ?")
        val decision = readLine.toInt
        var tt: List[Integer] = List()
        tt = controller.checkForJoker(list)
        if(tt.isEmpty) {
          controller.dropMultipleCards(list,decision,false)
        } else {
          val stringList: List[String] = List()
          if (decision == 0) { // nach Suit
            tt.map(x => println("Which Suit should your Joker have ?"))
            val input = readLine()
            stringList :+ List(input)
            controller.replaceCardSuit(tt, stringList)
            controller.dropMultipleCards(list, decision, true)
          } else {
            tt.map(x => println("Which Rank should your Joker have ?"))
            val input = readLine()
            stringList :+ List(input)
            controller.replaceCardOrder(tt, stringList)
            controller.dropMultipleCards(list, decision, true)
          }
        }
      case "show"    => print(controller.showCards)
      case "joker" => 
        println("Which Card would you like to drop ?")
        val cardInput = readLine().toInt
        println("Which Set would you like to change ?")
        val setInput = readLine().toInt
        controller.takeJoker(setInput,cardInput)
      case "showTable" => print(controller.showTable)
      case "sort" => controller.sortPlayersCards
      case "victory" => 
        val victory = controller.victory
        if (victory == true) {
          println("Victory ! You have won the Game !")
        } else {
          println("You havent won the Game yet !")
        }
      case "load" => controller.load
      case "save" => controller.save
      case _ =>  
    }
  }

  reactions += {
    case event: showPlayerCards => printCards
    case event: showPlayerTable => printTable
  }

  def printCards: Unit = {
    println(controller.showCards)
  }

  def printTable: Unit = {
    println(controller.showTable)
  }
}
