package save;

import game.*;
import game.buildings.*;
import java.util.*;

/**
 * Data Transfer Objects representing serializable game state.
 * Created to be serialized with Gson (or other JSON libs).
 */
public class GameState {
    public String version = "1.0";
    public long timestamp = System.currentTimeMillis();

    public int currentPlayerIndex;
    public List<PlayerDTO> players = new ArrayList<>();
    public List<TileDTO> tiles = new ArrayList<>();
    public List<BuildingDTO> buildings = new ArrayList<>();
    public List<RoadDTO> roads = new ArrayList<>();

    public int maxKnightsPlayed;
    public int maxKnightsOwnerId;

    public static class PlayerDTO {
        public int id;
        public String color;

        public int points;

        public int knightsPlayed;

        public Map<String,Integer> resources = new HashMap<>();
        public int settlementInventory;
        public int roadInventory;
        public int cityInventory;

        public int freeRoadCards;
        public int knightCards;
        public int victoryPointCards;
    }

    public static class TileDTO {
        public int x;
        public int y;
        public int corner;
        public String resource;
        public int number;
        public boolean thief;
    }

    public static class BuildingDTO {
        public String type;
        public int ownerId;
        public int x;
        public int y;
        public int corner;
    }

    public static class RoadDTO {
        public int ownerId;
        public int x1;
        public int y1;
        public int c1;
        public int x2;
        public int y2;
        public int c2;
    }

    /** Create a GameState DTO from a live Game instance. */
    public static GameState fromGame(Game game) {
        GameState s = new GameState();
        s.currentPlayerIndex = game.getPlayers().indexOf(game.getCurrentPlayer());
        s.maxKnightsPlayed = game.getMaxKnightsPlayed();
        s.maxKnightsOwnerId = (game.getMaxKnightsOwner() == null) ? -1 : game.getMaxKnightsOwner().getId();

        // players
        for (Player p : game.getPlayers()) {
            PlayerDTO pd = new PlayerDTO();
            pd.id = p.getId();
            pd.color = p.getColor().name();
            pd.points = p.getScore();
            pd.settlementInventory = p.getSettlementInventory();
            pd.roadInventory = p.getRoadInventory();
            pd.cityInventory = p.getCityInventory();
            
            pd.knightsPlayed = p.getKnightsPlayed();
            pd.freeRoadCards = p.getDevCardCount("ROAD");
            pd.knightCards = p.getDevCardCount("KNIGHT");
            pd.victoryPointCards = p.getDevCardCount("POINT");
            for (Resource r : Resource.values()) {
                pd.resources.put(r.name(), p.getResourceCount(r));
            }
            s.players.add(pd);
            // buildings will be captured below from player.getBuildings()
        }

        // tiles
        for (Map.Entry<Coordinate, Tile> e : game.getTileMap().entries()) {
            Coordinate c = e.getKey();
            Tile t = e.getValue();
            TileDTO td = new TileDTO();
            td.x = c.getX();
            td.y = c.getY();
            td.corner = c.getCorner();
            td.resource = t.getType().name();
            td.number = t.getNum();
            td.thief = t.getThief();
            s.tiles.add(td);
        }

        // buildings (iterate players' buildings)
        for (Player p : game.getPlayers()) {
            for (Building b : p.getBuildings()) {
                BuildingDTO bd = new BuildingDTO();
                bd.ownerId = p.getId();
                bd.x = b.getCoordinate().getX();
                bd.y = b.getCoordinate().getY();
                bd.corner = b.getCoordinate().getCorner();
                if (b.getClass() == Settlement.class) bd.type = "SETTLEMENT";
                else if (b.getClass() == City.class) bd.type = "CITY";
                else bd.type = "UNKNOWN";
                s.buildings.add(bd);
            }
        }

        // roads
        for (RoadNetwork.Road r : game.getRoads()) {
            RoadDTO rd = new RoadDTO();
            if(r.getOwner() != null)
                rd.ownerId = r.getOwner().getId();
            else
                rd.ownerId = -1;
            rd.x1 = r.getCoord1().getX(); rd.y1 = r.getCoord1().getY(); rd.c1 = r.getCoord1().getCorner();
            rd.x2 = r.getCoord2().getX(); rd.y2 = r.getCoord2().getY(); rd.c2 = r.getCoord2().getCorner();
            s.roads.add(rd);
        }

        return s;
    }
    
}
