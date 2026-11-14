package edu.uic.cs342.poker.logic;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Hand;
import edu.uic.cs342.poker.model.PlayerDecision;
import edu.uic.cs342.poker.model.PlayerState;
import edu.uic.cs342.poker.model.PlayerRoundResult;
import edu.uic.cs342.poker.model.RoundOutcome;

import java.util.List;

public final class PayoutCalculator {

    private PayoutCalculator() {}

    public static int pairPlusMultiplier(HandRank rank) {
        if (rank == HandRank.STRAIGHT_FLUSH) {
            return 40;
        }

        if (rank == HandRank.THREE_OF_A_KIND) {
            return 30;
        }

        if (rank == HandRank.STRAIGHT) {
            return 6;
        }

        if (rank == HandRank.FLUSH) {
            return 3;
        }

        if (rank == HandRank.PAIR) {
            return 1;
        }

        if (rank == HandRank.HIGH_CARD) {
            return 0;
        }

        throw new IllegalArgumentException("Hand rank is required");
    }

    public static int pairPlusWinnings(List<Card> cards, int bet) {
        if (bet != 0 && (bet < 5 || bet > 25)) {
            throw new IllegalArgumentException("Pair Plus must be $0 or between $5 and $25");
        }

        HandRank rank = HandEvaluator.evaluate(cards);
        return bet * pairPlusMultiplier(rank);
    }

    public static int pairPlusNetChange(List<Card> cards, int bet) {
        int winnings = pairPlusWinnings(cards, bet);

        if (winnings == 0) {
            return -bet;
        }

        return winnings;
    }

    public static PlayerRoundResult settlePlayer(PlayerState player, Hand dealer) {
        if (player == null || dealer == null || player.hand() == null || !player.wagersConfirmed() || (player.decision() != PlayerDecision.PLAY && player.decision() != PlayerDecision.FOLD)) {
            throw new IllegalArgumentException("A completed player decision and hands are required");
        }

        List<Card> cards = player.hand().getCards();
        HandRank rank = HandEvaluator.evaluate(cards);
        int ante = player.anteWager();
        int pairPlus = player.pairPlusWager();
        int play = player.playWager();

        if (ante < 5 || ante > 25 || (pairPlus != 0 && (pairPlus < 5 || pairPlus > 25)) || (player.decision() == PlayerDecision.PLAY && play != ante) || (player.decision() == PlayerDecision.FOLD && play != 0)) {
            throw new IllegalArgumentException("Invalid wagers for settlement");
        }

        int anteChange = 0;
        int playChange = 0;
        int pairPlusChange = 0;
        int multiplier = 0;
        int carriedAnte = 0;
        RoundOutcome outcome;

        if (player.decision() == PlayerDecision.FOLD) {
            outcome = RoundOutcome.FOLDED;
            anteChange = -ante;
            pairPlusChange = -pairPlus;
        } else {
            pairPlusChange = pairPlusNetChange(cards, pairPlus);

            if (pairPlus != 0) {
                multiplier = pairPlusMultiplier(rank);
            }

            if (!HandEvaluator.dealerQualifies(dealer.getCards())) {
                outcome = RoundOutcome.DEALER_NOT_QUALIFIED;
                carriedAnte = ante;
            } else {
                HandComparison comparison = HandEvaluator.compare(dealer.getCards(), cards);

                if (comparison == HandComparison.PLAYER_WINS) {
                    outcome = RoundOutcome.PLAYER_WINS;
                    anteChange = ante;
                    playChange = play;
                } else if (comparison == HandComparison.DEALER_WINS) {
                    outcome = RoundOutcome.DEALER_WINS;
                    anteChange = -ante;
                    playChange = -play;
                } else {
                    outcome = RoundOutcome.TIE;
                }
            }
        }

        int netChange = anteChange + playChange + pairPlusChange;

        return new PlayerRoundResult(player.name(), player.hand(), rank, player.decision(), outcome, ante, pairPlus, play, anteChange, playChange, pairPlusChange, multiplier, carriedAnte, netChange, player.cumulativeWinnings(), player.cumulativeWinnings() + netChange);
    }
}
