package edu.uic.cs342.poker.logic;

public enum HandRank {

    STRAIGHT_FLUSH(1, 6), THREE_OF_A_KIND(2, 5), STRAIGHT(3, 4), FLUSH(4, 3), PAIR(5, 2), HIGH_CARD(6, 1);

    private final int evaluationCode;
    private final int strength;

    HandRank(int evaluationCode, int strength) {
        this.evaluationCode = evaluationCode;
        this.strength = strength;
    }

    public int evaluationCode() {
        return evaluationCode;
    }

    public int strength() {
        return strength;
    }
}
