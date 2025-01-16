package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

import static hr.algebra.greatwesterntrail.utils.XmlUtils.createElement;

public final class XmlNodeUtils {

    public static void appendPlayerStateElement(GameMove gameMove, Document document, Element element) {
        Element playerStateElement = document.createElement(GameMoveTag.PLAYER_STATE.getTag());
        element.appendChild(playerStateElement);

        playerStateElement.appendChild(createElement(document, GameMoveTag.POINTS.getTag(), String.valueOf(gameMove.getPlayerState().getVp())));
        playerStateElement.appendChild(createElement(document, GameMoveTag.MONEY.getTag(), String.valueOf(gameMove.getPlayerState().getMoney())));
        playerStateElement.appendChild(createElement(document, GameMoveTag.TRAIN_PROGRESS.getTag(), String.valueOf(gameMove.getPlayerState().getTrainProgress())));

        appendCowDeckElements(gameMove, document, playerStateElement);
        appendWorkerDeckElements(gameMove, document, playerStateElement);
    }

    private static void appendCowDeckElements(GameMove gameMove, Document document, Element parentElement) {
        Element cowDeckElement = document.createElement(GameMoveTag.COW_DECK.getTag());
        parentElement.appendChild(cowDeckElement);
        for (Map.Entry<CowType, Integer> entry : gameMove.getPlayerState().getCowDeck().entrySet()) {
            Element cowTypeElement = document.createElement(GameMoveTag.COW_TYPE.getTag());
            cowTypeElement.setAttribute("name", entry.getKey().name());
            cowTypeElement.appendChild(createElement(document, GameMoveTag.COW_QUANTITY.getTag(), String.valueOf(entry.getValue())));
            cowDeckElement.appendChild(cowTypeElement);
        }
    }

    private static void appendWorkerDeckElements(GameMove gameMove, Document document, Element parentElement) {
        Element workerDeckElement = document.createElement(GameMoveTag.WORKER_DECK.getTag());
        parentElement.appendChild(workerDeckElement);
        for (Map.Entry<WorkerType, Integer> entry : gameMove.getPlayerState().getWorkerDeck().entrySet()) {
            Element workerTypeElement = document.createElement(GameMoveTag.WORKER_TYPE.getTag());
            workerTypeElement.setAttribute("name", entry.getKey().name());
            workerTypeElement.appendChild(createElement(document, GameMoveTag.WORKER_QUANTITY.getTag(), String.valueOf(entry.getValue())));
            workerDeckElement.appendChild(workerTypeElement);
        }
    }

    public static void appendTileStateElement(GameMove gameMove, Document document, Element parentElement) {
        Element tileStateElement = document.createElement(GameMoveTag.TILE_STATE.getTag());
        parentElement.appendChild(tileStateElement);

        appendPosition(gameMove, document, tileStateElement);
        appendTileType(gameMove, document, tileStateElement);

        appendBuildingType(gameMove, document, tileStateElement);
        appendHazardType(gameMove, document, tileStateElement);
        appendObjective(gameMove, document, tileStateElement);
    }

    private static void appendTileType(GameMove gameMove, Document document, Element tileStateElement) {
        tileStateElement.appendChild(createElement(document, GameMoveTag.TILE_TYPE.getTag(), String.valueOf(gameMove.getTileState().getTileType())));
    }

    private static void appendPosition(GameMove gameMove, Document document, Element tileStateElement) {
        Element positionElement = document.createElement(GameMoveTag.POSITION.getTag());
        tileStateElement.appendChild(positionElement);
        positionElement.appendChild(createElement(document, GameMoveTag.POSITION_ROW.getTag(), String.valueOf(gameMove.getPlayerState().getPosition().getRow())));
        positionElement.appendChild(createElement(document, GameMoveTag.POSITION_COLUMN.getTag(), String.valueOf(gameMove.getPlayerState().getPosition().getColumn())));
    }

    private static void appendBuildingType(GameMove gameMove, Document document, Element tileStateElement) {
        if (gameMove.getTileState().getBuildingType() != null) {
            tileStateElement.appendChild(createElement(document, GameMoveTag.BUILDING_TYPE.getTag(), gameMove.getTileState().getBuildingType().name()));
        } else {
            tileStateElement.appendChild(createElement(document, GameMoveTag.BUILDING_TYPE.getTag(), "null"));
        }
    }
    private static void appendHazardType(GameMove gameMove, Document document, Element tileStateElement) {
        if (gameMove.getTileState().getHazardType() != null) {
            tileStateElement.appendChild(createElement(document, GameMoveTag.HAZARD_TYPE.getTag(), gameMove.getTileState().getHazardType().name()));
        } else {
            tileStateElement.appendChild(createElement(document, GameMoveTag.HAZARD_TYPE.getTag(), "null"));
        }
    }
    private static void appendObjective(GameMove gameMove, Document document, Element tileStateElement) {
        if (gameMove.getTileState().getObjective() != null) {
            Element objectiveElement = document.createElement(GameMoveTag.OBJECTIVE.getTag());
            tileStateElement.appendChild(objectiveElement);
            objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_STATUS.getTag(), gameMove.getTileState().getObjective().getStatus().name()));
            objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_ACTION.getTag(), gameMove.getTileState().getObjective().getAction().name()));
            objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_QUANTITY.getTag(), String.valueOf(gameMove.getTileState().getObjective().getQuantity())));
        }
    }

    public static void appendBoardElements(GameMove gameMove, Document document, Element parentElement) {
        Element boardElement = document.createElement(GameMoveTag.BOARD.getTag());
        parentElement.appendChild(boardElement);

        Tile[][] tiles = gameMove.getTiles();
        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];

                Element tileElement = document.createElement(GameMoveTag.TILE.getTag());
                boardElement.appendChild(tileElement);

                Element positionElement = document.createElement(GameMoveTag.POSITION.getTag());
                tileElement.appendChild(positionElement);
                positionElement.appendChild(createElement(document, GameMoveTag.POSITION_ROW.getTag(), String.valueOf(row)));
                positionElement.appendChild(createElement(document, GameMoveTag.POSITION_COLUMN.getTag(), String.valueOf(col)));

                tileElement.appendChild(createElement(document, GameMoveTag.TILE_TYPE.getTag(), tile.getTileType().name()));

                if (tile.getBuildingType() != null) {
                    tileElement.appendChild(createElement(document, GameMoveTag.BUILDING_TYPE.getTag(), tile.getBuildingType().name()));
                } else {
                    tileElement.appendChild(createElement(document, GameMoveTag.BUILDING_TYPE.getTag(), "null"));
                }

                if (tile.getHazardType() != null) {
                    tileElement.appendChild(createElement(document, GameMoveTag.HAZARD_TYPE.getTag(), tile.getHazardType().name()));
                } else {
                    tileElement.appendChild(createElement(document, GameMoveTag.HAZARD_TYPE.getTag(), "null"));
                }

                if (tile.getObjective() != null) {
                    Element objectiveElement = document.createElement(GameMoveTag.OBJECTIVE.getTag());
                    tileElement.appendChild(objectiveElement);
                    objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_STATUS.getTag(), tile.getObjective().getStatus().name()));
                    objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_ACTION.getTag(), tile.getObjective().getAction().name()));
                    objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_QUANTITY.getTag(), String.valueOf(tile.getObjective().getQuantity())));
                }
            }
        }
    }


    public static void appendTimeElement(GameMove gameMove, Document document, Element parentElement) {
        parentElement.appendChild(createElement(document, GameMoveTag.TIME.getTag(), gameMove.getTime().toString()));
    }

    public static PlayerState deserializePlayerState(Element item) {
        int vp = Integer.parseInt(item.getElementsByTagName(GameMoveTag.POINTS.getTag()).item(0).getTextContent());
        int money = Integer.parseInt(item.getElementsByTagName(GameMoveTag.MONEY.getTag()).item(0).getTextContent());
        double trainProgress = Double.parseDouble(item.getElementsByTagName(GameMoveTag.TRAIN_PROGRESS.getTag()).item(0).getTextContent());

        Map<CowType, Integer> cowDeck = deserializeDeck(item, GameMoveTag.COW_TYPE.getTag(), CowType.class);
        Map<WorkerType, Integer> workerDeck = deserializeDeck(item, GameMoveTag.WORKER_TYPE.getTag(), WorkerType.class);
        Position position = deserializePosition(item);
        Player player = createPlayer(vp, money, trainProgress, position, cowDeck, workerDeck);

        return new PlayerState(player);
    }

    private static Player createPlayer(int vp, int money, double trainProgress, Position position, Map<CowType, Integer> cowDeck, Map<WorkerType, Integer> workerDeck) {
        Player player = new Player();
        player.setVp(vp);
        player.setMoney(money);
        player.setTrainProgress(trainProgress);
        player.setPlayerPosition(position);
        player.setCowDeck(cowDeck);
        player.setWorkerDeck(workerDeck);
        return player;
    }

    private static <T extends Enum<T>> Map<T, Integer> deserializeDeck(Element item, String tagName, Class<T> enumClass) {
        Map<T, Integer> deck = new EnumMap<>(enumClass);
        NodeList typeList = item.getElementsByTagName(tagName);
        for (int j = 0; j < typeList.getLength(); j++) {
            Element typeElement = (Element) typeList.item(j);
            T type = Enum.valueOf(enumClass, typeElement.getAttribute("name"));

            int quantity = 0;
            if (enumClass == CowType.class) {
                quantity = Integer.parseInt(typeElement.getElementsByTagName(GameMoveTag.COW_QUANTITY.getTag()).item(0).getTextContent());
            } else if (enumClass == WorkerType.class) {
                quantity = Integer.parseInt(typeElement.getElementsByTagName(GameMoveTag.WORKER_QUANTITY.getTag()).item(0).getTextContent());
            }
            deck.put(type, quantity);
        }
        return deck;
    }

    private static Position deserializePosition(Element item) {
        Element positionElement = (Element) item.getElementsByTagName(GameMoveTag.POSITION.getTag()).item(0);
        int row = Integer.parseInt(positionElement.getElementsByTagName(GameMoveTag.POSITION_ROW.getTag()).item(0).getTextContent());
        int column = Integer.parseInt(positionElement.getElementsByTagName(GameMoveTag.POSITION_COLUMN.getTag()).item(0).getTextContent());
        return new Position(row, column);
    }

    public static TileState deserializeTileState(Element item) {
        Tile tile = null;
        TileType tileType = TileType.valueOf(item.getElementsByTagName(GameMoveTag.TILE_TYPE.getTag()).item(0).getTextContent());

        if (tileType == TileType.EMPTY || tileType == TileType.START || tileType == TileType.END) {
            tile = new Tile(tileType);
        } else if (tileType == TileType.BUILDING) {
            BuildingType buildingType = BuildingType.valueOf(item.getElementsByTagName(GameMoveTag.BUILDING_TYPE.getTag()).item(0).getTextContent());
            tile = new Tile(buildingType);
        } else if (tileType == TileType.HAZARD) {
            HazardType hazardType = HazardType.valueOf(item.getElementsByTagName(GameMoveTag.HAZARD_TYPE.getTag()).item(0).getTextContent());
            tile = new Tile(hazardType);
        }

        TileState tileState = new TileState(tile);
        deserializeObjective(item, tileState);
        return tileState;
    }

    private static void deserializeObjective(Element item, TileState tileState) {
        if (item.getElementsByTagName(GameMoveTag.OBJECTIVE.getTag()).getLength() > 0) {
            Element objectiveElement = (Element) item.getElementsByTagName(GameMoveTag.OBJECTIVE.getTag()).item(0);

            ObjectiveStatus objectiveStatus = ObjectiveStatus.valueOf(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_STATUS.getTag()).item(0).getTextContent());
            ObjectiveAction objectiveAction = ObjectiveAction.valueOf(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_ACTION.getTag()).item(0).getTextContent());
            int objectiveQuantity = Integer.parseInt(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_QUANTITY.getTag()).item(0).getTextContent());

            Objective objective = new Objective(objectiveStatus, objectiveAction, objectiveQuantity);
            tileState.setObjective(objective);
        }
    }

    public static Tile[][] deserializeBoard(Element item) {
        Tile[][] tiles = new Tile[TileRepository.GRID_SIZE][TileRepository.GRID_SIZE];
        NodeList tileNodes = item.getElementsByTagName(GameMoveTag.TILE.getTag());

        for (int i = 0; i < tileNodes.getLength(); i++) {
            Element tileElement = (Element) tileNodes.item(i);

            int row = Integer.parseInt(tileElement.getElementsByTagName(GameMoveTag.POSITION_ROW.getTag()).item(0).getTextContent());
            int col = Integer.parseInt(tileElement.getElementsByTagName(GameMoveTag.POSITION_COLUMN.getTag()).item(0).getTextContent());

            Tile tile = new Tile();
            TileType tileType = TileType.valueOf(tileElement.getElementsByTagName(GameMoveTag.TILE_TYPE.getTag()).item(0).getTextContent());
            tile.setTileType(tileType);

            String buildingTypeString = tileElement.getElementsByTagName(GameMoveTag.BUILDING_TYPE.getTag()).item(0).getTextContent();
            if (!"null".equals(buildingTypeString)) {
                BuildingType buildingType = BuildingType.valueOf(buildingTypeString);
                tile.setBuildingType(buildingType);
            }

            String hazardTypeString = tileElement.getElementsByTagName(GameMoveTag.HAZARD_TYPE.getTag()).item(0).getTextContent();
            if (!"null".equals(hazardTypeString)) {
                HazardType hazardType = HazardType.valueOf(hazardTypeString);
                tile.setHazardType(hazardType);
            }

            if (tileElement.getElementsByTagName(GameMoveTag.OBJECTIVE.getTag()).getLength() > 0) {
                Element objectiveElement = (Element) tileElement.getElementsByTagName(GameMoveTag.OBJECTIVE.getTag()).item(0);

                ObjectiveStatus objectiveStatus = ObjectiveStatus.valueOf(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_STATUS.getTag()).item(0).getTextContent());
                ObjectiveAction objectiveAction = ObjectiveAction.valueOf(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_ACTION.getTag()).item(0).getTextContent());
                int objectiveQuantity = Integer.parseInt(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_QUANTITY.getTag()).item(0).getTextContent());

                Objective objective = new Objective(objectiveStatus, objectiveAction, objectiveQuantity);
                tile.setObjective(objective);
            }

            tiles[row][col] = tile;
        }
        return tiles;
    }

    public static LocalDateTime deserializeTime(Element item) {
        return LocalDateTime.parse(item.getElementsByTagName(GameMoveTag.TIME.getTag()).item(0).getTextContent());
    }
}
