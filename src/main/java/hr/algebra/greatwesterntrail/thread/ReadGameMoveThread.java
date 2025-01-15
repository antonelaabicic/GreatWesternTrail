package hr.algebra.greatwesterntrail.thread;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.model.*;
import hr.algebra.greatwesterntrail.utils.TrainProgressUtils;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ReadGameMoveThread extends GameMoveThread implements Runnable {

    private TextArea textArea;

    @Override
    public void run() {
        try {
            List<?> gameMoves = loadGameMoveList();

            if (!gameMoves.isEmpty()) {
                GameMove gameMove = (GameMove) gameMoves.getLast();
                String formattedText = buildFormattedText(gameMove);

                Platform.runLater(() -> textArea.setText(formattedText));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildFormattedText(GameMove gameMove) {
        StringBuilder formattedText = new StringBuilder();

        formattedText.append("- Tile: ").append(getTileDetails(gameMove.getTileState())).append("\n");
        formattedText.append("- Position: ")
                .append("(")
                .append(gameMove.getPlayerState().getPosition().getRow()).append(", ")
                .append(gameMove.getPlayerState().getPosition().getColumn()).append(")")
                .append("\n");

        double trainProgressPercentage = TrainProgressUtils.getTrainProgressPercentage(gameMove.getPlayerState().getTrainProgress());
        formattedText.append("- Player: ").append(GreatWesternTrailApplication.playerMode)
                .append(" (")
                .append(gameMove.getPlayerState().getMoney()).append("$, ")
                .append(gameMove.getPlayerState().getVp()).append(" VP, train progress: ")
                .append(trainProgressPercentage).append(" %)")
                .append("\n");

        formattedText.append("- Cow deck: ");
        appendDeckDetails(formattedText, gameMove.getPlayerState().getCowDeck(), CowType.values());
        formattedText.append("\n");

        formattedText.append("- Worker deck: ");
        appendDeckDetails(formattedText, gameMove.getPlayerState().getWorkerDeck(), WorkerType.values());
        formattedText.append("\n");

        return formattedText.toString();
    }

    private String getTileDetails(TileState tileState) {
        StringBuilder tileDetails = new StringBuilder();
        tileDetails.append(tileState.getTileType());

        switch (tileState.getTileType()) {
            case BUILDING:
                tileDetails.append(" (").append(tileState.getBuildingType()).append(")");
                break;
            case HAZARD:
                tileDetails.append(" (").append(tileState.getHazardType()).append(")");
                break;
            default:
                break;
        }

        if (tileState.getObjective() != null) {
            tileDetails.append(" => ").append(tileState.getObjective().getDescription());
        }

        return tileDetails.toString();
    }

    private <T> void appendDeckDetails(StringBuilder formattedText, Map<T, Integer> deck, T[] types) {
        int count = 0;
        for (T type : types) {
            int quantity = deck.getOrDefault(type, 0);
            if (quantity > 0) {
                if (count > 0) {
                    formattedText.append(", ");
                }
                formattedText.append(quantity).append(" ").append(type.toString());
                if (quantity > 1) {
                    if (type instanceof CowType cow && (cow == CowType.JERSEY || cow == CowType.BLACK_ANGUS)) {
                        formattedText.append("es");
                    } else {
                        formattedText.append("s");
                    }
                }
                count++;
            }
        }
    }
}

