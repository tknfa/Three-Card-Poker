package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.model.GamePhase;
import edu.uic.cs342.poker.service.GameService;
import edu.uic.cs342.poker.ui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class WelcomeController implements ScreenController {
    private ScreenNavigator navigator;
    private GameService gameService;

    @FXML
    private Button startButton;

    @FXML
    private Button themeButton;

    @Override
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
        gameService = navigator.getGameService();

        if (gameService.getPhase() == GamePhase.RESULTS) {
            startButton.setText("Next Round");
        } else if (gameService.getPhase() != GamePhase.READY) {
            startButton.setText("Resume Game");
        } else {
            startButton.setText("Start Game");
        }

        updateThemeButton();
    }

    @FXML
    private void handleStartGame() {
        if (gameService.getPhase() == GamePhase.READY) {
            gameService.startGame();
        } else if (gameService.getPhase() == GamePhase.RESULTS) {
            gameService.nextRound();
        }

        navigator.showGameplay();
    }

    @FXML
    private void handleShowRules() {
        navigator.showRules();
    }

    @FXML
    private void handleToggleTheme() {
        navigator.toggleTheme();
        updateThemeButton();
    }

    private void updateThemeButton() {
        if (navigator.isAlternateTheme()) {
            themeButton.setText("Theme: Crimson — Change");
        } else {
            themeButton.setText("Theme: Navy — Change");
        }
    }

    @FXML
    private void handleQuitGame() {
        navigator.requestQuit();
    }
}