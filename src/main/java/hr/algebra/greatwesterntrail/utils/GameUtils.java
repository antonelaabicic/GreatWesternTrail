package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.TileButton;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

public final class GameUtils {
    private static boolean hasMovedThisTurn = false;

    private GameUtils() { }

    public static void handleKeyboardNavigation(KeyEvent event, Player player, TileButton[][] tileButtons) {
        int currentRow = player.getPlayerPosition().getRow();
        int currentCol = player.getPlayerPosition().getColumn();
        int newRow = currentRow;
        int newCol = currentCol;

        switch (event.getCode()) {
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
            case ENTER -> {
                TileButton currentTileButton = tileButtons[currentRow][currentCol];
                currentTileButton.performAction();
                hasMovedThisTurn = false;
                return;
            }
            default -> { return; }
        }

        if (hasMovedThisTurn) { return; }
        if (isValidPosition(newRow, newCol)) {
            player.setPlayerPosition(newRow, newCol);
            TileUtils.highlightCurrentTile(player.getPlayerPosition(), tileButtons);
            hasMovedThisTurn = true;
        } else {
            DialogUtils.showDialog("Out of Bounds", "You cannot move outside the board!", Alert.AlertType.WARNING);
        }
    }

    private static boolean isValidPosition(int row, int column) {
        return row >= 0 && row < TileRepository.GRID_SIZE && column >= 0 && column < TileRepository.GRID_SIZE;
    }
}
