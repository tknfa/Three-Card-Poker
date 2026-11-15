package edu.uic.cs342.poker.model;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Deck;
import edu.uic.cs342.poker.model.Suit;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void newDeckContainsAllFiftyTwoCards() {
        Deck deck = new Deck();

        assertEquals(52, deck.size());

        for (Suit suit : Suit.values()) {
            for (int value = 2; value <= 14; value++) {
                assertTrue(deck.getCards().contains(new Card(suit, value)));
            }
        }
    }

    @Test
    void drawingCardRemovesItFromDeck() {
        Deck deck = new Deck();

        Card drawn = deck.drawCard();

        assertEquals(51, deck.size());
        assertFalse(deck.getCards().contains(drawn));
    }

    @Test
    void drawingEntireDeckDoesNotRepeatCards() {
        Deck deck = new Deck();
        ArrayList<Card> drawn = new ArrayList<>();

        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();

            assertFalse(drawn.contains(card));
            drawn.add(card);
        }

        assertEquals(52, drawn.size());
        assertEquals(0, deck.size());
    }

    @Test
    void drawingFromEmptyDeckThrowsException() {
        Deck deck = new Deck();

        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }

        assertThrows(IllegalStateException.class, () -> deck.drawCard());
        assertEquals(0, deck.size());
    }

    @Test
    void shufflingPreservesRemainingCards() {
        Deck deck = new Deck();
        Card drawn = deck.drawCard();
        List<Card> original = deck.getCards();

        deck.shuffle();

        assertEquals(51, deck.size());
        assertTrue(deck.getCards().containsAll(original));
        assertFalse(deck.getCards().contains(drawn));
    }

    @Test
    void resetRestoresCompleteDeckWithoutDuplicates() {
        Deck deck = new Deck();
        List<Card> original = deck.getCards();

        deck.drawCard();
        deck.reset();
        deck.reset();

        assertEquals(52, deck.size());
        assertEquals(original, deck.getCards());
    }

    @Test
    void returnedCardListCannotModifyDeck() {
        Deck deck = new Deck();
        List<Card> snapshot = deck.getCards();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.clear());

        deck.drawCard();

        assertEquals(52, snapshot.size());
        assertEquals(51, deck.size());
    }
}

