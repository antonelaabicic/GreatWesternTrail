package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.CowType;
import hr.algebra.greatwesterntrail.model.ObjectiveAction;
import hr.algebra.greatwesterntrail.model.PlayerMode;
import hr.algebra.greatwesterntrail.model.WorkerType;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DialogUtils {
    private DialogUtils() {
    }

    public static void showDialog(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showDialogAndDisable(String title, String content, Alert.AlertType alertType) {
        showDialog(title, content, alertType);

        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            UIUtils.disableGameScreen(BoardController.getInstance());
            NetworkingUtils.sendGameState(BoardController.getInstance().gameState);
        }
    }

    public static void showDialogAndDisableWithoutGamestate(String title, String content, Alert.AlertType alertType) {
        showDialog(title, content, alertType);

        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            UIUtils.disableGameScreen(BoardController.getInstance());
        }
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
            NetworkingUtils.sendGameState(BoardController.getInstance().gameState);
        }
        return result.isPresent() && result.get() == yesButton;
    }

    public static String generateBuyTransactionMessage(Map<CowType, Integer> buyQuantities) {
        return generateTransactionMessage(buyQuantities, "bought");
    }

    public static String generateSellTransactionMessage(Map<CowType, Integer> sellQuantities) {
        return generateTransactionMessage(sellQuantities, "sold");
    }

    public static String generateHireTransactionMessage(Map<WorkerType, Integer> hireQuantities) {
        return generateTransactionMessage(hireQuantities, "hired");
    }

    public static String generateFireTransactionMessage(Map<WorkerType, Integer> fireQuantities) {
        return generateTransactionMessage(fireQuantities, "fired");
    }

    private static<T> String generateTransactionMessage(Map<T, Integer> quantities, String action) {
        StringBuilder message = new StringBuilder();
        boolean first = true;

        message.append(GreatWesternTrailApplication.playerMode).append(" has ").append(action).append(": ");

        for (Map.Entry<T, Integer> entry : quantities.entrySet()) {
            T item = entry.getKey();
            int quantity = entry.getValue();
            if (quantity > 0) {
                if (!first) {
                    message.append(", ");
                }
                message.append(quantity).append(" ").append(item.toString());
                if (quantity > 1) {
                    if (item instanceof CowType cow && (cow == CowType.JERSEY || cow == CowType.BLACK_ANGUS)) {
                        message.append("es");
                    } else {
                        message.append("s");
                    }
                }
                first = false;
            }
        }
        return message.toString();
    }
}
