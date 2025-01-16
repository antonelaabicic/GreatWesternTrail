package hr.algebra.greatwesterntrail.thread;

import hr.algebra.greatwesterntrail.model.GameMove;
import hr.algebra.greatwesterntrail.utils.GameMoveUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class GameMoveThread {

    protected static Boolean FILE_ACCESS_IN_PROGRESS = false;

    public synchronized void saveTheLastGameMove(GameMove gameMove) {
        while(FILE_ACCESS_IN_PROGRESS) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        List<GameMove> finalGameMoveList = new ArrayList<>();
        if (Files.exists(Path.of(GameMoveUtils.FILE_PATH))) {
            try {
                List<GameMove> gameMoves = (List<GameMove>) loadGameMoveList();
                finalGameMoveList.addAll(gameMoves);
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        finalGameMoveList.add(gameMove);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GameMoveUtils.FILE_PATH))) {
            oos.writeObject(finalGameMoveList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FILE_ACCESS_IN_PROGRESS = false;
        notifyAll();
    }

    public synchronized List<?> loadGameMoveList() throws IOException, ClassNotFoundException {

        while(FILE_ACCESS_IN_PROGRESS) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        FILE_ACCESS_IN_PROGRESS = true;

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GameMoveUtils.FILE_PATH));
        List<GameMove> gameMoveList = new ArrayList<>((List<GameMove>) ois.readObject());

        FILE_ACCESS_IN_PROGRESS = false;
        notifyAll();

        return gameMoveList;
    }
}
