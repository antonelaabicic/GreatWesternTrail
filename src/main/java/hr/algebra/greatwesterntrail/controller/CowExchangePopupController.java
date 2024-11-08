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

        setInputValidation(tfBuyHolsteinQuantity);
        setInputValidation(tfBuyJerseyQuantity);
        setInputValidation(tfBuyAngusQuantity);
        setInputValidation(tfBuyLonghornQuantity);

        setInputValidation(tfSellHolsteinQuantity);
        setInputValidation(tfSellJerseyQuantity);
        setInputValidation(tfSellAngusQuantity);
        setInputValidation(tfSellLonghornQuantity);

        btnConfirm.setOnAction(event -> confirmPurchase());

        updateSellingTextFields();

        addCostCalculationListener(tfBuyHolsteinQuantity);
        addCostCalculationListener(tfBuyJerseyQuantity);
        addCostCalculationListener(tfBuyAngusQuantity);
        addCostCalculationListener(tfBuyLonghornQuantity);
        addCostCalculationListener(tfSellHolsteinQuantity);
        addCostCalculationListener(tfSellJerseyQuantity);
        addCostCalculationListener(tfSellAngusQuantity);
        addCostCalculationListener(tfSellLonghornQuantity);

        updateTotalCost();
    }

    private void setInputValidation(TextField textField) {
        textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().matches("\\d")) {
                event.consume();
            }
        });
    }

    private int parseTextFieldValue(TextField textField) {
        return textField.getText().isEmpty() ? 0 : Integer.parseInt(textField.getText());
    }

    private void confirmPurchase() {
        int buyHolstein = parseTextFieldValue(tfBuyHolsteinQuantity);
        int buyJersey = parseTextFieldValue(tfBuyJerseyQuantity);
        int buyAngus = parseTextFieldValue(tfBuyAngusQuantity);
        int buyLonghorn = parseTextFieldValue(tfBuyLonghornQuantity);

        int sellHolstein = parseTextFieldValue(tfSellHolsteinQuantity);
        int sellJersey = parseTextFieldValue(tfSellJerseyQuantity);
        int sellAngus = parseTextFieldValue(tfSellAngusQuantity);
        int sellLonghorn = parseTextFieldValue(tfSellLonghornQuantity);

        if (!canSellCow(CowType.HOLSTEIN, sellHolstein) || !canSellCow(CowType.JERSEY, sellJersey) ||
                !canSellCow(CowType.BLACK_ANGUS, sellAngus) || !canSellCow(CowType.TEXAS_LONGHORN, sellLonghorn)) {
            DialogUtils.showDialog("Invalid Sale",
                    "You cannot sell more cows than you currently own.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        int cowboyCount = player.getWorkerDeck().getOrDefault(WorkerType.COWBOY, 0);
        if (cowboyCount < 5 && moreThanOneBuyAttempted(buyHolstein, buyJersey, buyAngus, buyLonghorn)) {
            DialogUtils.showDialog("Purchase Limit",
                    "With fewer than 5 cowboys, you can only buy one type of cow per transaction.",
                    Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        int totalCostValue = calculateTotalCost(buyHolstein, buyJersey, buyAngus, buyLonghorn, sellHolstein,
                sellJersey, sellAngus, sellLonghorn);

        if (totalCostValue > player.getMoney()) {
            DialogUtils.showDialog("Insufficient Funds",
                    "You don't have enough money to complete the purchase.", Alert.AlertType.WARNING);
            resetTextFields();
            return;
        }

        updateCowDeck(buyHolstein, buyJersey, buyAngus, buyLonghorn, sellHolstein,
                sellJersey, sellAngus, sellLonghorn);

        player.setMoney(player.getMoney() - totalCostValue);

        int earnedVP = calculateVictoryPoints(buyHolstein, buyJersey, buyAngus, buyLonghorn,
                sellHolstein, sellJersey, sellAngus, sellLonghorn);
        player.setVp(player.getVp() + earnedVP);

        DialogUtils.showDialog("Transaction Complete",
                "You earned " + earnedVP + " Victory Points from this transaction!",
                Alert.AlertType.INFORMATION);

        ((Stage) btnConfirm.getScene().getWindow()).close();
    }

    private void resetTextFields() {
        List.of(
                        tfBuyHolsteinQuantity, tfBuyJerseyQuantity, tfBuyAngusQuantity, tfBuyLonghornQuantity,
                        tfSellHolsteinQuantity, tfSellJerseyQuantity, tfSellAngusQuantity, tfSellLonghornQuantity)
                .forEach(textField -> textField.setText("0"));
    }

    private boolean moreThanOneBuyAttempted(int... buys) {
        int buyCount = 0;
        for (int buy : buys) {
            if (buy > 0) buyCount++;
        }
        return buyCount > 1;
    }

    private boolean canSellCow(CowType cowType, int sellQuantity) {
        int availableCows = player.getCowDeck().getOrDefault(cowType, 0);
        return sellQuantity <= availableCows;
    }

    private int calculateTotalCost(int buyHolstein, int buyJersey, int buyAngus, int buyLonghorn,
                                   int sellHolstein, int sellJersey, int sellAngus, int sellLonghorn) {
        int incomeFromSelling = sellHolstein * CowType.HOLSTEIN.getCost() +
                sellJersey * CowType.JERSEY.getCost() +
                sellAngus * CowType.BLACK_ANGUS.getCost() +
                sellLonghorn * CowType.TEXAS_LONGHORN.getCost();

        int costForBuying = buyHolstein * CowType.HOLSTEIN.getCost() +
                buyJersey * CowType.JERSEY.getCost() +
                buyAngus * CowType.BLACK_ANGUS.getCost() +
                buyLonghorn * CowType.TEXAS_LONGHORN.getCost();

        return costForBuying - incomeFromSelling;
    }

    private void updateCowDeck(int buyHolstein, int buyJersey, int buyAngus, int buyLonghorn, int sellHolstein,
                               int sellJersey, int sellAngus, int sellLonghorn) {
        Map<CowType, Integer> cowDeck = player.getCowDeck();

        cowDeck.put(CowType.HOLSTEIN, cowDeck.getOrDefault(CowType.HOLSTEIN, 0) + buyHolstein - sellHolstein);
        cowDeck.put(CowType.JERSEY, cowDeck.getOrDefault(CowType.JERSEY, 0) + buyJersey - sellJersey);
        cowDeck.put(CowType.BLACK_ANGUS, cowDeck.getOrDefault(CowType.BLACK_ANGUS, 0) + buyAngus - sellAngus);
        cowDeck.put(CowType.TEXAS_LONGHORN, cowDeck.getOrDefault(CowType.TEXAS_LONGHORN, 0) + buyLonghorn - sellLonghorn);

        updateSellingTextFields();
    }

    private void updateSellingTextFields() {
        tfSellHolsteinQuantity.setDisable(player.getCowDeck().getOrDefault(CowType.HOLSTEIN, 0) == 0);
        tfSellJerseyQuantity.setDisable(player.getCowDeck().getOrDefault(CowType.JERSEY, 0) == 0);
        tfSellAngusQuantity.setDisable(player.getCowDeck().getOrDefault(CowType.BLACK_ANGUS, 0) == 0);
        tfSellLonghornQuantity.setDisable(player.getCowDeck().getOrDefault(CowType.TEXAS_LONGHORN, 0) == 0);
    }

    private int calculateVictoryPoints(int buyHolstein, int buyJersey, int buyAngus, int buyLonghorn, int sellHolstein, int sellJersey, int sellAngus, int sellLonghorn) {
        return (buyHolstein + sellHolstein) * CowType.HOLSTEIN.getVp() +
                (buyJersey + sellJersey) * CowType.JERSEY.getVp() +
                (buyAngus + sellAngus) * CowType.BLACK_ANGUS.getVp() +
                (buyLonghorn + sellLonghorn) * CowType.TEXAS_LONGHORN.getVp();
    }

    private void addCostCalculationListener(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> updateTotalCost());
    }

    private void updateTotalCost() {
        int buyHolstein = parseTextFieldValue(tfBuyHolsteinQuantity);
        int buyJersey = parseTextFieldValue(tfBuyJerseyQuantity);
        int buyAngus = parseTextFieldValue(tfBuyAngusQuantity);
        int buyLonghorn = parseTextFieldValue(tfBuyLonghornQuantity);

        int sellHolstein = parseTextFieldValue(tfSellHolsteinQuantity);
        int sellJersey = parseTextFieldValue(tfSellJerseyQuantity);
        int sellAngus = parseTextFieldValue(tfSellAngusQuantity);
        int sellLonghorn = parseTextFieldValue(tfSellLonghornQuantity);

        int totalCostValue = calculateTotalCost(buyHolstein, buyJersey, buyAngus, buyLonghorn, sellHolstein,
                sellJersey, sellAngus, sellLonghorn);

        totalCost.setText(totalCostValue + "$");
    }
}
