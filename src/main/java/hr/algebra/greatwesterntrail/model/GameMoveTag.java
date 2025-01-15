package hr.algebra.greatwesterntrail.model;

public enum GameMoveTag {
    GAME_MOVE("GameMove"),

    PLAYER_STATE("PlayerState"),
    POINTS("Points"),
    MONEY("Money"),
    TRAIN_PROGRESS("TrainProgress"),
    COW_DECK("CowDeck"),
    COW_TYPE("CowType"),
    COW_QUANTITY("CowQuantity"),
    WORKER_DECK("WorkerDeck"),
    WORKER_TYPE("WorkerType"),
    WORKER_QUANTITY("WorkerQuantity"),

    TILE_STATE("TileState"),
    POSITION("Position"),
    POSITION_ROW("Row"),
    POSITION_COLUMN("Column"),
    TILE_TYPE("TileType"),
    BUILDING_TYPE("BuildingType"),
    HAZARD_TYPE("HazardType"),
    OBJECTIVE("Objective"),
    OBJECTIVE_STATUS("ObjectiveStatus"),
    OBJECTIVE_ACTION("ObjectiveAction"),
    OBJECTIVE_QUANTITY("ObjectiveQuantity"),

    TIME("Time");

    private String tag;

    GameMoveTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
