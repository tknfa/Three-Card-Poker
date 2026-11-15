package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.GamePhase;
import edu.uic.cs342.poker.model.PlayerRoundResult;
import edu.uic.cs342.poker.model.RoundOutcome;
import edu.uic.cs342.poker.model.RoundResult;
import edu.uic.cs342.poker.service.GameService;
import edu.uic.cs342.poker.ui.CardDisplay;
import edu.uic.cs342.poker.ui.ScreenNavigator;
import edu.uic.cs342.poker.ui.ResultText;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ResultsController implements ScreenController {
    private ScreenNavigator navigator;
    private GameService gameService;

    @FXML private HBox dealerCardBox, player1CardBox, player2CardBox;
    @FXML private Label roundNumberLabel, dealerRankLabel;
    @FXML private Label p1RankTopLabel, p1OutcomeLabel, p1RankBoxLabel;
    @FXML private Label p1AntePayoutLabel, p1PlayPayoutLabel, p1PairPlusPayoutLabel, p1CumulativeLabel;
    @FXML private Label p2RankTopLabel, p2OutcomeLabel, p2RankBoxLabel;
    @FXML private Label p2AntePayoutLabel, p2PlayPayoutLabel, p2PairPlusPayoutLabel, p2CumulativeLabel;
    @FXML private Label p1NetLabel, p2NetLabel;
    @FXML private Button playAgainButton;

    @Override
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
        this.gameService = navigator.getGameService();
        RoundResult result = gameService.getResult();

        if (result == null) {
            roundNumberLabel.setText("No completed round");
            playAgainButton.setDisable(true);
            return;
        }

        roundNumberLabel.setText("Round " + result.roundNumber() + " Summary");
        dealerRankLabel.setText(ResultText.dealer(result));
        CardDisplay.showRanked(dealerCardBox, result.dealerHand(), 65, 90);
        displayPlayer(result.playerOne(), player1CardBox, p1RankTopLabel, p1OutcomeLabel, p1RankBoxLabel, p1AntePayoutLabel, p1PlayPayoutLabel, p1PairPlusPayoutLabel, p1CumulativeLabel, p1NetLabel);
        displayPlayer(result.playerTwo(), player2CardBox, p2RankTopLabel, p2OutcomeLabel, p2RankBoxLabel, p2AntePayoutLabel, p2PlayPayoutLabel, p2PairPlusPayoutLabel, p2CumulativeLabel, p2NetLabel);
    }

    private void displayPlayer(PlayerRoundResult result, HBox cards, Label rankTop, Label outcome, Label rankBox, Label ante, Label play, Label pairPlus, Label cumulative, Label net) {
        CardDisplay.showRanked(cards, result.hand(), 76, 106);
        String rank = ResultText.rank(result.hand(), result.handRank());
        rankTop.setText(rank);
        rankBox.setText("Rank: " + rank);
        outcome.setText(ResultText.outcome(result.outcome()));
        ante.setText(ResultText.ante(result));
        play.setText(ResultText.play(result));
        pairPlus.setText(ResultText.pairPlus(result));
        net.setText("Round net: " + ResultText.signedMoney(result.netChange()));
        cumulative.setText(ResultText.cumulative(result));

        if (result.outcome() == RoundOutcome.PLAYER_WINS) {
            cards.getStyleClass().add("showdown-winner");
        } else if (result.outcome() == RoundOutcome.DEALER_WINS || result.outcome() == RoundOutcome.FOLDED) {
            cards.getStyleClass().add("showdown-loser");
        }
    }

    @FXML
    private void handlePlayAgain() {
        if (gameService.getPhase() == GamePhase.RESULTS) {
            gameService.nextRound();
            navigator.showGameplay();
        }
    }

    @FXML
    private void handleMainMenu() { navigator.showWelcome(); }
}