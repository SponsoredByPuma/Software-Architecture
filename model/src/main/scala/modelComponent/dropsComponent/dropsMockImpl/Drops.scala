package modelComponent.dropsComponent.dropsMockImpl

import modelComponent.dropsComponent.DropsInterface
import scala.collection.mutable.ListBuffer
import modelComponent.gameComponent.gameBaseImpl.Card

abstract class Drops() extends DropsInterface {
  def strategy(numberOfStrategy: Integer): Integer = 0
  def strategySameSuit: Integer = 0
  def strategyOrder: Integer = 0
  def execute(
      cards: List[Card],
      numberOfStrategy: Integer,
      hasJoker: Boolean
  ): Integer = 0
}
