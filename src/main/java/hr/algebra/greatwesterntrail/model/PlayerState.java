package hr.algebra.greatwesterntrail.model;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;

@Data
public class PlayerState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1007L;

    private Position position;
    private int vp;
    private int money;
    private Map<CowType, Integer> cowDeck;
    private Map<WorkerType, Integer> workerDeck;
    private double trainProgress;

    public PlayerState(Player player) {
        this.position = player.getPlayerPosition();
        this.vp = player.getVp();
        this.money = player.getMoney();
        this.cowDeck = new EnumMap<>(player.getCowDeck());
        this.workerDeck = new EnumMap<>(player.getWorkerDeck());
        this.trainProgress = player.getTrainProgress();
    }
}

