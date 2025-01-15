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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
//        try {
            //GameState loadedState = SerializationUtils.read(SAVE_GAME_FILE_NAME);
            applyLoadedGameState();

            NetworkingUtils.showDialogAndSendGameStateUpdate(
                    "Loaded game",
                    GreatWesternTrailApplication.playerMode + " has loaded last saved game."
            );
//        } catch (IOException | ClassNotFoundException e) {
//            DialogUtils.showDialogAndDisable("Error", "Failed to load game state.", Alert.AlertType.ERROR);
//            e.printStackTrace();
//        }
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

    public static void applyLoadedGameState() {
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

    public static void loadGameReplay() {
        try {
            GameState loadedState = SerializationUtils.read(SAVE_GAME_FILE_NAME);
            applyLoadedGameStateReplay(loadedState);

            NetworkingUtils.showDialogAndSendGameStateUpdate(
                    "Loaded game",
                    GreatWesternTrailApplication.playerMode + " has loaded last saved game."
            );
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showDialogAndDisable("Error", "Failed to load game state.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public static void applyLoadedGameStateReplay(GameState gameState) {
        BoardController.getInstance().boardGrid.getChildren().clear();

        Tile[][] tiles = gameState.getTiles();
        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                tile.setIcons();

                StackPane tileStack = null;
                if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
                    tileStack = TileUtils.createTileStack(
                            tile, BoardController.getInstance().gameState.getPlayerOne(), BoardController.getInstance()
                    );
                    if (tile.getObjective() != null) {
                        ImageUtils.addIconToStackPane(tile.getIcons(), "../images/scroll_icon.png", 20, 20, Pos.TOP_LEFT);
                        TileRepository.INSTANCE.addObjectiveTooltip(tile, tile.getObjective());
                    }

                    TileButton tileButton = (TileButton) tileStack.getChildren().get(0);
                    BoardController.getInstance().tileButtons[row][col] = tileButton;
                }
                BoardController.getInstance().boardGrid.add(tileStack, col, row);
            }
        }

        Player player1 = BoardController.getInstance().gameState.getPlayerOne();

        if (player1 != null) {
            TrainProgressUtils.updateTrainProgressBar(BoardController.getInstance().pbTrain1, player1.getTrainProgress());
            BoardController.getInstance().pbTrain1.setBarColor(Color.RED);
            BoardController.getInstance().pbTrain1.setVisible(true);
        }

        TileUtils.highlightSinglePlayer(player1.getPlayerPosition(), BoardController.getInstance().tileButtons);
    }

    public static void initializeSinglePlayerGameState() {
        BoardController.getInstance().tileRepository = TileRepository.INSTANCE;
        Tile[][] tiles = BoardController.getInstance().tileRepository.getTiles();
        BoardController.getInstance().gameState = new GameState(
                new Player(),
                null,
                tiles,
                true,
                null,
                false
        );
        saveGameToFile(BoardController.getInstance().gameState);
        initializeBoard(BoardController.getInstance().gameState.getTiles());
        GameUtils.setupProgressBars();
    }

    public static void initializeBoard(Tile[][] tiles) {
        BoardController.getInstance().boardGrid.getChildren().clear();
        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                StackPane tileStack = TileUtils.createTileStack(
                        tile, BoardController.getInstance().gameState.getCurrentPlayer(),
                        BoardController.getInstance());
                BoardController.getInstance().boardGrid.add(tileStack, col, row);

                TileButton tileButton = (TileButton) tileStack.getChildren().getFirst();
                BoardController.getInstance().tileButtons[row][col] = tileButton;
            }
        }
    }

    public static void initializeMultiPlayerGameState() {
        GameState loadedState = GameStateUtils.loadGameFromFile();
        if (loadedState != null || GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            BoardController.getInstance().gameState = loadedState;
            applyLoadedGameState();
        } else {
            BoardController.getInstance().tileRepository = TileRepository.INSTANCE;
            Tile[][] tiles = BoardController.getInstance().tileRepository.getTiles();
            BoardController.getInstance().gameState = new GameState(
                    new Player(),
                    new Player(),
                    tiles,
                    GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE,
                    null,
                    false
            );
            saveGameToFile(BoardController.getInstance().gameState);
            applyLoadedGameState();
            GameUtils.setupProgressBars();
        }
    }

    public static void startNewGameSinglePlayer() {
        BoardController.getInstance().tileRepository.resetTiles();
        BoardController.getInstance().initialize();
        DialogUtils.showDialogAndDisable("New game", "A new game has started!", Alert.AlertType.INFORMATION);
    }

    public static void startNewGameMultiPlayer() {
        BoardController.getInstance().tileRepository = TileRepository.INSTANCE;
        BoardController.getInstance().gameState = new GameState(
                new Player(),
                new Player(),
                BoardController.getInstance().tileRepository.getTiles(),
                true,
                null,
                false
        );
        saveGameToFile(BoardController.getInstance().gameState);
        applyLoadedGameState();
        BoardController.getInstance().taChatMessages.clear();
        NetworkingUtils.showDialogAndSendGameStateUpdate(
                "New game",
                GreatWesternTrailApplication.playerMode + " has started a new game."
        );
    }
}
