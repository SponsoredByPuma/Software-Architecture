package cardComponent


trait CardInterface {
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
