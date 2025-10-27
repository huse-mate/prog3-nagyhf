package game;
import java.util.ArrayList;
import java.util.Set;

import IO.*;
import game.buildings.Building;
import game.buildings.City;
import game.buildings.RoadNetwork;
import game.buildings.Settlement;

public class Game {
    private TileMap tileMap;
    private RoadNetwork roads;
    private ArrayList<Player> players;
    private Player curPlayer;
    private int curIndex;
    private int maxRoadLength;
    private Player maxRoadOwner;

    public Game(TileMap tileMap){
        this.tileMap = tileMap;
        players = new ArrayList<>();
        roads = new RoadNetwork();
        for(int i=0;i<4;i++){
            players.add(new Player(i));
        }
        curPlayer = players.get(0);
        curIndex = 0;

        maxRoadLength = 0;
        maxRoadOwner = curPlayer;
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
        roads.newBuilding(loc);
        for (Tile tile : neighbours) {
            tile.addBuilding(newBuild);
        }
    }

    public void newRoad(Coordinate c1, Coordinate c2){
        roads.newRoad(curPlayer, c1, c2);
        int maxPlayerLength = roads.getLongestPath(curPlayer);
        if(maxPlayerLength > maxRoadLength){
            maxRoadLength = maxPlayerLength;
            maxRoadOwner = curPlayer;
        }
        
    }

    public Set<RoadNetwork.Road> getPossibleRoads(){
        return roads.getPossibleRoads(curPlayer);
    }

    public Set<Coordinate> getPossibleSettlements(boolean start){
        return roads.getPossibleSettlements(curPlayer, start);
    }
    
}
