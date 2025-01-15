package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.utils.ImageUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class Tile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1003L;

    private TileType tileType;
    private BuildingType buildingType;
    private HazardType hazardType;
    private Objective objective;
    private transient StackPane icons;

    //
    public Tile() {
        this.tileType = TileType.EMPTY;
        this.icons = new StackPane();
        setupIcons();
    }

    public Tile(BuildingType buildingType, Objective objective) {
        this.tileType = TileType.BUILDING;
        this.buildingType = buildingType;
        this.objective = objective;
        this.icons = new StackPane();
        setupIcons();
    }

    public Tile(BuildingType buildingType) {
        this(buildingType, null);
    }

    public Tile(HazardType hazardType) {
        this.tileType = TileType.HAZARD;
        this.hazardType = hazardType;
        this.icons = new StackPane();
        setupIcons();
    }

    public Tile(TileType tileType) {
        this.tileType = tileType;
        this.icons = new StackPane();
        setupIcons();
    }

    private void setupIcons() {
        if (icons == null) {
            icons = new StackPane();
        }
        icons.getChildren().clear();
        ImageView icon = null;

        switch (tileType) {
            case BUILDING:
                icon = switch (buildingType) {
                    case CATTLE_EXCHANGE -> ImageUtils.createImageView("../images/barn.png", 90, 90);
                    case HIRING_CENTER -> ImageUtils.createImageView("../images/hiring_center.png", 75, 75);
                    case TRAIN_STATION -> ImageUtils.createImageView("../images/train_station.png", 90, 90);
                };
                break;
            case HAZARD:
                icon = switch (hazardType) {
                    case FLOOD -> ImageUtils.createImageView("../images/flood_icon.png", 95, 95);
                    case ROCKSLIDE -> ImageUtils.createImageView("../images/rockslide_icon.png", 95, 95);
                    case BANDITS -> ImageUtils.createImageView("../images/bandit_icon.png", 95, 95);
                };
                break;
            case START:
                icon = ImageUtils.createImageView("../images/start_icon.png", 90, 90);
                break;
            case END:
                icon = ImageUtils.createImageView("../images/kansas_icon.png", 100, 100);
                break;
            case EMPTY:
                icon = ImageUtils.createImageView("../images/grass_icon.png", 95, 95);
                break;
        }
        if (icon != null) {
            icons.getChildren().add(icon);
        }
    }

    public void setIcons() {
        setupIcons();
    }
}
