package main;

import render.MainFrame;
import game.*;
import io.GameIO;

public class Main {
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            MainFrame frame = new MainFrame(game);
            GameIO.setGameScene(frame.getGameScene());
            
            

        });
    }
}
