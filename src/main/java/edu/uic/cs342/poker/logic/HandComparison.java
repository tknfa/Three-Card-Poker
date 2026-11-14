package edu.uic.cs342.poker.logic;

public enum HandComparison {
    TIE(0), DEALER_WINS(1), PLAYER_WINS(2);

    private final int comparisonCode;

    HandComparison(int comparisonCode) {
        this.comparisonCode = comparisonCode;
    }

    public int comparisonCode() {
        return comparisonCode;
    }
}
