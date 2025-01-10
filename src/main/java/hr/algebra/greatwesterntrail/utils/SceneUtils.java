package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.controller.BoardController;
import hr.algebra.greatwesterntrail.controller.HiringCenterPopupController;
import hr.algebra.greatwesterntrail.model.Player;
import hr.algebra.greatwesterntrail.model.TileButton;
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

    public static void loadScene(Class<?> appClass, String fxmlPath, String title, Object... dependencies) {
        try {
            FXMLLoader loader = new FXMLLoader(appClass.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);

            Object controller = loader.getController();
            if (controller != null) {
                invokeMatchingInitializeMethod(controller, dependencies);
            }

            stage.show();
        } catch (Exception e) {
            System.out.println("Error loading scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void invokeMatchingInitializeMethod(Object controller, Object... dependencies) {
        Method[] methods = controller.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.getName().equals("initialize")) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == dependencies.length) {
                    boolean matches = true;
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (!parameterTypes[i].isAssignableFrom(dependencies[i].getClass())) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        try {
                            method.invoke(controller, dependencies);
                            return;
                        } catch (Exception e) {
                            System.err.println("Error invoking initialize method: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        System.err.println("No matching initialize method found for controller: " + controller.getClass().getName());
    }
}
