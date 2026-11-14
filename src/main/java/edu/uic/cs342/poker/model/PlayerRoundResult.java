package edu.uic.cs342.poker.model;

import edu.uic.cs342.poker.logic.HandRank;

public record PlayerRoundResult (String name, Hand hand, HandRank handRank, PlayerDecision decision, RoundOutcome outcome,
                                 int anteWager, int pairPlusWager, int playWager, int anteNetChange, int playNetChange,
                                 int pairPlusNetChange, int pairPlusMultiplier, int carriedAnte, int netChange,
                                 int previousCumulativeWinnings, int updatedCumulativeWinnings){
}
