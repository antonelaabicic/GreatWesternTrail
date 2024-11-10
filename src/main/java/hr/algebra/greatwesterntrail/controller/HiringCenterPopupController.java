package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.WorkerType;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import hr.algebra.greatwesterntrail.utils.PopupUtils;
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
    private Map<WorkerType, TextField> hireTextFieldMap = new EnumMap<>(WorkerType.class);
    private Map<WorkerType, TextField> fireTextFieldMap = new EnumMap<>(WorkerType.class);

    public void initialize(Player player) {
        this.player = player;
        initializeTextFieldMaps();

        PopupUtils.setInputValidation(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);

        btnConfirm.setOnAction(event -> confirm());
        PopupUtils.addCostCalculationListener(this::updateTotalCost,
                tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);

        updateSellingTextFields();
        updateTotalCost();
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

        updateWorkerDeck(hireQuantities, fireQuantities);
        player.setMoney(player.getMoney() - totalCostValue);

        int earnedVP = PopupUtils.calculateVPs(hireQuantities, fireQuantities);;
        player.setVp(player.getVp() + earnedVP);

        DialogUtils.showDialog("Transaction complete",
                "You earned " + earnedVP + " VPs from this transaction! Your budget is currently " + player.getMoney() + "$.",
                Alert.AlertType.INFORMATION);
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
