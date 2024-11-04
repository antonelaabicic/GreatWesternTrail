package hr.algebra.greatwesterntrail.model;

import lombok.Data;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

@Data
public class Player {
    private int row;
    private int column;
    private int vp = 0;
    private int money = 75;
    private Map<CowType, Integer> cowDeck;
    private Map<WorkerType, Integer> workerDeck;

    public Player() {
        this.cowDeck = initializeDeck(CowType.class);
        this.workerDeck = initializeDeck(WorkerType.class);
    }

    private <E extends Enum<E>> Map<E, Integer> initializeDeck(Class<E> enumClass) {
        Map<E, Integer> deck = new EnumMap<>(enumClass);
        Random random = new Random();
        for (E type : enumClass.getEnumConstants()) {
            deck.put(type, random.nextInt(3));
        }
        return deck;
    }

//    private Map<CowType, Integer> initializeCowDeck() {
//        Map<CowType, Integer> deck = new EnumMap<>(CowType.class);
//        Random random = new Random();
//        for (CowType type : CowType.values()) {
//            deck.put(type, random.nextInt(3));
//        }
//        return deck;
//    }
//
//    private Map<WorkerType, Integer> initializeWorkerDeck() {
//        Map<WorkerType, Integer> deck = new EnumMap<>(WorkerType.class);
//        Random random = new Random();
//        for (WorkerType type : WorkerType.values()) {
//            deck.put(type, random.nextInt(3));
//        }
//        return deck;
//    }
}
