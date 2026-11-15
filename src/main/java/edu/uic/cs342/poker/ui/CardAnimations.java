package edu.uic.cs342.poker.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public final class CardAnimations {
    private CardAnimations() { }

    public static ParallelTransition deal(HBox dealer, HBox first, HBox second) {
        ParallelTransition movement = new ParallelTransition();
        addCardMovement(movement, dealer, 0, 215);
        addCardMovement(movement, first, 368, -296);
        addCardMovement(movement, second, -368, -296);

        return movement;
    }

    private static void addCardMovement(ParallelTransition movement, HBox tray, double fromX, double fromY) {
        int index = 0;

        for (Node card : tray.getChildren()) {
            double startX = fromX + (1 - index) * 67;
            card.setTranslateX(startX);
            card.setTranslateY(fromY);
            TranslateTransition slide = new TranslateTransition(Duration.millis(400), card);
            slide.setFromX(startX);
            slide.setFromY(fromY);
            slide.setToX(0);
            slide.setToY(0);
            slide.setDelay(Duration.millis(index * 65));
            movement.getChildren().add(slide);
            index++;
        }
    }

    public static SequentialTransition showdown(HBox dealer, HBox first, HBox second) {
        dealer.setOpacity(0.20);
        FadeTransition reveal = new FadeTransition(Duration.millis(330), dealer);
        reveal.setFromValue(0.20);
        reveal.setToValue(1.0);

        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().add(reveal);

        ParallelTransition winners = new ParallelTransition();
        addWinnerPulse(winners, first);
        addWinnerPulse(winners, second);

        if (!winners.getChildren().isEmpty()) {
            sequence.getChildren().add(winners);
        }

        return sequence;
    }

    private static void addWinnerPulse(ParallelTransition winners, HBox tray) {
        if (!tray.getStyleClass().contains("showdown-winner")) {
            return;
        }

        ScaleTransition pulse = new ScaleTransition(Duration.millis(220), tray);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.04);
        pulse.setToY(1.04);
        pulse.setCycleCount(2);
        pulse.setAutoReverse(true);
        winners.getChildren().add(pulse);
    }

    public static void reset(HBox tray) {
        tray.setOpacity(1.0);
        tray.setScaleX(1.0);
        tray.setScaleY(1.0);

        for (Node card : tray.getChildren()) {
            card.setTranslateX(0);
            card.setTranslateY(0);
        }
    }
}