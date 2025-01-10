package hr.algebra.greatwesterntrail;

import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.GameState;
import hr.algebra.greatwesterntrail.model.PlayerMode;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import hr.algebra.greatwesterntrail.utils.GameStateUtils;
import hr.algebra.greatwesterntrail.utils.SceneUtils;
import hr.algebra.greatwesterntrail.utils.UIUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class GreatWesternTrailApplication extends Application {

    public static final String HOST = "localhost";
    public static final int PLAYER_TWO_SERVER_PORT = 1989;
    public static final int PLAYER_ONE_SERVER_PORT = 1990;

    public static PlayerMode playerMode;

    @Override
    public void start(Stage stage) throws IOException {
        String title = (playerMode != null)
                ? "Welcome, " + playerMode.name() + " to Great Western Trail Game!"
                : "Welcome, to Great Western Trail Game!";
        SceneUtils.loadMainScene(stage, GreatWesternTrailApplication.class, "view/board.fxml", title);
    }

    public static void main(String[] args) {
        try {
            playerMode = PlayerMode.valueOf(args[0]);
            if(PlayerMode.PLAYER_TWO.name().equals(playerMode.name())) {
                Thread serverThread = new Thread(() -> playerTwoAcceptRequests());
                serverThread.start();
            }
            else if(PlayerMode.PLAYER_ONE.name().equals(playerMode.name())) {
                Thread serverThread = new Thread(() -> playerOneAcceptRequests());
                serverThread.start();
            }
            launch();
        }
        catch (IllegalArgumentException ex) {
            System.out.println("The only options for the user name are: "
                    + PlayerMode.PLAYER_ONE + ", " + PlayerMode.PLAYER_TWO
                    + " or " + PlayerMode.SINGLE_PLAYER);
        }
    }

    private static void playerTwoAcceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PLAYER_TWO_SERVER_PORT)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());
                new Thread(() ->  processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void playerOneAcceptRequests() {
        try (ServerSocket serverSocket = new ServerSocket(PLAYER_ONE_SERVER_PORT)){
            System.err.printf("Server listening on port: %d%n", serverSocket.getLocalPort());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.err.printf("Client connected from port %s%n", clientSocket.getPort());
                new Thread(() -> processSerializableClient(clientSocket)).start();
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processSerializableClient(Socket clientSocket) {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){

            GameState receivedState = (GameState) ois.readObject();
            Platform.runLater(() -> {
                BoardController.getInstance().gameState = receivedState;
                GameStateUtils.applyLoadedGameState(receivedState);
                BoardController.getInstance().gameState.nextTurn();
                UIUtils.enableGameScreen(BoardController.getInstance());
                if (receivedState.getDialogMessage() != null && !receivedState.getDialogMessage().isEmpty()) {
                    DialogUtils.showDialog("Message from Opponent", receivedState.getDialogMessage(), Alert.AlertType.INFORMATION);
                    receivedState.setDialogMessage(null);
                }

                // popravi
                boolean isFinished = GameStateUtils.checkForWinner(receivedState);
                if (isFinished) {

                } //else {
//                    if (receivedState.isPlayerOneTurn() && PlayerMode.PLAYER_ONE == playerMode) {
//                        UIUtils.enableGameScreen(BoardController.getInstance());
//                    } else if (!receivedState.isPlayerOneTurn() && PlayerMode.PLAYER_TWO == playerMode) {
//                        UIUtils.enableGameScreen(BoardController.getInstance());
//                    }
//                }
            });

            System.out.println("Game state successfuly received: " + BoardController.getInstance().gameState);
            oos.writeObject("Success!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequestFromPlayerOne(GameState gameState) {
        try (Socket clientSocket = new Socket(HOST, PLAYER_TWO_SERVER_PORT)){
            System.err.printf("Client is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequestFromPlayerTwo(GameState gameState) {
        try (Socket clientSocket = new Socket(HOST, PLAYER_ONE_SERVER_PORT)){
            System.err.printf("Client is connecting to %s:%d%n", clientSocket.getInetAddress(), clientSocket.getPort());
            sendSerializableRequest(clientSocket, gameState);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void sendSerializableRequest(Socket client, GameState gameState) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        oos.writeObject(gameState);
        System.out.printf("Received from the server: ", ois.readObject());
    }
}