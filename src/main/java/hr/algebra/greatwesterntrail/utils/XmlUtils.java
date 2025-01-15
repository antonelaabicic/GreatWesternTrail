package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.model.*;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class XmlUtils {

    private static final String DTD = "dtd/gameMoves.dtd";
    private static final String DOCTYPE = "DOCTYPE";
    private static final String GAME_MOVES = "GameMoves";
    public static final String XML_FILE_NAME = "xml/gameMovesXML.xml";

    public static void saveGameMove(GameMove gameMove) {

        if (!Files.exists(Path.of(XML_FILE_NAME))) {
            try {
                Document document = createDocument(GAME_MOVES);
                appendGameMoveElement(gameMove, document);
                saveDocument(document, XML_FILE_NAME);
            } catch (ParserConfigurationException | TransformerException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                List<GameMove> gameMoves = parse(XML_FILE_NAME);
                gameMoves.add(gameMove);
                Document document = createDocument(GAME_MOVES);
                for (GameMove gm : gameMoves) {
                    appendGameMoveElement(gm, document);
                }
                saveDocument(document, XML_FILE_NAME);
            } catch (ParserConfigurationException | TransformerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Document createDocument(String element) throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        DOMImplementation dom = builder.getDOMImplementation();
        DocumentType docType = dom.createDocumentType(DOCTYPE, null, DTD);
        return dom.createDocument(null, element, docType);
    }

    private static void appendGameMoveElement(GameMove gameMove, Document document) {
        Element element = document.createElement(GameMoveTag.GAME_MOVE.getTag());
        document.getDocumentElement().appendChild(element);

        Element playerStateElement = document.createElement(GameMoveTag.PLAYER_STATE.getTag());
        element.appendChild(playerStateElement);

        playerStateElement.appendChild(createElement(document, GameMoveTag.POINTS.getTag(), String.valueOf(gameMove.getPlayerState().getVp())));
        playerStateElement.appendChild(createElement(document, GameMoveTag.MONEY.getTag(), String.valueOf(gameMove.getPlayerState().getMoney())));
        playerStateElement.appendChild(createElement(document, GameMoveTag.TRAIN_PROGRESS.getTag(), String.valueOf(gameMove.getPlayerState().getTrainProgress())));

        Element cowDeckElement = document.createElement(GameMoveTag.COW_DECK.getTag());
        playerStateElement.appendChild(cowDeckElement);
        for (Map.Entry<CowType, Integer> entry : gameMove.getPlayerState().getCowDeck().entrySet()) {
            Element cowTypeElement = document.createElement(GameMoveTag.COW_TYPE.getTag());
            cowTypeElement.setAttribute("name", entry.getKey().name());
            cowTypeElement.appendChild(createElement(document, GameMoveTag.COW_QUANTITY.getTag(), String.valueOf(entry.getValue())));
            cowDeckElement.appendChild(cowTypeElement);
        }

        Element workerDeckElement = document.createElement(GameMoveTag.WORKER_DECK.getTag());
        playerStateElement.appendChild(workerDeckElement);
        for (Map.Entry<WorkerType, Integer> entry : gameMove.getPlayerState().getWorkerDeck().entrySet()) {
            Element workerTypeElement = document.createElement(GameMoveTag.WORKER_TYPE.getTag());
            workerTypeElement.setAttribute("name", entry.getKey().name());
            workerTypeElement.appendChild(createElement(document, GameMoveTag.WORKER_QUANTITY.getTag(), String.valueOf(entry.getValue())));
            workerDeckElement.appendChild(workerTypeElement);
        }

        Element tileStateElement = document.createElement(GameMoveTag.TILE_STATE.getTag());
        element.appendChild(tileStateElement);

        Element positionElement = document.createElement(GameMoveTag.POSITION.getTag());
        tileStateElement.appendChild(positionElement);
        positionElement.appendChild(createElement(document, GameMoveTag.POSITION_ROW.getTag(), String.valueOf(gameMove.getPlayerState().getPosition().getRow())));
        positionElement.appendChild(createElement(document, GameMoveTag.POSITION_COLUMN.getTag(), String.valueOf(gameMove.getPlayerState().getPosition().getColumn())));

        tileStateElement.appendChild(createElement(document, GameMoveTag.TILE_TYPE.getTag(), String.valueOf(gameMove.getTileState().getTileType())));
        if (gameMove.getTileState().getBuildingType() != null) {
            tileStateElement.appendChild(createElement(document, GameMoveTag.BUILDING_TYPE.getTag(), gameMove.getTileState().getBuildingType().name()));
        } else {
            tileStateElement.appendChild(createElement(document, GameMoveTag.BUILDING_TYPE.getTag(), "null"));
        }

        if (gameMove.getTileState().getHazardType() != null) {
            tileStateElement.appendChild(createElement(document, GameMoveTag.HAZARD_TYPE.getTag(), gameMove.getTileState().getHazardType().name()));
        } else {
            tileStateElement.appendChild(createElement(document, GameMoveTag.HAZARD_TYPE.getTag(), "null"));
        }

        if (gameMove.getTileState().getObjective() != null) {
            Element objectiveElement = document.createElement(GameMoveTag.OBJECTIVE.getTag());
            tileStateElement.appendChild(objectiveElement);

            objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_STATUS.getTag(), gameMove.getTileState().getObjective().getStatus().name()));
            objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_ACTION.getTag(), gameMove.getTileState().getObjective().getAction().name()));
            objectiveElement.appendChild(createElement(document, GameMoveTag.OBJECTIVE_QUANTITY.getTag(), String.valueOf(gameMove.getTileState().getObjective().getQuantity())));
        }

        element.appendChild(createElement(document, GameMoveTag.TIME.getTag(), gameMove.getTime().toString()));
    }

    private static Node createElement(Document document, String tagName, String data) {
        Element element = document.createElement(tagName);
        Text text = document.createTextNode(data);
        element.appendChild(text);
        return element;
    }

    private static void saveDocument(Document document, String filename) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());
        transformer.transform(new DOMSource(document), new StreamResult(new File(filename)));
    }

    public static List<GameMove> parse(String path) {
        Document document = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    System.err.println("Warning: " + exception);
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });

            document = builder.parse(new File(path));

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        return retrieveGameMoves(document);
    }

    private static List<GameMove> retrieveGameMoves(Document document) {
        List<GameMove> gameMoves = new ArrayList<>();
        Element documentElement = document.getDocumentElement();
        NodeList nodes = documentElement.getElementsByTagName(GameMoveTag.GAME_MOVE.getTag());

        for (int i = 0; i < nodes.getLength(); i++) {
            Element item = (Element) nodes.item(i);

            int vp = Integer.parseInt(item.getElementsByTagName(GameMoveTag.POINTS.getTag()).item(0).getTextContent());
            int money = Integer.parseInt(item.getElementsByTagName(GameMoveTag.MONEY.getTag()).item(0).getTextContent());
            double trainProgress = Double.parseDouble(item.getElementsByTagName(GameMoveTag.TRAIN_PROGRESS.getTag()).item(0).getTextContent());

            Map<CowType, Integer> cowDeck = new EnumMap<>(CowType.class);
            NodeList cowTypes = item.getElementsByTagName(GameMoveTag.COW_TYPE.getTag());
            for (int j = 0; j < cowTypes.getLength(); j++) {
                Element cowType = (Element) cowTypes.item(j);
                CowType type = CowType.valueOf(cowType.getAttribute("name"));
                int quantity = Integer.parseInt(cowType.getElementsByTagName(GameMoveTag.COW_QUANTITY.getTag()).item(0).getTextContent());
                cowDeck.put(type, quantity);
            }

            Map<WorkerType, Integer> workerDeck = new EnumMap<>(WorkerType.class);
            NodeList workerTypes = item.getElementsByTagName(GameMoveTag.WORKER_TYPE.getTag());
            for (int j = 0; j < workerTypes.getLength(); j++) {
                Element workerType = (Element) workerTypes.item(j);
                WorkerType type = WorkerType.valueOf(workerType.getAttribute("name"));
                int quantity = Integer.parseInt(workerType.getElementsByTagName(GameMoveTag.WORKER_QUANTITY.getTag()).item(0).getTextContent());
                workerDeck.put(type, quantity);
            }

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

            if (item.getElementsByTagName(GameMoveTag.OBJECTIVE.getTag()).getLength() > 0) {
                Element objectiveElement = (Element) item.getElementsByTagName(GameMoveTag.OBJECTIVE.getTag()).item(0);

                ObjectiveStatus objectiveStatus = ObjectiveStatus.valueOf(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_STATUS.getTag()).item(0).getTextContent());
                ObjectiveAction objectiveAction = ObjectiveAction.valueOf(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_ACTION.getTag()).item(0).getTextContent());
                int objectiveQuantity = Integer.parseInt(objectiveElement.getElementsByTagName(GameMoveTag.OBJECTIVE_QUANTITY.getTag()).item(0).getTextContent());

                Objective objective = new Objective(objectiveStatus, objectiveAction, objectiveQuantity);
                tileState.setObjective(objective);
            }

            Position position = new Position(
                    Integer.parseInt(item.getElementsByTagName(GameMoveTag.POSITION_ROW.getTag()).item(0).getTextContent()),
                    Integer.parseInt(item.getElementsByTagName(GameMoveTag.POSITION_COLUMN.getTag()).item(0).getTextContent())
            );

            String timeString = item.getElementsByTagName(GameMoveTag.TIME.getTag()).item(0).getTextContent();
            LocalDateTime time = LocalDateTime.parse(timeString);

            Player player = new Player();
            player.setVp(vp);
            player.setMoney(money);
            player.setTrainProgress(trainProgress);
            player.setPlayerPosition(position);
            player.setCowDeck(cowDeck);
            player.setWorkerDeck(workerDeck);

            PlayerState playerState = new PlayerState(player);

            GameMove gameMove = new GameMove(playerState, tileState);
            gameMove.setTime(time);
            gameMoves.add(gameMove);
        }
        return gameMoves;
    }
}
