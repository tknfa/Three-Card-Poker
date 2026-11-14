package edu.uic.cs342.poker.model;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DealerTest {
    private Deck deck;
    private Dealer dealer;

    @BeforeEach
    void setUp() {
        deck = new Deck();
        dealer = new Dealer(deck);
    }

    @Test
    void dealsExactlyThreeCards() {
        Hand hand = dealer.dealHand();
        assertEquals(3, hand.getCards().size());
        assertEquals(49, dealer.remainingCards());
    }

    @Test
    void suppliedDeckIsUsedInItsCurrentOrder() {
        List<Card> cards = deck.getCards();
        Hand hand = dealer.dealHand();
        assertIterableEquals(List.of(cards.get(51), cards.get(50), cards.get(49)), hand.getCards());
    }

    @Test
    void dealerAndTwoPlayersReceiveNineDifferentCards() {
        ArrayList<Card> dealt = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Hand hand = dealer.dealHand();
            for (Card card : hand.getCards()) {
                assertFalse(dealt.contains(card));
                dealt.add(card);
            }
        }
        assertEquals(9, dealt.size());
        assertEquals(43, dealer.remainingCards());
    }

    @Test
    void insufficientCardsDoNotCausePartialDeal() {
        for (int i = 0; i < 17; i++) {
            dealer.dealHand();
        }
        assertEquals(1, dealer.remainingCards());
        assertThrows(IllegalStateException.class, () -> dealer.dealHand());
        assertEquals(1, dealer.remainingCards());
    }

    @Test
    void resetRestoresDeckForNextRound() {
        dealer.dealHand();
        dealer.dealHand();
        dealer.dealHand();
        dealer.resetDeck();
        assertEquals(52, dealer.remainingCards());
        assertEquals(3, dealer.dealHand().getCards().size());
        assertEquals(49, dealer.remainingCards());
    }

    @Test
    void defaultDealerStartsWithFullDeck() {
        Dealer defaultDealer = new Dealer();
        assertEquals(52, defaultDealer.remainingCards());
        assertEquals(3, defaultDealer.dealHand().getCards().size());
    }
}