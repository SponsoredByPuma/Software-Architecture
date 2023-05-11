package de.htwg.se.romme.model.modelComponent.dropsComponent

import de.htwg.se.romme.model.modelComponent.gameComponent.gameBaseImpl.Card

trait DropsInterface:

    def strategy(numberOfStrategy: Integer) : Integer
    def strategySameSuit: Integer
    def strategyOrder: Integer
    def execute(cards: List[Card], numberOfStrategy: Integer, hasJoker: Boolean) : Integer