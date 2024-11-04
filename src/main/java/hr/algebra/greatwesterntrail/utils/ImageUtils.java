package hr.algebra.greatwesterntrail.utils;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static ImageView createImageView(String imagePath, double width, double height) {
        try {
            Image image = new Image(ImageUtils.class.getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);

            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            imageView.setPreserveRatio(true);

            imageView.setSmooth(true);
            imageView.setVisible(true);

            return imageView;
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            return new ImageView();
        }
    }

    public static void addIconToStackPane(StackPane stackPane, String imagePath, double width, double height, Pos position) {
        try {
            ImageView icon = createImageView(imagePath, width, height);
            stackPane.getChildren().add(icon);
            StackPane.setAlignment(icon, position);
        } catch (Exception e) {
            System.err.println("Error adding icon to StackPane: " + imagePath + " - " + e.getMessage());
        }
    }
}
