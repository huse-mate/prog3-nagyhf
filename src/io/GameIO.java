package io;

import render.GameScene;
import game.Player;
import game.Resource;

import java.util.Map;
import javax.swing.SwingUtilities;
import game.Game;

public class GameIO {
    private static GameScene gameScene;
    private static Game game;

    private GameIO() { /* prevent instantiation */ }

    public static void setGameScene(GameScene gs){
        GameIO.gameScene = gs;
    }

    public static void setGame(Game g){
        GameIO.game = g;
    }

    public static void gameStartSequenceBegin() {
        if (gameScene != null) {
            gameScene.gameStartSequenceBegin();
            gameScene.setSaveButtonEnabled(false);
            gameScene.setTradingEnabled(false);
            gameScene.setEndTurnEnabled(false);
            gameScene.setDiceButtonEnabled(false);
            gameScene.setBuildingEnabled(true);
        }
    }

    public static void gameStartSequenceEnd() {
        if (gameScene != null) {
            gameScene.gameStartSequenceEnd();
            gameScene.setSaveButtonEnabled(true);
            gameScene.setTradingEnabled(false);
            gameScene.setEndTurnEnabled(false);
            gameScene.setDiceButtonEnabled(true);
            gameScene.setBuildingEnabled(false);
        }
    }

    private static final Object starterPlacementLock = new Object();
    public static void waitForStarterSettlementPlacement(){
        synchronized (starterPlacementLock) {
            try {
                starterPlacementLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void waitForStarterRoadPlacement(){
        synchronized (starterPlacementLock) {
            try {
                starterPlacementLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void notifyStarterPlacement() {
        synchronized (starterPlacementLock) {
            starterPlacementLock.notifyAll();
        }
    }

    public static void beginTurn() {
        if (gameScene != null) {
            // ensure UI changes happen on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                gameScene.beginTurn();
                refresh();
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
        gameScene.setTradingEnabled(true);
        gameScene.setSaveButtonEnabled(false);
        refresh();
    }

    private static final Object thiefMovementLock = new Object();

    public static void addThief(game.Tile tile){
        for (game.Tile t : game.getTileMap()) {
            if(t.getThief()){
                t.removeThief();
                break;
            }
        }
        tile.addThief();
    }

    public static void thiefMovementStart(){
        if (gameScene != null) {
            gameScene.thiefMovementStart();
            gameScene.setSaveButtonEnabled(false);
        }
    }

    public static void thiefMovementEnd(){
        if (gameScene != null) {
            gameScene.thiefMovementEnd();
            gameScene.setSaveButtonEnabled(true);
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

    public static void attemptTrade(Player to, Map<Resource, Integer> give, Map<Resource, Integer> receive) {
        game.attemptTrade(to, give, receive);
        refresh();
    }

    public static void attemptTradeWithBank(Resource give, Resource receive){
        game.attemptTradeWithBank(give, receive);
        refresh();
    }

    public static void refresh() {
        if (gameScene != null) {
            gameScene.updateStatus();
        }
    }

    public static GameScene getGameScene() {
        return gameScene;
    }

}
