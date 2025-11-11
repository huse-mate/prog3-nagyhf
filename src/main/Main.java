package main;

import render.MainFrame;
import IO.GameIO;
import game.*;
import game.buildings.Building;

public class Main {
    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            MainFrame frame = new MainFrame(game);
            GameIO.setGameScene(frame.getGameScene());

            game.debugGiveResources(Resource.WOOD, 5);
            game.debugGiveResources(Resource.BRICK, 5);
            game.debugGiveResources(Resource.WOOL, 5);
            game.debugGiveResources(Resource.WHEAT, 5);
            game.debugGiveResources(Resource.ORE, 5);

            game.newBuilding(Building.Types.SETTLEMENT, new Coordinate(-1,0, 0));
            game.newRoad(new Coordinate(-1,0, 0), new Coordinate(-2,0,1));
            game.newRoad(new Coordinate(-2,0, 1), new Coordinate(-1,-1,0));

            GameLoop gameLoop = new GameLoop(game, frame.getGameScene());
            gameLoop.start();
            game.turn();

        });
    }
}
