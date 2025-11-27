package IO;

import game.Tile;
import render.GameScene;
import game.Player;
import game.Resource;

import java.util.Map;
import java.util.Set;
import java.util.EnumMap;
import javax.swing.SwingUtilities;

public class GameIO {
    private static GameScene gameScene;

    private GameIO() { /* prevent instantiation */ }

    public static void setGameScene(GameScene gs){
        gameScene = gs;
    }


    public static void beginTurn() {
        if (gameScene != null) {
            // ensure UI changes happen on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                gameScene.beginTurn();
            });
        }
    }

    private static final Object endTurnLock = new Object();

    /**
     * Blocks until the UI notifies that the player ended their turn.
     */
    public static void waitForEndTurn() {
        synchronized (endTurnLock) {
            try {
                endTurnLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Called by the UI when the End Turn button is pressed to wake the waiting turn thread.
     */
    public static void notifyEndTurn() {
        synchronized (endTurnLock) {
            endTurnLock.notifyAll();
        }
    }



    public static int getDiceThrow(){
        if (gameScene == null) return 0;
        return gameScene.waitForDiceThrow();
    }

    public static void diceThrowEnd() {
        gameScene.updateStatus();
        gameScene.setBuildingEnabled(true);
        gameScene.setEndTurnEnabled(true);
        gameScene.setDiceButtonEnabled(false);
        gameScene.repaint();
    }

    private static final Object thiefMovementLock = new Object();

    public static void thiefMovementStart(){
        if (gameScene != null) {
            gameScene.thiefMovementStart();
        }
    }

    public static void thiefMovementEnd(){
        if (gameScene != null) {
            gameScene.thiefMovementEnd();
            synchronized (thiefMovementLock) {
                thiefMovementLock.notifyAll();
            }
        }
    }

    public static void waitForThiefMovementEnd(){
        synchronized (thiefMovementLock) {
            try {
                thiefMovementLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


}
