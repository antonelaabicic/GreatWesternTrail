package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.Tile;
import hr.algebra.greatwesterntrail.model.TileButton;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import hr.algebra.greatwesterntrail.utils.ImageUtils;
import hr.algebra.greatwesterntrail.utils.SceneUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BoardController {

    @FXML
    public Button btnPoints;
    @FXML
    public Button btnMoney;
    @FXML
    public Button btnWorkers;
    @FXML
    public Button btnDeck;

    @FXML
    public GridPane boardGrid;

    private TileRepository tileRepository;
    private TileButton[][] tileButtons = new TileButton[TileRepository.GRID_SIZE][TileRepository.GRID_SIZE];
    private Player player;

    public void initialize() {
        player = new Player();
        tileRepository = TileRepository.INSTANCE;
        initializeBoard();
        setupButtons();
    }

    private void initializeBoard() {
        boardGrid.getChildren().clear();

        Tile[][] tiles = tileRepository.getTiles();

        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                TileButton tileButton = new TileButton(tile, player);
                tileButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                boardGrid.add(tileButton, col, row);
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
        SceneUtils.loadScene("view/workerDeckDetails.fxml", "Worker deck details", player);
    }

    public void onBtnDeckClicked(MouseEvent mouseEvent) {
        SceneUtils.loadScene("view/cowDeckDetails.fxml", "Cow deck details", player);
    }

    public void startNewGame(ActionEvent actionEvent) {
    }

    public void saveGame(ActionEvent actionEvent) {
    }

    public void loadGame(ActionEvent actionEvent) {
    }

    public void generateDocumentation(ActionEvent actionEvent) {
    }
}