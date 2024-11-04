package hr.algebra.greatwesterntrail.model;

import lombok.Getter;

public enum CowType {
    HOLSTEIN(1, 1),
    JERSEY(5, 5),
    BLACK_ANGUS(10, 10),
    TEXAS_LONGHORN(20, 20);

    @Getter
    private final int cost;
    @Getter
    private final int vp;

    CowType(int cost, int vp) {
        this.cost = cost;
        this.vp = vp;
    }
}
