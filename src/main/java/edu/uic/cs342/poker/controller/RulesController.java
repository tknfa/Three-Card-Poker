package edu.uic.cs342.poker.controller;

import edu.uic.cs342.poker.ui.ScreenNavigator;
import javafx.fxml.FXML;

public class RulesController implements ScreenController {

    private ScreenNavigator navigator;

    @Override
    public void setNavigator(ScreenNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    private void handleBackButton() {
        navigator.showWelcome();
    }
}
