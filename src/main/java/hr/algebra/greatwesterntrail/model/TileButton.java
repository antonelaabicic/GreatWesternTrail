package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import hr.algebra.greatwesterntrail.utils.SceneUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import lombok.Getter;

public class TileButton extends Button {
    @Getter
    private final Tile tile;
    private final Player player;

    private static final int CROSS_HAZARD_COST = 5;
    private static final int REMOVE_HAZARD_COST = 20;
    private static final int CROSS_HAZARD_VP = 5;
    private static final int REMOVE_HAZARD_VP = 20;

    public TileButton(Tile tile, Player player) {
        this.tile = tile;
        this.player = player;
        setGraphic(tile.getIcons());
        this.setOnAction(event -> handleTileAction());
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
        }
    }

    private void showEmptyTileDialog() {
        SceneUtils.loadScene(GreatWesternTrailApplication.class,
                "view/emptyTilePopup.fxml",
                "Empty Tile",
                player,
                this);
    }

    private void handleHazardDialog(String hazardName) {
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
                DialogUtils.showDialog(
                        "Hazard Removed",
                        "You've gained " + REMOVE_HAZARD_VP + " VPs. The hazard has been removed.",
                        Alert.AlertType.INFORMATION);
            } else {
                DialogUtils.showDialog(
                        "Hazard Crossed",
                        "You've paid " + CROSS_HAZARD_COST + "$.",
                        Alert.AlertType.INFORMATION);
            }
        } else {
            DialogUtils.showDialog(
                    "Insufficient Funds",
                    "You do not have enough money to " + (removeHazard ? "remove" : "cross") + " the hazard.",
                    Alert.AlertType.ERROR);
        }
    }


    private void showTrainStationDialog() {

    }

    private void showHiringCenterDialog() {
        SceneUtils.loadScene(GreatWesternTrailApplication.class,
                "view/hiringCenterPopup.fxml",
                "Hiring Center",
                player);
    }

    private void showCattleExchangeDialog() {
        SceneUtils.loadScene(GreatWesternTrailApplication.class,
                "view/cowExchangePopup.fxml",
                "Cow Exchange",
                player);
    }
}
