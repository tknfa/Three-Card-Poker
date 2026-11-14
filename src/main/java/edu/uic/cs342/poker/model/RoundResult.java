package edu.uic.cs342.poker.model;

import edu.uic.cs342.poker.logic.HandRank;

public record RoundResult(int roundNumber, Hand dealerHand, HandRank dealerRank, boolean dealerQualifies, PlayerRoundResult playerOne, PlayerRoundResult playerTwo) {
}
