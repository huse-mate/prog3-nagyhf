package game;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import IO.*;
import game.buildings.Building;
import game.buildings.City;
import game.buildings.RoadNetwork;
import game.buildings.Settlement;
import render.Colors;

public class Game {
    private TileMap tileMap;
    private RoadNetwork roads;
    private ArrayList<Player> players;
    private Player curPlayer;
    private int curIndex;
    private int maxRoadLength;
    private Player maxRoadOwner;

    public Game(){
        tileMap = new TileMap();
        players = new ArrayList<>();
        roads = new RoadNetwork();

        Player newPlayer1 = new Player(0, Colors.PLAYER1_COLOR);
        players.add(newPlayer1);
        Player newPlayer2 = new Player(1, Colors.PLAYER2_COLOR);
        players.add(newPlayer2);
        Player newPlayer3 = new Player(2, Colors.PLAYER3_COLOR);
        players.add(newPlayer3);
        Player newPlayer4 = new Player(3, Colors.PLAYER4_COLOR);
        players.add(newPlayer4);
        

        curPlayer = players.get(0);
        curIndex = 0;

        maxRoadLength = 0;
        maxRoadOwner = null;
    }

    public final TileMap getTileMap(){
        return tileMap;
    }

    public final List<Player> getPlayers() {
        return players;
    }

    public final Map<Coordinate, Player> getBuildingMap(){
        return roads.getBuildingMap();
    }

    public List<RoadNetwork.Road> getRoads() {
        return roads.getRoads();
    }

    public Set<RoadNetwork.Road> getPossibleRoads(){
        return roads.getPossibleRoads(curPlayer);
    }
    
    public Player getCurrentPlayer() {
        return curPlayer;
    }

    public List<RoadNetwork.Road> getAllRoads() {
        return roads.getAllRoads();
    }

    public void turn(){
        // enable UI for dice
        GameIO.beginTurn();

        // wait for dice (this blocks inside GameIO until user clicks)
        diceThrow();

        // notify UI that dice throw and its effects are done
        GameIO.diceThrowEnd();

        // now wait until the player presses End Turn
        GameIO.waitForEndTurn();

        // advance to next player
        nextPlayer();
    }

    public void diceThrow(){
        int dice = GameIO.getDiceThrow();
        if(dice == 7){
            for (Player p : players) {
                p.thiefSteal();
            }
            thiefMovement(curPlayer);
        } else {
            for (Tile t : tileMap) {
                if(t.getNum() == dice){
                    t.giveResources();
                }
            }
        }
    }

    public void thiefMovement(Player curPlayer){
        for (Tile t : tileMap) {
            if(t.getThief()){
                t.removeThief();
                break;
            }
        }
        GameIO.thiefMovementStart();
        GameIO.waitForThiefMovementEnd();

        Set<Player> victims = getThiefVictims();
        
        victims.forEach( victim -> {
            Resource loot = victim.removeRandomResource();
            if (loot != null) curPlayer.addResource(loot, 1);
        });
    }

    public Set<Player> getThiefVictims(){
        Set<Player> victims = new HashSet<>();
        players.forEach( p -> {
            if(p != curPlayer){
                boolean adjacentToThief = false;
                for (Building b : p.getBuildings()) {
                    for (Tile nb : tileMap.getNeighbouringTiles(b.getCoordinate())) {
                        if (nb.getThief()) {
                            adjacentToThief = true;
                            break;
                        }
                    }
                    if (adjacentToThief) {
                        victims.add(p);
                        break;
                    }
                }
            }
        }); 
        return victims;
    }

    public void newBuilding(Building.Types type, Coordinate loc){
        curPlayer.addPoints(1);
        ArrayList<Tile> neighbours = new ArrayList<>(tileMap.getNeighbouringTiles(loc));
        Building newBuild;
        if(type == Building.Types.SETTLEMENT){
            newBuild = new Settlement(curPlayer, loc, neighbours);
            curPlayer.addResource(Resource.WOOD, -1);
            curPlayer.addResource(Resource.BRICK, -1);
            curPlayer.addResource(Resource.WOOL, -1);
            curPlayer.addResource(Resource.WHEAT, -1);
        } else {
            newBuild = new City(curPlayer, loc, neighbours);
            curPlayer.addResource(Resource.WHEAT, -2);
            curPlayer.addResource(Resource.ORE, -3);
        }
        curPlayer.addBuilding(newBuild);
        roads.newBuilding(loc, curPlayer);
        for (Tile tile : neighbours) {
            tile.addBuilding(newBuild);
        }
    }

    public void newRoad(Coordinate c1, Coordinate c2){
        roads.newRoad(curPlayer, c1, c2);
        curPlayer.addResource(Resource.BRICK, -1);
        curPlayer.addResource(Resource.WOOD, -1);
        int maxPlayerLength = roads.getLongestPath(curPlayer);
        curPlayer.setMaxRoadLength(maxPlayerLength);
        if(maxPlayerLength > maxRoadLength){
            maxRoadLength = maxPlayerLength;
            if(maxRoadOwner != curPlayer && maxRoadLength >= 5){
                if(maxRoadOwner != null)
                    maxRoadOwner.addPoints(-2);
                curPlayer.addPoints(2);
                maxRoadOwner = curPlayer;
            }
        }
    }


    public Set<Coordinate> getPossibleSettlements(boolean start){
        return roads.getPossibleSettlements(curPlayer, start);
    }
    
    private void nextPlayer(){
        if(curIndex >= players.size()-1){
            curIndex = 0;
        } else {
            curIndex++;
        }
        curPlayer = players.get(curIndex);
    }

    public void debugGiveResources(Resource r, int n){
        curPlayer.addResource(r, n);
    }
}
