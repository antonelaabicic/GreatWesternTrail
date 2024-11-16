package hr.algebra.greatwesterntrail.model;

import hr.algebra.greatwesterntrail.repository.TileRepository;
import hr.algebra.greatwesterntrail.utils.TrainProgressUtils;
import lombok.Data;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

@Data
public class Player {
    private Position playerPosition;
    private int vp = 0;
    private int money = 75;
    private boolean objectiveCompleted = false;
    private boolean bonusAwarded = false;

    private Map<CowType, Integer> cowDeck;
    private Map<WorkerType, Integer> workerDeck;

    private final Map<CowType, Integer> initialCowDeck;
    private final Map<WorkerType, Integer> initialWorkerDeck;

    private Map<CowType, Integer> peakCowDeck;
    private Map<WorkerType, Integer> peakWorkerDeck;

    private int initialEngineers = 0;
    private double trainObjectiveCheck = 0;
    private double trainProgress = 0;

    @Setter
    private Consumer<Player> onTrainProgressMaxReached;
    @Setter
    private Consumer<Player> onGameOver;

    public Player() {
        this.initialCowDeck = initializeDeck(CowType.class);
        this.cowDeck = new EnumMap<>(initialCowDeck);

        this.initialWorkerDeck = initializeDeck(WorkerType.class);
        this.workerDeck = new EnumMap<>(initialWorkerDeck);

        this.peakCowDeck = new EnumMap<>(cowDeck);
        this.peakWorkerDeck = new EnumMap<>(workerDeck);

        this.initialEngineers = workerDeck.getOrDefault(WorkerType.ENGINEER, 0);
        this.trainProgress = this.initialEngineers;

        playerPosition = new Position(TileRepository.GRID_SIZE - 1, TileRepository.GRID_SIZE - 1);
    }

    private <E extends Enum<E>> Map<E, Integer> initializeDeck(Class<E> enumClass) {
        Map<E, Integer> deck = new EnumMap<>(enumClass);
        Random random = new Random();
        for (E type : enumClass.getEnumConstants()) {
            deck.put(type, random.nextInt(3));
        }
        return deck;
    }

    public void incrementTrainProgress(double increment) {
        this.trainProgress += increment;
        this.trainObjectiveCheck += increment;
        if (this.trainProgress >= TrainProgressUtils.MAX_PROGRESS_STEPS && onTrainProgressMaxReached != null) {
            onTrainProgressMaxReached.accept(this);
        }
    }

    public void showGameOver(Objective objective) {
        if (hasMetObjective(objective)) {
            setObjectiveCompleted(true);
            if (onGameOver != null) {
                onGameOver.accept(this);
            }
        }
    }

    public boolean hasMetObjective(Objective objective) {
        ObjectiveAction action = objective.getAction();
        int requiredQuantity = objective.getQuantity();

        return switch (action) {
            case BUY_JERSEY, SELL_JERSEY -> checkCowCount(CowType.JERSEY, requiredQuantity, action);
            case BUY_ANGUS, SELL_ANGUS -> checkCowCount(CowType.BLACK_ANGUS, requiredQuantity, action);
            case BUY_LONGHORN, SELL_LONGHORN -> checkCowCount(CowType.TEXAS_LONGHORN, requiredQuantity, action);
            case BUY_HOLSTEIN, SELL_HOLSTEIN -> checkCowCount(CowType.HOLSTEIN, requiredQuantity, action);
            case HIRE_COWBOY, FIRE_COWBOY -> checkWorkerCount(WorkerType.COWBOY, requiredQuantity, action);
            case HIRE_ENGINEER, FIRE_ENGINEER -> checkWorkerCount(WorkerType.ENGINEER, requiredQuantity, action);
            case HIRE_BUILDER, FIRE_BUILDER -> checkWorkerCount(WorkerType.BUILDER, requiredQuantity, action);
            case ADVANCE_TRAIN_PROGRESS -> trainObjectiveCheck >= requiredQuantity;
        };
    }

    private boolean checkCowCount(CowType cowType, int quantity, ObjectiveAction action) {
        int currentCount = cowDeck.getOrDefault(cowType, 0);
        int peakCount = peakCowDeck.getOrDefault(cowType, 0);

        if (action.isBuyAction()) {
            return currentCount >= quantity;
        }
        else if (action.isSellAction()){
            return peakCount >= quantity && currentCount < peakCount;
        }
        throw new IllegalArgumentException("Unsupported action: " + action);
    }

    private boolean checkWorkerCount(WorkerType workerType, int quantity, ObjectiveAction action) {
        int currentCount = workerDeck.getOrDefault(workerType, 0);
        int peakCount = peakWorkerDeck.getOrDefault(workerType, 0);

        if (action.isHireAction()) {
            return currentCount >= quantity;
        }
        else if (action.isFireAction()) {
            return peakCount >= quantity && currentCount < peakCount;
        }
        throw new IllegalArgumentException("Unsupported action: " + action);
    }

    public void updatePeakValues(CowType cowType, int newCount) {
        peakCowDeck.put(cowType, Math.max(peakCowDeck.getOrDefault(cowType, 0), newCount));
    }

    public void updatePeakValues(WorkerType workerType, int newCount) {
        peakWorkerDeck.put(workerType, Math.max(peakWorkerDeck.getOrDefault(workerType, 0), newCount));
    }

    public void setPlayerPosition(int row, int column) {
        this.playerPosition.setRow(row);
        this.playerPosition.setColumn(column);
    }
}
