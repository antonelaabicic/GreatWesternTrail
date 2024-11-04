package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.model.CowType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import hr.algebra.greatwesterntrail.model.Player;

public class CowDeckController {
    @FXML
    public Label holsteinCount;
    @FXML
    public Label jerseyCount;
    @FXML
    public Label angusCount;
    @FXML
    public Label longhornCount;

    public void initialize(Player player) {
        player.getCowDeck().forEach((cowType, count) -> {
            switch (cowType) {
                case HOLSTEIN -> holsteinCount.setText(String.valueOf(count));
                case JERSEY -> jerseyCount.setText(String.valueOf(count));
                case BLACK_ANGUS -> angusCount.setText(String.valueOf(count));
                case TEXAS_LONGHORN -> longhornCount.setText(String.valueOf(count));
            }
        });
    }
}
