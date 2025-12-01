package io;

import game.*;
import render.GameScene;
import java.util.Map;
import javax.swing.*;

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
        return game.getCurrentPlayer();
    }

    public static GameScene getGameScene() {
        return gameScene;
    }

    public static void gameOver(Player winner) {
        gameScene.gameOver(winner);
    }

    /** 
     * Configures buttons at the game start sequence, so only the allowed actions are possible
     */
    public static void gameStartSequenceBegin() {
        SwingUtilities.invokeLater(() -> {
            gameScene.gameStartSequenceBegin();
            gameScene.setSaveButtonEnabled(false);
            gameScene.setTradingEnabled(false);
            gameScene.setEndTurnEnabled(false);
            gameScene.setDiceButtonEnabled(false);
            gameScene.setBuildingEnabled(true);
        });
    }

    /** 
     * Re-configures buttons after the game start sequence is complete
     */
    public static void gameStartSequenceEnd() {
        SwingUtilities.invokeLater(() -> {
            gameScene.gameStartSequenceEnd();
            gameScene.setSaveButtonEnabled(true);
            gameScene.setTradingEnabled(false);
            gameScene.setEndTurnEnabled(false);
            gameScene.setDiceButtonEnabled(true);
            gameScene.setBuildingEnabled(false);
        });
    }

    /** 
     * Blocks until the UI notifies that the player has placed their starter settlement.
     */
    public static void waitForStarterSettlementPlacement(){
        synchronized (starterPlacementLock) {
            try {
                starterPlacementLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** 
     * Blocks until the UI notifies that the player has placed their starter road.
     */
    public static void waitForStarterRoadPlacement(){
        synchronized (starterPlacementLock) {
            try {
                starterPlacementLock.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /** 
     * Called by the UI when a starter placement is placed to wake the waiting thread.
     */
    public static void notifyStarterPlacement() {
        synchronized (starterPlacementLock) {
            starterPlacementLock.notifyAll();
        }
    }

    /** 
     * Prepares the UI for the beginning of a new turn.
     */
    public static void beginTurn() {
        SwingUtilities.invokeLater(() -> {
                gameScene.setBuildingEnabled(false);
                gameScene.setEndTurnEnabled(false);
                gameScene.setDiceButtonEnabled(true);
                gameScene.setTradingEnabled(false);
                gameScene.setSaveButtonEnabled(true);
            });
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


    /**
     * Blocks until the UI provides a dice throw result.
     */
    public static int getDiceThrow(){
        if (gameScene == null) return 0;
        return gameScene.waitForDiceThrow();
    }

    /**
     * Re-configures buttons after a dice throw is completed.
     */
    public static void diceThrowEnd() {
        SwingUtilities.invokeLater(() -> {
            gameScene.updateStatus();
            gameScene.setBuildingEnabled(true);
            gameScene.setEndTurnEnabled(true);
            gameScene.setDiceButtonEnabled(false);
            gameScene.setTradingEnabled(true);
            gameScene.setSaveButtonEnabled(false);
        });
    }

    
    /**
     * Adds the thief to the specified tile, removing it from any other tile that has it.
     * @param tile the tile to move the thief to
     */
    public static void moveThief(Tile tile){
        game.moveThief(tile);
    }


    /**    
     * Re-configures buttons and UI state at the beginning of a thief movement phase.
     */
    public static void thiefMovementStart(){
        SwingUtilities.invokeLater(() -> {
            gameScene.thiefMovementStart();
            gameScene.setEndTurnEnabled(false);
            gameScene.setDiceButtonEnabled(false);
            gameScene.setBuildingEnabled(false);
            gameScene.setTradingEnabled(false);
            gameScene.setSaveButtonEnabled(false);
        });
    }

    /**    
     * Re-configures buttons and UI state at the end of a thief movement phase.
     */
    public static void thiefMovementEnd(){
        SwingUtilities.invokeLater(() -> {
            gameScene.thiefMovementEnd();
            gameScene.setEndTurnEnabled(true);
            gameScene.setDiceButtonEnabled(false);
            gameScene.setBuildingEnabled(true);
            gameScene.setTradingEnabled(true);
            gameScene.setSaveButtonEnabled(true);
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

    /**
     * Attempt to perform a trade between the current player and another player.
     * @param to the player to trade with
     * @param give the resources to give
     * @param receive the resources to receive
     */
    public static void attemptTrade(Player to, Map<Resource, Integer> give, Map<Resource, Integer> receive) {
        game.attemptTrade(to, give, receive);
        refresh();
    }

    /**
     * Attempt to perform a trade between the current player and the bank.
     * @param give the resource to give
     * @param receive the resource to receive
     */
    public static void attemptTradeWithBank(Resource give, Resource receive){
        game.attemptTradeWithBank(give, receive);
        refresh();
    }

    /**
     * Buy a development card for the current player.
     */
    public static void buyDevCard() {
        game.buyDevCard(game.getCurrentPlayer());
        refresh();
    }

    /**
     * Use a development card for the current player.
     * Does not check if the card can be used, that is the caller's responsibility.
     * @param card the card to use ("KNIGHT", "ROAD", "POINT")
     */
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
    }

    /**
     * Refresh the game scene UI to reflect the current game state.
     */
    public static void refresh() {
        if (gameScene != null) {
            gameScene.updateStatus();
        }
    }
}
