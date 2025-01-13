package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.thread.ReadGameMoveThread;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;

public final class GameMoveUtils {
    private GameMoveUtils() { }

    public static final String FILE_PATH = "game/gameMoves.dat";

    public static void createAndRunTheLastGameMoveTimeline(TextArea textArea) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            if(Files.exists(Path.of(FILE_PATH))) {
                ReadGameMoveThread readGameMoveThread = new ReadGameMoveThread(textArea);
                Thread runnerThread = new Thread(readGameMoveThread);
                runnerThread.start();
            }
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
