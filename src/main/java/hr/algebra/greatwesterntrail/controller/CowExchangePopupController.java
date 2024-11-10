package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.model.CowType;
import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.WorkerType;
import hr.algebra.greatwesterntrail.utils.DialogUtils;
import hr.algebra.greatwesterntrail.utils.PopupUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.EnumMap;
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
    private Map<CowType, TextField> buyTextFieldMap = new EnumMap<>(CowType.class);
    private Map<CowType, TextField> sellTextFieldMap = new EnumMap<>(CowType.class);

    public void initialize(Player player) {
        this.player = player;
        initializeTextFieldMaps();

        PopupUtils.setInputValidation(
                tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity,
                tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity
        );

        btnConfirm.setOnAction(event -> confirmPurchase());

        PopupUtils.addCostCalculationListener(this::updateTotalCost,
                tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity,
                tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity
        );

        updateSellingTextFields();
        updateTotalCost();
    }

    private void initializeTextFieldMaps() {
        buyTextFieldMap = PopupUtils.createEnumTextFieldMap(CowType.class, tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity);
        sellTextFieldMap = PopupUtils.createEnumTextFieldMap(CowType.class, tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity);
    }

    private void confirmPurchase() {
        Map<CowType, Integer> buyQuantities = getCowQuantities(buyTextFieldMap);
        Map<CowType, Integer> sellQuantities = getCowQuantities(sellTextFieldMap);

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

        int totalCostValue = PopupUtils.calculateTransactionCost(buyQuantities, sellQuantities);
        if (totalCostValue > player.getMoney()) {
            DialogUtils.showDialog("Insufficient funds", "You don't have enough money to complete the purchase.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        updateCowDeck(buyQuantities, sellQuantities);
        player.setMoney(player.getMoney() - totalCostValue);

        int earnedVP = PopupUtils.calculateVPs(buyQuantities, sellQuantities);
        player.setVp(player.getVp() + earnedVP);

        DialogUtils.showDialog("Transaction complete",
                "You earned " + earnedVP + " VPs from this transaction! Your budget is currently " + player.getMoney() + "$.",
                Alert.AlertType.INFORMATION);
        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    private Map<CowType, Integer> getCowQuantities(Map<CowType, TextField> textFieldMap) {
        return PopupUtils.getQuantities(CowType.values(), textFieldMap.values().toArray(new TextField[0]));
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

    private void updateCowDeck(Map<CowType, Integer> buyQuantities, Map<CowType, Integer> sellQuantities) {
        PopupUtils.updateDeck(player.getCowDeck(), buyQuantities, sellQuantities);
        updateSellingTextFields();
    }

    private void updateSellingTextFields() {
        PopupUtils.updateSellingTextFields(sellTextFieldMap, player.getCowDeck(), CowType.values() );
    }

    private void updateTotalCost() {
        Map<CowType, Integer> buyQuantities = getCowQuantities(buyTextFieldMap);
        Map<CowType, Integer> sellQuantities = getCowQuantities(sellTextFieldMap);
        int totalCostValue = PopupUtils.calculateTransactionCost(buyQuantities, sellQuantities);
        totalCost.setText(totalCostValue + "$");
    }

    private void resetTextFields() {
        PopupUtils.resetTextFields(
                tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity,
                tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity
        );
    }
}
