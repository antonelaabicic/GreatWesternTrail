package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.PlayerMode;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class DialogUtils {
    private DialogUtils() {
    }

    public static void showDialogAndDisable(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();

        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            UIUtils.disableGameScreen(BoardController.getInstance());
            //boardController.gameState.nextTurn();
            NetworkingUtils.sendGameState(BoardController.getInstance().gameState);
        }
    }

    public static void showDialog(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirmDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        ButtonType yesButton = new ButtonType("Remove");
        ButtonType noButton = new ButtonType("Cross");
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            UIUtils.disableGameScreen(BoardController.getInstance());
            //boardController.gameState.nextTurn();
            NetworkingUtils.sendGameState(BoardController.getInstance().gameState);
        }
        return result.isPresent() && result.get() == yesButton;
    }
}
