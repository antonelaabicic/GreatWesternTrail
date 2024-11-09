package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.WorkerType;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.List;
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

    public void initialize(Player player) {
        this.player = player;

        setInputValidation(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);

        btnConfirm.setOnAction(event -> confirm());
        updateSellingTextFields();

        addCostCalculationListener(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);

        updateTotalCost();
    }

    private void setInputValidation(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                if (!event.getCharacter().matches("\\d")) {
                    event.consume();
                }
            });
        }
    }

    private int parseTextFieldValue(TextField textField) {
        return textField.getText().isEmpty() ? 0 : Integer.parseInt(textField.getText());
    }

    private void confirm() {
        Map<WorkerType, Integer> hireQuantities = getWorkerQuantities(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity);
        Map<WorkerType, Integer> fireQuantities = getWorkerQuantities(tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);

        if (!canHireWorkers(fireQuantities)) {
            DialogUtils.showDialog("Invalid hiring", "You cannot fire more workers than you currently have.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        int totalCostValue = calculateTotalCost(hireQuantities, fireQuantities);
        if (totalCostValue > player.getMoney()) {
            DialogUtils.showDialog("Insufficient funds", "You don't have enough money to complete the purchase.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        updateWorkerDeck(hireQuantities, fireQuantities);
        player.setMoney(player.getMoney() - totalCostValue);

        int earnedVP = calculateVPs(hireQuantities, fireQuantities);
        player.setVp(player.getVp() + earnedVP);

        DialogUtils.showDialog("Transaction complete",
                "You earned " + earnedVP + " VPs from this transaction! Your budget is currently " + player.getMoney() + "$.",
                Alert.AlertType.INFORMATION);
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    private Map<WorkerType, Integer> getWorkerQuantities(TextField... fields) {
        return Map.of(
                WorkerType.COWBOY, parseTextFieldValue(fields[0]),
                WorkerType.BUILDER, parseTextFieldValue(fields[1]),
                WorkerType.ENGINEER, parseTextFieldValue(fields[2])
        );
    }

    private boolean canHireWorkers(Map<WorkerType, Integer> fireQuantities) {
        for (Map.Entry<WorkerType, Integer> entry : fireQuantities.entrySet()) {
            if (entry.getValue() > player.getWorkerDeck().getOrDefault(entry.getKey(), 0)) {
                return false;
            }
        }
        return true;
    }

    private boolean canHireMultipleWorkers(Map<WorkerType, Integer> buyQuantities) {
        int cowboyCount = player.getWorkerDeck().getOrDefault(WorkerType.COWBOY, 0);
        if (cowboyCount < 5) {
            return buyQuantities.values().stream().filter(q -> q > 0).count() <= 1;
        }
        return true;
    }

    private int calculateTotalCost(Map<WorkerType, Integer> hireQuantities, Map<WorkerType, Integer> fireQuantities) {
        int incomeFromSelling = fireQuantities.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * entry.getKey().getCost())
                .sum();
        int costForBuying = hireQuantities.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * entry.getKey().getCost())
                .sum();
        return costForBuying - incomeFromSelling;
    }

    private void updateWorkerDeck(Map<WorkerType, Integer> hireQuantities, Map<WorkerType, Integer> fireQuantities) {
        Map<WorkerType, Integer> workerDeck = player.getWorkerDeck();
        for (WorkerType workerType : WorkerType.values()) {
            int bought = hireQuantities.getOrDefault(workerType, 0);
            int sold = fireQuantities.getOrDefault(workerType, 0);
            workerDeck.put(workerType, workerDeck.getOrDefault(workerType, 0) + bought - sold);
        }
        updateSellingTextFields();
    }

    private void updateSellingTextFields() {
        for (WorkerType workerType : WorkerType.values()) {
            TextField fireField = getFireTextField(workerType);
            fireField.setDisable(player.getWorkerDeck().getOrDefault(workerType, 0) == 0);
        }
    }

    private TextField getFireTextField(WorkerType workerType) {
        return switch (workerType) {
            case COWBOY -> tfFireCowboyQuantity;
            case BUILDER -> tfFireBuilderQuantity;
            case ENGINEER -> tfFireEngineerQuantity;
            default -> throw new IllegalArgumentException("Unknown worker type: " + workerType);
        };
    }

    private int calculateVPs(Map<WorkerType, Integer> hireQuantities, Map<WorkerType, Integer> fireQuantities) {
        return hireQuantities.entrySet().stream()
                .mapToInt(entry -> (entry.getValue() + fireQuantities.getOrDefault(entry.getKey(), 0)) * entry.getKey().getVp())
                .sum();
    }

    private void addCostCalculationListener(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> updateTotalCost());
        }
    }

    private void updateTotalCost() {
        Map<WorkerType, Integer> hireQuantities = getWorkerQuantities(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity);
        Map<WorkerType, Integer> fireQuantities = getWorkerQuantities(tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity);
        int totalCostValue = calculateTotalCost(hireQuantities, fireQuantities);
        totalCost.setText(totalCostValue + "$");
    }

    private void resetTextFields() {
        List
                .of(tfHireCowboyQuantity, tfHireBuilderQuantity, tfHireEngineerQuantity,
                        tfFireCowboyQuantity, tfFireBuilderQuantity, tfFireEngineerQuantity)
                .forEach(tf -> tf.setText("0"));
    }
}
