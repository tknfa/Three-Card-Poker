package edu.uic.cs342.poker.service;

import edu.uic.cs342.poker.logic.HandEvaluator;
import edu.uic.cs342.poker.logic.PayoutCalculator;
import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Dealer;
import edu.uic.cs342.poker.model.GamePhase;
import edu.uic.cs342.poker.model.GameRound;
import edu.uic.cs342.poker.model.Hand;
import edu.uic.cs342.poker.model.HandDealer;
import edu.uic.cs342.poker.model.Player;
import edu.uic.cs342.poker.model.PlayerState;
import edu.uic.cs342.poker.model.PlayerRoundResult;
import edu.uic.cs342.poker.model.RoundResult;

import java.util.ArrayList;

public final class GameService {
    private final HandDealer dealer;
    private final Player[] players = {new Player("Player 1"), new Player("Player 2")};
    private final boolean[] confirmed = new boolean[2];
    private final int[] carriedAntes = new int[2];
    private GamePhase phase = GamePhase.READY;
    private int roundNumber;
    private Hand dealerHand;
    private RoundResult result;

    public GameService() {
        this(new Dealer());
    }

    public GameService(HandDealer dealer) {
        if (dealer == null) {
            throw new IllegalArgumentException("A dealer is required");
        }
        this.dealer = dealer;
    }

    public void startGame() {
        requirePhase(GamePhase.READY);
        beginRound();
    }

    public void nextRound() {
        requirePhase(GamePhase.RESULTS);
        beginRound();
    }

    private void beginRound() {
        for (int i = 0; i < players.length; i++) {
            players[i].resetRound();
            confirmed[i] = false;
        }
        dealerHand = null;
        result = null;
        roundNumber++;
        phase = GamePhase.WAGERING;
    }

    // Player numbers are 1 and 2, matching the labels on the wireframes.
    public void confirmWagers(int playerNumber, int ante, int pairPlus) {
        int index = playerIndex(playerNumber);
        requirePhase(GamePhase.WAGERING);
        if (confirmed[index]) {
            throw new IllegalStateException("This player's wagers are already confirmed");
        }
        // Provisional interpretation of the assignment's carry-forward rule.
        if (carriedAntes[index] != 0 && ante != carriedAntes[index]) {
            throw new IllegalArgumentException("Ante must match the carried wager of $" + carriedAntes[index]);
        }
        players[index].setWagers(ante, pairPlus);
        confirmed[index] = true;
    }

    public boolean canDeal() {
        return phase == GamePhase.WAGERING && confirmed[0] && confirmed[1];
    }

    public void deal() {
        requirePhase(GamePhase.WAGERING);
        if (!canDeal()) {
            throw new IllegalStateException("Both players must confirm wagers before dealing");
        }

        dealer.resetDeck();
        Hand first = dealer.dealHand();
        Hand second = dealer.dealHand();
        Hand house = dealer.dealHand();
        verifyUniqueCards(first, second, house);

        // Assign only after all three valid hands have been obtained.
        players[0].setHand(first);
        players[1].setHand(second);
        dealerHand = house;
        phase = GamePhase.PLAYER_ONE_TURN;
    }

    private void verifyUniqueCards(Hand first, Hand second, Hand house) {
        Hand[] hands = {first, second, house};
        ArrayList<Card> dealt = new ArrayList<>();
        for (Hand hand : hands) {
            if (hand == null) {
                throw new IllegalStateException("Dealer returned a missing hand");
            }
            for (Card card : hand.getCards()) {
                if (dealt.contains(card)) {
                    throw new IllegalStateException("Dealer returned a duplicate card");
                }
                dealt.add(card);
            }
        }
    }

    public void play(int playerNumber) {
        int index = requireTurn(playerNumber);
        players[index].play();
        advanceTurn(index);
    }

    public void fold(int playerNumber) {
        int index = requireTurn(playerNumber);
        players[index].fold();
        advanceTurn(index);
    }

    private int requireTurn(int playerNumber) {
        int index = playerIndex(playerNumber);
        if (getActivePlayer() != playerNumber) {
            throw new IllegalStateException("It is not this player's turn");
        }
        return index;
    }

    private void advanceTurn(int index) {
        if (index == 0) {
            phase = GamePhase.PLAYER_TWO_TURN;
        } else {
            phase = GamePhase.SHOWDOWN;
        }
    }

    public RoundResult resolveRound() {
        requirePhase(GamePhase.SHOWDOWN);
        PlayerRoundResult first = PayoutCalculator.settlePlayer(getPlayer(1), dealerHand);
        PlayerRoundResult second = PayoutCalculator.settlePlayer(getPlayer(2), dealerHand);
        RoundResult completed = new RoundResult(roundNumber, dealerHand, HandEvaluator.evaluate(dealerHand.getCards()), HandEvaluator.dealerQualifies(dealerHand.getCards()), first, second);

        players[0].addWinnings(first.netChange());
        players[1].addWinnings(second.netChange());
        carriedAntes[0] = first.carriedAnte();
        carriedAntes[1] = second.carriedAnte();
        result = completed;
        phase = GamePhase.RESULTS;
        return result;
    }

    public GamePhase getPhase() { return phase; }
    public int getRoundNumber() { return roundNumber; }

    public int getActivePlayer() {
        if (phase == GamePhase.PLAYER_ONE_TURN) { return 1; }
        if (phase == GamePhase.PLAYER_TWO_TURN) { return 2; }
        return 0;
    }

    public PlayerState getPlayer(int playerNumber) {
        int index = playerIndex(playerNumber);
        Player player = players[index];
        return new PlayerState(player.getName(), player.getHand(), player.getAnteWager(), player.getPairPlusWager(), player.getPlayWager(), player.getDecision(), player.getCumulativeWinnings(), confirmed[index], carriedAntes[index]);
    }

    public Hand getDealerHand() {
        if (phase == GamePhase.SHOWDOWN || phase == GamePhase.RESULTS) {
            return dealerHand;
        }
        return null;
    }

    public RoundResult getResult() { return result; }

    public GameRound getRound() {
        return new GameRound(roundNumber, phase, getPlayer(1), getPlayer(2), getDealerHand(), result);
    }

    private int playerIndex(int playerNumber) {
        if (playerNumber != 1 && playerNumber != 2) {
            throw new IllegalArgumentException("Player number must be 1 or 2");
        }
        return playerNumber - 1;
    }

    private void requirePhase(GamePhase required) {
        if (phase != required) {
            throw new IllegalStateException("Action requires phase " + required + "; current phase is " + phase);
        }
    }
}