package edu.uic.cs342.poker.model;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Deck;
import edu.uic.cs342.poker.model.Dealer;
import edu.uic.cs342.poker.model.Hand;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DealerTest {

    @Test
    void dealerDealsThreeCards() {
        Dealer dealer = new Dealer();

        Hand hand = dealer.dealHand();

        assertEquals(3, hand.getCards().size());
        assertEquals(49, dealer.remainingCards());
    }

    @Test
    void threeHandsContainNineUniqueCards() {
        Dealer dealer = new Dealer();
        ArrayList<Card> dealt = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Hand hand = dealer.dealHand();

            assertEquals(3, hand.getCards().size());

            for (Card card : hand.getCards()) {
                assertFalse(dealt.contains(card));
                dealt.add(card);
            }
        }

        assertEquals(9, dealt.size());
        assertEquals(43, dealer.remainingCards());
    }

    @Test
    void dealerCanDealLastThreeCards() {
        Deck deck = new Deck();
        Dealer dealer = new Dealer(deck);

        for (int i = 0; i < 49; i++) {
            deck.drawCard();
        }

        List<Card> remaining = deck.getCards();

        Hand hand = dealer.dealHand();

        assertEquals(3, hand.getCards().size());
        assertTrue(hand.getCards().containsAll(remaining));
        assertEquals(0, dealer.remainingCards());
    }

    @Test
    void dealerRejectsIncompleteHandWithoutRemovingCards() {
        Deck deck = new Deck();
        Dealer dealer = new Dealer(deck);

        for (int i = 0; i < 50; i++) {
            deck.drawCard();
        }

        List<Card> remaining = deck.getCards();

        assertThrows(IllegalStateException.class, () -> dealer.dealHand());

        assertEquals(2, dealer.remainingCards());
        assertEquals(remaining, deck.getCards());
    }

    @Test
    void dealerResetRestoresDeckForNextRound() {
        Dealer dealer = new Dealer();

        dealer.dealHand();
        dealer.dealHand();
        dealer.dealHand();
        dealer.resetDeck();

        assertEquals(52, dealer.remainingCards());

        Hand hand = dealer.dealHand();

        assertEquals(3, hand.getCards().size());
        assertEquals(49, dealer.remainingCards());
    }
}
