package game;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game.buildings.Building;
import game.buildings.City;
import game.buildings.RoadNetwork;
import game.buildings.Settlement;
import io.*;
import render.Colors;

public class Game {
    private TileMap tileMap;
    private RoadNetwork roads;
    private ArrayList<Player> players;
    private Player curPlayer;
    private int curIndex;
    private int maxRoadLength;
    private Player maxRoadOwner;

    private Random random = new Random();

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

    public void gameStartSequence(){
        GameIO.gameStartSequenceBegin();
        boolean reverse = false;
        for(int round = 0; round < 2; round++){
            for(int i = 0; i < players.size(); i++){
                curPlayer = players.get( reverse ? players.size()-1 - i : i );
                curPlayer.addSettlementForStart();
                GameIO.refresh();
                GameIO.waitForStarterSettlementPlacement();
                curPlayer.addRoadForStart();
                GameIO.waitForStarterRoadPlacement();
                GameIO.refresh();
            }
            reverse = !reverse;
        }

        curIndex = random.nextInt(players.size());
        curPlayer = players.get(curIndex);
        
        GameIO.gameStartSequenceEnd();
        GameIO.refresh();
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
        if ( victims.isEmpty() ) {
            return;
        }
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

    public boolean canGive(Player p, Map<Resource, Integer> give){
        for (Resource r : Resource.values()) {
            int amountGive = give.getOrDefault(r, 0);
            if (p.getResourceCount(r) < amountGive) {
                return false;
            }
        }
        return true;
    }

    public void attemptTrade(Player to, Map<Resource, Integer> give, Map<Resource, Integer> receive){
        if (!canGive(curPlayer, give) || !canGive(to, receive)) 
            return;

        for (Resource r : Resource.values()) {
            int amountGive = give.getOrDefault(r, 0);
            int amountReceive = receive.getOrDefault(r, 0);
            curPlayer.addResource(r, -amountGive);
            to.addResource(r, amountGive);
            curPlayer.addResource(r, amountReceive);
            to.addResource(r, -amountReceive);
        }
    }

    public void attemptTradeWithBank(Resource give, Resource receive){
        int rate = 4; // default 4:1
        if (curPlayer.getResourceCount(give) < rate) {
            return;
        }
        curPlayer.addResource(give, -rate);
        curPlayer.addResource(receive, 1);
    }


    public void newBuilding(Building.Types type, Coordinate loc){
        curPlayer.addPoints(1);
        ArrayList<Tile> neighbours = new ArrayList<>(tileMap.getNeighbouringTiles(loc));
        Building newBuild;
        if(type == Building.Types.SETTLEMENT){
            newBuild = new Settlement(curPlayer, loc, neighbours);
            if (curPlayer.getSettlementInventory() > 0) {
                curPlayer.removeSettlementInventory();
            } else {
                curPlayer.addResource(Resource.WOOD, -1);
                curPlayer.addResource(Resource.BRICK, -1);
                curPlayer.addResource(Resource.WOOL, -1);
                curPlayer.addResource(Resource.WHEAT, -1);
            }
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
        GameIO.refresh();
    }

    public void newRoad(Coordinate c1, Coordinate c2){
        roads.newRoad(curPlayer, c1, c2);
        if (curPlayer.getRoadInventory() > 0) {
            curPlayer.removeRoadInventory();
        } else {
            curPlayer.addResource(Resource.BRICK, -1);
            curPlayer.addResource(Resource.WOOD, -1);
        }
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
        GameIO.refresh();
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

    public void debugGiveResources(int playerId, Resource r, int n){
        players.get(playerId).addResource(r, n);
    }
}
