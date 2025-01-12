package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.IOException;

public final class GameStateUtils {

    private static final String SAVE_GAME_FILE_NAME = "game/savedGame.dat";

    private GameStateUtils() { }

    public static void saveGame(GameState gameState) {
        try {
            SerializationUtils.write(gameState, SAVE_GAME_FILE_NAME);
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
                    if (tile.getObjective() != null) {
                        ImageUtils.addIconToStackPane(tile.getIcons(), "../images/scroll_icon.png", 20, 20, Pos.TOP_LEFT);
                        TileRepository.INSTANCE.addObjectiveTooltip(tile, tile.getObjective());
                    }

                    TileButton tileButton = (TileButton) tileStack.getChildren().get(0);
                    boardController.tileButtons[row][col] = tileButton;
                }
                boardController.boardGrid.add(tileStack, col, row);
            }
        }

        Player player1 = boardController.gameState.getPlayerOne();
        Player player2 = boardController.gameState.getPlayerTwo();

        if (player1 != null) {
            TrainProgressUtils.updateTrainProgressBar(boardController.pbTrain1, player1.getTrainProgress());
            boardController.pbTrain1.setBarColor(Color.RED);
            boardController.pbTrain1.setVisible(true);
        }

        if (player2 != null) {
            TrainProgressUtils.updateTrainProgressBar(boardController.pbTrain2, player2.getTrainProgress());
            boardController.pbTrain2.setBarColor(Color.BLUE);
            boardController.pbTrain2.setVisible(true);
        }

        TileUtils.highlightTwoPlayers(player1, player2, boardController.tileButtons);
    }
}
