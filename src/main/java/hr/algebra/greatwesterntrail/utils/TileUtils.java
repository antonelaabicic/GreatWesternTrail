package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.Position;
import hr.algebra.greatwesterntrail.model.Tile;
import hr.algebra.greatwesterntrail.model.TileButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public final class TileUtils {
    private TileUtils() { }

    public static StackPane createTileStack(Tile tile, Player player, BoardController boardController) {
        TileButton tileButton = new TileButton(tile, player, boardController);
        tileButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        StackPane tileStack = new StackPane();
        tileStack.getChildren().add(tileButton);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.BLACK);

        Circle highlightCircle = new Circle(20, Color.RED);
        highlightCircle.setEffect(dropShadow);
        highlightCircle.setVisible(false);
        tileStack.getChildren().add(highlightCircle);

        return tileStack;
    }

    private static void clearAllHighlights(TileButton[][] tileButtons) {
        for (TileButton[] row : tileButtons) {
            for (TileButton tileButton : row) {
                StackPane tileStack = (StackPane) tileButton.getParent();
                Circle circle = (Circle) tileStack.getChildren().get(1);
                circle.setVisible(false);
            }
        }
    }

    private static void setHighlightVisible(TileButton tileButton) {
        if (tileButton != null) {
            StackPane tileStack = (StackPane) tileButton.getParent();
            Circle circle = (Circle) tileStack.getChildren().get(1);
            circle.setVisible(true);
        }
    }

    public static void highlightCurrentTile(Position playerPosition, TileButton[][] tileButtons) {
        clearAllHighlights(tileButtons);

        int row = playerPosition.getRow();
        int col = playerPosition.getColumn();
        TileButton currentTile = tileButtons[row][col];

        setHighlightVisible(currentTile);
    }
}
