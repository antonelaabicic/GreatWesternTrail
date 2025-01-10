package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.shared.Tradeable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class PopupUtils {
    private PopupUtils() {}

    public static <E extends Enum<E>> Map<E, TextField> createEnumTextFieldMap(Class<E> enumClass, TextField... fields) {
        E[] enumConstants = enumClass.getEnumConstants();
        Map<E, TextField> map = new EnumMap<>(enumClass);
        for (int i = 0; i < enumConstants.length; i++) {
            map.put(enumConstants[i], fields[i]);
        }
        return map;
    }

    public static void addCostCalculationListener(Runnable updateFunction, TextField... textFields) {
        for (TextField textField : textFields) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> updateFunction.run());
        }
    }

    public static void setInputValidation(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                if (!event.getCharacter().matches("\\d")) {
                    event.consume();
                }
            });
        }
    }

    public static int parseTextFieldValue(TextField textField) {
        return textField.getText().isEmpty() ? 0 : Integer.parseInt(textField.getText());
    }

    public static <T extends Enum<T>> Map<T, Integer> getQuantities(T[] enumTypes, TextField... fields) {
        Map<T, Integer> quantities = new EnumMap<>(enumTypes[0].getDeclaringClass());
        for (int i = 0; i < enumTypes.length; i++) {
            quantities.put(enumTypes[i], parseTextFieldValue(fields[i]));
        }
        return quantities;
    }

    public static void resetTextFields(TextField... textFields) {
        List
                .of(textFields)
                .forEach(tf -> tf.setText("0"));
    }

    public static <T extends Tradeable> int calculateVPs(Map<T, Integer> itemsOwned, Map<T, Integer> itemsAcquired) {
        return itemsOwned.entrySet().stream()
                .mapToInt(entry -> (entry.getValue() + itemsAcquired.getOrDefault(entry.getKey(), 0)) * entry.getKey().getVp())
                .sum();
    }

    public static int calculateTransactionCost(Map<? extends Tradeable, Integer> itemsToAdd,
                                               Map<? extends Tradeable, Integer> itemsToRemove) {
        return - calculateItemCosts(itemsToRemove) + calculateItemCosts(itemsToAdd);
    }

    private static int calculateItemCosts(Map<? extends Tradeable, Integer> items) {
        return items.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * entry.getKey().getCost())
                .sum();
    }

    public static <T> void updateDeck(Map<T, Integer> deck, Map<T, Integer> itemsToAdd, Map<T, Integer> itemsToRemove) {
        for (T item : itemsToAdd.keySet()) {
            int bought = itemsToAdd.getOrDefault(item, 0);
            int sold = itemsToRemove.getOrDefault(item, 0);
            deck.put(item, deck.getOrDefault(item, 0) + bought - sold);
        }
    }

    public static <T extends Enum<T>> void updateSellingTextFields(Map<T, TextField> textFieldMap, Map<T, Integer> playerDeck, T[] enumValues) {
        for (T type : enumValues) {
            TextField textField = textFieldMap.get(type);
            textField.setDisable(playerDeck.getOrDefault(type, 0) == 0);
        }
    }

    public static <T> boolean areAllQuantitiesZero(Map<T, Integer> quantities) {
        return quantities.values().stream().allMatch(quantity -> quantity == 0);
    }

}
