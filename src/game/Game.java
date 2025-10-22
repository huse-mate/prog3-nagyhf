package game;
import java.util.ArrayList;
import java.util.Set;

import IO.*;
import game.buildings.Building;
import game.buildings.City;
import game.buildings.Settlement;

public class Game {
    private TileMap tileMap;
    private ArrayList<Player> players;
    private Player curPlayer;
    private int curIndex;

    public Game(TileMap tileMap){
        this.tileMap = tileMap;
        players = new ArrayList<>();
        for(int i=0;i<4;i++){
            players.add(new Player(i));
        }
        curPlayer = players.get(0);
        curIndex = 0;
    }

    public void turn(){
        diceThrow();

        //TODO trade
        // GameIO.buildQuerry();

        if(curIndex >= players.size()-1){
            curIndex = 0;
        } else {
            curIndex++;
        }
        curPlayer = players.get(curIndex);
    }

    public void diceThrow(){
        int dice = GameIO.getDiceThrow();
        if(dice == 7){
            for (Player p : players) {
                p.thiefAction();
            }
            thiefMovement(GameIO.getThiefMove(), curPlayer);
        } else {
            for (Tile t : tileMap) {
                if(t.getNum() == dice){
                    t.giveResources();
                }
            }
        }
    }

    public void thiefMovement(Tile dest, Player curPlayer){
        for (Tile t : tileMap) {
            if(t.getThief()){
                t.removeThief();
                break;
            }
        }
        Set<Player> stealFrom = dest.addThief(curPlayer);
        Resource loot = GameIO.chooseVictim(curPlayer, stealFrom).removeRandomResource();
        curPlayer.addResource(loot, 1);
    }

    public void newBuilding(Building.Types type, Coordinate loc){
        ArrayList<Tile> neighbours = new ArrayList<>(tileMap.getNeighbouringTiles(loc));
        Building newBuild;
        if(type == Building.Types.SETTLEMENT){
            newBuild = new Settlement(curPlayer, loc, neighbours);
        } else {
            newBuild = new City(curPlayer, loc, neighbours);
        }
        curPlayer.addBuilding(newBuild);
        for (Tile tile : neighbours) {
            tile.addBuilding(newBuild);
        }
    }

    
}
