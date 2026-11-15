package edu.uic.cs342.poker.ui;

public record WagerInput(int ante, int pairPlus) {
    public static WagerInput parse(String anteText, String pairPlusText) {
        if (anteText == null || anteText.isBlank()) {
            throw new IllegalArgumentException("Enter an Ante wager.");
        }

        int ante;

        try {
            ante = Integer.parseInt(anteText.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ante must be a whole-dollar amount.", e);
        }

        int pairPlus = 0;
        if (pairPlusText != null && !pairPlusText.isBlank()) {
            try {
                pairPlus = Integer.parseInt(pairPlusText.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Pair Plus must be a whole-dollar amount or blank.", e);
            }
        }

        return new WagerInput(ante, pairPlus);
    }
}
