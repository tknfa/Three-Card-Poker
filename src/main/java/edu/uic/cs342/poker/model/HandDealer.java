package edu.uic.cs342.poker.model;

public interface HandDealer {
    void resetDeck();
    Hand dealHand();
}
