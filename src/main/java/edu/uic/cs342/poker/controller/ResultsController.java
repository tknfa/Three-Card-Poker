package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.GamePhase;
import edu.uic.cs342.poker.model.PlayerRoundResult;
import edu.uic.cs342.poker.model.RoundOutcome;
import edu.uic.cs342.poker.model.RoundResult;
import edu.uic.cs342.poker.service.GameService;
import edu.uic.cs342.poker.ui.CardDisplay;
import edu.uic.cs342.poker.ui.ScreenNavigator;
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
        dealerRankLabel.setText(result.dealerRank().name().replace('_', ' ') + (result.dealerQualifies() ? " — QUALIFIES" : " — NOT QUALIFIED"));
        CardDisplay.show(dealerCardBox, result.dealerHand(), true, 76, 108);
        displayPlayer(result.playerOne(), player1CardBox, p1RankTopLabel, p1OutcomeLabel, p1RankBoxLabel, p1AntePayoutLabel, p1PlayPayoutLabel, p1PairPlusPayoutLabel, p1CumulativeLabel);
        displayPlayer(result.playerTwo(), player2CardBox, p2RankTopLabel, p2OutcomeLabel, p2RankBoxLabel, p2AntePayoutLabel, p2PlayPayoutLabel, p2PairPlusPayoutLabel, p2CumulativeLabel);
    }

    private void displayPlayer(PlayerRoundResult result, HBox cards, Label rankTop, Label outcome, Label rankBox, Label ante, Label play, Label pairPlus, Label cumulative) {
        CardDisplay.show(cards, result.hand(), true, 76, 108);
        String rank = result.handRank().name().replace('_', ' ');
        rankTop.setText(rank);
        rankBox.setText("RANK: " + rank);

        outcome.setText(outcomeText(result.outcome()));

        String anteDetails = result.carriedAnte() != 0 ? "CARRIED $" + result.carriedAnte() : signedMoney(result.anteNetChange());
        ante.setText("ANTE ($" + result.anteWager() + "): " + anteDetails);

        play.setText("PLAY ($" + result.playWager() + "): " + signedMoney(result.playNetChange()));
        String multiplier = result.pairPlusMultiplier() > 0 ? " [" + result.pairPlusMultiplier() + ":1]" : "";
        pairPlus.setText("PAIR PLUS ($" + result.pairPlusWager() + "): " + signedMoney(result.pairPlusNetChange()) + multiplier);
        cumulative.setText(money(result.updatedCumulativeWinnings()) + " (" + signedMoney(result.netChange()) + ")");
    }

    private String outcomeText(RoundOutcome outcome) {
        if (outcome == RoundOutcome.PLAYER_WINS) { return "WIN"; }
        if (outcome == RoundOutcome.DEALER_WINS) { return "LOSS"; }
        if (outcome == RoundOutcome.FOLDED) { return "FOLDED"; }
        if (outcome == RoundOutcome.TIE) { return "TIE"; }

        return "DEALER NOT QUALIFIED";
    }

    private String money(int amount) {
        return amount < 0 ? "-$" + Math.abs(amount) : "$" + amount;
    }

    private String signedMoney(int amount) {
        return amount > 0 ? "+" + money(amount) : money(amount);
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

