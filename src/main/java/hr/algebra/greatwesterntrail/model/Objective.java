package hr.algebra.greatwesterntrail.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Objective implements Serializable {
    @Serial
    private static final long serialVersionUID = 1005L;

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

    public boolean isCompletedByPlayer(Player player) {
        return player.hasMetObjective(this);
    }
}
