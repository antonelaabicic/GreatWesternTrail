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

public class BoardController {

    @FXML
    public Button btnPoints, btnMoney, btnWorkers, btnDeck;

    @FXML
    public GridPane boardGrid;
    @FXML
    public VerticalProgressBar pbTrain;

    private TileRepository tileRepository;
    public TileButton[][] tileButtons = new TileButton[TileRepository.GRID_SIZE][TileRepository.GRID_SIZE];
    public Player player;

    public void initialize() {
        player = new Player();
        tileRepository = TileRepository.INSTANCE;
        initializeBoard();
        setupButtons();

        TrainProgressUtils.updateTrainProgressBar(pbTrain, player.getWorkerDeck().getOrDefault(WorkerType.ENGINEER, 0));

        Platform.runLater(() -> {
            boardGrid.requestFocus();
            TileUtils.highlightCurrentTile(player.getPlayerPosition(), tileButtons);
        });

        boardGrid.setOnKeyPressed(event -> GameUtils.handleKeyboardNavigation(event, player, tileButtons));
        player.setOnTrainProgressMaxReached(GameStateUtils::showWinnerDialog);
        player.setOnGameOver(p -> UIUtils.disableGameScreen(this, p));
    }

    private void initializeBoard() {
        boardGrid.getChildren().clear();
        Tile[][] tiles = tileRepository.getTiles();

        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                StackPane tileStack = TileUtils.createTileStack(tile, player, this);
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

    public void onBtnPointsClicked(MouseEvent mouseEvent) {
        DialogUtils.showDialog(
                "Victory points",
                "You have " + player.getVp() + " VPs.",
                Alert.AlertType.INFORMATION);
    }

    public void onBtnMoneyClicked(MouseEvent mouseEvent) {
        DialogUtils.showDialog(
                "Money",
                "You have " + player.getMoney() + "$.",
                Alert.AlertType.INFORMATION);
    }

    public void onBtnWorkersClicked(MouseEvent mouseEvent) {
        SceneUtils.loadScene(
                GreatWesternTrailApplication.class,
                "view/workerDeckDetails.fxml",
                "Worker deck details",
                player
        );
    }

    public void onBtnDeckClicked(MouseEvent mouseEvent) {
        SceneUtils.loadScene(
                GreatWesternTrailApplication.class,
                "view/cowDeckDetails.fxml",
                "Cow deck details",
                player
        );
    }

    public void startNewGame(ActionEvent actionEvent) {
        tileRepository.resetTiles();
        initialize();
        DialogUtils.showDialog("New game", "A new game has started!", Alert.AlertType.INFORMATION);
    }

    public void saveGame(ActionEvent actionEvent) {
        GameStateUtils.saveGame(player, tileRepository.getTiles());
    }

    public void loadGame(ActionEvent actionEvent) {
        GameStateUtils.loadGame(this);
    }

    public void generateDocumentation(ActionEvent actionEvent) {
        try {
            DocumentationUtils.generateDocumentation();
            DialogUtils.showDialog(
                    "Documentation",
                    "HTML documentation successfully generated!",
                    Alert.AlertType.INFORMATION
            );
        } catch (RuntimeException e) {
            DialogUtils.showDialog(
                    "Error",
                    "Something went wrong while generating documentation.",
                    Alert.AlertType.ERROR
            );
        }
    }
}