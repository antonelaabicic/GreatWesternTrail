package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.controller.CowDeckController;
import hr.algebra.greatwesterntrail.controller.WorkerDeckController;
import hr.algebra.greatwesterntrail.model.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.Method;

public final class SceneUtils {
    private SceneUtils() { }

    public static void loadMainScene(Stage stage, Class<?> appClass, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(appClass.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error loading main scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void loadScene(Class<?> appClass, String fxmlPath, String title, Player player) {
        try {
            FXMLLoader loader = new FXMLLoader(appClass.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);

            Object controller = loader.getController();
            invokeInitializeIfPresent(controller, player);

            stage.show();
        } catch (Exception e) {
            System.out.println("Error loading scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void invokeInitializeIfPresent(Object controller, Player player) {
        if (controller != null) {
            try {
                Method initializeMethod = controller.getClass().getMethod("initialize", Player.class);
                initializeMethod.invoke(controller, player);
            } catch (NoSuchMethodException e) {
                System.out.println("No initialize(Player) method found for controller: " + controller.getClass().getName());
            } catch (Exception e) {
                System.out.println("Error invoking initialize method on controller: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
