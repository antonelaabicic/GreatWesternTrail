package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.utils.DialogUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import lombok.Getter;

import static hr.algebra.greatwesterntrail.model.TileType.HAZARD;

public class TileButton extends Button {
    @Getter
    private final Tile tile;
    private final Player player;

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
    }

    private void handleHazardDialog(String hazardName) {
        boolean removeHazard = DialogUtils.showConfirmDialog(
                "Hazard Encountered!",
                "Do you want to cross the " + hazardName
                        + " hazard or remove it? \nCrossing costs 5$ and removing costs 20$.");

        if (removeHazard) {
            player.setMoney(player.getMoney() - 20);
            player.setVp(player.getVp() + 20);
            tile.setTileType(TileType.EMPTY);
            tile.setHazardType(null);
            setGraphic(tile.getIcons());
            DialogUtils.showDialog(
                    "Hazard Removed",
                    "You've gained 20 VPs. The hazard has been removed.",
                    Alert.AlertType.INFORMATION);
        } else {
            player.setMoney(player.getMoney() - 5);
            player.setVp(player.getVp() + 5);
            DialogUtils.showDialog(
                    "Hazard Crossed",
                    "You've paid 5 VPs.",
                    Alert.AlertType.INFORMATION);
        }
    }

    private void showTrainStationDialog() {
    }

    private void showHiringCenterDialog() {
    }

    private void showCattleExchangeDialog() {
    }
}
