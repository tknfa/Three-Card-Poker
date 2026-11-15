package edu.uic.cs342.poker.ui;

import edu.uic.cs342.poker.model.GamePhase;

public enum CardVisibility {
    EMPTY, FACE_DOWN, FACE_UP;

    public static CardVisibility forDealer(GamePhase phase) {
        if (phase == GamePhase.READY || phase == GamePhase.WAGERING) {
            return EMPTY;
        }

        if (phase == GamePhase.SHOWDOWN || phase == GamePhase.RESULTS) {
            return FACE_UP;
        }

        return FACE_DOWN;
    }

    public static CardVisibility forPlayer(GamePhase phase, int playerNumber) {
        if (playerNumber != 1 && playerNumber != 2) {
            throw new IllegalArgumentException("Player number must be 1 or 2");
        }

        if (phase == GamePhase.READY || phase == GamePhase.WAGERING) {
            return EMPTY;
        }

        if (phase == GamePhase.SHOWDOWN || phase == GamePhase.RESULTS || (phase == GamePhase.PLAYER_ONE_TURN && playerNumber == 1) || (phase == GamePhase.PLAYER_TWO_TURN && playerNumber == 2)) {
            return FACE_UP;
        }

        return FACE_DOWN;
    }
}
