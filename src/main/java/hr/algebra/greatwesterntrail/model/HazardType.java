package hr.algebra.greatwesterntrail.model;

public enum HazardType {
    FLOOD,
    ROCKSLIDE,
    BANDITS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
