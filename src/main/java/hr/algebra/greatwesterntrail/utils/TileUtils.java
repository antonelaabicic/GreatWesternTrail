package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.model.*;
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

        if (GreatWesternTrailApplication.playerMode == PlayerMode.SINGLE_PLAYER) {
            Circle circlePlayerOne = buildHighlightCircle(Color.RED);
            tileStack.getChildren().add(circlePlayerOne);
        } else {
            Circle circlePlayerOne = buildHighlightCircle(Color.RED);
            circlePlayerOne.setTranslateX(-15);
            tileStack.getChildren().add(circlePlayerOne);
            Circle circlePlayerTwo = buildHighlightCircle(Color.BLUE);
            circlePlayerTwo.setTranslateX(15);
            tileStack.getChildren().add(circlePlayerTwo);
        }

        return tileStack;
    }

    private static Circle buildHighlightCircle(Color color) {
        Circle circle = new Circle(10, color);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(7);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(0);
        dropShadow.setColor(Color.BLACK);

        circle.setEffect(dropShadow);
        circle.setVisible(false);
        return circle;
    }

    public static void clearAllHighlights(TileButton[][] tileButtons) {
        for (TileButton[] row : tileButtons) {
            for (TileButton tileButton : row) {
                if (tileButton != null && tileButton.getParent() instanceof StackPane stack) {
                    if (stack.getChildren().size() > 1) {
                        stack.getChildren().get(1).setVisible(false);
                    }
                    if (stack.getChildren().size() > 2) {
                        stack.getChildren().get(2).setVisible(false);
                    }
                }
            }
        }
    }

    private static void showCircle(TileButton tileButton, int circleIndex) {
        if (tileButton != null && tileButton.getParent() instanceof StackPane stack) {
            if (stack.getChildren().size() > circleIndex) {
                stack.getChildren().get(circleIndex).setVisible(true);
            }
        }
    }

    public static void highlightSinglePlayer(Position playerPosition, TileButton[][] tileButtons) {
        clearAllHighlights(tileButtons);

        if (playerPosition == null) return;

        int row = playerPosition.getRow();
        int col = playerPosition.getColumn();
        TileButton currentTile = tileButtons[row][col];
        if (currentTile != null) {
            showCircle(currentTile, 1);
        }
    }

    public static void highlightTwoPlayers(Player playerOne, Player playerTwo, TileButton[][] tileButtons) {
        clearAllHighlights(tileButtons);

        if (playerOne != null && playerOne.getPlayerPosition() != null) {
            int row = playerOne.getPlayerPosition().getRow();
            int col = playerOne.getPlayerPosition().getColumn();
            TileButton tileBtn = tileButtons[row][col];
            showCircle(tileBtn, 1);
        }

        if (playerTwo != null && playerTwo.getPlayerPosition() != null) {
            int row = playerTwo.getPlayerPosition().getRow();
            int col = playerTwo.getPlayerPosition().getColumn();
            TileButton tileBtn = tileButtons[row][col];
            showCircle(tileBtn, 2);
        }
    }
}
