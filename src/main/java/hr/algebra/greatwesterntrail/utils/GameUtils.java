package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.TileButton;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;

public class GameUtils {
    private GameUtils() { }

    public static void handleKeyboardNavigation(KeyEvent event, Player player, TileButton[][] tileButtons) {
        int newRow = player.getPlayerPosition().getRow();
        int newCol = player.getPlayerPosition().getColumn();

        switch (event.getCode()) {
            case UP -> newRow--;
            case DOWN -> newRow++;
            case LEFT -> newCol--;
            case RIGHT -> newCol++;
            case ENTER -> {
                TileButton currentTileButton = tileButtons[player.getPlayerPosition().getRow()][player.getPlayerPosition().getColumn()];
                currentTileButton.performAction();
                return;
            }
            default -> { return; }
        }

        if (isValidPosition(newRow, newCol)) {
            player.setPlayerPosition(newRow, newCol);
            TileUtils.highlightCurrentTile(player.getPlayerPosition(), tileButtons);
        } else {
            DialogUtils.showDialog("Out of Bounds", "You cannot move outside the board!", Alert.AlertType.WARNING);
        }
    }

    private static boolean isValidPosition(int row, int column) {
        return row >= 0 && row < TileRepository.GRID_SIZE && column >= 0 && column < TileRepository.GRID_SIZE;
    }
}
