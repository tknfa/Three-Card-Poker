package edu.uic.cs342.poker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final ArrayList<Card> cards = new ArrayList<>();

    public Deck() {
        reset();
    }

    public void reset() {
        cards.clear();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.size() == 0) {
            throw new IllegalStateException("No cards remain in the deck");
        }

        return cards.remove(cards.size() - 1);
    }

    public int size() {
        return cards.size();
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}
