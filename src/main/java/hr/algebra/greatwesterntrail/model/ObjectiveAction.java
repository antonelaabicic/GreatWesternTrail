package hr.algebra.greatwesterntrail.model;

import lombok.Getter;

public enum ObjectiveAction {
    BUY_HOLSTEIN(1,1, "Buy Holstein"),
    BUY_JERSEY(5, 5, "Buy Jersey"),
    BUY_ANGUS(10,10, "Buy Black Angus"),
    BUY_LONGHORN(20,20, "Buy Texas Longhorn"),
    SELL_HOLSTEIN(1, 1, "Sell Holstein"),
    SELL_JERSEY(5,5, "Sell Jersey"),
    SELL_ANGUS(10, 10, "Sell Black Angus"),
    SELL_LONGHORN(20, 20, "Sell Texas Longhorn"),

    HIRE_COWBOY(5, 1, "Hire cowboy"),
    HIRE_ENGINEER(5, 1, "Hire engineer"),
    HIRE_BUILDER(5, 1, "Hire builder"),
    FIRE_COWBOY(5, 1, "Fire cowboy"),
    FIRE_ENGINEER(5, 1, "Fire engineer"),
    FIRE_BUILDER(5,1, "Fire builder"),

    ADVANCE_TRAIN_PROGRESS(5, 1, "Advance train progress");

    @Getter
    private final int cost;
    @Getter
    private final int victoryPoints;
    @Getter
    private final String baseDescription;

    ObjectiveAction(int cost, int victoryPoints, String baseDescription) {
        this.cost = cost;
        this.victoryPoints = victoryPoints;
        this.baseDescription = baseDescription;
    }

    public boolean isBuyAction() {
        return this == BUY_JERSEY || this == BUY_ANGUS || this == BUY_LONGHORN || this == BUY_HOLSTEIN;
    }

    public boolean isSellAction() {
        return this == SELL_JERSEY || this == SELL_ANGUS || this == SELL_LONGHORN || this == SELL_HOLSTEIN;
    }

    public boolean isHireAction() {
        return this == HIRE_COWBOY || this == HIRE_ENGINEER || this == HIRE_BUILDER;
    }

    public boolean isFireAction() {
        return this == FIRE_COWBOY || this == FIRE_ENGINEER || this == FIRE_BUILDER;
    }
}
