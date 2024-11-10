package hr.algebra.greatwesterntrail.model;

import lombok.Getter;

public enum BuildingType {
    CATTLE_EXCHANGE(20, 20),
    HIRING_CENTER(20, 20),
    TRAIN_STATION(20, 20);

    @Getter
    private final int cost;
    @Getter
    private final int value;

    BuildingType(int cost, int value) {
        this.cost = cost;
        this.value = value;
    }

    @Override
    public String toString() {
        return name().replace('_', ' ').toLowerCase();
    }
}
