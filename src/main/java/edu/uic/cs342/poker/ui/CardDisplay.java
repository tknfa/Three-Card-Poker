package edu.uic.cs342.poker.ui;

import edu.uic.cs342.poker.model.Card;
import edu.uic.cs342.poker.model.Hand;
import edu.uic.cs342.poker.model.Suit;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class CardDisplay {
    private CardDisplay() { }

    public static void show(HBox box, Hand hand, boolean faceUp, double width, double height) {
        box.getChildren().clear();

        if (hand == null && faceUp) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            VBox tile = new VBox(2);
            tile.setAlignment(Pos.CENTER);
            tile.setMinSize(width, height);
            tile.setPrefSize(width, height);
            tile.setMaxSize(width, height);

            if (!faceUp) {
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

                tile.setAccessibleText(card.rank().name() + " of " + card.suit().name());
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
