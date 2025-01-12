package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.io.IOException;

public final class GameUtils {

    private static boolean hasMovedThisTurn = false;

    private GameUtils() { }

    public static void handleKeyboardNavigation(KeyEvent event, Player player, TileButton[][] tileButtons) {
        int currentRow = player.getPlayerPosition().getRow();
        int currentCol = player.getPlayerPosition().getColumn();
        int newRow = currentRow;
        int newCol = currentCol;

        switch (event.getCode()) {
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
            case ENTER -> {
                TileButton currentTileButton = tileButtons[currentRow][currentCol];
                currentTileButton.performAction();
                hasMovedThisTurn = false;
                return;
            }
            default -> { return; }
        }

        if (hasMovedThisTurn) { return; }
        if (isValidPosition(newRow, newCol)) {
            player.setPlayerPosition(newRow, newCol);
            TileUtils.highlightSinglePlayer(player.getPlayerPosition(), tileButtons);
            hasMovedThisTurn = true;
        } else {
            DialogUtils.showDialog("Out of Bounds", "You cannot move outside the board!", Alert.AlertType.WARNING);
        }
    }

    private static boolean isValidPosition(int row, int column) {
        return row >= 0 && row < TileRepository.GRID_SIZE && column >= 0 && column < TileRepository.GRID_SIZE;
    }

    public static void showWinnerDialog(Player player) {
        int bonusVP = 0;
        if (!player.isBonusAwarded()) {
            Tile[][] tiles = getTiles();
            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    Objective objective = tile.getObjective();
                    if (objective != null && player.hasMetObjective(objective)) {
                        bonusVP += objective.calculateTotalVictoryPoints();
                        player.setObjectiveCompleted(true);
                        DialogUtils.showDialogAndDisable(
                                "Success",
                                GreatWesternTrailApplication.playerMode + " has achieved game's objective.\n" +
                                        "They get extra " + bonusVP + " VPs.",
                                Alert.AlertType.INFORMATION
                        );
                    }
                }
            }
            player.setBonusAwarded(true);
        }

        player.setVp(player.getVp() + bonusVP);
        if (player.getVp() > 99 && player.getPlayerPosition().getRow() == 0 && player.getPlayerPosition().getColumn() == 0) {
            BoardController.getInstance().gameState.setGameFinished(true);
            NetworkingUtils.showDialogAndSendGameStateUpdate(
                    "Success",
                    GreatWesternTrailApplication.playerMode +" has won! \nThey're in " +
                            "Kansas and have " + player.getVp() + " VPs."
            );
            if (player.getOnGameOver() != null) { player.getOnGameOver().accept(player); }
        }
        else if (player.getVp() < 99 && player.getPlayerPosition().getRow() == 0 && player.getPlayerPosition().getColumn() == 0) {
            DialogUtils.showDialogAndDisable(
                    "Failure",
                    GreatWesternTrailApplication.playerMode + " is in Kansas, but needs min. 100 VPs " +
                            "to win. \nThey have " + player.getVp() + ".",
                    Alert.AlertType.ERROR
            );
        }
    }

    private static Tile[][] getTiles() {
        if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            return TileRepository.INSTANCE.getTiles();
        } else {
            return BoardController.getInstance().gameState.getTiles();
        }
    }

    public static void setupProgressBars() {
        Player p1 = BoardController.getInstance().gameState.getPlayerOne();
        if (p1 != null) {
            TrainProgressUtils.updateTrainProgressBar(
                    BoardController.getInstance().pbTrain1,
                    p1.getWorkerDeck().getOrDefault(WorkerType.ENGINEER, 0)
            );
            BoardController.getInstance().pbTrain1.setBarColor(Color.RED);
        }

        Player p2 = BoardController.getInstance().gameState.getPlayerTwo();
        if (p2 == null) { BoardController.getInstance().pbTrain2.setVisible(false); }
        else {
            BoardController.getInstance().pbTrain2.setVisible(true);
            TrainProgressUtils.updateTrainProgressBar(
                    BoardController.getInstance().pbTrain2,
                    p2.getWorkerDeck().getOrDefault(WorkerType.ENGINEER, 0)
            );
            BoardController.getInstance().pbTrain2.setBarColor(Color.BLUE);
        }
    }

    public static void highlightPlayers() {
        if (BoardController.getInstance().gameState.getPlayerTwo() == null) {
            TileUtils.highlightSinglePlayer(
                    BoardController.getInstance().gameState.getPlayerOne().getPlayerPosition(),
                    BoardController.getInstance().tileButtons
            );
        } else {
            TileUtils.highlightTwoPlayers(
                    BoardController.getInstance().gameState.getPlayerOne(),
                    BoardController.getInstance().gameState.getPlayerTwo(),
                    BoardController.getInstance().tileButtons
            );
        }
    }

    public static void showPlayerInfo(String title, String message) {
        Player active = BoardController.getInstance().gameState.getCurrentPlayer();
        if (active != null) {
            DialogUtils.showDialog(title, message, Alert.AlertType.INFORMATION);
        }
    }

    public static void loadPlayerDeckDetails(String fxmlPath, String title) {
        Player active = BoardController.getInstance().gameState.getCurrentPlayer();
        if (active != null) {
            SceneUtils.loadScene(GreatWesternTrailApplication.class, fxmlPath, title, active);
        }
    }

    public static void toggleChatAndLastMoveVisibility() {
        if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            BoardController.getInstance().taChatMessages.setVisible(false);
            BoardController.getInstance().taChatMessages.setManaged(false);
            BoardController.getInstance().btnSend.setVisible(false);
            BoardController.getInstance().btnSend.setManaged(false);
            BoardController.getInstance().tfChatMessages.setVisible(false);
            BoardController.getInstance().tfChatMessages.setManaged(false);
        } else {
            BoardController.getInstance().taLastMove.setVisible(false);
            BoardController.getInstance().taLastMove.setManaged(false);
        }
    }
}
