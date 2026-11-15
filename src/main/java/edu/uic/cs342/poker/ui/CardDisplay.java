package edu.uic.cs342.poker.ui;

import edu.uic.cs342.poker.logic.HandEvaluator;
import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Hand;
import edu.uic.cs342.poker.model.Suit;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class CardDisplay {
    private CardDisplay() { }

    public static void show(HBox box, Hand hand, boolean faceUp, double width, double height) {
        show(box, hand, faceUp ? CardVisibility.FACE_UP : CardVisibility.FACE_DOWN, width, height);
    }

    public static void show(HBox box, Hand hand, CardVisibility visibility, double width, double height) {
        showCards(box, hand, visibility, width, height, false);
    }

    public static void showRanked(HBox box, Hand hand, double width, double height) {
        showCards(box, hand, CardVisibility.FACE_UP, width, height, true);
    }

    private static void showCards(HBox box, Hand hand, CardVisibility visibility, double width, double height, boolean emphasizeRank) {
        box.getChildren().clear();

        if (visibility == CardVisibility.EMPTY || (visibility == CardVisibility.FACE_UP && hand == null)) {
            return;
        }

        List<Card> relevant = List.of();

        if (emphasizeRank) {
            relevant = HandEvaluator.rankCards(hand.getCards());
        }

        for (int i = 0; i < 3; i++) {
            VBox tile = new VBox(2);
            tile.setAlignment(Pos.CENTER);
            tile.setMinSize(width, height);
            tile.setPrefSize(width, height);
            tile.setMaxSize(width, height);

            if (visibility == CardVisibility.FACE_DOWN) {
                tile.getStyleClass().add("card-back");
                tile.setAccessibleText("Face-down card");
            } else {
                Card card = hand.getCards().get(i);
                tile.getStyleClass().add("card-face");
                Label rank = new Label(rankText(card));
                Label suit = new Label(suitText(card.suit()));
                rank.getStyleClass().add("card-rank");
                suit.getStyleClass().add("card-suit");

                if (card.suit() == Suit.HEARTS || card.suit() == Suit.DIAMONDS) {
                    rank.getStyleClass().add("red-suit");
                    suit.getStyleClass().add("red-suit");
                }

                String description = card.rank().name() + " of " + card.suit().name();
                if (emphasizeRank) {
                    if (relevant.contains(card)) {
                        tile.getStyleClass().add("rank-card-primary");
                        description += ", establishes hand rank";
                    } else {
                        tile.getStyleClass().add("rank-card-secondary");
                        description += ", may break a tie";
                    }
                }

                tile.setAccessibleText(description);
                tile.getChildren().addAll(rank, suit);
            }

            box.getChildren().add(tile);
        }
    }

    private static String rankText(Card card) {
        if (card.value() == 14) { return "A"; }
        if (card.value() == 13) { return "K"; }
        if (card.value() == 12) { return "Q"; }
        if (card.value() == 11) { return "J"; }
        return "" + card.value();
    }

    private static String suitText(Suit suit) {
        if (suit == Suit.HEARTS) { return "♥"; }
        if (suit == Suit.DIAMONDS) { return "♦"; }
        if (suit == Suit.CLUBS) { return "♣"; }
        return "♠";
    }
}