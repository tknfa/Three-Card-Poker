package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.ui.ScreenNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameplayController implements ScreenController {
    private ScreenNavigator navigator;

    @FXML
    private Label phaseLabel;

    @Override
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;

        phaseLabel.setText("Round " + navigator.getGameService().getRoundNumber() + " | Wagering - Navigation preview");
    }

    @FXML
    private void handlePreviewResults() {
        navigator.showResults();
    }

    @FXML
    private void handleMainMenu() {
        navigator.showWelcome();
    }
}
