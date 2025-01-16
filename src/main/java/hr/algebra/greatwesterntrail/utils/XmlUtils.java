package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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

import static hr.algebra.greatwesterntrail.utils.XmlNodeUtils.deserializePlayerState;

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

        XmlNodeUtils.appendPlayerStateElement(gameMove, document, element);
        XmlNodeUtils.appendTileStateElement(gameMove, document, element);
        XmlNodeUtils.appendBoardElements(gameMove, document, element);
        XmlNodeUtils.appendTimeElement(gameMove, document, element);
    }

    public static Node createElement(Document document, String tagName, String data) {
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

            PlayerState playerState = XmlNodeUtils.deserializePlayerState(item);
            TileState tileState = XmlNodeUtils.deserializeTileState(item);
            Tile[][] tiles = XmlNodeUtils.deserializeBoard(item);
            LocalDateTime time = XmlNodeUtils.deserializeTime(item);

            //GameMove gameMove = new GameMove(playerState, tileState, BoardController.getInstance().gameState.getTiles());
            GameMove gameMove = new GameMove(playerState, tileState, tiles);
            gameMove.setTime(time);
            gameMoves.add(gameMove);
        }
        return gameMoves;
    }

    public static void restoreBoard(Tile[][] tiles) {
        BoardController.getInstance().boardGrid.getChildren().clear();

        for (int row = 0; row < TileRepository.GRID_SIZE; row++) {
            for (int col = 0; col < TileRepository.GRID_SIZE; col++) {
                Tile tile = tiles[row][col];
                tile.setIcons();

                StackPane tileStack = null;
                if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
                    tileStack = TileUtils.createTileStack(
                            tile, BoardController.getInstance().gameState.getPlayerOne(), BoardController.getInstance()
                    );
                    if (tile.getObjective() != null) {
                        ImageUtils.addIconToStackPane(tile.getIcons(), "../images/scroll_icon.png", 20, 20, Pos.TOP_LEFT);
                        TileRepository.INSTANCE.addObjectiveTooltip(tile, tile.getObjective());
                    }
                    TileButton tileButton = (TileButton) tileStack.getChildren().get(0);
                    BoardController.getInstance().tileButtons[row][col] = tileButton;
                }
                BoardController.getInstance().boardGrid.add(tileStack, col, row);
            }
        }

        Player player1 = BoardController.getInstance().gameState.getPlayerOne();
        if (player1 != null) {
            TrainProgressUtils.updateTrainProgressBar(BoardController.getInstance().pbTrain1, player1.getTrainProgress());
            BoardController.getInstance().pbTrain1.setBarColor(Color.RED);
            BoardController.getInstance().pbTrain1.setVisible(true);
        }
        TileUtils.highlightSinglePlayer(player1.getPlayerPosition(), BoardController.getInstance().tileButtons);
    }
}
