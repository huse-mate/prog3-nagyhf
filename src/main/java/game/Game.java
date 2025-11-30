package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.Random;
import save.GameState;

import io.*;
import render.Colors;
import game.buildings.*;

public class Game {
    private TileMap tileMap;
    private RoadNetwork roads;
    private ArrayList<Player> players;
    private Player curPlayer;
    private int curIndex;

    public static final int POINTS_TO_WIN = 10;

    private int maxRoadLength = 0;
    private Player maxRoadOwner = null;

    private int maxKnightsPlayed = 0;
    private Player maxKnightsOwner = null;

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
    }

    /**
     * Construct a new Game instance from a previously saved GameState DTO.
     * This replaces tiles, players, buildings and roads to match the saved state.
     */
    public static Game fromState(GameState s) {
        Game g = new Game();

        // Rebuild tiles
        Map<Coordinate, Tile> tileMap = new HashMap<>();
        for (GameState.TileDTO td : s.tiles) {
            Coordinate c = new Coordinate(td.x, td.y, td.corner);
            Resource res = Resource.valueOf(td.resource);
            Tile t = new Tile(td.number, res);
            if (td.thief) t.addThief();
            tileMap.put(c, t);
        }
        g.tileMap = new TileMap(tileMap);

        // Rebuild players
        ArrayList<Player> newPlayers = new ArrayList<>();
        for (GameState.PlayerDTO pd : s.players) {
            Colors col = Colors.valueOf(pd.color);
            Player p = new Player(pd.id, col);
            if (pd.points > 0) p.addPoints(pd.points);
            if (pd.knightsPlayed > 0) p.setKnightsPlayed(pd.knightsPlayed);
            
            for (int i = 0; i < pd.freeRoadCards; i++) p.addFreeRoadCard();
            for (int i = 0; i < pd.knightCards; i++) p.addKnightCard();
            for (int i = 0; i < pd.victoryPointCards; i++) p.addPointCard();

            for (Map.Entry<String, Integer> en : pd.resources.entrySet()) {
                Resource r = Resource.valueOf(en.getKey());
                int count = en.getValue();
                if (count > 0) p.addResource(r, count);
            }
            for (int i = 0; i < pd.settlementInventory; i++) p.addSettlementForStart();
            for (int i = 0; i < pd.roadInventory; i++) p.addRoadForStart();
            for (int i = 0; i < pd.cityInventory; i++) p.addCityForStart();
            newPlayers.add(p);
        }
        g.players = newPlayers;

        // Rebuild roads/buildings
        g.roads = new RoadNetwork();
        // buildings
        for (GameState.BuildingDTO bd : s.buildings) {
            Player owner = g.players.stream().filter(pp -> pp.getId() == bd.ownerId).findFirst().orElse(null);
            if (owner == null) continue;
            Coordinate coord = new Coordinate(bd.x, bd.y, bd.corner);
            if ("SETTLEMENT".equals(bd.type)) {
                owner.addSettlementForStart();
                g.newBuilding(owner, Building.Types.SETTLEMENT, coord);
                owner.addPoints(-1);
            } else if ("CITY".equals(bd.type)) {
                owner.addCityForStart();
                g.newBuilding(owner, Building.Types.CITY, coord);
                owner.addPoints(-2);
            }
        }

        GameIO.setGame(g);
        GameIO.getGameScene().setGame(g);

        // roads
        for (GameState.RoadDTO rd : s.roads) {
            Player owner = g.players.stream().filter(pp -> pp.getId() == rd.ownerId).findFirst().orElse(null);
            if (owner == null || rd.ownerId == -1) continue;
            Coordinate c1 = new Coordinate(rd.x1, rd.y1, rd.c1);
            Coordinate c2 = new Coordinate(rd.x2, rd.y2, rd.c2);
            owner.addRoadForStart();
            g.newRoad(owner, c1, c2);
        }

        // set current player
        g.curIndex = s.currentPlayerIndex;
        g.curPlayer = g.players.get(g.curIndex);
        g.maxKnightsPlayed = s.maxKnightsPlayed;
        g.maxKnightsOwner = (s.maxKnightsOwnerId == -1) ? null : g.players.get(s.maxKnightsOwnerId);
        

        return g;
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

    public int getMaxKnightsPlayed() {
        return maxKnightsPlayed;
    }

    public Player getMaxKnightsOwner() {
        return maxKnightsOwner;
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
        if (curPlayer.getScore() >= POINTS_TO_WIN) {
            GameIO.gameOver(curPlayer);
            return;
        }
        nextPlayer();
        GameIO.refresh();
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
        // Create a latch and enter thief-movement UI mode, then wait for UI
        // to signal the end of the movement phase.
        GameIO.beginThiefMovement();
        GameIO.waitForThiefMovementEnd();

        Set<Player> victims = getThiefVictims(curPlayer);
        if ( victims.isEmpty() ) {
            return;
        }
        int victimIndex = random.nextInt(victims.size());
        Player victim = (Player) victims.toArray()[victimIndex];
        Resource loot = victim.removeRandomResource();
        if (loot != null) curPlayer.addResource(loot, 1);
    }

    public Set<Player> getThiefVictims(Player cur){
        Set<Player> victims = new HashSet<>();
        players.forEach( p -> {
            if(p != cur){
                boolean adjacentToThief = false;
                for (Building b : p.getBuildings()) {
                    for (Tile nb : tileMap.getNeighbouringTiles(b.getCoordinate())) {
                        if (nb.getThief()) {
                            adjacentToThief = true;
                            break;
                        }
                    }
                    if (adjacentToThief && p.getCardCount() > 0) {
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


    public void newBuilding(Player p, Building.Types type, Coordinate loc){
        p.addPoints(1);
        ArrayList<Tile> neighbours = new ArrayList<>(tileMap.getNeighbouringTiles(loc));
        Building newBuild;
        if(type == Building.Types.SETTLEMENT){
            newBuild = new Settlement(p, loc, neighbours);
            if (p.getSettlementInventory() > 0) {
                p.removeSettlementInventory();
            } else {
                p.addResource(Resource.WOOD, -1);
                p.addResource(Resource.BRICK, -1);
                p.addResource(Resource.WOOL, -1);
                p.addResource(Resource.WHEAT, -1);
            }
        } else {
            newBuild = new City(p, loc, neighbours);
            if (p.getCityInventory() > 0) {
                p.removeCityInventory();
            } else {
                p.addResource(Resource.WHEAT, -2);
                p.addResource(Resource.ORE, -3);
            }
        }
        p.addBuilding(newBuild);
        roads.newBuilding(loc, p);
        for (Tile tile : neighbours) {
            tile.addBuilding(newBuild);
        }
        GameIO.refresh();
    }

    public void newRoad(Player p, Coordinate c1, Coordinate c2){
        roads.newRoad(p, c1, c2);
        if (p.getRoadInventory() > 0) {
            p.removeRoadInventory();
        } else {
            p.addResource(Resource.BRICK, -1);
            p.addResource(Resource.WOOD, -1);
        }
        int maxPlayerLength = roads.getLongestPath(p);
        p.setMaxRoadLength(maxPlayerLength);
        if(maxPlayerLength > maxRoadLength){
            maxRoadLength = maxPlayerLength;
            if(maxRoadOwner != p && maxRoadLength >= 5){
                if(maxRoadOwner != null)
                    maxRoadOwner.addPoints(-2);
                p.addPoints(2);
                maxRoadOwner = p;
            }
        }
        GameIO.refresh();
    }

    public void buyDevCard(Player p){
        if (!p.canBuyDevCard())
            return;
        p.addResource(Resource.WOOL, -1);
        p.addResource(Resource.WHEAT, -1);
        p.addResource(Resource.ORE, -1);

        int rand = random.nextInt(25);
        if (rand < 5)
            p.addPointCard();
        else if (rand < 8)  
            p.addFreeRoadCard();
        else 
            p.addKnightCard();

    }

    public void useKnightCard(Player p){
        p.removeKnightCard();
        p.addKnightsPlayed();
        if (p.getKnightsPlayed() >= 3 && p.getKnightsPlayed() > maxKnightsPlayed) {
            if (maxKnightsOwner != null && maxKnightsOwner != p) {
                maxKnightsOwner.addPoints(-2);
            }
            maxKnightsOwner = p;
            maxKnightsPlayed = p.getKnightsPlayed();
            p.addPoints(2);
        }

        // Ensure thief movement doesn't block the EDT. If this method was
        // called from the Swing event thread (e.g. via a button), run the
        // thief movement on a background thread so the UI remains responsive.
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            new Thread(() -> thiefMovement(p), "thief-movement-thread").start();
        } else {
            thiefMovement(p);
        }
    }

    public void useRoadBuildingCard(Player p){
        p.removeRoadBuildingCard();
        p.addRoadForStart();
        p.addRoadForStart();
    }

    public void useVictoryPointCard(Player p){
        p.removePointCard();
        p.addPoints(1);
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
