package hr.algebra.greatwesterntrail.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class GameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1001L;

    private final Player playerOne;
    private final Player playerTwo;
    private final Tile[][] tiles;
    private boolean isPlayerOneTurn;
    private String dialogMessage;
    private boolean gameFinished;

    public Player getCurrentPlayer() {
        return isPlayerOneTurn ? playerOne : playerTwo;
    }

    public void nextTurn() {
        isPlayerOneTurn = !isPlayerOneTurn;
    }
}


