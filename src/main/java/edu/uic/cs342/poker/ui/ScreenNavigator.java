package edu.uic.cs342.poker.ui;

import java.io.IOException;

import edu.uic.cs342.poker.controller.ScreenController;
import edu.uic.cs342.poker.model.GamePhase;
import edu.uic.cs342.poker.service.GameService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ScreenNavigator {
    private final Stage stage;
    private final GameService gameService;
    private boolean alternateTheme;

    public ScreenNavigator(Stage stage, GameService gameService) {
        this.stage = stage;
        this.gameService = gameService;

        stage.setOnCloseRequest(event -> {
            event.consume();
            requestQuit();
        });
    }

    public GameService getGameService() {
        return gameService;
    }

    public void showWelcome() {
        showScreen("welcome.fxml");
    }

    public void showRules() {
        showScreen("rules.fxml");
    }

    public void showGameplay() {
        if (gameService.getPhase() == GamePhase.READY) {
            showWelcome();
            return;
        }
        if (gameService.getPhase() == GamePhase.RESULTS) {
            showResults();
            return;
        }
        showScreen("gameplay.fxml");
    }

    public void showResults() {
        if (gameService.getPhase() != GamePhase.RESULTS) {
            showGameplay();
            return;
        }
        showScreen("results.fxml");
    }

    private void showScreen(String fileName) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenNavigator.class.getResource("/edu/uic/cs342/poker/fxml/" + fileName));
            Parent root = loader.load();

            ScreenController controller = loader.getController();
            controller.setNavigator(this);

            setScene(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not open screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void setScene(Parent root) {
        Scene scene = new Scene(root, 1000, 650);
        if (alternateTheme) {
            root.getStyleClass().add("classic-green");
        }
        scene.getStylesheets().add(ScreenNavigator.class.getResource("/edu/uic/cs342/poker/css/style.css").toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public boolean isAlternateTheme() {
        return alternateTheme;
    }

    public void toggleTheme() {
        alternateTheme = !alternateTheme;
        Parent root = stage.getScene().getRoot();
        if (alternateTheme) {
            root.getStyleClass().add("classic-green");
        } else {
            root.getStyleClass().remove("classic-green");
        }
    }

    public void requestQuit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Three Card Poker");
        alert.setHeaderText("Are you sure you want to leave?");

        if (gameService.getPhase() == GamePhase.READY) {
            alert.setContentText("Choose OK to quit or Cancel to stay on this screen.");
        } else {
            alert.setContentText("Quitting will end the current game and discard its round and winnings. " + "Choose OK to quit or Cancel to keep playing.");
        }

        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            stage.close();
        }
    }
}