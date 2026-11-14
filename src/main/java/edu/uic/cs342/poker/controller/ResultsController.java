package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.ui.ScreenNavigator;
import javafx.fxml.FXML;

public class ResultsController implements ScreenController {

    private ScreenNavigator navigator;

    @Override
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void handlePlayAgain() {
        navigator.showResults();
    }

    @FXML
    private void handleMainMenu() {
        navigator.showWelcome();
    }
}
