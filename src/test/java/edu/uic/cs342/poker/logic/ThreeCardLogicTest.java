package edu.uic.cs342.poker.logic;

import edu.uic.cs342.poker.logic.ThreeCardLogic;
import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Suit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static edu.uic.cs342.poker.model.Suit.*;

class ThreeCardLogicTest {

    private ArrayList<Card> straightFlush;
    private ArrayList<Card> threeOfAKind;
    private ArrayList<Card> straight;
    private ArrayList<Card> flush;
    private ArrayList<Card> pair;
    private ArrayList<Card> highCard;

    @BeforeEach
    void setUp() {
        straightFlush = cards(HEARTS, 7, HEARTS, 5, HEARTS, 6);
        threeOfAKind = cards(CLUBS, 9, HEARTS, 9, SPADES, 9);
        straight = cards(CLUBS, 5, DIAMONDS, 6, SPADES, 7);
        flush = cards(DIAMONDS, 2, DIAMONDS, 8, DIAMONDS, 12);
        pair = cards(HEARTS, 12, DIAMONDS, 4, SPADES, 12);
        highCard = cards(HEARTS, 14, CLUBS, 8, SPADES, 2);
    }

    private ArrayList<Card> cards(Suit s1, int v1, Suit s2, int v2, Suit s3, int v3) {
        return new ArrayList<>(List.of(new Card(s1, v1), new Card(s2, v2), new Card(s3, v3)));
    }

    @Test
    void identifiesStraightFlush() {
        int result = ThreeCardLogic.evalHand(straightFlush);
        assertEquals(1, result);
    }

    @Test
    void identifiesThreeOfAKind() {
        int result = ThreeCardLogic.evalHand(threeOfAKind);
        assertEquals(2, result);
    }

    @Test
    void identifiesStraight() {
        assertEquals(3, ThreeCardLogic.evalHand(straight));
        assertEquals(3, ThreeCardLogic.evalHand(cards(CLUBS, 2, HEARTS, 3, SPADES, 4)));

        ArrayList<Card> aceHigh = cards(CLUBS, 14, HEARTS, 12, SPADES, 13);
        ArrayList<Card> original = new ArrayList<>(aceHigh);

        assertEquals(3, ThreeCardLogic.evalHand(aceHigh));
        assertEquals(original, aceHigh);
    }

    @Test
    void identifiesFlush() {
        int result = ThreeCardLogic.evalHand(flush);
        assertEquals(4, result);
    }

    @Test
    void identifiesPair() {
        int result = ThreeCardLogic.evalHand(pair);
        assertEquals(5, result);
    }

    @Test
    void identifiesHighCard() {
        int result = ThreeCardLogic.evalHand(highCard);
        assertEquals(6, result);
    }

    @Test
    void strongerHandCategoryWins() {
        assertEquals(2, ThreeCardLogic.compareHands(threeOfAKind, straightFlush));
        assertEquals(2, ThreeCardLogic.compareHands(straight, threeOfAKind));
        assertEquals(2, ThreeCardLogic.compareHands(flush, straight));
        assertEquals(2, ThreeCardLogic.compareHands(pair, flush));
        assertEquals(2, ThreeCardLogic.compareHands(highCard, pair));
        assertEquals(1, ThreeCardLogic.compareHands(pair, highCard));
    }

    @Test
    void higherStraightFlushWins() {
        ArrayList<Card> player = cards(CLUBS, 8, CLUBS, 9, CLUBS, 10);

        assertEquals(2, ThreeCardLogic.compareHands(straightFlush, player));
    }

    @Test
    void higherThreeOfAKindWins() {
        ArrayList<Card> player = cards(CLUBS, 10, HEARTS, 10, SPADES, 10);

        assertEquals(2, ThreeCardLogic.compareHands(threeOfAKind, player));
    }

    @Test
    void higherStraightWins() {
        ArrayList<Card> player = cards(CLUBS, 12, HEARTS, 13, SPADES, 14);

        assertEquals(2, ThreeCardLogic.compareHands(straight, player));
    }

    @Test
    void flushComparisonUsesAllThreeRanks() {
        assertEquals(2, ThreeCardLogic.compareHands(flush, cards(CLUBS, 2, CLUBS, 6, CLUBS, 13)));
        assertEquals(2, ThreeCardLogic.compareHands(flush, cards(CLUBS, 2, CLUBS, 9, CLUBS, 12)));
        assertEquals(2, ThreeCardLogic.compareHands(flush, cards(CLUBS, 3, CLUBS, 8, CLUBS, 12)));
    }

    @Test
    void pairComparisonUsesPairRankThenKicker() {
        ArrayList<Card> dealer = cards(CLUBS, 12, HEARTS, 12, SPADES, 14);
        ArrayList<Card> player = cards(CLUBS, 13, HEARTS, 13, SPADES, 2);

        assertEquals(2, ThreeCardLogic.compareHands(dealer, player));

        ArrayList<Card> higherKicker = cards(CLUBS, 12, DIAMONDS, 12, SPADES, 5);

        assertEquals(2, ThreeCardLogic.compareHands(pair, higherKicker));
    }

    @Test
    void highCardComparisonUsesAllThreeRanks() {
        ArrayList<Card> dealer = cards(CLUBS, 13, HEARTS, 8, DIAMONDS, 2);

        assertEquals(2, ThreeCardLogic.compareHands(dealer, highCard));
        assertEquals(2, ThreeCardLogic.compareHands(highCard, cards(CLUBS, 14, HEARTS, 9, DIAMONDS, 2)));
        assertEquals(2, ThreeCardLogic.compareHands(highCard, cards(CLUBS, 14, HEARTS, 8, DIAMONDS, 3)));
    }

    @Test
    void equivalentHandsTieRegardlessOfSuit() {
        assertEquals(0, ThreeCardLogic.compareHands(highCard, cards(CLUBS, 14, HEARTS, 8, DIAMONDS, 2)));
        assertEquals(0, ThreeCardLogic.compareHands(pair, cards(CLUBS, 12, DIAMONDS, 12, HEARTS, 4)));
        assertEquals(0, ThreeCardLogic.compareHands(straight, cards(HEARTS, 5, CLUBS, 6, DIAMONDS, 7)));
        assertEquals(0, ThreeCardLogic.compareHands(flush, cards(CLUBS, 2, CLUBS, 8, CLUBS, 12)));
        assertEquals(0, ThreeCardLogic.compareHands(threeOfAKind, cards(CLUBS, 9, HEARTS, 9, DIAMONDS, 9)));
        assertEquals(0, ThreeCardLogic.compareHands(straightFlush, cards(CLUBS, 5, CLUBS, 6, CLUBS, 7)));
    }

    @Test
    void straightFlushPaysFortyToOne() {
        int result = ThreeCardLogic.evalPPWinnings(straightFlush, 10);
        assertEquals(400, result);
    }

    @Test
    void threeOfAKindPaysThirtyToOne() {
        int result = ThreeCardLogic.evalPPWinnings(threeOfAKind, 10);
        assertEquals(300, result);
    }

    @Test
    void straightPaysSixToOne() {
        int result = ThreeCardLogic.evalPPWinnings(straight, 10);
        assertEquals(60, result);
    }

    @Test
    void flushPaysThreeToOne() {
        int result = ThreeCardLogic.evalPPWinnings(flush, 10);
        assertEquals(30, result);
    }

    @Test
    void pairPaysOneToOne() {
        int result = ThreeCardLogic.evalPPWinnings(pair, 10);
        assertEquals(10, result);
    }

    @Test
    void highCardHasNoPairPlusPayout() {
        int result = ThreeCardLogic.evalPPWinnings(highCard, 10);
        assertEquals(0, result);
    }

    @Test
    void rejectsInvalidHands() {
        ArrayList<Card> duplicate = new ArrayList<>(straight);
        duplicate.set(2, duplicate.get(0));

        assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalHand(duplicate));
        assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalHand(new ArrayList<>()));
        assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalHand(null));
    }

    @Test
    void validatesPairPlusWagerBoundaries() {
        assertEquals(0, ThreeCardLogic.evalPPWinnings(pair, 0));
        assertEquals(200, ThreeCardLogic.evalPPWinnings(straightFlush, 5));
        assertEquals(1000, ThreeCardLogic.evalPPWinnings(straightFlush, 25));

        assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalPPWinnings(pair, -1));
        assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalPPWinnings(pair, 4));
        assertThrows(IllegalArgumentException.class, () -> ThreeCardLogic.evalPPWinnings(pair, 26));
    }
}
