package IO;

import game.Tile;
import render.GameScene;
import game.Player;
import game.Resource;

import java.util.Map;
import java.util.Set;
import java.util.EnumMap;

public class GameIO {
    private static GameScene gameScene;

    private GameIO() { /* prevent instantiation */ }

    public static void setGameScene(GameScene gs){
        gameScene = gs;
    }


    public static void beginTurn() {
        if (gameScene != null) gameScene.beginTurn();
    }

    public static int getDiceThrow(){
        if (gameScene == null) return 0;
        return gameScene.waitForDiceThrow();
    }

    public static Tile getThiefMove(){
		// TODO
		return new Tile(0, null);
	}

    public static Player chooseVictim(Player curPlayer, Set<Player> players){
        // TODO
        return null;
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
