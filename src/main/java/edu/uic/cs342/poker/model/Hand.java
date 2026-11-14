package edu.uic.cs342.poker.model;

import java.util.List;

public final class Hand {

    private final List<Card> cards;

    public Hand(List<Card> cards) {
        if (cards == null || cards.size() != 3) {
            throw new IllegalArgumentException("A hand must contain three cards");
        }

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);

            if (card == null) {
                throw new IllegalArgumentException("A hand cannot contain a missing card");
            }

            for (int j = 0; j < i; j++) {
                if (card.equals(cards.get(j))) {
                    throw new IllegalArgumentException("A hand cannot contain duplicate cards");
                }
            }
        }

        this.cards = List.copyOf(cards);
    }

    public List<Card> getCards() {
        return cards;
    }
}
