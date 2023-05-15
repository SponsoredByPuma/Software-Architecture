package restCard

object RestCard {
  @main def run =
    CardService().start()
}