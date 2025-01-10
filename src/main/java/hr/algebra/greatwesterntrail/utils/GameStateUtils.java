package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public final class GameStateUtils {

    private static final String SAVE_GAME_FILE_NAME = "game/savedGame.dat";

    private GameStateUtils() { }

    public static void showWinnerDialog(Player player) {
        int bonusVP = 0;
        if (!player.isBonusAwarded()) {
            Tile[][] tiles = TileRepository.INSTANCE.getTiles();
            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    Objective objective = tile.getObjective();
                    if (objective != null && player.hasMetObjective(objective)) {
                        bonusVP += objective.calculateTotalVictoryPoints();
                        player.setObjectiveCompleted(true);
                        DialogUtils.showDialogAndDisable(
                                "Success",
                                "You've achieved game's objective: " + objective.getDescription().toLowerCase()
                                        + "\nThat is why you get extra " + bonusVP + " VPs.",
                                Alert.AlertType.INFORMATION
                        );
                    }
                }
            }
            player.setBonusAwarded(true);
        }

        player.setVp(player.getVp() + bonusVP);
        if (player.getVp() > 99) {
            DialogUtils.showDialogAndDisable(
                    "Success",
                    "Congratulations, you have won! \nYou've reached Kansas and have " + player.getVp() + " VPs.",
                    Alert.AlertType.INFORMATION
            );
            if (player.getOnGameOver() != null) { player.getOnGameOver().accept(player); }
        }
        else {
            DialogUtils.showDialogAndDisable(
                    "Failure",
                    "You've reached Kansas, but you need at least 100 VPs to win. \nYou have " + player.getVp() + ".",
                    Alert.AlertType.ERROR
            );
        }
    }

    public static void saveGame(GameState gameState) {
        try {
            SerializationUtils.write(gameState, SAVE_GAME_FILE_NAME);
            //DialogUtils.showDialogAndDisable("Game saved", "Game state successfully saved!", Alert.AlertType.INFORMATION);
            NetworkingUtils.showDialogAndSendGameStateUpdate(
                    "Save game",
                    GreatWesternTrailApplication.playerMode + " has saved the current game."
            );
        } catch (IOException e) {
            DialogUtils.showDialogAndDisable("Error", "Failed to save game state.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public static void loadGame() {
        try {
            GameState loadedState = SerializationUtils.read(SAVE_GAME_FILE_NAME);
            applyLoadedGameState(loadedState);

            NetworkingUtils.showDialogAndSendGameStateUpdate(
                    "Loaded game",
                    GreatWesternTrailApplication.playerMode + " has loaded last saved game."
            );
            //DialogUtils.showDialogAndDisable("Game loaded", "Game state successfully loaded!", Alert.AlertType.INFORMATION);
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showDialogAndDisable("Error", "Failed to load game state.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public static void saveGameToFile(GameState gameState) {
        try {
            SerializationUtils.write(gameState, SAVE_GAME_FILE_NAME);
        } catch (IOException e) {
            System.err.println("Failed to save game state: " + e.getMessage());
        }
    }

    public static GameState loadGameFromFile() {
        try {
            return SerializationUtils.read(SAVE_GAME_FILE_NAME);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game state: " + e.getMessage());
            return null;
        }
    }

    public static void applyLoadedGameState(GameState loadedState) {
        BoardController boardController = BoardController.getInstance();
        boardController.boardGrid.getChildren().clear();

        boardController.gameState = loadedState;
        Tile[][] tiles = boardController.gameState.getTiles();

        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                tile.setIcons();

                StackPane tileStack = null;
                if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
                    tileStack = TileUtils.createTileStack(
                            tile,
                            GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE ?
                            boardController.gameState.getPlayerOne() : boardController.gameState.getPlayerTwo(),
                            boardController
                    );
                } else {
                    tileStack = TileUtils.createTileStack(
                            tile, boardController.gameState.getCurrentPlayer(), boardController
                    );
                }
                boardController.boardGrid.add(tileStack, col, row);

                TileButton tileButton = (TileButton) tileStack.getChildren().get(0);
                boardController.tileButtons[row][col] = tileButton;
            }
        }

        Player player1 = boardController.gameState.getPlayerOne();
        Player player2 = boardController.gameState.getPlayerTwo();

        if (player1 != null) {
            TrainProgressUtils.updateTrainProgressBar(boardController.pbTrain1, player1.getTrainProgress());
            boardController.pbTrain1.setBarColor(Color.RED);
            boardController.pbTrain1.setVisible(true);
        } else {
            boardController.pbTrain1.setVisible(false);
        }

        if (player2 != null) {
            boardController.pbTrain2.setVisible(true);
            TrainProgressUtils.updateTrainProgressBar(boardController.pbTrain2, player2.getTrainProgress());
            boardController.pbTrain2.setBarColor(Color.BLUE);
        } else {
            boardController.pbTrain2.setVisible(false);
        }

        if (player2 == null) {
            TileUtils.highlightSinglePlayer(player1.getPlayerPosition(), boardController.tileButtons);
        } else {
            TileUtils.highlightTwoPlayers(player1, player2, boardController.tileButtons);
        }
    }

    public static boolean checkForWinner(GameState gameState) {
        Player p1 = gameState.getPlayerOne();
        Player p2 = gameState.getPlayerTwo();

        if (p1 != null && p1.getVp() >= 100) {
            GameStateUtils.showWinnerDialog(p1);
            return true;
        }
        if (p2 != null && p2.getVp() >= 100) {
            GameStateUtils.showWinnerDialog(p2);
            return true;
        }
        return false;
    }
}
