package hr.algebra.greatwesterntrail.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GameMove implements Serializable {
    @Serial
    private static final long serialVersionUID = 1006L;

    private PlayerState playerState;
    private TileState tileState;
    private Tile[][] tiles;
    private LocalDateTime time;

    public GameMove(PlayerState playerState, TileState tileState, Tile[][] tiles) {
        this.playerState = playerState;
        this.tileState = tileState;
        this.tiles = tiles;
        this.time = LocalDateTime.now();
    }
}
