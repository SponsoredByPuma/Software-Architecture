package restDeck

import deckComponent.deckBaseImpl.Deck
import cardComponent.CardInterface

object RestDeck {
  @main def run =
    DeckService(Deck(List[CardInterface]())).start()
}