package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public final class UIUtils {
    private UIUtils() { }

    public static void disableGameScreen(BoardController boardController, Player player) {
        Platform.runLater(() -> {
            boardController.boardGrid.setDisable(true);
            boardController.btnPoints.setDisable(true);
            boardController.btnMoney.setDisable(true);
            boardController.btnWorkers.setDisable(true);
            boardController.btnDeck.setDisable(true);

            if (player.getVp() > 99) {
                DialogUtils.showDialog("Game Over", "You have won!", Alert.AlertType.INFORMATION);
            } else {
                DialogUtils.showDialog("Game Over", "You have lost!", Alert.AlertType.INFORMATION);
            }
        });
    }

    public static void enableGameScreen(BoardController boardController) {
        Platform.runLater(() -> {
            boardController.boardGrid.setDisable(false);
            boardController.btnPoints.setDisable(false);
            boardController.btnMoney.setDisable(false);
            boardController.btnWorkers.setDisable(false);
            boardController.btnDeck.setDisable(false);
        });
    }
}
