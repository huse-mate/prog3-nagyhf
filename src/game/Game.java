package game;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Map;

import IO.*;
import game.buildings.Building;
import game.buildings.City;
import game.buildings.RoadNetwork;
import game.buildings.Settlement;
import render.Colors;
import javax.swing.SwingUtilities;

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
        GameIO.beginTurn();
        // start the turn - enable UI elements via GameIO and perform dice throw
        diceThrow();

        nextPlayer();
    }

    public void diceThrow(){

        // Wait for the dice on a background thread so we don't block the EDT
        new Thread(() -> {
            int dice = GameIO.getDiceThrow();
            if(dice == 7){
                for (Player p : players) {
                    p.thiefSteal();
                }
                thiefMovement(GameIO.getThiefMove(), curPlayer);
            } else {
                for (Tile t : tileMap) {
                    if(t.getNum() == dice){
                        t.giveResources();
                    }
                }
            }
            // schedule UI update on EDT (GameLoop / GameScene will pick this up)
            SwingUtilities.invokeLater(() -> {
                // no-op placeholder: callers can refresh UI when appropriate
            });
        }, "DiceWaitThread").start();
    }

    public void thiefMovement(Tile dest, Player curPlayer){
        for (Tile t : tileMap) {
            if(t.getThief()){
                t.removeThief();
                break;
            }
        }
        Set<Player> stealFrom = dest.addThief(curPlayer);
        Player victim = GameIO.chooseVictim(curPlayer, stealFrom);
        if (victim != null) {
            Resource loot = victim.removeRandomResource();
            if (loot != null) curPlayer.addResource(loot, 1);
        }
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
