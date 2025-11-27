package main;

import render.MainFrame;
import game.*;
import io.GameIO;

public class Main {
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            MainFrame frame = new MainFrame(game);
            GameIO.setGameScene(game, frame.getGameScene());

            Thread gameThread = new Thread(() -> {
                // run the startup placement sequence
                game.gameStartSequence();
                // after the startup sequence finishes, start the first normal turn
                // run on the same background thread so the EDT remains responsive
                game.turn();
            }, "Game-Thread");
            gameThread.start();

        });
    }
}
