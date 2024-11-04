package hr.algebra.greatwesterntrail.controller;

import hr.algebra.greatwesterntrail.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WorkerDeckController {
    @FXML
    public Label cowboyCount;
    @FXML
    public Label builderCount;
    @FXML
    public Label engineerCount;

    public void initialize(Player player) {
        player.getWorkerDeck().forEach((workerType, count) -> {
            switch (workerType) {
                case COWBOY -> cowboyCount.setText(String.valueOf(count));
                case BUILDER -> builderCount.setText(String.valueOf(count));
                case ENGINEER -> engineerCount.setText(String.valueOf(count));
            }
        });
    }
}
