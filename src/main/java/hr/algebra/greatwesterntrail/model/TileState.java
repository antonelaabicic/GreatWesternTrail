package hr.algebra.greatwesterntrail.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@Data
public class TileState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1008L;

    private TileType tileType;
    private BuildingType buildingType;
    private HazardType hazardType;
    private Objective objective;

    public TileState(Tile tile) {
        this.tileType = tile.getTileType();
        this.buildingType = (tile.getTileType() == TileType.BUILDING) ? tile.getBuildingType() : null;
        this.hazardType = (tile.getTileType() == TileType.HAZARD) ? tile.getHazardType() : null;
        this.objective = (tile.getBuildingType() != null) ? tile.getObjective() : null;
    }
}
