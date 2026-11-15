package edu.uic.cs342.poker.ui;

import edu.uic.cs342.poker.logic.HandEvaluator;
import edu.uic.cs342.poker.logic.HandRank;
import edu.uic.cs342.poker.model.Hand;
import edu.uic.cs342.poker.model.PlayerRoundResult;
import edu.uic.cs342.poker.model.RoundOutcome;
import edu.uic.cs342.poker.model.RoundResult;

public final class ResultText {
    private ResultText() { }

    public static String outcome(RoundOutcome outcome) {
        if (outcome == RoundOutcome.PLAYER_WINS) { return "WIN vs dealer"; }
        if (outcome == RoundOutcome.DEALER_WINS) { return "LOSS vs dealer"; }
        if (outcome == RoundOutcome.FOLDED) { return "FOLDED"; }
        if (outcome == RoundOutcome.TIE) { return "TIE"; }

        return "DEALER NOT QUALIFIED";
    }

    public static String rank(Hand hand, HandRank rank) {
        String name = rank.name().replace('_', ' ');

        if (rank == HandRank.HIGH_CARD) {
            return name + " (" + HandEvaluator.rankCards(hand.getCards()).get(0).rank().name() + ")";
        }

        return name;
    }

    public static String dealer(RoundResult result) {
        return rank(result.dealerHand(), result.dealerRank()) + (result.dealerQualifies() ? " — QUALIFIES" : " — NOT QUALIFIED");
    }

    public static String ante(PlayerRoundResult player) {
        String prefix = "Ante (" + money(player.anteWager()) + "): ";

        if (player.carriedAnte() != 0) {
            return prefix + money(player.carriedAnte()) + " carried forward (net $0)";
        }

        if (player.outcome() == RoundOutcome.TIE) { return prefix + "returned (net $0)"; }

        return prefix + signedMoney(player.anteNetChange());
    }

    public static String play(PlayerRoundResult player) {
        String prefix = "Play (" + money(player.playWager()) + "): ";

        if (player.outcome() == RoundOutcome.FOLDED) { return prefix + "not placed"; }
        if (player.outcome() == RoundOutcome.TIE || player.outcome() == RoundOutcome.DEALER_NOT_QUALIFIED) {
            return prefix + "returned (net $0)";
        }

        return prefix + signedMoney(player.playNetChange());
    }

    public static String pairPlus(PlayerRoundResult player) {
        String prefix = "Pair Plus (" + money(player.pairPlusWager()) + "): ";

        if (player.pairPlusWager() == 0) { return prefix + "not placed"; }
        if (player.outcome() == RoundOutcome.FOLDED) {
            return prefix + "forfeited " + signedMoney(player.pairPlusNetChange());
        }

        if (player.pairPlusMultiplier() == 0) {
            return prefix + "no payout (" + signedMoney(player.pairPlusNetChange()) + ")";
        }

        return prefix + player.pairPlusMultiplier() + ":1 — " + money(player.pairPlusWager()) + " × " + player.pairPlusMultiplier() + " = " + signedMoney(player.pairPlusNetChange());
    }

    public static String cumulative(PlayerRoundResult player) {
        String change = player.netChange() < 0 ? " - " + money(-player.netChange()) : " + " + money(player.netChange());
        return money(player.previousCumulativeWinnings()) + change + " = " + money(player.updatedCumulativeWinnings());
    }

    public static String money(int amount) {
        return amount < 0 ? "-$" + Math.abs(amount) : "$" + amount;
    }

    public static String signedMoney(int amount) {
        return amount > 0 ? "+" + money(amount) : money(amount);
    }
}
