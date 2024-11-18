package hr.algebra.greatwesterntrail.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class GameState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1001L;
    private final Player player;
    private final Tile[][] tiles;
}
