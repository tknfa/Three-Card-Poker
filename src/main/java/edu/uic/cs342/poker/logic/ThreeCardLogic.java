package edu.uic.cs342.poker.logic;

import edu.uic.cs342.poker.model.Card;

import java.util.ArrayList;

public final class ThreeCardLogic {

    private ThreeCardLogic() {}

    public static int evalHand(ArrayList<Card> hand) {
        return HandEvaluator.evaluate(hand).evaluationCode();
    }

    public static int compareHands(ArrayList<Card> dealer, ArrayList<Card> player) {
        return HandEvaluator.compare(dealer, player).comparisonCode();
    }

    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        return PayoutCalculator.pairPlusWinnings(hand, bet);
    }
}
