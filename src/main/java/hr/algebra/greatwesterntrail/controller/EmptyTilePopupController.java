package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EmptyTilePopupController {

    @FXML
    public Button btnConfirm;

    @FXML
    public StackPane cattleExchangePane, hiringCenterPane, trainStationPane;

    private BuildingType selectedBuilding;
    private Player player;
    private final int REQUIRED_BUILDERS = 7;
    private TileButton tileButton;

    public void initialize(Player player, TileButton tileButton) {
        this.player = player;
        this.tileButton = tileButton;

        cattleExchangePane.setOnMouseClicked(event -> selectBuilding(BuildingType.CATTLE_EXCHANGE));
        hiringCenterPane.setOnMouseClicked(event -> selectBuilding(BuildingType.HIRING_CENTER));
        trainStationPane.setOnMouseClicked(event -> selectBuilding(BuildingType.TRAIN_STATION));

        btnConfirm.setOnAction(event -> confirm());
    }

    private void selectBuilding(BuildingType buildingType) {
        selectedBuilding = buildingType;

        cattleExchangePane.getStyleClass().remove("empty-tile-highlight");
        hiringCenterPane.getStyleClass().remove("empty-tile-highlight");
        trainStationPane.getStyleClass().remove("empty-tile-highlight");

        switch (buildingType) {
            case CATTLE_EXCHANGE -> cattleExchangePane.getStyleClass().add("empty-tile-highlight");
            case HIRING_CENTER -> hiringCenterPane.getStyleClass().add("empty-tile-highlight");
            case TRAIN_STATION -> trainStationPane.getStyleClass().add("empty-tile-highlight");
        }
    }

    private void confirm() {
        if (selectedBuilding == null) {
            DialogUtils.showDialog("No selection",
                    "Please select a building to construct.",
                    Alert.AlertType.WARNING);
            return;
        }

        int currentBuilders = player.getWorkerDeck().getOrDefault(WorkerType.BUILDER, 0);
        if (currentBuilders < REQUIRED_BUILDERS) {
            DialogUtils.showDialog(
                    "Not enough builders",
                    "You do not have enough builders to construct any building. \nYou need at least "
                            + REQUIRED_BUILDERS + " builders.",
                    Alert.AlertType.ERROR
            );
            deselectSelectedBuilding();
            return;
        }

        if (selectedBuilding != null && player.getMoney() >= selectedBuilding.getCost()) {
            player.setMoney(player.getMoney() - selectedBuilding.getCost());
            player.setVp(player.getVp() + selectedBuilding.getValue());

            tileButton.getTile().setTileType(TileType.BUILDING);
            tileButton.getTile().setBuildingType(selectedBuilding);
            tileButton.getTile().setIcons();
            tileButton.setGraphic(tileButton.getTile().getIcons());

            DialogUtils.showDialog(
                    "Building constructed!",
                    "You've constructed the " + selectedBuilding + " and earned " + selectedBuilding.getValue()
                            + " VPs! \nYour budget is currently " + player.getMoney() + "$.",
                    Alert.AlertType.INFORMATION
            );
        } else {
            DialogUtils.showDialog(
                    "Requirements not met",
                    "You do not meet the requirements to construct " + selectedBuilding + ".",
                    Alert.AlertType.ERROR
            );
            deselectSelectedBuilding();
        }

        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    private void deselectSelectedBuilding() {
        switch (selectedBuilding) {
            case CATTLE_EXCHANGE -> cattleExchangePane.getStyleClass().remove("empty-tile-highlight");
            case HIRING_CENTER -> hiringCenterPane.getStyleClass().remove("empty-tile-highlight");
            case TRAIN_STATION -> trainStationPane.getStyleClass().remove("empty-tile-highlight");
        }
    }
}