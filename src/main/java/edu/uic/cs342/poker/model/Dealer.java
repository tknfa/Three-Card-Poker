package edu.uic.cs342.poker.model;

import java.util.ArrayList;

public final class Dealer implements HandDealer {

    private final Deck deck;

    public Dealer() {
        this(new Deck());
        deck.shuffle();
    }

    public Dealer(Deck deck) {
        if (deck == null) {
            throw new IllegalArgumentException("Dealer requires a deck");
        }

        this.deck = deck;
    }

    @Override
    public Hand dealHand() {
        if (deck.size() < 3) {
            throw new IllegalStateException("Not enough cards to deal a hand");
        }

        ArrayList<Card> cards = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            cards.add(deck.drawCard());
        }

        return new Hand(cards);
    }

    @Override
    public void resetDeck() {
        deck.reset();
        deck.shuffle();
    }

    public int remainingCards() {
        return deck.size();
    }
}
