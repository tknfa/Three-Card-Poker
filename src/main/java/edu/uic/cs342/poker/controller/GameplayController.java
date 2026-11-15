package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.GamePhase;
import edu.uic.cs342.poker.model.GameRound;
import edu.uic.cs342.poker.model.PlayerState;
import edu.uic.cs342.poker.model.PlayerDecision;
import edu.uic.cs342.poker.model.PlayerRoundResult;
import edu.uic.cs342.poker.model.RoundOutcome;
import edu.uic.cs342.poker.model.RoundResult;
import edu.uic.cs342.poker.service.GameService;
import edu.uic.cs342.poker.ui.CardDisplay;
import edu.uic.cs342.poker.ui.CardAnimations;
import edu.uic.cs342.poker.ui.CardVisibility;
import edu.uic.cs342.poker.ui.ScreenNavigator;
import edu.uic.cs342.poker.ui.WagerInput;
import edu.uic.cs342.poker.ui.ResultText;
import javafx.animation.Transition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;

public class GameplayController implements ScreenController {
    private ScreenNavigator navigator;
    private GameService gameService;
    private boolean hintsShown = true;
    private Transition currentAnimation;
    private String animationInstruction;

    @FXML private Label roundStateLabel, roundStateSubtitle;
    @FXML private Label p1CumulativeWinningsLabel, p2CumulativeWinningsLabel;
    @FXML private Label hintTitleLabel, hintDescriptionLabel;
    @FXML private Label p1AnteDisplayLabel, p1PairPlusDisplayLabel;
    @FXML private Label p2AnteDisplayLabel, p2PairPlusDisplayLabel;
    @FXML private Label p1CurrentWagerLabel, p2CurrentWagerLabel, totalBetsLabel;
    @FXML private Label p1WagerStatusLabel, p2WagerStatusLabel, wagerPhaseLabel;
    @FXML private Label p1DecisionLabel, p2DecisionLabel, dealerShowdownLabel;
    @FXML private TextField p1AnteField, p1PairPlusField, p2AnteField, p2PairPlusField;
    @FXML private Button p1ConfirmButton, p2ConfirmButton, startDealButton;
    @FXML private Button p1PlayButton, p1FoldButton, p2PlayButton, p2FoldButton;
    @FXML private Button viewResultsButton, hintButton, handHelpButton, closeHandHelpButton;
    @FXML private Button motionButton;
    @FXML private HBox dealerCardBox, player1CardBox, player2CardBox;
    @FXML private VBox wagerOverlayPane, handHelpPane;
    @FXML private AnchorPane tableControls;
    @FXML private StackPane wagerOverlayBackdrop;

    @Override
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
        this.gameService = navigator.getGameService();

        setHandHelpVisible(false);
        updateMotionButton();

        restoreWagerFields(gameService.getPlayer(1), p1AnteField, p1PairPlusField, p1WagerStatusLabel);
        restoreWagerFields(gameService.getPlayer(2), p2AnteField, p2PairPlusField, p2WagerStatusLabel);

        refresh();
    }

    private void restoreWagerFields(PlayerState player, TextField ante, TextField pairPlus, Label status) {
        if (player.wagersConfirmed()) {
            ante.setText("" + player.anteWager());
            pairPlus.setText("" + player.pairPlusWager());
        } else if (player.carriedAnte() != 0) {
            ante.setText("" + player.carriedAnte());
            status.setText("Carried Ante: " + money(player.carriedAnte()) + ". Enter Pair Plus, then Confirm.");
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
            WagerInput input = WagerInput.parse(anteField.getText(), pairPlusField.getText());
            gameService.confirmWagers(player, input.ante(), input.pairPlus());
            anteField.setText("" + input.ante());
            pairPlusField.setText("" + input.pairPlus());
            refresh();
        } catch (IllegalArgumentException | IllegalStateException e) {
            status.setText("Not confirmed: " + e.getMessage());
            status.getStyleClass().remove("wager-feedback-confirmed");
            status.getStyleClass().remove("wager-feedback-error");
            status.getStyleClass().add("wager-feedback-error");
        }
    }

    @FXML private void handleStartDealAfterWagers() {
        try {
            gameService.deal();
            refresh();

            if (wagerOverlayBackdrop != null) {
                wagerOverlayBackdrop.setVisible(false);
                wagerOverlayBackdrop.setManaged(false);
            }

            if (navigator.isMotionEnabled()) {
                startAnimation(CardAnimations.deal(dealerCardBox, player1CardBox, player2CardBox), "Dealing cards. Turn motion off to skip.");
            }

        } catch (IllegalStateException e) {
            wagerPhaseLabel.setText(e.getMessage());
        }
    }

    @FXML private void handlePlayer1Play() { decide(1, true); }
    @FXML private void handlePlayer1Fold() { decide(1, false); }
    @FXML private void handlePlayer2Play() { decide(2, true); }
    @FXML private void handlePlayer2Fold() { decide(2, false); }

    private void decide(int player, boolean play) {
        if (currentAnimation != null) { return; }

        try {
            if (play) { gameService.play(player); }
            else { gameService.fold(player); }

            if (!navigator.isMotionEnabled()) {
                refresh();
                return;
            }

            if (gameService.getActivePlayer() == 2) {
                ParallelTransition turnFlip = new ParallelTransition(
                        CardAnimations.flip(player1CardBox, (node, i) -> {}),
                        CardAnimations.flip(player2CardBox, (node, i) -> {})
                );

                turnFlip.setOnFinished(e -> {
                    completeAnimation();
                    refresh();
                });

                startAnimation(turnFlip, "Switching to Player 2's turn.");
            }
        } catch (IllegalStateException e) {
            roundStateSubtitle.setText(e.getMessage());
        }
    }

    @FXML private void handleViewResults() {
        if (currentAnimation != null) { return; }

        try {
            if (gameService.getPhase() == GamePhase.SHOWDOWN) {
                gameService.resolveRound();
            } else if (gameService.getPhase() != GamePhase.RESULTS) {
                throw new IllegalStateException("Both players must decide before viewing results");
            }

            navigator.showResults();
        } catch (IllegalStateException e) {
            roundStateSubtitle.setText(e.getMessage());
        }
    }

    @FXML private void handleMainMenu() {
        skipAnimation();
        navigator.showWelcome();
    }

    @FXML private void handleToggleMotion() {
        navigator.toggleMotion();

        if (!navigator.isMotionEnabled()) {
            skipAnimation();
        }

        updateMotionButton();
    }

    private void updateMotionButton() {
        boolean enabled = navigator.isMotionEnabled();
        motionButton.setText(enabled ? "Motion: On" : "Motion: Off");
        motionButton.setAccessibleText(enabled ? "Motion is on. Select to turn motion off and skip animations." : "Motion is off. Select to enable animations.");
    }

    private void startAnimation(Transition animation, String message) {
        currentAnimation = animation;
        animationInstruction = roundStateSubtitle.getText();
        roundStateSubtitle.setText(message);
        updateActionAvailability();
        animation.setOnFinished(event -> completeAnimation());
        animation.play();
    }

    private void skipAnimation() {
        if (currentAnimation != null) {
            currentAnimation.setOnFinished(null);
            currentAnimation.stop();
            completeAnimation();
            refresh();
        }
    }

    private void completeAnimation() {
        CardAnimations.reset(dealerCardBox);
        CardAnimations.reset(player1CardBox);
        CardAnimations.reset(player2CardBox);
        currentAnimation = null;
        roundStateSubtitle.setText(animationInstruction);
        updateActionAvailability();
    }

    private void updateActionAvailability() {
        boolean moving = currentAnimation != null;
        int active = gameService.getActivePlayer();
        boolean showdown = gameService.getPhase() == GamePhase.SHOWDOWN;
        startDealButton.setDisable(moving || !gameService.canDeal());
        p1PlayButton.setDisable(moving || active != 1);
        p1FoldButton.setDisable(moving || active != 1);
        p2PlayButton.setDisable(moving || active != 2);
        p2FoldButton.setDisable(moving || active != 2);
        viewResultsButton.setDisable(moving || !showdown);
        handHelpButton.setDisable(moving || active == 0);
    }

    @FXML private void handleHideHints() {
        hintsShown = !hintsShown;
        hintDescriptionLabel.setVisible(hintsShown);
        hintButton.setText(hintsShown ? "Hide Hints" : "Show Hints");
    }

    @FXML private void handleShowHandHelp() {
        if (currentAnimation == null && gameService.getActivePlayer() != 0) {
            setHandHelpVisible(true);
            closeHandHelpButton.requestFocus();
        }
    }

    @FXML private void handleCloseHandHelp() {
        setHandHelpVisible(false);
        handHelpButton.requestFocus();
    }

    private void setHandHelpVisible(boolean visible) {
        handHelpPane.setVisible(visible);
        handHelpPane.setManaged(visible);
        tableControls.setDisable(visible);
    }

    private void refresh() {
        GameRound round = gameService.getRound();
        PlayerState one = round.playerOne();
        PlayerState two = round.playerTwo();
        GamePhase phase = round.phase();
        boolean wagering = phase == GamePhase.WAGERING;
        boolean showdown = phase == GamePhase.SHOWDOWN;
        dealerShowdownLabel.setVisible(showdown);
        int active = gameService.getActivePlayer();

        wagerOverlayPane.setVisible(wagering);
        wagerOverlayPane.setManaged(wagering);
        if (wagerOverlayBackdrop != null) {
            wagerOverlayBackdrop.setVisible(wagering);
            wagerOverlayBackdrop.setManaged(wagering);
        }
        roundStateLabel.setVisible(!wagering);
        roundStateSubtitle.setVisible(!wagering);
        updateActionAvailability();
        viewResultsButton.setVisible(showdown);
        handHelpButton.setVisible(active != 0);
        p1PlayButton.setText(one.wagersConfirmed() ? "Play " + money(one.anteWager()) : "Play");
        p2PlayButton.setText(two.wagersConfirmed() ? "Play " + money(two.anteWager()) : "Play");
        p1PlayButton.setAccessibleText(one.name() + " " + p1PlayButton.getText());
        p2PlayButton.setAccessibleText(two.name() + " " + p2PlayButton.getText());

        updateWagerControls(one, p1AnteField, p1PairPlusField, p1ConfirmButton, p1WagerStatusLabel);
        updateWagerControls(two, p2AnteField, p2PairPlusField, p2ConfirmButton, p2WagerStatusLabel);

        p1CumulativeWinningsLabel.setText(one.name() + ": " + money(one.cumulativeWinnings()));
        p2CumulativeWinningsLabel.setText(two.name() + ": " + money(two.cumulativeWinnings()));
        p1AnteDisplayLabel.setText("Ante: " + money(one.wagersConfirmed() ? one.anteWager() : one.carriedAnte()));
        p2AnteDisplayLabel.setText("Ante: " + money(two.wagersConfirmed() ? two.anteWager() : two.carriedAnte()));
        p1PairPlusDisplayLabel.setText("Pair Plus: " + money(one.pairPlusWager()));
        p2PairPlusDisplayLabel.setText("Pair Plus: " + money(two.pairPlusWager()));
        int firstWager = one.currentWager();
        int secondWager = two.currentWager();
        p1CurrentWagerLabel.setText(money(firstWager));
        p2CurrentWagerLabel.setText(money(secondWager));
        totalBetsLabel.setText(money(firstWager + secondWager));
        p1DecisionLabel.setText(decisionText(one, active == 1));
        p2DecisionLabel.setText(decisionText(two, active == 2));

        if (!showdown) {
            CardDisplay.show(dealerCardBox, round.dealerHand(), CardVisibility.forDealer(phase), 105, 150);
            CardDisplay.show(player1CardBox, one.hand(), CardVisibility.forPlayer(phase, 1), 105, 150);
            CardDisplay.show(player2CardBox, two.hand(), CardVisibility.forPlayer(phase, 2), 105, 150);
        }

        player1CardBox.getStyleClass().remove("showdown-winner");
        player1CardBox.getStyleClass().remove("showdown-loser");
        player2CardBox.getStyleClass().remove("showdown-winner");
        player2CardBox.getStyleClass().remove("showdown-loser");
        indicateActiveTurn(player1CardBox, p1DecisionLabel, active == 1);
        indicateActiveTurn(player2CardBox, p2DecisionLabel, active == 2);

        String title;
        String instruction;

        if (wagering) {
            title = "PLACE YOUR BETS";

            if (gameService.canDeal()) {
                instruction = "Both wagers confirmed. Select Deal.";
            } else if (one.wagersConfirmed()) {
                instruction = "Waiting for Player 2 to confirm.";
            } else if (two.wagersConfirmed()) {
                instruction = "Waiting for Player 1 to confirm.";
            } else {
                instruction = "Confirm both players’ wagers before dealing.";
            }

            wagerPhaseLabel.setText("Round " + round.roundNumber() + " | " + instruction);
        } else if (active != 0) {
            title = "PLAYER " + active + "'S TURN";
            PlayerState current = gameService.getPlayer(active);
            instruction = current.name() + ": Play " + money(current.anteWager()) + " to match Ante, or Fold and forfeit Ante / Pair Plus.";
        } else if (showdown) {
            title = "SHOWDOWN";
            RoundResult preview = gameService.previewRound();
            displayShowdown(preview);
            instruction = "Select View Results to settle the round.";
        } else {
            title = "RESULTS";
            instruction = "Round settled.";
        }

        roundStateLabel.setText("Round " + round.roundNumber() + " | " + title);
        roundStateSubtitle.setText(instruction);
        hintTitleLabel.setText(title);

        if (showdown) {
            hintDescriptionLabel.setText("Bright cards establish rank; other cards may break ties. View Results settles the round.");
        } else {
            hintDescriptionLabel.setText(instruction);
        }
    }

    private void displayShowdown(RoundResult preview) {
        dealerShowdownLabel.setText(ResultText.dealer(preview));
        CardDisplay.showRanked(dealerCardBox, preview.dealerHand(), 105, 150);
        displayShowdownPlayer(preview.playerOne(), player1CardBox, p1DecisionLabel);
        displayShowdownPlayer(preview.playerTwo(), player2CardBox, p2DecisionLabel);
    }

    private void displayShowdownPlayer(PlayerRoundResult player, HBox cards, Label label) {
        CardDisplay.showRanked(cards, player.hand(), 105, 150);
        label.setText(player.name() + " — " + ResultText.outcome(player.outcome()));

        if (player.outcome() == RoundOutcome.PLAYER_WINS) {
            cards.getStyleClass().add("showdown-winner");
        } else if (player.outcome() == RoundOutcome.DEALER_WINS || player.outcome() == RoundOutcome.FOLDED) {
            cards.getStyleClass().add("showdown-loser");
        }
    }

    private void updateWagerControls(PlayerState player, TextField ante, TextField pp, Button confirm, Label status) {
        boolean locked = player.wagersConfirmed();
        ante.setDisable(locked || player.carriedAnte() != 0);
        pp.setDisable(locked);
        confirm.setDisable(locked || gameService.getPhase() != GamePhase.WAGERING);
        confirm.setText(locked ? "Confirmed" : "Confirm");

        if (locked) {
            status.setText("Wagers confirmed: " + money(player.currentWager()) + ".");
            status.getStyleClass().remove("wager-feedback-error");
            status.getStyleClass().remove("wager-feedback-confirmed");
            status.getStyleClass().add("wager-feedback-confirmed");
        }
    }

    private void indicateActiveTurn(HBox cards, Label decision, boolean active) {
        cards.getStyleClass().remove("active-turn");
        decision.getStyleClass().remove("active-turn-label");

        if (active) {
            cards.getStyleClass().add("active-turn");
            decision.getStyleClass().add("active-turn-label");
        }
    }

    private String decisionText(PlayerState player, boolean active) {
        if (player.decision() == PlayerDecision.PLAY) {
            return player.name() + " — Played (" + money(player.playWager()) + ")";
        }

        if (player.decision() == PlayerDecision.FOLD) { return player.name() + " — Folded"; }

        return player.name() + (active ? " — YOUR TURN" : " — Waiting");
    }

    private String money(int amount) {
        if (amount < 0) { return "-$" + Math.abs(amount); }

        return "$" + amount;
    }
}
