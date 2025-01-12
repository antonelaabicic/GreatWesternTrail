package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.utils.*;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import lombok.Getter;

import java.util.Random;

public class TileButton extends Button {
    @Getter
    private final Tile tile;
    private final Player player;
    private final BoardController boardController;

    private static final int CROSS_HAZARD_COST = 5;
    private static final int REMOVE_HAZARD_COST = 20;
    private static final int CROSS_HAZARD_VP = 5;
    private static final int REMOVE_HAZARD_VP = 20;

    public TileButton(Tile tile, Player player, BoardController boardController) {
        this.tile = tile;
        this.player = player;
        this.boardController = boardController;
        setGraphic(tile.getIcons());

        this.setOnMouseClicked(Event::consume);
        this.setFocusTraversable(false);
    }

    public void performAction() {
        handleTileAction();
    }

    private void handleTileAction() {
        switch (tile.getTileType()) {
            case EMPTY -> showEmptyTileDialog();
            case HAZARD -> {
                switch (tile.getHazardType()) {
                    case FLOOD -> handleHazardDialog("flood");
                    case ROCKSLIDE -> handleHazardDialog("rockslide");
                    case BANDITS -> handleHazardDialog("bandits");
                    default -> throw new IllegalArgumentException("Invalid hazard type!");
                }
            }
            case BUILDING -> {
                switch (tile.getBuildingType()) {
                    case CATTLE_EXCHANGE -> showCattleExchangeDialog();
                    case HIRING_CENTER -> showHiringCenterDialog();
                    case TRAIN_STATION -> showTrainStationDialog();
                    default -> throw new IllegalArgumentException("Invalid building type!");
                }
            }
            case END -> {
                GameUtils.showWinnerDialog(player);
            }
        }
    }

    private void showEmptyTileDialog() {
        SceneUtils.loadScene(GreatWesternTrailApplication.class,
                "view/emptyTilePopup.fxml",
                "Empty Tile",
                player,
                this,
                boardController);
    }

    private void handleHazardDialog(String hazardName) {
        String formattedHazardName = hazardName.substring(0, 1).toUpperCase() + hazardName.substring(1);
        boolean removeHazard = DialogUtils.showConfirmDialog(
                "Hazard Encountered!",
                "Do you want to cross the " + hazardName + " hazard or remove it? " +
                        "\nCrossing costs " + CROSS_HAZARD_COST +"$ and removing " + REMOVE_HAZARD_COST +"$.");

        int cost = removeHazard ? REMOVE_HAZARD_COST : CROSS_HAZARD_COST;
        int vpReward = removeHazard ? REMOVE_HAZARD_VP : CROSS_HAZARD_VP;

        if (player.getMoney() >= cost) {
            player.setMoney(player.getMoney() - cost);
            player.setVp(player.getVp() + vpReward);

            if (removeHazard) {
                tile.setTileType(TileType.EMPTY);
                tile.setHazardType(null);
                tile.setIcons();
                setGraphic(tile.getIcons());

                NetworkingUtils.showDialogAndSendGameStateUpdate("Success",
                        GreatWesternTrailApplication.playerMode.name() + " has gained " + REMOVE_HAZARD_VP +
                                " VPs. " + formattedHazardName + " hazard has been removed.");

            } else {
                NetworkingUtils.showDialogAndSendGameStateUpdate("Success",
                        GreatWesternTrailApplication.playerMode.name() + " has paid " + CROSS_HAZARD_COST + "$. " +
                                formattedHazardName + " hazard has been crossed.");
            }
        } else {
            DialogUtils.showDialogAndDisable(
                    "Insufficient Funds",
                    GreatWesternTrailApplication.playerMode.name() + " doesn't have enough money to " + (removeHazard ? "remove" : "cross") + hazardName,
                    Alert.AlertType.ERROR);
        }
    }

    private void showTrainStationDialog() {
        Random random = new Random();
        int steps = 1 + random.nextInt(5);
        player.incrementTrainProgress(steps);
        player.setTrainProgress(player.getTrainProgress());

        if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
            TrainProgressUtils.updateTrainProgressBar(
                    GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE ?
                            boardController.pbTrain1 : boardController.pbTrain2, player.getTrainProgress());
        } else {
            TrainProgressUtils.updateTrainProgressBar(boardController.pbTrain1, player.getTrainProgress());
        }
        if (player.getVp() > 99 && player.getTrainProgress() > 21) {
            BoardController.getInstance().gameState.setGameFinished(true);
            NetworkingUtils.showDialogAndSendGameStateUpdate(
                    "Success",
                    GreatWesternTrailApplication.playerMode +" has won! \nThey're in " +
                            "Kansas and have " + player.getVp() + " VPs."
            );
        } else {
            NetworkingUtils.showDialogAndSendGameStateUpdate("Success",
                    GreatWesternTrailApplication.playerMode.name() + " has moved " + steps + " on the train track!");
        }
    }

    private void showHiringCenterDialog() {
        SceneUtils.loadScene(GreatWesternTrailApplication.class,
                "view/hiringCenterPopup.fxml",
                "Hiring Center",
                player,
                boardController);
    }

    private void showCattleExchangeDialog() {
        SceneUtils.loadScene(GreatWesternTrailApplication.class,
                "view/cowExchangePopup.fxml",
                "Cow Exchange",
                player,
                boardController);
    }
}
