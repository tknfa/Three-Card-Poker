package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.GamePhase;
import edu.uic.cs342.poker.model.GameRound;
import edu.uic.cs342.poker.model.PlayerState;
import edu.uic.cs342.poker.model.PlayerDecision;
import edu.uic.cs342.poker.service.GameService;
import edu.uic.cs342.poker.ui.CardDisplay;
import edu.uic.cs342.poker.ui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class GameplayController implements ScreenController {
    private ScreenNavigator navigator;
    private GameService gameService;
    private boolean hintsShown = true;

    @FXML private Label roundStateLabel, roundStateSubtitle;
    @FXML private Label p1CumulativeWinningsLabel, p2CumulativeWinningsLabel;
    @FXML private Label hintTitleLabel, hintDescriptionLabel;
    @FXML private Label p1AnteDisplayLabel, p1PairPlusDisplayLabel;
    @FXML private Label p2AnteDisplayLabel, p2PairPlusDisplayLabel;
    @FXML private Label p1CurrentWagerLabel, p2CurrentWagerLabel, totalBetsLabel;
    @FXML private Label p1WagerStatusLabel, p2WagerStatusLabel, wagerPhaseLabel;
    @FXML private Label p1DecisionLabel, p2DecisionLabel;
    @FXML private TextField p1AnteField, p1PairPlusField, p2AnteField, p2PairPlusField;
    @FXML private Button p1ConfirmButton, p2ConfirmButton, startDealButton;
    @FXML private Button p1PlayButton, p1FoldButton, p2PlayButton, p2FoldButton;
    @FXML private Button viewResultsButton, hintButton;
    @FXML private HBox dealerCardBox, player1CardBox, player2CardBox;
    @FXML private AnchorPane wagerOverlayPane, tableControlsPane;

    @Override
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
        this.gameService = navigator.getGameService();
        restoreWagerFields(gameService.getPlayer(1), p1AnteField, p1PairPlusField);
        restoreWagerFields(gameService.getPlayer(2), p2AnteField, p2PairPlusField);
        refresh();
    }

    private void restoreWagerFields(PlayerState player, TextField ante, TextField pairPlus) {
        if (player.wagersConfirmed()) {
            ante.setText("" + player.anteWager());
            pairPlus.setText("" + player.pairPlusWager());
        } else if (player.carriedAnte() != 0) {
            ante.setText("" + player.carriedAnte());
        }
    }

    @FXML private void handleP1ConfirmWager() {
        confirm(1, p1AnteField, p1PairPlusField, p1WagerStatusLabel);
    }

    @FXML private void handleP2ConfirmWager() {
        confirm(2, p2AnteField, p2PairPlusField, p2WagerStatusLabel);
    }

    private void confirm(int player, TextField anteField, TextField pairPlusField, Label status) {
        try {
            int ante = Integer.parseInt(anteField.getText().trim());
            String optional = pairPlusField.getText().trim();
            int pairPlus = optional.isEmpty() ? 0 : Integer.parseInt(optional);
            gameService.confirmWagers(player, ante, pairPlus);
            refresh();
        } catch (NumberFormatException e) {
            status.setText("Enter whole-dollar wagers (blank Pair Plus = $0).");
        } catch (IllegalArgumentException | IllegalStateException e) {
            status.setText(e.getMessage());
        }
    }

    @FXML private void handleStartDealAfterWagers() {
        try {
            gameService.deal();
            refresh();
        } catch (IllegalStateException e) {
            wagerPhaseLabel.setText(e.getMessage());
        }
    }

    @FXML private void handlePlayer1Play() { decide(1, true); }
    @FXML private void handlePlayer1Fold() { decide(1, false); }
    @FXML private void handlePlayer2Play() { decide(2, true); }
    @FXML private void handlePlayer2Fold() { decide(2, false); }

    private void decide(int player, boolean play) {
        try {
            if (play) { gameService.play(player); }
            else { gameService.fold(player); }
            refresh();
        } catch (IllegalStateException e) {
            roundStateSubtitle.setText(e.getMessage());
        }
    }

    @FXML private void handleViewResults() {
        try {
            gameService.resolveRound();
            navigator.showResults();
        } catch (IllegalStateException e) {
            roundStateSubtitle.setText(e.getMessage());
        }
    }

    @FXML private void handleMainMenu() { navigator.showWelcome(); }

    @FXML private void handleHideHints() {
        hintsShown = !hintsShown;
        hintDescriptionLabel.setVisible(hintsShown);
        hintButton.setText(hintsShown ? "Hide Hints" : "Show Hints");
    }

    private void refresh() {
        GameRound round = gameService.getRound();
        PlayerState one = round.playerOne();
        PlayerState two = round.playerTwo();
        GamePhase phase = round.phase();
        boolean wagering = phase == GamePhase.WAGERING;
        boolean showdown = phase == GamePhase.SHOWDOWN;
        int active = gameService.getActivePlayer();

        wagerOverlayPane.setVisible(wagering);
        wagerOverlayPane.setManaged(wagering);
        tableControlsPane.setDisable(wagering);
        startDealButton.setDisable(!gameService.canDeal());
        p1PlayButton.setDisable(active != 1);
        p1FoldButton.setDisable(active != 1);
        p2PlayButton.setDisable(active != 2);
        p2FoldButton.setDisable(active != 2);
        viewResultsButton.setVisible(showdown);
        viewResultsButton.setDisable(!showdown);

        updateWagerControls(one, p1AnteField, p1PairPlusField, p1ConfirmButton, p1WagerStatusLabel);
        updateWagerControls(two, p2AnteField, p2PairPlusField, p2ConfirmButton, p2WagerStatusLabel);

        p1CumulativeWinningsLabel.setText(one.name() + "'s Cumulative Winnings: " + money(one.cumulativeWinnings()));
        p2CumulativeWinningsLabel.setText(two.name() + "'s Cumulative Winnings: " + money(two.cumulativeWinnings()));
        p1AnteDisplayLabel.setText("Ante: " + money(one.anteWager()));
        p2AnteDisplayLabel.setText("Ante: " + money(two.anteWager()));
        p1PairPlusDisplayLabel.setText("Pair Plus: " + money(one.pairPlusWager()));
        p2PairPlusDisplayLabel.setText("Pair Plus: " + money(two.pairPlusWager()));
        int firstWager = currentWager(one);
        int secondWager = currentWager(two);
        p1CurrentWagerLabel.setText(money(firstWager));
        p2CurrentWagerLabel.setText(money(secondWager));
        totalBetsLabel.setText(money(firstWager + secondWager));
        p1DecisionLabel.setText(decisionText(one, active == 1));
        p2DecisionLabel.setText(decisionText(two, active == 2));

        CardDisplay.show(dealerCardBox, round.dealerHand(), wagering || showdown, 62, 88);
        CardDisplay.show(player1CardBox, one.hand(), wagering || active == 1 || showdown, 62, 88);
        CardDisplay.show(player2CardBox, two.hand(), wagering || active == 2 || showdown, 62, 88);

        String title;
        String instruction;
        if (wagering) {
            title = "PLACE YOUR BETS";
            instruction = "Confirm both wagers, then Deal.";
            wagerPhaseLabel.setText("Round " + round.roundNumber() + " | " + instruction);
        } else if (active != 0) {
            title = "PLAYER " + active + "'S TURN";
            instruction = "Choose Play (match Ante) or Fold.";
        } else {
            title = "SHOWDOWN";
            instruction = "Both decisions recorded. Select View Results.";
        }

        roundStateLabel.setText("Round " + round.roundNumber() + " | " + title);
        roundStateSubtitle.setText(instruction);
        hintTitleLabel.setText(title);
        hintDescriptionLabel.setText(instruction);
    }

    private void updateWagerControls(PlayerState player, TextField ante, TextField pp, Button confirm, Label status) {
        boolean locked = player.wagersConfirmed();
        ante.setDisable(locked || player.carriedAnte() != 0);
        pp.setDisable(locked);
        confirm.setDisable(locked || gameService.getPhase() != GamePhase.WAGERING);
        confirm.setText(locked ? "Confirmed" : "✓ Confirm");

        if (locked) {
            status.setText("Wagers confirmed.");
        } else if (player.carriedAnte() != 0) {
            status.setText("Carried Ante: " + money(player.carriedAnte()) + ".");
        }
        else {
            status.setText("Waiting for wager...");
        }
    }

    private int currentWager(PlayerState player) {
        if (player.decision() == PlayerDecision.FOLD) {
            return 0;
        }

        if (!player.wagersConfirmed()) {
            return player.carriedAnte();
        }

        return player.anteWager() + player.pairPlusWager() + player.playWager();
    }

    private String decisionText(PlayerState player, boolean active) {
        if (player.decision() == PlayerDecision.PLAY) {
            return player.name() + " — Play";
        }

        if (player.decision() == PlayerDecision.FOLD) {
            return player.name() + " — Folded";
        }

        return player.name() + (active ? " — Your turn" : " — Waiting");
    }

    private String money(int amount) {
        if (amount < 0) {
            return "-$" + Math.abs(amount);
        }

        return "$" + amount;
    }
}

