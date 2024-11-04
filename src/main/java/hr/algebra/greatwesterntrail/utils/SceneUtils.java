package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.CowDeckController;
import hr.algebra.greatwesterntrail.controller.WorkerDeckController;
import hr.algebra.greatwesterntrail.model.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class SceneUtils {
    private SceneUtils() {
    }

    public static void loadMainScene(Stage stage, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(GreatWesternTrailApplication.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error loading main scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadScene(String fxmlPath, String title, Player player) {
        try {
            FXMLLoader loader = new FXMLLoader(GreatWesternTrailApplication.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);

            Object controller = loader.getController();
            if (controller instanceof CowDeckController) {
                ((CowDeckController) controller).initialize(player);
            } else if (controller instanceof WorkerDeckController) {
                ((WorkerDeckController) controller).initialize(player);
            }

            stage.show();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
