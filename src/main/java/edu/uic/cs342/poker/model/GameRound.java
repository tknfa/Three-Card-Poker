package edu.uic.cs342.poker.model;

public record GameRound(int roundNumber, GamePhase phase, PlayerState playerOne, PlayerState playerTwo, Hand dealerHand, RoundResult result){
}
