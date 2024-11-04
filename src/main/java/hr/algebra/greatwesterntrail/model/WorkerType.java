package hr.algebra.greatwesterntrail.model;

import lombok.Getter;

public enum WorkerType {
    BUILDER(5, 1),
    ENGINEER(5, 1),
    COWBOY(5, 1);

    @Getter
    private final int cost;
    @Getter
    private final int vp;

    WorkerType(int cost, int vp) {
        this.cost = cost;
        this.vp = vp;
    }
}
