package edu.uic.cs342.poker.model;

public record PlayerState (String name, Hand hand, int anteWager, int pairPlusWager, int playWager, PlayerDecision decision, int cumulativeWinnings, boolean wagersConfirmed, int carriedAnte){
    public int currentWager() {
        if (decision == PlayerDecision.FOLD) {
            return 0;
        }

        if (!wagersConfirmed) {
            return carriedAnte;
        }

        return anteWager + pairPlusWager + playWager;
    }
}