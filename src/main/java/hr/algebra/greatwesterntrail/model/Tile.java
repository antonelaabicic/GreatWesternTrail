package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.utils.ImageUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import lombok.Data;

@Data
public class Tile {
    private TileType tileType;
    private BuildingType buildingType;
    private HazardType hazardType;
    private Objective objective;
    private boolean completed;
    private StackPane icons;

    public Tile(BuildingType buildingType, Objective objective) {
        this.tileType = TileType.BUILDING;
        this.buildingType = buildingType;
        this.objective = objective;
        this.completed = false;
        this.icons = new StackPane();
        setupBuildingIcons();
    }

    public Tile(BuildingType buildingType) {
        this(buildingType, null);
    }

    public Tile(HazardType hazardType) {
        this.tileType = TileType.HAZARD;
        this.hazardType = hazardType;
        this.icons = new StackPane();
        setupHazardIcons();
    }

    public Tile(TileType tileType) {
        this.tileType = tileType;
        this.icons = new StackPane();
        setupSpecialTileIcons();
    }

    private void setupBuildingIcons() {
        ImageView buildingIcon = null;

        switch (buildingType) {
            case CATTLE_EXCHANGE:
                buildingIcon = ImageUtils.createImageView("../images/barn.png", 90, 90);
                break;
            case HIRING_CENTER:
                buildingIcon = ImageUtils.createImageView("../images/hiring_center.png", 75, 75);
                break;
            case TRAIN_STATION:
                buildingIcon = ImageUtils.createImageView("../images/train_station.png", 90, 90);
                break;
        }

        if (buildingIcon != null) {
            icons.getChildren().add(buildingIcon);
        }
    }

    private void setupHazardIcons() {
        ImageView hazardIcon = null;

        switch (hazardType) {
            case FLOOD:
                hazardIcon = ImageUtils.createImageView("../images/flood_icon.png", 100, 100);
                break;
            case ROCKSLIDE:
                hazardIcon = ImageUtils.createImageView("../images/rockslide_icon.png", 100, 100);
                break;
            case BANDITS:
                hazardIcon = ImageUtils.createImageView("../images/bandit_icon.png", 100, 100);
                break;
        }

        if (hazardIcon != null) {
            icons.getChildren().add(hazardIcon);
        }
    }

    private void setupSpecialTileIcons() {
        switch (tileType) {
            case START:
                ImageView startIcon = ImageUtils.createImageView("../images/start_icon.png", 90, 90);
                icons.getChildren().add(startIcon);
                break;
            case END:
                ImageView endIcon = ImageUtils.createImageView("../images/kansas_icon.png", 100, 100);
                icons.getChildren().add(endIcon);
                break;
            case EMPTY:
                ImageView grassIcon = ImageUtils.createImageView("../images/grass_icon.png", 100, 100);
                icons.getChildren().add(grassIcon);
                break;
        }
    }
}
