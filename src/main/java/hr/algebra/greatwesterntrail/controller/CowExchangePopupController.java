package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.model.CowType;
import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.WorkerType;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class CowExchangePopupController {
    @FXML
    public TextField tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity;
    @FXML
    public TextField tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity;

    @FXML
    public Label totalCost;
    @FXML
    public Button btnConfirm;

    private Player player;

    public void initialize(Player player) {
        this.player = player;

        setInputValidation(tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity,
                tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity);

        btnConfirm.setOnAction(event -> confirmPurchase());
        updateSellingTextFields();

        addCostCalculationListener(tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity,
                tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity);

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

    private void confirmPurchase() {
        Map<CowType, Integer> buyQuantities = getCowQuantities(tfBuyHolsteinQuantity, tfBuyJerseyQuantity,
                tfBuyAngusQuantity, tfBuyLonghornQuantity);
        Map<CowType, Integer> sellQuantities = getCowQuantities(tfSellHolsteinQuantity, tfSellJerseyQuantity,
                tfSellAngusQuantity, tfSellLonghornQuantity);

        if (!canSellCows(sellQuantities)) {
            DialogUtils.showDialog("Invalid sale", "You cannot sell more cows than you currently own.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        if (!canBuyMultipleCows(buyQuantities)) {
            DialogUtils.showDialog("Purchase limit", "With fewer than 5 cowboys, you can only buy one type of cow per transaction.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        int totalCostValue = calculateTotalCost(buyQuantities, sellQuantities);
        if (totalCostValue > player.getMoney()) {
            DialogUtils.showDialog("Insufficient funds", "You don't have enough money to complete the purchase.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        updateCowDeck(buyQuantities, sellQuantities);
        player.setMoney(player.getMoney() - totalCostValue);

        int earnedVP = calculateVPs(buyQuantities, sellQuantities);
        player.setVp(player.getVp() + earnedVP);

        DialogUtils.showDialog("Transaction complete", "You earned " + earnedVP + " Victory Points from this transaction!", Alert.AlertType.INFORMATION);
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    private Map<CowType, Integer> getCowQuantities(TextField... fields) {
        return Map.of(
                CowType.HOLSTEIN, parseTextFieldValue(fields[0]),
                CowType.JERSEY, parseTextFieldValue(fields[1]),
                CowType.BLACK_ANGUS, parseTextFieldValue(fields[2]),
                CowType.TEXAS_LONGHORN, parseTextFieldValue(fields[3])
        );
    }

    private boolean canSellCows(Map<CowType, Integer> sellQuantities) {
        for (Map.Entry<CowType, Integer> entry : sellQuantities.entrySet()) {
            if (entry.getValue() > player.getCowDeck().getOrDefault(entry.getKey(), 0)) {
                return false;
            }
        }
        return true;
    }

    private boolean canBuyMultipleCows(Map<CowType, Integer> buyQuantities) {
        int cowboyCount = player.getWorkerDeck().getOrDefault(WorkerType.COWBOY, 0);
        if (cowboyCount < 5) {
            return buyQuantities.values().stream().filter(q -> q > 0).count() <= 1;
        }
        return true;
    }

    private int calculateTotalCost(Map<CowType, Integer> buyQuantities, Map<CowType, Integer> sellQuantities) {
        int incomeFromSelling = sellQuantities.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * entry.getKey().getCost())
                .sum();
        int costForBuying = buyQuantities.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * entry.getKey().getCost())
                .sum();
        return costForBuying - incomeFromSelling;
    }

    private void updateCowDeck(Map<CowType, Integer> buyQuantities, Map<CowType, Integer> sellQuantities) {
        Map<CowType, Integer> cowDeck = player.getCowDeck();
        for (CowType cowType : CowType.values()) {
            int bought = buyQuantities.getOrDefault(cowType, 0);
            int sold = sellQuantities.getOrDefault(cowType, 0);
            cowDeck.put(cowType, cowDeck.getOrDefault(cowType, 0) + bought - sold);
        }
        updateSellingTextFields();
    }

    private void updateSellingTextFields() {
        for (CowType cowType : CowType.values()) {
            TextField sellField = getSellTextField(cowType);
            sellField.setDisable(player.getCowDeck().getOrDefault(cowType, 0) == 0);
        }
    }

    private TextField getSellTextField(CowType cowType) {
        switch (cowType) {
            case HOLSTEIN: return tfSellHolsteinQuantity;
            case JERSEY: return tfSellJerseyQuantity;
            case BLACK_ANGUS: return tfSellAngusQuantity;
            case TEXAS_LONGHORN: return tfSellLonghornQuantity;
            default: throw new IllegalArgumentException("Unknown CowType: " + cowType);
        }
    }

    private int calculateVPs(Map<CowType, Integer> buyQuantities, Map<CowType, Integer> sellQuantities) {
        return buyQuantities.entrySet().stream()
                .mapToInt(entry -> (entry.getValue() + sellQuantities.getOrDefault(entry.getKey(), 0)) * entry.getKey().getVp())
                .sum();
    }

    private void addCostCalculationListener(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> updateTotalCost());
        }
    }

    private void updateTotalCost() {
        Map<CowType, Integer> buyQuantities = getCowQuantities(tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity);
        Map<CowType, Integer> sellQuantities = getCowQuantities(tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity);
        int totalCostValue = calculateTotalCost(buyQuantities, sellQuantities);
        totalCost.setText(totalCostValue + "$");
    }

    private void resetTextFields() {
        List
                .of(tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity,
                        tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity)
                .forEach(tf -> tf.setText("0"));
    }
}
