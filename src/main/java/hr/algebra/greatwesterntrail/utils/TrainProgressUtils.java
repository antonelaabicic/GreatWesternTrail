package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.model.VerticalProgressBar;

public class TrainProgressUtils {

    private TrainProgressUtils() { }

    public static final int MAX_PROGRESS_STEPS = 20;

    public static void updateTrainProgressBar(VerticalProgressBar progressBar, double trainProgress) {
        double progressPercentage = trainProgress / MAX_PROGRESS_STEPS;
        progressBar.setProgress(progressPercentage);
    }
}
