package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import hr.algebra.greatwesterntrail.rmi.ChatRemoteService;
import hr.algebra.greatwesterntrail.rmi.ChatServer;
import hr.algebra.greatwesterntrail.utils.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class BoardController {
    @FXML
    public Button btnPoints, btnMoney, btnWorkers, btnDeck, btnSend;
    @FXML
    public GridPane boardGrid;
    @FXML
    public VerticalProgressBar pbTrain1, pbTrain2;
    @FXML
    public TextField tfChatMessages;
    @FXML
    public TextArea taChatMessages, taLastMove;

    public TileRepository tileRepository;
    public TileButton[][] tileButtons = new TileButton[TileRepository.GRID_SIZE][TileRepository.GRID_SIZE];
    @Setter
    public GameState gameState;
    @Getter
    public static BoardController instance;

    public BoardController() { instance = this; }

    private static Registry registry;
    private static ChatRemoteService chatRemoteService;

    public void initialize() {
        initializeGameState();
        setupButtons();

        Platform.runLater(() -> {
            GameUtils.highlightPlayers();
            GameUtils.toggleChatAndLastMoveVisibility();
            boardGrid.requestFocus();
        });
        boardGrid.setOnKeyPressed(event -> {
            Player active = gameState.getCurrentPlayer();
            if (active != null) {
                GameUtils.handleKeyboardNavigation(event, active, tileButtons);
                GameUtils.highlightPlayers();
            }
        });
        if (GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_TWO) {
            UIUtils.disableGameScreen(instance);
        }



        try {
            registry = LocateRegistry.getRegistry(ChatServer.CHAT_HOST_NAME, ChatServer.RMI_PORT);
            chatRemoteService = (ChatRemoteService) registry.lookup(ChatRemoteService.CHAT_REMOTE_OBJECT_NAME);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        ChatUtils.createAndRunChatTimeline(chatRemoteService, taChatMessages);
    }

    private void initializeGameState() {
        if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            tileRepository = TileRepository.INSTANCE;
            Tile[][] tiles = tileRepository.getTiles();
            this.gameState = new GameState(new Player(), null, tiles, true, null, false);
            initializeBoard(gameState.getTiles());
            GameUtils.setupProgressBars();
        } else {
            GameState loadedState = GameStateUtils.loadGameFromFile();
            if (loadedState != null || GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
                this.gameState = loadedState;
                GameStateUtils.applyLoadedGameState(loadedState);
            } else {
                tileRepository = TileRepository.INSTANCE;
                Tile[][] tiles = tileRepository.getTiles();
                this.gameState = new GameState(new Player(), new Player(), tiles,
                        GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE, null, false);
                GameStateUtils.saveGameToFile(this.gameState);
                GameStateUtils.applyLoadedGameState(this.gameState);
                GameUtils.setupProgressBars();
            }
        }
    }

    public void initializeBoard(Tile[][] tiles) {
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

    public void onBtnPointsClicked(MouseEvent mouseEvent) {
        GameUtils.showPlayerInfo("Victory points", "You have " + gameState.getCurrentPlayer().getVp() + " VPs.");
    }

    public void onBtnMoneyClicked(MouseEvent mouseEvent) {
        GameUtils.showPlayerInfo("Money", "You have " + gameState.getCurrentPlayer().getMoney() + "$.");
    }

    public void onBtnWorkersClicked(MouseEvent mouseEvent) {
        GameUtils.loadPlayerDeckDetails("view/workerDeckDetails.fxml", "Worker deck details");
    }

    public void onBtnDeckClicked(MouseEvent mouseEvent) {
        GameUtils.loadPlayerDeckDetails("view/cowDeckDetails.fxml", "Cow deck details");
    }

    public void startNewGame(ActionEvent actionEvent) {
        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            tileRepository = TileRepository.INSTANCE;
            this.gameState = new GameState(new Player(), new Player(), tileRepository.getTiles(), true, null, false);
            GameStateUtils.saveGameToFile(this.gameState);
            GameStateUtils.applyLoadedGameState(this.gameState);
            taChatMessages.clear();
            NetworkingUtils.showDialogAndSendGameStateUpdate(
                    "New game",
                    GreatWesternTrailApplication.playerMode + " has started a new game."
            );
        } else {
            tileRepository.resetTiles();
            initialize();
            DialogUtils.showDialogAndDisable("New game", "A new game has started!", Alert.AlertType.INFORMATION);
        }
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

    public void sendChatMessage(ActionEvent actionEvent) {
        ChatUtils.sendChatMessage(tfChatMessages, taChatMessages, chatRemoteService);
        tfChatMessages.clear();
        boardGrid.requestFocus();
    }
}