package hr.algebra.greatwesterntrail.utils;

import hr.algebra.greatwesterntrail.GreatWesternTrailApplication;
import hr.algebra.greatwesterntrail.model.GameState;
import hr.algebra.greatwesterntrail.model.PlayerMode;

public class NetworkingUtils {
    public static void sendGameState(GameState gameState) {
        if (GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_ONE) {
            GreatWesternTrailApplication.sendRequestFromPlayerOne(gameState);
        }
        else if (GreatWesternTrailApplication.playerMode == PlayerMode.PLAYER_TWO) {
            GreatWesternTrailApplication.sendRequestFromPlayerTwo(gameState);
        }
    }
}
