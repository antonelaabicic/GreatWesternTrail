package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.PlayerMode;
import hr.algebra.greatwesterntrail.model.WorkerType;
import hr.algebra.greatwesterntrail.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;

public class HiringCenterPopupController {
    @FXML
    public TextField tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity;
    @FXML
    public TextField tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity;

    @FXML
    public Label totalCost;
    @FXML
    public Button btnConfirm;

    private Player player;
    private BoardController boardController;
    private Map<WorkerType, TextField> hireTextFieldMap = new EnumMap<>(WorkerType.class);
    private Map<WorkerType, TextField> fireTextFieldMap = new EnumMap<>(WorkerType.class);

    public void initialize(Player player, BoardController boardController) {
        this.player = player;
        this.boardController = boardController;
        initializeTextFieldMaps();

        PopupUtils.setInputValidation(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);

        btnConfirm.setOnAction(event -> confirm());
        PopupUtils.addCostCalculationListener(this::updateTotalCost,
                tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);

        updateSellingTextFields();
        updateTotalCost();
        player.setOnTrainProgressMaxReached(GameUtils::showWinnerDialog);

        Platform.runLater(() -> {
            Stage stage = (Stage) btnConfirm.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if (GreatWesternTrailApplication.playerMode != PlayerMode.SINGLE_PLAYER) {
                    UIUtils.disableGameScreen(boardController);
                    NetworkingUtils.sendGameState(boardController.gameState);
                }
            });
        });
    }

    private void initializeTextFieldMaps() {
        hireTextFieldMap = PopupUtils.createEnumTextFieldMap(WorkerType.class, tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity);
        fireTextFieldMap = PopupUtils.createEnumTextFieldMap(WorkerType.class, tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);
    }

    private void confirm() {
        Map<WorkerType, Integer> hireQuantities = getWorkerQuantities(hireTextFieldMap);
        Map<WorkerType, Integer> fireQuantities = getWorkerQuantities(fireTextFieldMap);

        if (!canHireWorkers(fireQuantities)) {
            DialogUtils.showDialog("Invalid hiring", "You cannot fire more workers than you currently have.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        int totalCostValue = PopupUtils.calculateTransactionCost(hireQuantities, fireQuantities);
        if (totalCostValue > player.getMoney()) {
            DialogUtils.showDialog("Insufficient funds", "You don't have enough money to complete the purchase.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        int firstEngineerCount = player.getWorkerDeck().getOrDefault(WorkerType.ENGINEER, 0);
        updateWorkerDeck(hireQuantities, fireQuantities);
        int additionalEngineerCount = player.getWorkerDeck().getOrDefault(WorkerType.ENGINEER, 0) ;
        player.incrementTrainProgress(additionalEngineerCount - firstEngineerCount);
        TrainProgressUtils.updateTrainProgressBar(
                GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE ?
                boardController.pbTrain1 : boardController.pbTrain2,
                player.getTrainProgress());

        player.setMoney(player.getMoney() - totalCostValue);
        int earnedVP = PopupUtils.calculateVPs(hireQuantities, fireQuantities);;
        player.setVp(player.getVp() + earnedVP);

        if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            DialogUtils.showDialog("Transaction complete",
                    "You earned " + earnedVP + " VPs from this transaction! Your budget is currently " + player.getMoney() + "$.",
                    Alert.AlertType.INFORMATION);
        } else {
            boolean transactionOccurred = false;

            if (!PopupUtils.areAllQuantitiesZero(hireQuantities)) {
                String hireMessage = DialogUtils.generateHireTransactionMessage(hireQuantities);
                NetworkingUtils.showDialogAndSendGameStateUpdate("Hiring Success", hireMessage);
                transactionOccurred = true;
            }

            if (!PopupUtils.areAllQuantitiesZero(fireQuantities)) {
                String fireMessage = DialogUtils.generateFireTransactionMessage(fireQuantities);
                NetworkingUtils.showDialogAndSendGameStateUpdate("Firing Success", fireMessage);
                transactionOccurred = true;
            }

            if (!transactionOccurred) {
                UIUtils.disableGameScreen(boardController);
                NetworkingUtils.sendGameState(boardController.gameState);
            }
        }
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    private Map<WorkerType, Integer> getWorkerQuantities(Map<WorkerType, TextField> textFieldMap) {
        return PopupUtils.getQuantities(WorkerType.values(), textFieldMap.values().toArray(new TextField[0]));
    }

    private boolean canHireWorkers(Map<WorkerType, Integer> fireQuantities) {
        for (Map.Entry<WorkerType, Integer> entry : fireQuantities.entrySet()) {
            if (entry.getValue() > player.getWorkerDeck().getOrDefault(entry.getKey(), 0)) {
                return false;
            }
        }
        return true;
    }

    private void updateWorkerDeck(Map<WorkerType, Integer> hireQuantities, Map<WorkerType, Integer> fireQuantities) {
        PopupUtils.updateDeck(player.getWorkerDeck(), hireQuantities, fireQuantities);
        //
        hireQuantities.forEach((workerType, count) -> {
            int newCount = player.getWorkerDeck().getOrDefault(workerType, 0);
            player.updatePeakValues(workerType, newCount);
        });
        updateSellingTextFields();
    }

    public void updateSellingTextFields() {
        PopupUtils.updateSellingTextFields(fireTextFieldMap, player.getWorkerDeck(), WorkerType.values());
    }

    private void updateTotalCost() {
        Map<WorkerType, Integer> hireQuantities = getWorkerQuantities(hireTextFieldMap);
        Map<WorkerType, Integer> fireQuantities = getWorkerQuantities(fireTextFieldMap);
        int totalCostValue = PopupUtils.calculateTransactionCost(hireQuantities, fireQuantities);
        totalCost.setText(totalCostValue + "$");
    }

    private void resetTextFields() {
        PopupUtils.resetTextFields(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);
    }
}
