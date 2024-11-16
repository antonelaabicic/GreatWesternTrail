package hr.algebra.greatwesterntrail.repository;

import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.utils.ImageUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import lombok.Getter;

import java.util.*;

public enum TileRepository {
    INSTANCE;

    public static final int GRID_SIZE = 5;
    @Getter
    private final Tile[][] tiles = new Tile[GRID_SIZE][GRID_SIZE];
    private final Random random = new Random();

    TileRepository() {
        initializeTiles();
    }

    private void initializeTiles() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                tiles[row][col] = new Tile(TileType.EMPTY);
            }
        }

        tiles[0][0] = new Tile(TileType.END);
        tiles[GRID_SIZE - 1][GRID_SIZE - 1] = new Tile(TileType.START);

        List<Tile> specialTiles = List.of(
                new Tile(BuildingType.CATTLE_EXCHANGE),
                new Tile(BuildingType.TRAIN_STATION),
                new Tile(BuildingType.HIRING_CENTER),
                new Tile(HazardType.FLOOD),
                new Tile(HazardType.ROCKSLIDE),
                new Tile(HazardType.BANDITS)
        );

        int placedTiles = 0;
        while (placedTiles < specialTiles.size()) {
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);

            if ((row == 0 && col == 0) || (row == GRID_SIZE - 1 && col == GRID_SIZE - 1)) {
                continue;
            }

            if (tiles[row][col].getTileType() == TileType.EMPTY) {
                tiles[row][col] = specialTiles.get(placedTiles);
                placedTiles++;
            }
        }
        assignObjectiveToRandomBuilding();
    }

    private void assignObjectiveToRandomBuilding() {
        List<Tile> buildingTiles = Arrays.stream(tiles)
                .flatMap(Arrays::stream)
                .filter(tile -> tile.getTileType() == TileType.BUILDING)
                .toList();

        if (!buildingTiles.isEmpty()) {
            Tile tileWithObjective = buildingTiles.get(random.nextInt(buildingTiles.size()));
            Objective objective = createObjectiveForBuilding(tileWithObjective.getBuildingType());
            tileWithObjective.setObjective(objective);
            ImageUtils.addIconToStackPane(tileWithObjective.getIcons(), "../images/scroll_icon.png", 20, 20, Pos.TOP_LEFT);
            addObjectiveTooltip(tileWithObjective, objective);
        }
    }

    private void addObjectiveTooltip(Tile tile, Objective objective) {
        Tooltip tooltip = new Tooltip(objective.getDescription());
        Tooltip.install(tile.getIcons(), tooltip);
    }

    private Objective createObjectiveForBuilding(BuildingType buildingType) {
        ObjectiveAction action;
        int quantity = 1;

        switch (buildingType) {
            case CATTLE_EXCHANGE:
                action = randomObjective(List.of(
                        ObjectiveAction.BUY_JERSEY, ObjectiveAction.BUY_ANGUS,
                        ObjectiveAction.BUY_LONGHORN, ObjectiveAction.BUY_HOLSTEIN,
                        ObjectiveAction.SELL_JERSEY, ObjectiveAction.SELL_ANGUS,
                        ObjectiveAction.SELL_LONGHORN, ObjectiveAction.SELL_HOLSTEIN));
                quantity = random.nextInt(5) + 1;
                break;
            case HIRING_CENTER:
                action = randomObjective(List.of(
                        ObjectiveAction.HIRE_COWBOY, ObjectiveAction.HIRE_ENGINEER, ObjectiveAction.HIRE_BUILDER,
                        ObjectiveAction.FIRE_COWBOY, ObjectiveAction.FIRE_ENGINEER, ObjectiveAction.FIRE_BUILDER));
                quantity = random.nextInt(5) + 1;
                break;
            case TRAIN_STATION:
                action = ObjectiveAction.ADVANCE_TRAIN_PROGRESS;
                quantity = random.nextInt(5) + 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid building type!");
        }
        return new Objective(ObjectiveStatus.HAS_OBJECTIVE, action, quantity);
    }

    private ObjectiveAction randomObjective(List<ObjectiveAction> actions) {
        return actions.get(random.nextInt(actions.size()));
    }
}
