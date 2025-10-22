package IO;

import game.Tile;
import render.GameRender;
import game.Player;
import game.Resource;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.EnumMap;

public class GameIO {
    private static GameRender gameEngine;
    private static Random rand;

    public GameIO(){
        // TODO
    }

    public static int getDiceThrow(){
        int dice = rand.nextInt(6) + rand.nextInt(6) + 2;
        gameEngine.renderDiceThrow(dice);
        return dice;
    }

    public static Tile getThiefMove(){
		// TODO
		return new Tile(0, null);
	}

    public static Player chooseVictim(Player curPlayer, Set<Player> players){
        // TODO
        return new Player(0);
    }

    public static Map<Resource, Integer> chooseToThrow(Player p, int n){
        EnumMap<Resource, Integer> toThrow = new EnumMap<>(Resource.class);
        for (Resource r : Resource.values()) {
            toThrow.put(r, 0);
        }
        // TODO
        return toThrow;
    }
}
