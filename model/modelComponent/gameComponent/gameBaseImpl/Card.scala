package modelComponent.gameComponent.gameBaseImpl

trait Card {
  def getSuit: String
  def getSuitNumber: Integer
  def getValue: Integer
  def getCardName: (String, String)
  def placeInList: Option[Integer]
  def getCardNameAsString: String
  def getRank: Integer
  val rankList: List[String] = List(
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
    "ten",
    "jack",
    "queen",
    "king",
    "ace"
  )
  val valueForCard = Map(
    0 -> 2,
    1 -> 3,
    2 -> 4,
    3 -> 5,
    4 -> 6,
    5 -> 7,
    6 -> 8,
    7 -> 9,
    8 -> 10,
    9 -> 10,
    10 -> 10,
    11 -> 10,
    12 -> 10,
    15 -> 99
  )
}

private class Heart(rank: Integer) extends Card {
  override def getSuit: String = "Heart"
  override def getSuitNumber: Integer = 0
  override def getValue: Integer = valueForCard.apply(rank)
  override def getCardName: (String, String) = ("Heart", rankList(rank))

  override def placeInList: Option[Integer] = Some(rank)

  override def getCardNameAsString: String = {
    val s = "(Heart," + rankList(rank) + ")"
    s
  }
  override def getRank: Integer = this.rank
}

private class Diamond(rank: Integer) extends Card {
  override def getSuit: String = "Diamond"
  override def getSuitNumber: Integer = 1
  override def getValue: Integer = valueForCard.apply(rank)
  override def getCardName: (String, String) = ("Diamond", rankList(rank))
  override def placeInList: Option[Integer] = Some(rank)

  override def getCardNameAsString: String = {
    val s = "(Diamond," + rankList(rank) + ")"
    s
  }
  override def getRank: Integer = this.rank
}

private class Spades(rank: Integer) extends Card {
  override def getSuit: String = "Spades"
  override def getSuitNumber: Integer = 3
  override def getValue: Integer = valueForCard.apply(rank)
  override def getCardName: (String, String) = ("Spades", rankList(rank))
  override def placeInList: Option[Integer] = Some(rank)

  override def getCardNameAsString: String = {
    val s = "(Spades," + rankList(rank) + ")"
    s
  }
  override def getRank: Integer = this.rank
}

private class Club(rank: Integer) extends Card {
  override def getSuit: String = "Club"
  override def getSuitNumber: Integer = 2
  override def getValue: Integer = valueForCard.apply(rank)
  override def getCardName: (String, String) = ("Club", rankList(rank))

  override def placeInList: Option[Integer] = Some(rank)

  override def getCardNameAsString: String = {
    val s = "(Club," + rankList(rank) + ")"
    s
  }
  override def getRank: Integer = this.rank
}

case class Joker() extends Card {
  var rank = 15
  var suit = "Joker"
  override def getSuit: String = suit
  override def getSuitNumber: Integer = 4
  override def getValue: Integer = valueForCard.apply(rank)
  override def getCardName: (String, String) = ("Joker", "")
  def setValue(value: String): Joker = {
    this.rank = rankList.indexOf(value)
    this
  }

  def setRank(value: Integer): Joker = {
    this.rank = value
    this
  }
  def setSuit(s: String): Joker = {
    this.suit = s;
    this
  }
  override def placeInList: Option[Integer] = Some(rank)

  override def getCardNameAsString: String = {
    val s = "(Joker, )"
    s
  }
  override def getRank: Integer = this.rank
}

private class EmptyCard() extends Card {
  override def getSuit: String = ""
  override def getSuitNumber: Integer = 10
  override def getValue: Integer = 0
  override def getCardName: (String, String) = ("", "")
  override def placeInList: Option[Integer] = None
  override def getCardNameAsString: String = "(,)"
  override def getRank: Integer = 100
}

object Card {
  def apply(suit: Integer, rank: Integer) = suit match {
    case 0 => new Heart(rank)
    case 1 => new Diamond(rank)
    case 2 => new Club(rank)
    case 3 => new Spades(rank)
    case 4 => new Joker()
    case 5 => new EmptyCard()
  }
}
