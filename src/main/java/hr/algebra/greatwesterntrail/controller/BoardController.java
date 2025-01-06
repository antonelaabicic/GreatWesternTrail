package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import hr.algebra.greatwesterntrail.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

public class BoardController {
    @FXML
    public Button btnPoints, btnMoney, btnWorkers, btnDeck;
    @FXML
    public GridPane boardGrid;
    @FXML
    public VerticalProgressBar pbTrain1, pbTrain2;

    public TileRepository tileRepository;
    public TileButton[][] tileButtons = new TileButton[TileRepository.GRID_SIZE][TileRepository.GRID_SIZE];
    @Setter
    public GameState gameState;
    @Getter
    public static BoardController instance;
    public BoardController() { instance = this; }

    public void initialize() {
        initializeGameState();
        setupButtons();
        setupProgressBars();

        Platform.runLater(() -> {
            boardGrid.requestFocus();
            highlightPlayers();
        });
        boardGrid.setOnKeyPressed(event -> {
            Player active = gameState.getCurrentPlayer();
            if (active != null) {
                GameUtils.handleKeyboardNavigation(event, active, tileButtons);
                highlightPlayers();
            }
        });
        if (PlayerMode.PLAYER_TWO.name().equals(GreatWesternTrailApplication.playerMode.name())) {
            UIUtils.disableGameScreen(instance);
        }


//        if (gameState.getPlayerOne() != null) {
//            gameState.getPlayerOne().setOnTrainProgressMaxReached(GameStateUtils::showWinnerDialog);
//            gameState.getPlayerOne().setOnGameOver(p -> UIUtils.disableGameScreen(this, p));
//        }
//        if (gameState.getPlayerTwo() != null) {
//            gameState.getPlayerTwo().setOnTrainProgressMaxReached(GameStateUtils::showWinnerDialog);
//            gameState.getPlayerTwo().setOnGameOver(p -> UIUtils.disableGameScreen(this, p));
//        }
    }

    private void initializeGameState() {
        if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            tileRepository = TileRepository.INSTANCE;
            Tile[][] tiles = tileRepository.getTiles();
            this.gameState = new GameState(new Player(), null, tiles, true);
            initializeBoard(gameState.getTiles());
        } else {
            GameState loadedState = GameStateUtils.loadGameFromFile();
            if (loadedState != null || GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
                this.gameState = loadedState;
                GameStateUtils.applyLoadedGameState(loadedState);
            } else {
                tileRepository = TileRepository.INSTANCE;
                Tile[][] tiles = tileRepository.getTiles();
                this.gameState = new GameState(new Player(), new Player(), tiles,
                        GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE); // <3
                GameStateUtils.saveGameToFile(this.gameState);
                GameStateUtils.applyLoadedGameState(this.gameState);
            }
        }
    }

    private void initializeBoard(Tile[][] tiles) {
        boardGrid.getChildren().clear();
        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                StackPane tileStack = TileUtils.createTileStack(tile, gameState.getCurrentPlayer(), this);
                boardGrid.add(tileStack, col, row);

                TileButton tileButton = (TileButton) tileStack.getChildren().getFirst();
                tileButtons[row][col] = tileButton;
            }
        }
    }

    private void setupButtons() {
        btnPoints.setGraphic(ImageUtils.createImageView("../images/trophy_icon.png", 45, 45));
        btnMoney.setGraphic(ImageUtils.createImageView("../images/money_icon.png", 45, 45));
        btnWorkers.setGraphic(ImageUtils.createImageView("../images/worker_icon.png", 45, 45));
        btnDeck.setGraphic(ImageUtils.createImageView("../images/cow_head_icon.png", 45, 45));
    }

    private void setupProgressBars() {
        Player p1 = gameState.getPlayerOne();
        if (p1 != null) {
            TrainProgressUtils.updateTrainProgressBar(pbTrain1, p1.getWorkerDeck().getOrDefault(WorkerType.ENGINEER, 0));
            pbTrain1.setBarColor(Color.RED);
        }

        Player p2 = gameState.getPlayerTwo();
        if (p2 == null) { pbTrain2.setVisible(false); }
        else {
            pbTrain2.setVisible(true);
            TrainProgressUtils.updateTrainProgressBar(pbTrain2, p2.getWorkerDeck().getOrDefault(WorkerType.ENGINEER, 0));
            pbTrain2.setBarColor(Color.BLUE);
        }
    }

    public void onBtnPointsClicked(MouseEvent mouseEvent) {
        Player active = gameState.getCurrentPlayer();
        if (active != null) {
            DialogUtils.showDialog("Victory points", "You have " + active.getVp() + " VPs.", Alert.AlertType.INFORMATION);
        }
    }

    public void onBtnMoneyClicked(MouseEvent mouseEvent) {
        Player active = gameState.getCurrentPlayer();
        if (active != null) {
            DialogUtils.showDialog("Money", "You have " + active.getMoney() + "$.", Alert.AlertType.INFORMATION);
        }
    }

    public void onBtnWorkersClicked(MouseEvent mouseEvent) {
        Player active = gameState.getCurrentPlayer();
        if (active != null) {
            SceneUtils.loadScene(GreatWesternTrailApplication.class, "view/workerDeckDetails.fxml", "Worker deck details", active);
        }
    }

    public void onBtnDeckClicked(MouseEvent mouseEvent) {
        Player active = gameState.getCurrentPlayer();
        if (active != null) {
            SceneUtils.loadScene(GreatWesternTrailApplication.class, "view/cowDeckDetails.fxml", "Cow deck details", active);
        }
    }

    public void startNewGame(ActionEvent actionEvent) {
        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            tileRepository = TileRepository.INSTANCE;
            this.gameState = new GameState(new Player(), new Player(), tileRepository.getTiles(), true);
            GameStateUtils.saveGameToFile(this.gameState);
            GameStateUtils.applyLoadedGameState(this.gameState);
            NetworkingUtils.sendGameState(gameState);
        }
        tileRepository.resetTiles();
        initialize();
        DialogUtils.showDialogAndDisable("New game", "A new game has started!", Alert.AlertType.INFORMATION);
    }

    public void saveGame(ActionEvent actionEvent) { GameStateUtils.saveGame(gameState); }
    public void loadGame(ActionEvent actionEvent) { GameStateUtils.loadGame(); }

    public void generateDocumentation(ActionEvent actionEvent) {
        try {
            DocumentationUtils.generateDocumentation();
            DialogUtils.showDialogAndDisable("Documentation", "HTML documentation successfully generated!", Alert.AlertType.INFORMATION);
        } catch (RuntimeException e) {
            DialogUtils.showDialogAndDisable("Error", "Something went wrong while generating documentation.", Alert.AlertType.ERROR);
        }
    }

    private void highlightPlayers() {
        if (gameState.getPlayerTwo() == null) {
            TileUtils.highlightSinglePlayer(gameState.getPlayerOne().getPlayerPosition(), tileButtons);
        } else {
            TileUtils.highlightTwoPlayers(gameState.getPlayerOne(), gameState.getPlayerTwo(), tileButtons);
        }
    }
}