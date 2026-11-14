package edu.uic.cs342.poker.service;

import edu.uic.cs342.poker.logic.HandRank;
import edu.uic.cs342.poker.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService service;

    // Like the controlled provider in WeatherServiceTest, this dealer returns known inputs.
    private static class FixedDealer implements HandDealer {
        private final Hand[][] rounds;
        private int round = -1;
        private int next;

        FixedDealer(Hand[][] rounds) { this.rounds = rounds; }

        @Override
        public void resetDeck() {
            round++;
            next = 0;
        }

        @Override
        public Hand dealHand() { return rounds[round][next++]; }
    }

    private static Hand hand(Suit first, int a, Suit second, int b, Suit third, int c) {
        return new Hand(List.of(new Card(first, a), new Card(second, b), new Card(third, c)));
    }

    private static Hand straight() {
        return hand(Suit.CLUBS, 5, Suit.DIAMONDS, 6, Suit.SPADES, 7);
    }

    private static Hand highCard() {
        return hand(Suit.HEARTS, 14, Suit.CLUBS, 8, Suit.SPADES, 2);
    }

    private static Hand qualifiedDealer() {
        return hand(Suit.HEARTS, 12, Suit.SPADES, 12, Suit.DIAMONDS, 4);
    }

    private static Hand unqualifiedDealer() {
        return hand(Suit.HEARTS, 11, Suit.CLUBS, 9, Suit.SPADES, 3);
    }

    private static Hand[] standardRound() {
        return new Hand[]{straight(), highCard(), qualifiedDealer()};
    }

    private static Hand[] nonqualifyingRound() {
        return new Hand[]{straight(), highCard(), unqualifiedDealer()};
    }

    @BeforeEach
    void setUp() {
        service = new GameService(new FixedDealer(new Hand[][]{standardRound(), standardRound()}));
    }

    private void confirmBoth() {
        service.confirmWagers(1, 10, 5);
        service.confirmWagers(2, 20, 10);
    }

    private void dealStandard() {
        service.startGame();
        confirmBoth();
        service.deal();
    }

    private RoundResult bothPlay() {
        service.play(1);
        service.play(2);
        return service.resolveRound();
    }

    @Test void startsReadyWithZeroWinnings() {
        assertEquals(GamePhase.READY, service.getPhase());
        assertEquals(0, service.getRoundNumber());
        assertEquals(0, service.getPlayer(1).cumulativeWinnings());
        assertEquals(0, service.getPlayer(2).cumulativeWinnings());
    }

    @Test void startGameBeginsFirstRound() {
        service.startGame();
        assertEquals(GamePhase.WAGERING, service.getPhase());
        assertEquals(1, service.getRoundNumber());
        assertFalse(service.canDeal());
    }

    @Test void rejectsMissingDealer() {
        assertThrows(IllegalArgumentException.class, () -> new GameService(null));
    }

    @Test void cannotRestartActiveGame() {
        service.startGame();
        assertThrows(IllegalStateException.class, () -> service.startGame());
        assertEquals(1, service.getRoundNumber());
    }

    @Test void cannotWagerBeforeStarting() {
        assertThrows(IllegalStateException.class, () -> service.confirmWagers(1, 10, 0));
    }

    @Test void invalidPlayerNumberRejected() {
        service.startGame();
        assertThrows(IllegalArgumentException.class, () -> service.confirmWagers(3, 10, 0));
        assertThrows(IllegalArgumentException.class, () -> service.getPlayer(0));
        assertFalse(service.canDeal());
    }

    @Test void validatesAnteWithoutChangingState() {
        service.startGame();
        assertThrows(IllegalArgumentException.class, () -> service.confirmWagers(1, 4, 5));
        assertThrows(IllegalArgumentException.class, () -> service.confirmWagers(1, 26, 5));
        assertEquals(0, service.getPlayer(1).anteWager());
        assertFalse(service.getPlayer(1).wagersConfirmed());
    }

    @Test void validatesPairPlusWithoutChangingState() {
        service.startGame();
        assertThrows(IllegalArgumentException.class, () -> service.confirmWagers(1, 10, 4));
        assertThrows(IllegalArgumentException.class, () -> service.confirmWagers(1, 10, 26));
        assertEquals(0, service.getPlayer(1).anteWager());
    }

    @Test void acceptsBoundaryAndOptionalWagers() {
        service.startGame();
        service.confirmWagers(1, 5, 0);
        service.confirmWagers(2, 25, 25);
        assertTrue(service.canDeal());
        assertEquals(0, service.getPlayer(1).pairPlusWager());
        assertEquals(25, service.getPlayer(2).anteWager());
    }

    @Test void oneConfirmationDoesNotEnableDeal() {
        service.startGame();
        service.confirmWagers(2, 10, 0);
        assertTrue(service.getPlayer(2).wagersConfirmed());
        assertFalse(service.getPlayer(1).wagersConfirmed());
        assertFalse(service.canDeal());
        assertThrows(IllegalStateException.class, () -> service.deal());
    }

    @Test void confirmedWagersCannotBeChanged() {
        service.startGame();
        service.confirmWagers(1, 10, 0);
        assertThrows(IllegalStateException.class, () -> service.confirmWagers(1, 15, 10));
        assertEquals(10, service.getPlayer(1).anteWager());
    }

    @Test void bothConfirmationsEnableDeal() {
        service.startGame();
        confirmBoth();
        assertTrue(service.canDeal());
    }

    @Test void dealingBeginsPlayerOneTurn() {
        dealStandard();
        assertEquals(GamePhase.PLAYER_ONE_TURN, service.getPhase());
        assertEquals(1, service.getActivePlayer());
        assertFalse(service.canDeal());
        assertEquals(3, service.getPlayer(1).hand().getCards().size());
        assertEquals(3, service.getPlayer(2).hand().getCards().size());
    }

    @Test void dealerHiddenUntilBothDecisions() {
        dealStandard();
        assertNull(service.getDealerHand());
        service.play(1);
        assertNull(service.getRound().dealerHand());
        service.fold(2);
        assertNotNull(service.getDealerHand());
    }

    @Test void realDealerDealsNineUniqueCards() {
        service = new GameService();
        dealStandard();
        service.play(1);
        service.play(2);
        ArrayList<Card> cards = new ArrayList<>();
        for (Hand hand : List.of(service.getPlayer(1).hand(), service.getPlayer(2).hand(), service.getDealerHand())) {
            for (Card card : hand.getCards()) {
                assertFalse(cards.contains(card));
                cards.add(card);
            }
        }
        assertEquals(9, cards.size());
    }

    @Test void duplicateDealerCardsDoNotPartiallyAssignHands() {
        service = new GameService(new FixedDealer(new Hand[][]{{straight(), straight(), qualifiedDealer()}}));
        service.startGame();
        confirmBoth();
        assertThrows(IllegalStateException.class, () -> service.deal());
        assertNull(service.getPlayer(1).hand());
        assertNull(service.getPlayer(2).hand());
        assertEquals(GamePhase.WAGERING, service.getPhase());
    }

    @Test void cannotDealTwiceOrChangeWagersAfterDeal() {
        dealStandard();
        assertThrows(IllegalStateException.class, () -> service.deal());
        assertThrows(IllegalStateException.class, () -> service.confirmWagers(1, 20, 5));
    }

    @Test void playAndFoldBeforeDealRejected() {
        service.startGame();
        confirmBoth();
        assertThrows(IllegalStateException.class, () -> service.play(1));
        assertThrows(IllegalStateException.class, () -> service.fold(1));
        assertEquals(PlayerDecision.UNDECIDED, service.getPlayer(1).decision());
    }

    @Test void playerTwoCannotActFirst() {
        dealStandard();
        assertThrows(IllegalStateException.class, () -> service.play(2));
        assertThrows(IllegalStateException.class, () -> service.fold(2));
        assertEquals(1, service.getActivePlayer());
    }

    @Test void playMatchesAnteAndAdvancesTurn() {
        dealStandard();
        service.play(1);
        assertEquals(10, service.getPlayer(1).playWager());
        assertEquals(PlayerDecision.PLAY, service.getPlayer(1).decision());
        assertEquals(2, service.getActivePlayer());
        assertEquals(0, service.getPlayer(2).playWager());
    }

    @Test void cannotDecideTwice() {
        dealStandard();
        service.fold(1);
        assertThrows(IllegalStateException.class, () -> service.play(1));
        assertEquals(PlayerDecision.FOLD, service.getPlayer(1).decision());
        assertEquals(0, service.getPlayer(1).playWager());
    }

    @Test void secondDecisionEntersShowdownWithoutSettlement() {
        dealStandard();
        service.play(1);
        service.fold(2);
        assertEquals(GamePhase.SHOWDOWN, service.getPhase());
        assertEquals(0, service.getActivePlayer());
        assertEquals(0, service.getPlayer(1).cumulativeWinnings());
        assertNull(service.getResult());
    }

    @Test void cannotSettleBeforeBothDecisions() {
        dealStandard();
        service.play(1);
        assertThrows(IllegalStateException.class, () -> service.resolveRound());
        assertEquals(0, service.getPlayer(1).cumulativeWinnings());
    }

    @Test void cannotStartNextRoundBeforeSettlement() {
        dealStandard();
        service.play(1);
        service.play(2);
        assertThrows(IllegalStateException.class, () -> service.nextRound());
        assertEquals(GamePhase.SHOWDOWN, service.getPhase());
    }

    @Test void independentlySettlesWinnerAndLoser() {
        dealStandard();
        RoundResult result = bothPlay();
        assertTrue(result.dealerQualifies());
        assertEquals(RoundOutcome.PLAYER_WINS, result.playerOne().outcome());
        assertEquals(RoundOutcome.DEALER_WINS, result.playerTwo().outcome());
        assertEquals(50, result.playerOne().netChange());
        assertEquals(-50, result.playerTwo().netChange());
        assertEquals(50, service.getPlayer(1).cumulativeWinnings());
        assertEquals(-50, service.getPlayer(2).cumulativeWinnings());
        assertEquals(GamePhase.RESULTS, service.getPhase());
    }

    @Test void resultPreservesWagersRanksAndBreakdown() {
        dealStandard();
        RoundResult result = bothPlay();
        PlayerRoundResult first = result.playerOne();
        assertEquals(HandRank.PAIR, result.dealerRank());
        assertEquals(HandRank.STRAIGHT, first.handRank());
        assertEquals(10, first.anteWager());
        assertEquals(10, first.playWager());
        assertEquals(5, first.pairPlusWager());
        assertEquals(10, first.anteNetChange());
        assertEquals(10, first.playNetChange());
        assertEquals(30, first.pairPlusNetChange());
        assertEquals(6, first.pairPlusMultiplier());
        assertEquals(0, first.previousCumulativeWinnings());
        assertEquals(50, first.updatedCumulativeWinnings());
    }

    @Test void foldForfeitsEvenWinningPairPlus() {
        dealStandard();
        service.fold(1);
        service.play(2);
        RoundResult result = service.resolveRound();
        assertEquals(RoundOutcome.FOLDED, result.playerOne().outcome());
        assertEquals(-10, result.playerOne().anteNetChange());
        assertEquals(-5, result.playerOne().pairPlusNetChange());
        assertEquals(0, result.playerOne().playNetChange());
        assertEquals(-15, result.playerOne().netChange());
        assertEquals(-50, result.playerTwo().netChange());
    }

    @Test void bothMayFold() {
        dealStandard();
        service.fold(1);
        service.fold(2);
        RoundResult result = service.resolveRound();
        assertEquals(-15, result.playerOne().netChange());
        assertEquals(-30, result.playerTwo().netChange());
        assertEquals(RoundOutcome.FOLDED, result.playerTwo().outcome());
    }

    @Test void pairPlusPaysDespiteLosingAgainstDealer() {
        Hand stronger = hand(Suit.DIAMONDS, 10, Suit.DIAMONDS, 11, Suit.DIAMONDS, 12);
        service = new GameService(new FixedDealer(new Hand[][]{{straight(), highCard(), stronger}}));
        dealStandard();
        RoundResult result = bothPlay();
        assertEquals(RoundOutcome.DEALER_WINS, result.playerOne().outcome());
        assertEquals(30, result.playerOne().pairPlusNetChange());
        assertEquals(10, result.playerOne().netChange());
    }

    @Test void tieReturnsAnteAndPlayButPairPlusStillSettles() {
        Hand player = hand(Suit.DIAMONDS, 12, Suit.CLUBS, 7, Suit.HEARTS, 3);
        Hand dealer = hand(Suit.CLUBS, 12, Suit.HEARTS, 7, Suit.SPADES, 3);
        service = new GameService(new FixedDealer(new Hand[][]{{player, highCard(), dealer}}));
        dealStandard();
        RoundResult result = bothPlay();
        assertEquals(RoundOutcome.TIE, result.playerOne().outcome());
        assertEquals(0, result.playerOne().anteNetChange());
        assertEquals(0, result.playerOne().playNetChange());
        assertEquals(-5, result.playerOne().netChange());
    }

    @Test void nonqualifyingDealerReturnsPlayAndCarriesAnte() {
        service = new GameService(new FixedDealer(new Hand[][]{nonqualifyingRound()}));
        dealStandard();
        RoundResult result = bothPlay();
        assertFalse(result.dealerQualifies());
        assertEquals(RoundOutcome.DEALER_NOT_QUALIFIED, result.playerOne().outcome());
        assertEquals(10, result.playerOne().carriedAnte());
        assertEquals(20, result.playerTwo().carriedAnte());
        assertEquals(0, result.playerOne().anteNetChange());
        assertEquals(0, result.playerOne().playNetChange());
        assertEquals(30, result.playerOne().netChange());
        assertEquals(-10, result.playerTwo().netChange());
    }

    @Test void foldedPlayerDoesNotCarryAnteWhenDealerFails() {
        service = new GameService(new FixedDealer(new Hand[][]{nonqualifyingRound()}));
        dealStandard();
        service.fold(1);
        service.play(2);
        RoundResult result = service.resolveRound();
        assertEquals(0, result.playerOne().carriedAnte());
        assertEquals(-15, result.playerOne().netChange());
        assertEquals(20, result.playerTwo().carriedAnte());
    }

    @Test void repeatedSettlementCannotDuplicateWinnings() {
        dealStandard();
        RoundResult result = bothPlay();
        assertThrows(IllegalStateException.class, () -> service.resolveRound());
        assertEquals(result, service.getResult());
        assertEquals(50, service.getPlayer(1).cumulativeWinnings());
    }

    @Test void nextRoundClearsRoundStateButPreservesWinnings() {
        dealStandard();
        RoundResult result = bothPlay();
        service.nextRound();
        assertEquals(2, service.getRoundNumber());
        assertEquals(GamePhase.WAGERING, service.getPhase());
        assertNull(service.getResult());
        assertNull(service.getDealerHand());
        assertNull(service.getPlayer(1).hand());
        assertEquals(0, service.getPlayer(1).anteWager());
        assertEquals(0, service.getPlayer(1).playWager());
        assertEquals(PlayerDecision.UNDECIDED, service.getPlayer(1).decision());
        assertFalse(service.getPlayer(1).wagersConfirmed());
        assertEquals(50, service.getPlayer(1).cumulativeWinnings());
        assertEquals(50, result.playerOne().updatedCumulativeWinnings());
    }

    @Test void twoSuccessiveRoundsAccumulateExactlyOnce() {
        dealStandard();
        bothPlay();
        service.nextRound();
        confirmBoth();
        service.deal();
        RoundResult result = bothPlay();
        assertEquals(50, result.playerOne().previousCumulativeWinnings());
        assertEquals(100, result.playerOne().updatedCumulativeWinnings());
        assertEquals(-100, result.playerTwo().updatedCumulativeWinnings());
    }

    @Test void carriedAnteCannotBeChangedInNextRound() {
        service = new GameService(new FixedDealer(new Hand[][]{nonqualifyingRound(), standardRound()}));
        dealStandard();
        bothPlay();
        service.nextRound();
        assertEquals(10, service.getPlayer(1).carriedAnte());
        assertThrows(IllegalArgumentException.class, () -> service.confirmWagers(1, 15, 0));
        assertFalse(service.getPlayer(1).wagersConfirmed());
        service.confirmWagers(1, 10, 0);
        assertTrue(service.getPlayer(1).wagersConfirmed());
    }

    @Test void carriedAnteSettlesOnceInLaterQualifiedRound() {
        service = new GameService(new FixedDealer(new Hand[][]{nonqualifyingRound(), standardRound()}));
        dealStandard();
        bothPlay();
        service.nextRound();
        service.confirmWagers(1, 10, 0);
        service.confirmWagers(2, 20, 0);
        service.deal();
        RoundResult result = bothPlay();
        assertEquals(50, result.playerOne().updatedCumulativeWinnings());
        assertEquals(-50, result.playerTwo().updatedCumulativeWinnings());
        assertEquals(0, service.getPlayer(1).carriedAnte());
        assertEquals(0, service.getPlayer(2).carriedAnte());
    }

    @Test void carriedAnteLostOnceWhenPlayerFoldsNextRound() {
        service = new GameService(new FixedDealer(new Hand[][]{nonqualifyingRound(), nonqualifyingRound()}));
        dealStandard();
        bothPlay();
        service.nextRound();
        service.confirmWagers(1, 10, 0);
        service.confirmWagers(2, 20, 0);
        service.deal();
        service.fold(1);
        service.play(2);
        RoundResult result = service.resolveRound();
        assertEquals(20, result.playerOne().updatedCumulativeWinnings());
        assertEquals(0, result.playerOne().carriedAnte());
        assertEquals(20, result.playerTwo().carriedAnte());
    }

    @Test void roundSnapshotDoesNotChangeAfterNewActions() {
        service.startGame();
        GameRound snapshot = service.getRound();
        confirmBoth();
        assertFalse(snapshot.playerOne().wagersConfirmed());
        assertEquals(0, snapshot.playerOne().anteWager());
        assertTrue(service.getRound().playerOne().wagersConfirmed());
    }
    @Test void bothPlayersCanWinAgainstSameDealer() {
        Hand second = hand(Suit.HEARTS, 8, Suit.DIAMONDS, 8, Suit.SPADES, 8);
        service = new GameService(new FixedDealer(new Hand[][]{{straight(), second, qualifiedDealer()}}));
        dealStandard();
        RoundResult result = bothPlay();
        assertEquals(RoundOutcome.PLAYER_WINS, result.playerOne().outcome());
        assertEquals(RoundOutcome.PLAYER_WINS, result.playerTwo().outcome());
        assertEquals(340, result.playerTwo().netChange());
    }

    @Test void optionalPairPlusHasNoEffectOnSettlement() {
        service.startGame();
        service.confirmWagers(1, 5, 0);
        service.confirmWagers(2, 25, 0);
        service.deal();
        RoundResult result = bothPlay();
        assertEquals(0, result.playerOne().pairPlusNetChange());
        assertEquals(0, result.playerTwo().pairPlusNetChange());
        assertEquals(10, result.playerOne().netChange());
        assertEquals(-50, result.playerTwo().netChange());
    }

    @Test void missingDealerHandLeavesWageringStateUnchanged() {
        service = new GameService(new FixedDealer(new Hand[][]{{straight(), highCard(), null}}));
        service.startGame();
        confirmBoth();
        assertThrows(IllegalStateException.class, () -> service.deal());
        assertTrue(service.canDeal());
        assertNull(service.getPlayer(1).hand());
        assertNull(service.getPlayer(2).hand());
    }

    @Test void carryDoesNotAccumulateOnRepeatedNonqualifyingRounds() {
        service = new GameService(new FixedDealer(new Hand[][]{nonqualifyingRound(), nonqualifyingRound()}));
        dealStandard();
        bothPlay();
        service.nextRound();
        service.confirmWagers(1, 10, 0);
        service.confirmWagers(2, 20, 0);
        service.deal();
        RoundResult result = bothPlay();
        assertEquals(10, result.playerOne().carriedAnte());
        assertEquals(20, result.playerTwo().carriedAnte());
        assertEquals(30, result.playerOne().updatedCumulativeWinnings());
        assertEquals(-10, result.playerTwo().updatedCumulativeWinnings());
    }
}
