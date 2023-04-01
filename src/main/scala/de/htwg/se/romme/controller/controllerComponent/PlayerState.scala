package de.htwg.se.romme.controller.controllerComponent

trait PlayerState {
    def changePlayer: PlayerState
    def getPlayer: Int
    def name: String
}

object PlayerOne extends PlayerState {
    override def changePlayer: PlayerState = PlayerTwo
    override def getPlayer: Int = 0
    override def name: String = "Player 1"
}

object PlayerTwo extends PlayerState {
    override def changePlayer: PlayerState = PlayerOne
    override def getPlayer: Int = 1
    override def name: String = "Player 2"
}

