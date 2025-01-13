package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.shared.Tradeable;
import lombok.Getter;

public enum WorkerType implements Tradeable {
    COWBOY(5, 1),
    BUILDER(5, 1),
    ENGINEER(5, 1);

    @Getter
    private final int cost;
    @Getter
    private final int vp;

    WorkerType(int cost, int vp) {
        this.cost = cost;
        this.vp = vp;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
