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
    public static final int KNIGHT_CARD_WEIGHT = 17;
    public static final int ROAD_CARD_WEIGHT = 3;
    public static final int POINT_CARD_WEIGHT = 5;

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
     * @param s the GameState DTO to restore from
     * @return a new Game instance matching the saved state
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

    /**
     * The game start sequence, where players place their starting settlements and roads
     */
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

    /**     
     * One turn of the current player, including dice throw and waiting for end turn, then advancing to the next player
     */
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

    /**
     * Handle a dice throw by the current player, distributing resources or handling a 7 throw
     */
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

    /**
     * Handle the thief movement phase for the current player
     * @param curPlayer the current player
     */
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

    /**
     * Get the set of players who are victims of the thief for the current player
     * @param cur the current player
     * @return a set of players who are victims of the thief
     */
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

    /**
     * Check if player p can give the specified resources, used for trading
     * @param p the player to check
     * @param give map of the resources to give, with the resource as key and amount as value
     * @return true if player p can give the specified resources, false otherwise
     */
    public boolean canGive(Player p, Map<Resource, Integer> give){
        for (Resource r : Resource.values()) {
            int amountGive = give.getOrDefault(r, 0);
            if (p.getResourceCount(r) < amountGive) {
                return false;
            }
        }
        return true;
    }

    /**
     * Attempt a trade between the current player and another player, only does so if both players have the specified resources
     * @param to the player to trade with
     * @param give map of resources the current player gives, with resource as key and amount as value
     * @param receive map of resources the current player receives, with resource as key and amount as value
     */
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

    /**
     * Attempt a trade between the current player and the bank, at a default rate of 4:1
     * @param give the resource to give to the bank
     * @param receive the resource to receive from the bank
     */
    public void attemptTradeWithBank(Resource give, Resource receive){
        int rate = 4;
        if (curPlayer.getResourceCount(give) < rate) {
            return;
        }
        curPlayer.addResource(give, -rate);
        curPlayer.addResource(receive, 1);
    }

    /**
     * Create a new building of the specified type for player p at location loc
     * This function doesnt check for input validity
     * @param p the player to create the building for
     * @param type the type of building to create
     * @param loc the coordinate to create the building at
     */
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

    /**
     * Create a new road for player p between coordinates c1 and c2
     * This function doesnt check for input validity
     * @param p the player to create the road for
     * @param c1 the first coordinate of the road
     * @param c2 the second coordinate of the road
     */
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

    /**
     * Adds the thief to the specified tile, removing it from any other tile that has it.
     * @param tile the tile to add the thief to
     */
    public void moveThief(Tile tile){
        for (Tile t : tileMap) {
            if(t.getThief()){
                t.removeThief();
                break;
            }
        }
        tile.addThief();
    }

    /**
     * Buy a development card for player p, if they can afford it
     * Increases the player's development card inventory accordingly
     * Chooses a random development card to give to the player
     * The odds are specified by the weights defined in Game class
     * @param p the player buying the development card
     */
    public void buyDevCard(Player p){
        if (!p.canBuyDevCard())
            return;
        p.addResource(Resource.WOOL, -1);
        p.addResource(Resource.WHEAT, -1);
        p.addResource(Resource.ORE, -1);

        int rand = random.nextInt(KNIGHT_CARD_WEIGHT + ROAD_CARD_WEIGHT + POINT_CARD_WEIGHT);
        if (rand < POINT_CARD_WEIGHT)
            p.addPointCard();
        else if (rand < POINT_CARD_WEIGHT + ROAD_CARD_WEIGHT)  
            p.addFreeRoadCard();
        else 
            p.addKnightCard();

    }

    /**
     * Use a knight card for player p, moving the thief and updating largest army if needed
     * @param p the player using the knight card
     */
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

    /**
     * Use a road building card for player p, allowing them to place two roads for free
     * @param p the player using the road building card
     */
    public void useRoadBuildingCard(Player p){
        p.removeRoadBuildingCard();
        p.addRoadForStart();
        p.addRoadForStart();
    }

    /**
     * Use a victory point card for player p, increasing their points by 1
     * @param p the player using the victory point card
     */
    public void useVictoryPointCard(Player p){
        p.removePointCard();
        p.addPoints(1);
    }

    /**
     * Get all possible settlement locations for the current player
     * @param start whether this is the starting placement phase
     * @return a set of possible settlement coordinates for the current player, if start is true, all road nodes are considered
     */
    public Set<Coordinate> getPossibleSettlements(boolean start){
        return roads.getPossibleSettlements(curPlayer, start);
    }
    
    /**
     * Advance to the next player in turn order
     */
    private void nextPlayer(){
        if(curIndex >= players.size()-1){
            curIndex = 0;
        } else {
            curIndex++;
        }
        curPlayer = players.get(curIndex);
    }

    /**
     * Debug function for giving resources to a player
     * @param playerId the id of the player to give resources to
     * @param r the resource type to give
     * @param n the amount of resources to give
     */
    public void debugGiveResources(int playerId, Resource r, int n){
        players.get(playerId).addResource(r, n);
    }
}
