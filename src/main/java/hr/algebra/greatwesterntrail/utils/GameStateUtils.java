package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.model.Objective;
import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.Tile;
import hr.algebra.greatwesterntrail.repository.TileRepository;
import javafx.scene.control.Alert;

public class GameStateUtils {
    private GameStateUtils() { }

    public static void showWinnerDialog(Player player) {
        int bonusVP = 0;
        if (!player.isBonusAwarded()) {
            Tile[][] tiles = TileRepository.INSTANCE.getTiles();
            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    Objective objective = tile.getObjective();
                    if (objective != null && player.hasMetObjective(objective)) {
                        bonusVP += objective.calculateTotalVictoryPoints();
                        player.setObjectiveCompleted(true);
                        DialogUtils.showDialog(
                                "Success",
                                "You've achieved game's objective: " + objective.getDescription().toLowerCase()
                                        + "\nThat is why you get extra " + bonusVP + " VPs.",
                                Alert.AlertType.INFORMATION
                        );
                    }
                }
            }
            player.setBonusAwarded(true);
        }

        player.setVp(player.getVp() + bonusVP);
        if (player.getVp() > 99) {
            DialogUtils.showDialog(
                    "Success",
                    "Congratulations, you have won! \nYou've reached Kansas and have " + player.getVp() + " VPs.",
                    Alert.AlertType.INFORMATION
            );
            if (player.getOnGameOver() != null) { player.getOnGameOver().accept(player); }
        }
        else {
            DialogUtils.showDialog(
                    "Failure",
                    "You've reached Kansas, but you need at least 100 VPs to win. \nYou have " + player.getVp() + ".",
                    Alert.AlertType.ERROR
            );
        }
    }
}
