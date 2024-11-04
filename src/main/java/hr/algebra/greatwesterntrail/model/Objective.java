package hr.algebra.greatwesterntrail.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Objective {
    private ObjectiveStatus status;
    private ObjectiveAction action;
    private int quantity;

    public String getDescription() {
        return quantity > 1
                ? String.format("%s (x%d).", action.getBaseDescription(), quantity)
                : action.getBaseDescription() + ".";
    }

    public int calculateTotalCost() {
        return action.getCost() * quantity;
    }

    public int calculateTotalVictoryPoints() {
        return action.getVictoryPoints() * quantity;
    }
}
