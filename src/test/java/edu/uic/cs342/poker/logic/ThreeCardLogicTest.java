package edu.uic.cs342.poker.logic;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Suit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThreeCardLogicTest {
    private ArrayList<Card> hand(Card first, Card second, Card third) {
        return new ArrayList<>(List.of(first, second, third));
    }

    // These numeric expectations use the temporary assignment-list numbering.
    // Update them when the original project's return codes are confirmed.
    @Test
    void evaluatesStraightFlush() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6), new Card(Suit.HEARTS, 7));
        assertEquals(1, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesThreeOfAKind() {
        ArrayList<Card> cards = hand(new Card(Suit.CLUBS, 9),
                new Card(Suit.HEARTS, 9), new Card(Suit.SPADES, 9));
        assertEquals(2, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesStraight() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.CLUBS, 6), new Card(Suit.SPADES, 7));
        assertEquals(3, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesFlush() {
        ArrayList<Card> cards = hand(new Card(Suit.DIAMONDS, 2),
                new Card(Suit.DIAMONDS, 8), new Card(Suit.DIAMONDS, 13));
        assertEquals(4, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesPair() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 10),
                new Card(Suit.SPADES, 10), new Card(Suit.CLUBS, 4));
        assertEquals(5, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesHighCard() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 2),
                new Card(Suit.CLUBS, 8), new Card(Suit.DIAMONDS, 13));
        assertEquals(6, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesLowestConsecutiveNumericStraight() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 2),
                new Card(Suit.CLUBS, 3), new Card(Suit.DIAMONDS, 4));
        assertEquals(3, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesAceHighStraight() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 12),
                new Card(Suit.CLUBS, 13), new Card(Suit.DIAMONDS, 14));
        assertEquals(3, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesAceHighStraightFlush() {
        ArrayList<Card> cards = hand(new Card(Suit.SPADES, 12),
                new Card(Suit.SPADES, 13), new Card(Suit.SPADES, 14));
        assertEquals(1, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluatesLowestConsecutiveNumericStraightFlush() {
        ArrayList<Card> cards = hand(new Card(Suit.CLUBS, 2),
                new Card(Suit.CLUBS, 3), new Card(Suit.CLUBS, 4));
        assertEquals(1, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void recognizesPairInFirstAndThirdPositions() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 7),
                new Card(Suit.CLUBS, 11), new Card(Suit.DIAMONDS, 7));
        assertEquals(5, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void recognizesPairInSecondAndThirdPositions() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 7),
                new Card(Suit.CLUBS, 11), new Card(Suit.DIAMONDS, 11));
        assertEquals(5, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void repeatedRankWithinTwoValueSpanIsNotStraight() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 6),
                new Card(Suit.CLUBS, 6), new Card(Suit.DIAMONDS, 8));
        assertEquals(5, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void threeAcesAreNotReducedToPair() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.CLUBS, 14), new Card(Suit.DIAMONDS, 14));
        assertEquals(2, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void sameSuitWithGapIsOnlyFlush() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6), new Card(Suit.HEARTS, 8));
        assertEquals(4, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void twoMatchingSuitsWithGapAreHighCard() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6), new Card(Suit.SPADES, 8));
        assertEquals(6, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void allSixOrdersOfStraightHaveSameClassification() {
        Card first = new Card(Suit.CLUBS, 5);
        Card second = new Card(Suit.HEARTS, 6);
        Card third = new Card(Suit.SPADES, 7);

        assertEquals(3, ThreeCardLogic.evalHand(hand(first, second, third)));
        assertEquals(3, ThreeCardLogic.evalHand(hand(first, third, second)));
        assertEquals(3, ThreeCardLogic.evalHand(hand(second, first, third)));
        assertEquals(3, ThreeCardLogic.evalHand(hand(second, third, first)));
        assertEquals(3, ThreeCardLogic.evalHand(hand(third, first, second)));
        assertEquals(3, ThreeCardLogic.evalHand(hand(third, second, first)));
    }

    @Test
    void unsortedStraightFlushIsStillStraightFlush() {
        ArrayList<Card> cards = hand(new Card(Suit.DIAMONDS, 10),
                new Card(Suit.DIAMONDS, 8), new Card(Suit.DIAMONDS, 9));
        assertEquals(1, ThreeCardLogic.evalHand(cards));
    }

    @Test
    void evaluationDoesNotChangeInputCardOrder() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 13),
                new Card(Suit.CLUBS, 11), new Card(Suit.DIAMONDS, 12));
        List<Card> original = List.copyOf(cards);

        ThreeCardLogic.evalHand(cards);

        assertIterableEquals(original, cards);
    }

    @Test
    void rejectsNullHand() {
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalHand(null));
    }

    @Test
    void rejectsTooFewCards() {
        ArrayList<Card> cards = new ArrayList<>(List.of(new Card(Suit.HEARTS, 5),
                new Card(Suit.CLUBS, 6)));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalHand(cards));
    }

    @Test
    void rejectsTooManyCards() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.CLUBS, 6), new Card(Suit.DIAMONDS, 7));
        cards.add(new Card(Suit.SPADES, 8));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalHand(cards));
    }

    @Test
    void rejectsDuplicatePhysicalCard() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 5), new Card(Suit.DIAMONDS, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalHand(cards));
    }

    @Test
    void rejectsMissingCard() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.CLUBS, 6), new Card(Suit.DIAMONDS, 7));
        cards.set(1, null);
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalHand(cards));
    }

    @Test
    void straightFlushBeatsThreeOfAKind() {
        ArrayList<Card> stronger = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        ArrayList<Card> weaker = hand(new Card(Suit.CLUBS, 9),
                new Card(Suit.DIAMONDS, 9),
                new Card(Suit.SPADES, 9));

        assertEquals(2, ThreeCardLogic.compareHands(weaker, stronger));
        assertEquals(1, ThreeCardLogic.compareHands(stronger, weaker));
    }

    @Test
    void threeOfAKindBeatsStraight() {
        ArrayList<Card> stronger = hand(new Card(Suit.CLUBS, 9),
                new Card(Suit.DIAMONDS, 9),
                new Card(Suit.SPADES, 9));
        ArrayList<Card> weaker = hand(new Card(Suit.CLUBS, 10),
                new Card(Suit.HEARTS, 11),
                new Card(Suit.SPADES, 12));

        assertEquals(2, ThreeCardLogic.compareHands(weaker, stronger));
        assertEquals(1, ThreeCardLogic.compareHands(stronger, weaker));
    }

    @Test
    void straightBeatsFlush() {
        ArrayList<Card> stronger = hand(new Card(Suit.CLUBS, 10),
                new Card(Suit.HEARTS, 11),
                new Card(Suit.SPADES, 12));
        ArrayList<Card> weaker = hand(new Card(Suit.DIAMONDS, 2),
                new Card(Suit.DIAMONDS, 8),
                new Card(Suit.DIAMONDS, 13));

        assertEquals(2, ThreeCardLogic.compareHands(weaker, stronger));
        assertEquals(1, ThreeCardLogic.compareHands(stronger, weaker));
    }

    @Test
    void flushBeatsPair() {
        ArrayList<Card> stronger = hand(new Card(Suit.DIAMONDS, 2),
                new Card(Suit.DIAMONDS, 8),
                new Card(Suit.DIAMONDS, 13));
        ArrayList<Card> weaker = hand(new Card(Suit.CLUBS, 4),
                new Card(Suit.SPADES, 4),
                new Card(Suit.HEARTS, 14));

        assertEquals(2, ThreeCardLogic.compareHands(weaker, stronger));
        assertEquals(1, ThreeCardLogic.compareHands(stronger, weaker));
    }

    @Test
    void pairBeatsHighCard() {
        ArrayList<Card> stronger = hand(new Card(Suit.CLUBS, 4),
                new Card(Suit.SPADES, 4),
                new Card(Suit.HEARTS, 14));
        ArrayList<Card> weaker = hand(new Card(Suit.CLUBS, 2),
                new Card(Suit.HEARTS, 8),
                new Card(Suit.SPADES, 13));

        assertEquals(2, ThreeCardLogic.compareHands(weaker, stronger));
        assertEquals(1, ThreeCardLogic.compareHands(stronger, weaker));
    }

    @Test
    void higherStraightFlushWins() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 2),
                new Card(Suit.HEARTS, 3),
                new Card(Suit.HEARTS, 4));
        ArrayList<Card> player = hand(new Card(Suit.SPADES, 5),
                new Card(Suit.SPADES, 6),
                new Card(Suit.SPADES, 7));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void higherThreeOfAKindWins() {
        ArrayList<Card> dealer = hand(new Card(Suit.CLUBS, 9),
                new Card(Suit.HEARTS, 9),
                new Card(Suit.SPADES, 9));
        ArrayList<Card> player = hand(new Card(Suit.CLUBS, 10),
                new Card(Suit.HEARTS, 10),
                new Card(Suit.DIAMONDS, 10));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void aceHighStraightBeatsKingHighStraight() {
        ArrayList<Card> dealer = hand(new Card(Suit.CLUBS, 11),
                new Card(Suit.HEARTS, 12),
                new Card(Suit.DIAMONDS, 13));
        ArrayList<Card> player = hand(new Card(Suit.CLUBS, 12),
                new Card(Suit.HEARTS, 13),
                new Card(Suit.SPADES, 14));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void higherFlushTopCardWins() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 13),
                new Card(Suit.HEARTS, 8),
                new Card(Suit.HEARTS, 2));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 14),
                new Card(Suit.DIAMONDS, 5),
                new Card(Suit.DIAMONDS, 3));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void flushSecondCardBreaksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.HEARTS, 8),
                new Card(Suit.HEARTS, 3));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 14),
                new Card(Suit.DIAMONDS, 9),
                new Card(Suit.DIAMONDS, 2));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void flushThirdCardBreaksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.HEARTS, 9),
                new Card(Suit.HEARTS, 3));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 14),
                new Card(Suit.DIAMONDS, 9),
                new Card(Suit.DIAMONDS, 2));

        assertEquals(1, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void higherPairBeatsLowerPairRegardlessOfKicker() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 7),
                new Card(Suit.SPADES, 7),
                new Card(Suit.DIAMONDS, 14));
        ArrayList<Card> player = hand(new Card(Suit.HEARTS, 8),
                new Card(Suit.SPADES, 8),
                new Card(Suit.DIAMONDS, 2));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void pairKickerBreaksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 7),
                new Card(Suit.SPADES, 7),
                new Card(Suit.DIAMONDS, 14));
        ArrayList<Card> player = hand(new Card(Suit.CLUBS, 7),
                new Card(Suit.DIAMONDS, 7),
                new Card(Suit.HEARTS, 13));

        assertEquals(1, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void highCardTopRankBreaksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 13),
                new Card(Suit.SPADES, 9),
                new Card(Suit.CLUBS, 2));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 14),
                new Card(Suit.SPADES, 5),
                new Card(Suit.CLUBS, 3));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void highCardSecondRankBreaksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.SPADES, 8),
                new Card(Suit.CLUBS, 3));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 14),
                new Card(Suit.SPADES, 9),
                new Card(Suit.CLUBS, 2));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void highCardThirdRankBreaksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.SPADES, 9),
                new Card(Suit.CLUBS, 3));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 14),
                new Card(Suit.CLUBS, 9),
                new Card(Suit.SPADES, 2));

        assertEquals(1, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void equalHighCardRanksTieRegardlessOfSuit() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.SPADES, 9),
                new Card(Suit.CLUBS, 3));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 3),
                new Card(Suit.DIAMONDS, 14),
                new Card(Suit.CLUBS, 9));

        assertEquals(0, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void equalPairAndKickerTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 7),
                new Card(Suit.SPADES, 7),
                new Card(Suit.DIAMONDS, 14));
        ArrayList<Card> player = hand(new Card(Suit.CLUBS, 14),
                new Card(Suit.CLUBS, 7),
                new Card(Suit.DIAMONDS, 7));

        assertEquals(0, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void equalStraightRanksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 4),
                new Card(Suit.SPADES, 5),
                new Card(Suit.CLUBS, 6));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 6),
                new Card(Suit.CLUBS, 4),
                new Card(Suit.HEARTS, 5));

        assertEquals(0, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void equalFlushRanksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.HEARTS, 9),
                new Card(Suit.HEARTS, 3));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 3),
                new Card(Suit.DIAMONDS, 14),
                new Card(Suit.DIAMONDS, 9));

        assertEquals(0, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void equalStraightFlushRanksTie() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        ArrayList<Card> player = hand(new Card(Suit.SPADES, 7),
                new Card(Suit.SPADES, 5),
                new Card(Suit.SPADES, 6));

        assertEquals(0, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void comparisonIsIndependentOfQualification() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 11),
                new Card(Suit.SPADES, 8),
                new Card(Suit.CLUBS, 3));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 10),
                new Card(Suit.SPADES, 7),
                new Card(Suit.CLUBS, 2));

        assertEquals(1, ThreeCardLogic.compareHands(dealer, player));
    }

    @Test
    void twoPlayersCanHaveDifferentOutcomesAgainstSameDealer() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 12),
                new Card(Suit.SPADES, 12),
                new Card(Suit.DIAMONDS, 4));
        ArrayList<Card> playerOne = hand(new Card(Suit.CLUBS, 5),
                new Card(Suit.DIAMONDS, 6),
                new Card(Suit.SPADES, 7));
        ArrayList<Card> playerTwo = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.CLUBS, 8),
                new Card(Suit.SPADES, 2));

        assertEquals(2, ThreeCardLogic.compareHands(dealer, playerOne));
        assertEquals(1, ThreeCardLogic.compareHands(dealer, playerTwo));
    }

    @Test
    void comparisonPreservesBothHands() {
        ArrayList<Card> dealer = hand(new Card(Suit.CLUBS, 3),
                new Card(Suit.SPADES, 14),
                new Card(Suit.HEARTS, 8));
        ArrayList<Card> player = hand(new Card(Suit.DIAMONDS, 14),
                new Card(Suit.SPADES, 7),
                new Card(Suit.HEARTS, 7));
        List<Card> originalDealer = List.copyOf(dealer);
        List<Card> originalPlayer = List.copyOf(player);

        ThreeCardLogic.compareHands(dealer, player);

        assertIterableEquals(originalDealer, dealer);
        assertIterableEquals(originalPlayer, player);
    }

    @Test
    void comparisonRejectsInvalidDealer() {
        ArrayList<Card> player = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.compareHands(null, player));
    }

    @Test
    void comparisonRejectsInvalidPlayer() {
        ArrayList<Card> dealer = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.compareHands(dealer, new ArrayList<>()));
    }

    @Test
    void pairPlusStraightFlushWinnings() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertEquals(400, ThreeCardLogic.evalPPWinnings(cards, 10));
    }

    @Test
    void pairPlusThreeOfAKindWinnings() {
        ArrayList<Card> cards = hand(new Card(Suit.CLUBS, 9),
                new Card(Suit.DIAMONDS, 9),
                new Card(Suit.SPADES, 9));
        assertEquals(300, ThreeCardLogic.evalPPWinnings(cards, 10));
    }

    @Test
    void pairPlusStraightWinnings() {
        ArrayList<Card> cards = hand(new Card(Suit.CLUBS, 10),
                new Card(Suit.HEARTS, 11),
                new Card(Suit.SPADES, 12));
        assertEquals(60, ThreeCardLogic.evalPPWinnings(cards, 10));
    }

    @Test
    void pairPlusFlushWinnings() {
        ArrayList<Card> cards = hand(new Card(Suit.DIAMONDS, 2),
                new Card(Suit.DIAMONDS, 8),
                new Card(Suit.DIAMONDS, 13));
        assertEquals(30, ThreeCardLogic.evalPPWinnings(cards, 10));
    }

    @Test
    void pairPlusPairWinnings() {
        ArrayList<Card> cards = hand(new Card(Suit.CLUBS, 4),
                new Card(Suit.SPADES, 4),
                new Card(Suit.HEARTS, 14));
        assertEquals(10, ThreeCardLogic.evalPPWinnings(cards, 10));
    }

    @Test
    void pairPlusHighCardWinnings() {
        ArrayList<Card> cards = hand(new Card(Suit.CLUBS, 2),
                new Card(Suit.HEARTS, 8),
                new Card(Suit.SPADES, 13));
        assertEquals(0, ThreeCardLogic.evalPPWinnings(cards, 10));
    }

    @Test
    void minimumPairPlusWager() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertEquals(200, ThreeCardLogic.evalPPWinnings(cards, 5));
    }

    @Test
    void maximumPairPlusWager() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertEquals(1000, ThreeCardLogic.evalPPWinnings(cards, 25));
    }

    @Test
    void optionalPairPlusOmitted() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertEquals(0, ThreeCardLogic.evalPPWinnings(cards, 0));
    }

    @Test
    void rejectsNegativePairPlusWager() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalPPWinnings(cards, -5));
    }

    @Test
    void rejectsBelowMinimumPairPlusWager() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalPPWinnings(cards, 4));
    }

    @Test
    void rejectsAboveMaximumPairPlusWager() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalPPWinnings(cards, 26));
    }

    @Test
    void pairPlusRejectsInvalidHand() {
        assertThrows(IllegalArgumentException.class,
                () -> ThreeCardLogic.evalPPWinnings(null, 10));
    }

    @Test
    void pairPlusDoesNotChangeInput() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 9),
                new Card(Suit.HEARTS, 7),
                new Card(Suit.HEARTS, 8));
        List<Card> original = List.copyOf(cards);

        ThreeCardLogic.evalPPWinnings(cards, 10);

        assertIterableEquals(original, cards);
    }

    @Test
    void jackHighDoesNotQualify() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 11),
                new Card(Suit.SPADES, 8),
                new Card(Suit.CLUBS, 3));
        assertFalse(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void queenHighQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 12),
                new Card(Suit.SPADES, 8),
                new Card(Suit.CLUBS, 3));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void kingHighQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 13),
                new Card(Suit.SPADES, 8),
                new Card(Suit.CLUBS, 3));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void aceHighQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 14),
                new Card(Suit.SPADES, 8),
                new Card(Suit.CLUBS, 3));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void lowPairQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 2),
                new Card(Suit.SPADES, 2),
                new Card(Suit.CLUBS, 3));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void lowFlushQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.DIAMONDS, 2),
                new Card(Suit.DIAMONDS, 4),
                new Card(Suit.DIAMONDS, 8));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void lowStraightQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 2),
                new Card(Suit.SPADES, 3),
                new Card(Suit.CLUBS, 4));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void lowThreeOfAKindQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 2),
                new Card(Suit.SPADES, 2),
                new Card(Suit.CLUBS, 2));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void lowStraightFlushQualifies() {
        ArrayList<Card> cards = hand(new Card(Suit.DIAMONDS, 2),
                new Card(Suit.DIAMONDS, 3),
                new Card(Suit.DIAMONDS, 4));
        assertTrue(HandEvaluator.dealerQualifies(cards));
    }

    @Test
    void winningPairPlusNetChangeExcludesReturnedStake() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertEquals(400, PayoutCalculator.pairPlusNetChange(cards, 10));
    }

    @Test
    void losingPairPlusNetChangeDebitsWager() {
        ArrayList<Card> cards = hand(new Card(Suit.CLUBS, 2),
                new Card(Suit.HEARTS, 8),
                new Card(Suit.SPADES, 13));
        assertEquals(-10, PayoutCalculator.pairPlusNetChange(cards, 10));
    }

    @Test
    void omittedPairPlusHasZeroNetChange() {
        ArrayList<Card> cards = hand(new Card(Suit.HEARTS, 5),
                new Card(Suit.HEARTS, 6),
                new Card(Suit.HEARTS, 7));
        assertEquals(0, PayoutCalculator.pairPlusNetChange(cards, 0));
    }
}
