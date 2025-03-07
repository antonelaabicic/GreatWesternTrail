package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import hr.algebra.greatwesterntrail.rmi.ChatRemoteService;
import hr.algebra.greatwesterntrail.rmi.ChatServer;
import hr.algebra.greatwesterntrail.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    @FXML
    public MenuItem miReplay;

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
            boardGrid.requestFocus();
        });
        if (GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_TWO) {
            UIUtils.disableGameScreen(instance);
        }

        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            try {
                registry = LocateRegistry.getRegistry(ChatServer.CHAT_HOST_NAME, ChatServer.RMI_PORT);
                chatRemoteService = (ChatRemoteService) registry.lookup(ChatRemoteService.CHAT_REMOTE_OBJECT_NAME);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
            ChatUtils.createAndRunChatTimeline(chatRemoteService, taChatMessages);
        } else {
            taLastMove.clear();
            GameMoveUtils.createAndRunTheLastGameMoveTimeline(taLastMove);
        }
    }

    private void initializeGameState() {
        GameUtils.deleteFile(XmlUtils.XML_FILE_NAME);
        GameUtils.toggleVisibilityReplayMenuItem();
        if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            GameStateUtils.initializeSinglePlayerGameState();
        } else {
            GameStateUtils.initializeMultiPlayerGameState();
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
            GameStateUtils.startNewGameMultiPlayer();
        } else {
            GameStateUtils.startNewGameSinglePlayer();
        }
        boardGrid.requestFocus();
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

    public void replayGame(ActionEvent actionEvent) {
        List<GameMove> gameMoves = XmlUtils.parse(XmlUtils.XML_FILE_NAME);
        final AtomicInteger counter = new AtomicInteger(0);

        Timeline replay = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            GameMove gameMove = gameMoves.get(counter.get());

            XmlUtils.restoreBoard(gameMove.getTiles());

            PlayerState playerState = gameMove.getPlayerState();
            Position playerPosition = playerState.getPosition();

            TileUtils.highlightSinglePlayer(playerPosition, tileButtons);
            counter.set(counter.get() + 1);
        }), new KeyFrame(Duration.seconds(1)));
        replay.setCycleCount(gameMoves.size());
        replay.play();
    }
}