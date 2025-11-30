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

    private static final Object starterPlacementLock = new Object();
    private static final Object endTurnLock = new Object();
    private static final java.util.concurrent.atomic.AtomicReference<java.util.concurrent.CountDownLatch> thiefMovementLatch = new java.util.concurrent.atomic.AtomicReference<>();

    private GameIO() { /* prevent instantiation */ }

    public static void setGameScene(GameScene gs){
        GameIO.gameScene = gs;
    }

    public static void setGame(Game g){
        GameIO.game = g;
    }

    public static Player getCurrentPlayer(){
        if (game != null) {
            return game.getCurrentPlayer();
        }
        return null;
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
            SwingUtilities.invokeLater(() -> {
                gameScene.beginTurn();
                refresh();
            });
            
        }
        
    }


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
        gameScene.thiefMovementStart();
        gameScene.setEndTurnEnabled(false);
        gameScene.setDiceButtonEnabled(false);
        gameScene.setBuildingEnabled(false);
        gameScene.setTradingEnabled(false);
        gameScene.setSaveButtonEnabled(false);
        refresh();
    }

    public static void thiefMovementEnd(){
        SwingUtilities.invokeLater(() -> {
            gameScene.thiefMovementEnd();
            gameScene.setEndTurnEnabled(true);
            gameScene.setDiceButtonEnabled(false);
            gameScene.setBuildingEnabled(true);
            gameScene.setTradingEnabled(true);
            gameScene.setSaveButtonEnabled(true);
            refresh();
            java.util.concurrent.CountDownLatch latch = thiefMovementLatch.getAndSet(null);
            if (latch != null) {
                latch.countDown();
            }
        });
    }

    /**
     * Begin a thief movement phase and create a latch that waiters can await.
     * This sets up the synchronization and triggers the UI to enter thief-move mode.
     */
    public static void beginThiefMovement() {
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        thiefMovementLatch.set(latch);
        thiefMovementStart();
    }

    /**
     * Wait until the thief movement phase finishes (signalled by UI).
     */
    public static void waitForThiefMovementEnd(){
        java.util.concurrent.CountDownLatch latch = thiefMovementLatch.get();
        if (latch == null) return; // nothing to wait for
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

    public static void buyDevCard() {
        game.buyDevCard(game.getCurrentPlayer());
        refresh();
    }

    public static void useDevCard(String card) {
        switch (card) {
            case "KNIGHT":
                game.useKnightCard(game.getCurrentPlayer());
                break;
            case "ROAD":
                game.useRoadBuildingCard(game.getCurrentPlayer());
                break;
            case "POINT":
                game.useVictoryPointCard(game.getCurrentPlayer());
                break;
            default:
                break;
        }
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
