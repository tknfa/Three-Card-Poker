package edu.uic.cs342.poker.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void startsWith52Cards() {
        assertEquals(52, deck.size());
    }

    @Test
    void containsEverySuitAndValueExactlyOnce() {
        List<Card> cards = deck.getCards();
        for (Suit suit : Suit.values()) {
            for (int value = 2; value <= 14; value++) {
                int count = 0;
                for (Card card : cards) {
                    if (card.suit() == suit && card.value() == value) {
                        count++;
                    }
                }
                assertEquals(1, count);
            }
        }
    }

    @Test
    void drawingRemovesOneCard() {
        Card drawn = deck.drawCard();
        assertEquals(51, deck.size());
        assertFalse(deck.getCards().contains(drawn));
    }

    @Test
    void drawingEntireDeckNeverRepeatsACard() {
        ArrayList<Card> drawn = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();
            assertFalse(drawn.contains(card));
            drawn.add(card);
        }
        assertEquals(0, deck.size());
        assertEquals(52, drawn.size());
    }

    @Test
    void emptyDeckRejectsAnotherDraw() {
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }
        assertThrows(IllegalStateException.class, () -> deck.drawCard());
        assertEquals(0, deck.size());
    }

    @Test
    void resetRestoresOriginalCardsAfterDrawing() {
        List<Card> original = deck.getCards();
        for (int i = 0; i < 9; i++) {
            deck.drawCard();
        }
        deck.reset();
        assertEquals(52, deck.size());
        assertIterableEquals(original, deck.getCards());
    }

    @Test
    void repeatedResetDoesNotAddExtraCards() {
        List<Card> original = deck.getCards();
        deck.reset();
        deck.reset();
        assertEquals(52, deck.size());
        assertIterableEquals(original, deck.getCards());
    }

    @Test
    void shufflePreservesAllCards() {
        List<Card> original = deck.getCards();
        deck.shuffle();
        assertEquals(52, deck.size());
        for (Card card : original) {
            assertTrue(deck.getCards().contains(card));
        }
    }

    @Test
    void shuffleDoesNotRestoreAlreadyDrawnCard() {
        Card drawn = deck.drawCard();
        deck.shuffle();
        assertEquals(51, deck.size());
        assertFalse(deck.getCards().contains(drawn));
    }

    @Test
    void returnedCardsCannotModifyDeck() {
        List<Card> snapshot = deck.getCards();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.clear());
        deck.drawCard();
        assertEquals(52, snapshot.size());
        assertEquals(51, deck.size());
    }
}