package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.ConfigurationKey;
import hr.algebra.greatwesterntrail.model.ConfigurationReader;
import hr.algebra.greatwesterntrail.model.GameState;
import hr.algebra.greatwesterntrail.model.PlayerMode;
import javafx.scene.control.Alert;

public class NetworkingUtils {
    public static void sendGameState(GameState gameState) {
        if (GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE) {
            GreatWesternTrailApplication.sendRequestFromPlayer(
                    gameState,
                    ConfigurationReader.getStringValueForKey(ConfigurationKey.HOST),
                    ConfigurationReader.getIntegerValueForKey(ConfigurationKey.PLAYER_TWO_SERVER_PORT)
            );
        }
        else if (GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_TWO) {
            GreatWesternTrailApplication.sendRequestFromPlayer(
                    gameState,
                    ConfigurationReader.getStringValueForKey(ConfigurationKey.HOST),
                    ConfigurationReader.getIntegerValueForKey(ConfigurationKey.PLAYER_ONE_SERVER_PORT)
            );
        }
    }

    public static void showDialogAndSendGameStateUpdate(String title, String content) {
        BoardController boardController = BoardController.getInstance();
        boardController.gameState.setDialogMessage(content);
        DialogUtils.showDialogAndDisableWithoutGamestate(title, content, Alert.AlertType.INFORMATION);
        NetworkingUtils.sendGameState(boardController.gameState);
    }

    public static void showDialogAndSendGameStateUpdateNoDisable(String title, String content) {
        BoardController boardController = BoardController.getInstance();
        boardController.gameState.setDialogMessage(content);
        DialogUtils.showDialog(title, content, Alert.AlertType.INFORMATION);
        NetworkingUtils.sendGameState(boardController.gameState);
    }
}
