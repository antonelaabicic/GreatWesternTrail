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

    HIRE_COWBOY(5, 5, "Hire a Cowboy"),
    HIRE_ENGINEER(5, 5, "Hire an Engineer"),
    HIRE_BUILDER(5, 5, "Hire a Builder"),
    FIRE_COWBOY(5, 5, "Fire a Cowboy"),
    FIRE_ENGINEER(5, 5, "Fire an Engineer"),
    FIRE_BUILDER(5,5, "Fire a Builder"),

    CROSS_FLOOD(5, 5, "Cross a flood"),
    CROSS_ROCKSLIDE(5, 5, "Cross a rockslide"),
    CROSS_BANDITS(5, 5, "Cross bandits"),
    REMOVE_FLOOD(20, 20, "Remove a flood"),
    REMOVE_ROCKSLIDE(20, 20, "Remove a rockslide"),
    REMOVE_BANDITS(20, 20, "Remove bandits"),

    ADVANCE_TRAIN_PROGRESS(0, 0, "Advance train progress");

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
}
