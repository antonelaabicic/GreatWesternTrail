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
    private LocalDateTime time;

    public GameMove(PlayerState playerState, TileState tileState) {
        this.playerState = playerState;
        this.tileState = tileState;
        this.time = LocalDateTime.now();
    }
}
