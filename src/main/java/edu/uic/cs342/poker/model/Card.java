package edu.uic.cs342.poker.model;

public record Card(Suit suit, Rank rank) {

    public Card {
        if (suit == null || rank == null) {
            throw new IllegalArgumentException("Card requires a suit and rank");
        }
    }

    public Card(Suit suit, int value) {
        this(suit, Rank.fromValue(value));
    }

    public int value() {
        return rank.value();
    }
}
