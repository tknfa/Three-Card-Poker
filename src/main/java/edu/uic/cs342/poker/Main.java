package edu.uic.cs342.poker;

import edu.uic.cs342.poker.service.GameService;
import edu.uic.cs342.poker.ui.ScreenNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("CS 342 - Three Card Poker");
        GameService gameService = new GameService();
        ScreenNavigator navigator = new ScreenNavigator(stage, gameService);
        navigator.showWelcome();
    }

    public static void main(String[] args) {

        launch(args);
    }
}
