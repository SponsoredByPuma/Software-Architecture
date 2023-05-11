package fileIOComponent.fileIOXmlImpl

import fileIOComponent.FileIOInterface
import modelComponent.gameComponent.GameInterface
import com.google.inject.Guice
import de.htwg.se.romme.RommeModule
import scala.xml.Elem
import modelComponent.gameComponent.gameBaseImpl.Player
import modelComponent.gameComponent.gameBaseImpl.Game
import modelComponent.gameComponent.gameBaseImpl.Table
import scala.xml.NodeSeq
import modelComponent.gameComponent.gameBaseImpl.Card
import scala.collection.mutable.ListBuffer
import modelComponent.gameComponent.gameBaseImpl.Deck
import scala.xml.PrettyPrinter
import java.io.PrintWriter
import java.io.File
import com.google.inject.Inject

class FileIO @Inject() extends FileIOInterface {

  val player1h:List[Card] = List()
  val player2h: List[Card] = List()

  val suitForCard = Map(
    "Heart" -> 0,
    "Diamond" -> 1,
    "Club" -> 2,
    "Spades" -> 3,
    "Joker" -> 4,
    "" -> 5
  )

  val rankForCard = Map(
    "two" -> 0,
    "three" -> 1,
    "four" -> 2,
    "five" -> 3,
    "six" -> 4,
    "seven" -> 5,
    "eight" -> 6,
    "nine" -> 7,
    "ten" -> 8,
    "jack" -> 9,
    "queen" -> 10,
    "king" -> 11,
    "ace" -> 12
  )

  override def load: GameInterface = {
    var game: GameInterface = null
    val file = scala.xml.XML.loadFile("game.xml")
    var injector =
      Guice.createInjector(new RommeModule).getInstance(classOf[GameInterface])
    val table = loadTable(file)
    val player1 = loadPlayer1(file,table)
    val player2 = loadPlayer2(file,table)
    val deck = loadDeck(file)
    game = Game(table, List(player1, player2), deck)
    game
  }

  def prepCard(s: String): Array[String] = {
    val cText = s.replace(")(",":")
    val klammerweg = cText.dropRight(1)
    val dText = klammerweg.substring(1)
    val fText = dText.split(":")
    val geteilt = fText(0).split(",")
    geteilt
  }

  def loadDeck(file: Elem): Deck = {
    val ll: List[Card] = List() // Liste der Karten
    val deck: Deck = Deck(List[Card]())
    val dNode = (file \\ "deck")
    val tmp = getSeqFromSeq(dNode,"cards", "cC").text // hier muss noch was anderes in die Klammer
    val tt = tmp.replace(")(", " ") // entferne die Klammern zwischen den Karten und mach ein Leerzeichen dazwischen
    val tt2 = tt.substring(1,tt.length- 1) // Entferne die erste und letzte Klammer
    val tt3 = tt2.split(" ")
    for(x<- tt3) // für jede Karte
      val neuSplit = x.split(",") // beim Komma teilen
      val sI = suitForCard.apply(neuSplit(0)) // finde den Suit der Karte heraus
      if (sI != 4)
        val rI = rankForCard.apply(neuSplit(1)) // finde den Rank heraus
        val tmpCard: Card = Card(sI,rI) // erstelle die neue Karte
        ll :+ List(tmpCard) // füge die Karte der Liste von Karten hinzu
      else
        val tmpCard: Card = Card(sI, 0) // erstelle den Joker
        ll :+ List(tmpCard) // füge den Joker der Liset von Karten hinzu
      end if
    deck.deckList :+ List(ll)
    deck
  }

  def loadTable(file: Elem): Table = {
    val table: Table = Table(Card(5, 0), List[List[Card]]())
    val tNode = (file \\ "table")
    //---------------------Friedhofskarte
    val grave = getSeqFromTable(tNode,"graveYard").text // hier muss noch was anders in die Strings rein
    println(grave)
    if(grave.equals("(,)"))
      print("Test")
      //table.graveYard = Card(5,0)
    else
      val geteilt = prepCard(grave)
      val suitInteger = suitForCard.apply(geteilt(0))
      if (suitInteger == 4)
        print("Gleichw ieder löschen")
      //  table.graveYard = Card(4,0)
      end if
      if(suitInteger != 4 || suitInteger != 5)
        val rankInteger = rankForCard.apply(geteilt(1))
      //  table.graveYard = Card(suitInteger,rankInteger)
      end if
    end if
    //--------------------Ende Friedhofskarte
    val tableCards = getSeqFromTable2(tNode,"droppedCards","LL")
    for(liste <- tableCards)
      val ll: List[Card] = List() // Liste der Karten
      val dC = liste.text
      val tt = dC.split("\\s+")
      val tmpSpeicher = new Array[String](tt.size)
      var counter = 0
      for(t<- tt)
        val t1 = t.substring(1)
        val t2 = t1.dropRight(1)
        tmpSpeicher(counter) = t2
        counter = counter + 1
      for(x <- tmpSpeicher) // nun muss noch jede der Karten gesplittet werden
        val neuSplit = x.split(",") // beim komma
        val sI = suitForCard.apply(neuSplit(0)) // schaue nach dem Suit
        if (sI != 4) // wenn es kein Joker ist
          val rI = rankForCard.apply(neuSplit(1)) // finde den Rank heraus
          val tmpCard: Card = Card(sI,rI) // erstelle die neue Karte
          ll :+ List(tmpCard) // füge die Karte der Liste von Karten hinzu
        else
          val tmpCard: Card = Card(sI, 0) // erstelle den Joker
          ll :+ List(tmpCard) // füge den Joker der Liset von Karten hinzu
        end if
      table.droppedCardsList :+ List(ll) // überreiche die Liste von Karten dem Tisch
    table // gebe den Tisch zurück
  }

  def loadPlayer1(file: Elem, table: Table): Player = { // gleich wieder Player reinmachen
    val p1 = (file \\ "player1")
    val p1Name: String = (p1 \\ "name").text
    val player1Cards = getSeqFromSeq(p1, "cards", "cC")
    for (card <- player1Cards) {
      val geteilt = prepCard(card.text)
      val suitString = geteilt(0) // Suit als String
      val rankString = geteilt(1) // Rank als String
      val suitInteger = suitForCard.apply(suitString)
      if (rankString.equals("")) // ist es ein Joker
        val cardTmp: Card = Card(suitInteger,0) // erstelle die Joker Karte
        println(cardTmp.getCardName)
        player1h :+ List(cardTmp) // füge die Karte hinzu
      else
        val rankInteger = rankForCard.apply(rankString) // finde den rank als Integer heraus
        val cardTmp: Card = Card(suitInteger,rankInteger) // erstelle die jeweilige Karte
        println(cardTmp.getCardName)
        player1h :+ List(cardTmp) // füge die Karte zur Hand hinzu
      end if
    }
    val player1 = Player(p1Name,player1h,false)
    player1
  }

  def loadPlayer2(file: Elem,table: Table): Player = {
    val p2 = (file \\ "player2")
    val p2Name: String = (p2 \\ "name").text
    val player2Cards = getSeqFromSeq(p2, "cards", "cC")
    for (card <- player2Cards) {
      val geteilt = prepCard(card.text)
      val suitString = geteilt(0) // Suit als String
      if (suitString.equals("Joker"))
        val cardTmp: Card = Card(4,0) // erstelle die Joker Karte
        player2h :+ List(cardTmp) // füge die Karte hinzu
      else
        val rankString = geteilt(1) // Rank als String
        val suitInteger = suitForCard.apply(suitString)
        val rankInteger = rankForCard.apply(rankString) // finde den rank als Integer heraus
        val cardTmp: Card = Card(suitInteger,rankInteger) // erstelle die jeweilige Karte
        player2h :+ List(cardTmp) // füge die Karte zur Hand hinzu
      end if
    }
    val player2 = Player(p2Name,player2h,false)
    player2
  }

  def getSeqFromFile(file: Elem, s: String, s2: String): NodeSeq = {
    val first = (file \\ s)
    val second = (first \\ s2)
    val end = (second \\ "card")
    end
  }

  def getSeqFromTable(file: NodeSeq, s: String): NodeSeq = {
    val first = (file \\ s)
    val second = (first \\ "card")
    second
  }

  def getSeqFromTable2(file: NodeSeq, s: String, s2: String): NodeSeq = {
    val first = (file \\ s) // das markierte nur noch von cards
    val second = (first \\ s2) // nur noch CC
    val end = (second \\ "L") // nur noch card
    end
  }

  def getSeqFromSeq(file: NodeSeq, s: String, s2: String): NodeSeq = {
    val first = (file \\ s) // das markierte nur noch von cards
    val second = (first \\ s2) // nur noch CC
    val end = (second \\ "card") // nur noch card
    end
  }
  override def save(game: GameInterface): Unit = saveString(game)

  def saveString(game: GameInterface): Unit = {
    val pw = new PrintWriter(new File("game.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(gameToXml(game))
    pw.write(xml)
    pw.close
  }

  def gameToXml(game: GameInterface): Elem = {
    <game>
      {deckToXml(game.deck)}
      {player1ToXml(game.players(0))}
      {player2ToXml(game.players(1))}
      {tableToXml(game.table)}
    </game>
  }

  def deckToXml(deck: Deck): Elem = {
    <deck>
      <cards>{listToXml(deck.deckList)} </cards>
    </deck>
  }

  def player1ToXml(player: Player): Elem = {
    <player1>
      <name>{player.name}</name>
      <cards>{listToXml(player.hand)}</cards>
    </player1>
  }

  def player2ToXml(player: Player): Elem = {
    <player2>
      <name>{player.name}</name>
      <cards>{listToXml(player.hand)}</cards>
    </player2>
  }

  def tableToXml(table: Table): Elem = {
    <table>
      <graveYard>{cardToXml(table.graveYard)}</graveYard>
      <droppedCards>{ListListToXml(table.droppedCardsList)}</droppedCards>
    </table>
  }

  def ListListToXml(stack:List[List[Card]]): Elem = {
    <LL>{for(liste <- stack) yield tabledropped(liste)}</LL>
  }

  def tabledropped (stack: List[Card]): Elem = {
    <L>{for(card <-stack) yield card.getCardName}</L>
  }

  def listToXml(stack: List[Card]): Elem = {
    <cC>{
      for(card <- stack) yield cardToXml(card)
      }</cC>
  }

  def cardToXml(card: Card): Elem = {
    <card>{card.getCardName}</card>
  }
}
