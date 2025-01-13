package hr.algebra.greatwesterntrail.thread;

import hr.algebra.greatwesterntrail.model.GameMove;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SaveGameMoveThread extends GameMoveThread implements Runnable {

    private GameMove gameMove;

    @Override
    public void run() {
        saveTheLastGameMove(gameMove);
    }
}
