package hr.algebra.greatwesterntrail.model;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class VerticalProgressBar extends Region {
    private double progress = 0.0;
    private Color barColor = Color.RED;
    private Rectangle progressRect;

    //
    private static final int MAX_PROGRESS_STEPS = 20;
    private int currentProgressSteps = 0;

    public VerticalProgressBar() {
        setPrefWidth(48);
        setPrefHeight(443);

        progressRect = new Rectangle(0, 0, getPrefWidth(), 0);
        progressRect.setFill(barColor);

        progressRect.setArcWidth(15);
        progressRect.setArcHeight(15);

        getChildren().add(progressRect);

        Tooltip tooltip = new Tooltip();
        setOnMouseEntered(event -> {
            double percentage = progress * 100;
            tooltip.setText(String.format("%.0f%%", percentage));
            Tooltip.install(this, tooltip);
        });

        setOnMouseExited(event -> Tooltip.uninstall(this, tooltip));
    }

    public void setProgress(double progress) {
        this.progress = Math.max(0.0, Math.min(progress, 1.0));
        requestLayout();
    }

    //
    public void incrementProgress() {
        if (currentProgressSteps < MAX_PROGRESS_STEPS) {
            currentProgressSteps++;
            setProgress((double) currentProgressSteps / MAX_PROGRESS_STEPS);
        }
    }

    public void setBarColor(Color color) {
        this.barColor = color;
        progressRect.setFill(color);
    }

    @Override
    protected void layoutChildren() {
        double height = getHeight();
        double fillHeight = height * progress;
        progressRect.setHeight(fillHeight);
        progressRect.setY(height - fillHeight);
    }
}
