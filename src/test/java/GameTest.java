import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import game.*;
import game.buildings.*;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Map;

public class GameTest {
    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game();
    }

    @Test
    public void testAttemptTrade_success() {
        Player cur = game.getCurrentPlayer();
        Player other = game.getPlayers().get(1);

        game.debugGiveResources(0, Resource.WOOD, 2);
        game.debugGiveResources(1, Resource.BRICK, 2);

        Map<Resource, Integer> give = new HashMap<>();
        give.put(Resource.WOOD, 2);
        Map<Resource, Integer> receive = new HashMap<>();
        receive.put(Resource.BRICK, 2);

        game.attemptTrade(other, give, receive);

        assertEquals(0, cur.getResourceCount(Resource.WOOD));
        assertEquals(2, cur.getResourceCount(Resource.BRICK));
        assertEquals(0, other.getResourceCount(Resource.BRICK));
        assertEquals(2, other.getResourceCount(Resource.WOOD));
    }

    @Test
    public void testAttemptTrade_insufficient_prevents_trade() {
        Player cur = game.getCurrentPlayer();
        Player other = game.getPlayers().get(1);

        game.debugGiveResources(0, Resource.WOOD, 1);
        game.debugGiveResources(1, Resource.BRICK, 2);

        Map<Resource, Integer> give = new HashMap<>();
        give.put(Resource.WOOD, 2);
        Map<Resource, Integer> receive = new HashMap<>();
        receive.put(Resource.BRICK, 2);

        game.attemptTrade(other, give, receive);

        assertEquals(1, cur.getResourceCount(Resource.WOOD));
        assertEquals(0, cur.getResourceCount(Resource.BRICK));
        assertEquals(2, other.getResourceCount(Resource.BRICK));
        assertEquals(0, other.getResourceCount(Resource.WOOD));
    }

    @Test
    public void testAttemptTradeWithBank_success() {
        Player cur = game.getCurrentPlayer();

        game.debugGiveResources(0, Resource.WOOD, 5);

        game.attemptTradeWithBank(Resource.WOOD, Resource.BRICK);

        assertEquals(1, cur.getResourceCount(Resource.WOOD));
        assertEquals(1, cur.getResourceCount(Resource.BRICK));
    }

    @Test
    public void testAttemptTradeWithBank_insufficient_prevents_trade() {
        Player cur = game.getCurrentPlayer();

        game.debugGiveResources(0, Resource.WOOD, 3);

        game.attemptTradeWithBank(Resource.WOOD, Resource.BRICK);

        assertEquals(3, cur.getResourceCount(Resource.WOOD));
        assertEquals(0, cur.getResourceCount(Resource.BRICK));
    }

    @Test
    public void testNewBuilding_deducts_resources_when_no_inventory() {
        Player cur = game.getCurrentPlayer();

        game.debugGiveResources(cur.getId(), Resource.WOOD, 1);
        game.debugGiveResources(cur.getId(), Resource.BRICK, 1);
        game.debugGiveResources(cur.getId(), Resource.WOOL, 1);
        game.debugGiveResources(cur.getId(), Resource.WHEAT, 1);

        int beforeScore = cur.getScore();
        Coordinate coord = new Coordinate(0, 0, 0);

        game.newBuilding(cur, Building.Types.SETTLEMENT, coord);

        assertEquals(0, cur.getResourceCount(Resource.WOOD));
        assertEquals(0, cur.getResourceCount(Resource.BRICK));
        assertEquals(0, cur.getResourceCount(Resource.WOOL));
        assertEquals(0, cur.getResourceCount(Resource.WHEAT));

        assertEquals(beforeScore + 1, cur.getScore());

        assertEquals(cur, game.getBuildingMap().get(coord));
    }

    @Test
    public void testNewBuilding_uses_inventory_no_resource_cost() {
        Player cur = game.getCurrentPlayer();

        cur.addSettlementForStart();

        int beforeScore = cur.getScore();
        Coordinate coord = new Coordinate(1, 0, 0);

        game.newBuilding(cur, Building.Types.SETTLEMENT, coord);

        assertEquals(0, cur.getResourceCount(Resource.WOOD));
        assertEquals(0, cur.getResourceCount(Resource.BRICK));
        assertEquals(beforeScore + 1, cur.getScore());
        assertEquals(cur, game.getBuildingMap().get(coord));
    }

    @Test
    public void testNewRoad_deducts_resources_and_adds_road() {
        Player cur = game.getCurrentPlayer();

        game.debugGiveResources(cur.getId(), Resource.BRICK, 1);
        game.debugGiveResources(cur.getId(), Resource.WOOD, 1);

        int beforeRoads = game.getRoads().size();
        Coordinate c1 = new Coordinate(2, 0, 0);
        Coordinate c2 = new Coordinate(3, 0, 0);

        game.newRoad(cur, c1, c2);

        assertEquals(0, cur.getResourceCount(Resource.BRICK));
        assertEquals(0, cur.getResourceCount(Resource.WOOD));

        assertEquals(beforeRoads + 1, game.getRoads().size());
    }

    @Test
    public void testNewRoad_uses_inventory_no_resource_cost() {
        Player cur = game.getCurrentPlayer();

        cur.addRoadForStart();

        int beforeRoads = game.getRoads().size();
        Coordinate c1 = new Coordinate(4, 0, 0);
        Coordinate c2 = new Coordinate(5, 0, 0);

        game.newRoad(cur, c1, c2);

        assertEquals(0, cur.getResourceCount(Resource.BRICK));
        assertEquals(0, cur.getResourceCount(Resource.WOOD));

        assertEquals(beforeRoads + 1, game.getRoads().size());
    }

    @Test
    public void testGetThiefVictims_no_victims_when_no_cards() {
        // current player is player 0
        Player cur = game.getCurrentPlayer();
        Player other = game.getPlayers().get(1);

        // ensure other has no cards
        // place a settlement for other adjacent to the center tile (0,0,-1)
        other.addSettlementForStart();
        Coordinate buildCoord = new Coordinate(0, 0, 0);
        game.newBuilding(other, Building.Types.SETTLEMENT, buildCoord);

        // place thief on the central tile which is neighbouring the building
        Tile center = game.getTileMap().get(new Coordinate(0, 0, -1));
        center.addThief();

        // other has no cards -> should not be listed as victim
        var victims = game.getThiefVictims(cur);
        assertFalse(victims.contains(other));
    }

    @Test
    public void testGetThiefVictims_detects_victim_with_card() {
        Player cur = game.getCurrentPlayer();
        Player victim = game.getPlayers().get(1);

        // give the victim one resource so cardCount > 0
        game.debugGiveResources(victim.getId(), Resource.WOOD, 1);

        // place a settlement for victim adjacent to central tile
        victim.addSettlementForStart();
        Coordinate buildCoord = new Coordinate(0, 0, 0);
        game.newBuilding(victim, Building.Types.SETTLEMENT, buildCoord);

        // place thief on central tile
        Tile center = game.getTileMap().get(new Coordinate(0, 0, -1));
        center.addThief();

        var victims = game.getThiefVictims(cur);
        assertTrue(victims.contains(victim));
    }

    @Test
    public void testNewBuilding_upgradeToCity_replacesSettlementAndConsumesResources() {
        Player cur = game.getCurrentPlayer();

        // create a settlement first (provide resources)
        game.debugGiveResources(cur.getId(), Resource.WOOD, 1);
        game.debugGiveResources(cur.getId(), Resource.BRICK, 1);
        game.debugGiveResources(cur.getId(), Resource.WOOL, 1);
        game.debugGiveResources(cur.getId(), Resource.WHEAT, 1);

        Coordinate coord = new Coordinate(0, 1, 0);
        game.newBuilding(cur, Building.Types.SETTLEMENT, coord);

        // give resources required to upgrade to a city: 2 WHEAT and 3 ORE
        game.debugGiveResources(cur.getId(), Resource.WHEAT, 2);
        game.debugGiveResources(cur.getId(), Resource.ORE, 3);

        int beforeWheat = cur.getResourceCount(Resource.WHEAT);
        int beforeOre = cur.getResourceCount(Resource.ORE);

        game.newBuilding(cur, Building.Types.CITY, coord);

        // resources should be consumed
        assertEquals(beforeWheat - 2, cur.getResourceCount(Resource.WHEAT));
        assertEquals(beforeOre - 3, cur.getResourceCount(Resource.ORE));

        // player's buildings should include a City at that coordinate
        boolean hasCity = cur.getBuildings().stream().anyMatch(b -> b.getClass() == City.class && b.getCoordinate().equals(coord));
        assertTrue(hasCity, "expected a City to replace the Settlement at the coordinate");
    }
}