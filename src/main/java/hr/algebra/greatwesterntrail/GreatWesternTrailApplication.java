package hr.algebra.greatwesterntrail;

import hr.algebra.greatwesterntrail.utils.SceneUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GreatWesternTrailApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneUtils.loadMainScene(stage, "view/board.fxml", "The Great Western Trail");
    }

    public static void main(String[] args) {
        launch();
    }
}