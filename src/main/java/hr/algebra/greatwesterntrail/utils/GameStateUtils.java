package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

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
                        DialogUtils.showDialog(
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
            DialogUtils.showDialog(
                    "Success",
                    "Congratulations, you have won! \nYou've reached Kansas and have " + player.getVp() + " VPs.",
                    Alert.AlertType.INFORMATION
            );
            if (player.getOnGameOver() != null) { player.getOnGameOver().accept(player); }
        }
        else {
            DialogUtils.showDialog(
                    "Failure",
                    "You've reached Kansas, but you need at least 100 VPs to win. \nYou have " + player.getVp() + ".",
                    Alert.AlertType.ERROR
            );
        }
    }

    public static void saveGame(Player player, Tile[][] tiles) {
        try {
            GameState gameState = new GameState(player, tiles);
            SerializationUtils.write(gameState, SAVE_GAME_FILE_NAME);
            DialogUtils.showDialog("Game saved", "Game state successfully saved!", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            DialogUtils.showDialog("Error", "Failed to save game state.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public static void loadGame(BoardController boardController) {
        try {
            GameState loadedState = SerializationUtils.read(SAVE_GAME_FILE_NAME);
            boardController.player = loadedState.getPlayer();

            Tile[][] tiles = loadedState.getTiles();
            for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
                for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                    Tile tile = tiles[row][col];
                    tile.setIcons();
                    StackPane tileStack = TileUtils.createTileStack(tile, boardController.player, boardController);
                    boardController.boardGrid.add(tileStack, col, row);

                    TileButton tileButton = (TileButton) tileStack.getChildren().getFirst();
                    boardController.tileButtons[row][col] = tileButton;

                    if (boardController.player.getPlayerPosition().getRow() == row && boardController.player.getPlayerPosition().getColumn() == col) {
                        TileUtils.highlightCurrentTile(boardController.player.getPlayerPosition(), boardController.tileButtons);
                    }
                }
            }
            TrainProgressUtils.updateTrainProgressBar(boardController.pbTrain, boardController.player.getTrainProgress());
            DialogUtils.showDialog("Game loaded", "Game state successfully loaded!", Alert.AlertType.INFORMATION);
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showDialog("Error", "Failed to load game state.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
