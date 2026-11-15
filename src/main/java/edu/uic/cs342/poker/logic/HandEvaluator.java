package edu.uic.cs342.poker.logic;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Hand;

import java.util.ArrayList;
import java.util.List;

public final class HandEvaluator {

    private HandEvaluator() {}

    public static HandRank evaluate(List<Card> cards) {
        Hand hand = new Hand(cards);
        List<Card> handCards = hand.getCards();

        Card first = handCards.get(0);
        Card second = handCards.get(1);
        Card third = handCards.get(2);

        int firstValue = first.value();
        int secondValue = second.value();
        int thirdValue = third.value();

        boolean sameSuit = first.suit() == second.suit() && second.suit() == third.suit();
        boolean threeOfAKind = firstValue == secondValue && secondValue == thirdValue;
        boolean hasPair = firstValue == secondValue || secondValue == thirdValue || firstValue == thirdValue;
        boolean straight = !hasPair && hasConsecutiveValues(handCards);

        if (straight && sameSuit) {
            return HandRank.STRAIGHT_FLUSH;
        }

        if (threeOfAKind) {
            return HandRank.THREE_OF_A_KIND;
        }

        if (straight) {
            return HandRank.STRAIGHT;
        }

        if (sameSuit) {
            return HandRank.FLUSH;
        }

        if (hasPair) {
            return HandRank.PAIR;
        }

        return HandRank.HIGH_CARD;
    }

    public static List<Card> rankCards(List<Card> cards) {
        HandRank rank = evaluate(cards);
        ArrayList<Card> relevant = new ArrayList<>();
        if (rank == HandRank.PAIR) {
            for (int i = 0; i < cards.size(); i++) {
                for (int k = 0; k < cards.size(); k++) {
                    if (i != k && cards.get(i).value() == cards.get(k).value()) {
                        relevant.add(cards.get(i));
                        break;
                    }
                }
            }
        } else if (rank == HandRank.HIGH_CARD) {
            Card highest = cards.get(0);
            for (Card card : cards) {
                if (card.value() > highest.value()) {
                    highest = card;
                }
            }
            relevant.add(highest);
        } else {
            return List.copyOf(cards);
        }
        return List.copyOf(relevant);
    }

    private static boolean hasConsecutiveValues(List<Card> cards) {
        int lowest = cards.get(0).value();
        int highest = lowest;

        for (Card card : cards) {
            int value = card.value();

            if (value < lowest) {
                lowest = value;
            }

            if (value > highest) {
                highest = value;
            }
        }

        boolean consecutiveValues = highest - lowest == 2;
        return consecutiveValues;
    }

    public static HandComparison compare(List<Card> dealer, List<Card> player) {
        HandRank dealerRank = evaluate(dealer);
        HandRank playerRank = evaluate(player);

        if (playerRank.strength() > dealerRank.strength()) {
            return HandComparison.PLAYER_WINS;
        }

        if (playerRank.strength() < dealerRank.strength()) {
            return HandComparison.DEALER_WINS;
        }

        int[] dealerValues = comparisonValues(dealer, dealerRank);
        int[] playerValues = comparisonValues(player, playerRank);

        for (int i = 0; i < dealerValues.length; i++) {
            if (playerValues[i] > dealerValues[i]) {
                return HandComparison.PLAYER_WINS;
            }

            if (playerValues[i] < dealerValues[i]) {
                return HandComparison.DEALER_WINS;
            }
        }

        return HandComparison.TIE;
    }

    public static boolean dealerQualifies(List<Card> dealer) {
        HandRank rank = evaluate(dealer);

        if (rank != HandRank.HIGH_CARD) {
            return true;
        }

        return comparisonValues(dealer, rank)[0] >= 12;
    }

    private static int[] comparisonValues(List<Card> cards, HandRank rank) {
        int[] values = {cards.get(0).value(), cards.get(1).value(), cards.get(2).value()};

        for (int i = 0; i < values.length - 1; i++) {
            for (int j = i + 1; j < values.length; j++) {
                if (values[j] > values[i]) {
                    int temp = values[i];
                    values[i] = values[j];
                    values[j] = temp;
                }
            }
        }

        if (rank == HandRank.PAIR && values[0] != values[1]) {
            int kicker = values[0];
            values[0] = values[1];
            values[1] = values[2];
            values[2] = kicker;
        }

        return values;
    }
}
