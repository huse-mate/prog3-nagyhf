import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Set;

import game.*;
import render.Colors;
import game.buildings.*;

public class RoadNetworkTest {

	@Test
	public void testGetPossibleSettlements_excludesOccupiedNode() {
		RoadNetwork rn = new RoadNetwork();
		Player p1 = new Player(0, Colors.PLAYER1_COLOR);
		Player p2 = new Player(1, Colors.PLAYER2_COLOR);

		Coordinate c = new Coordinate(0, 0, 0);

		// initial set for "start" should contain the coordinate
		Set<Coordinate> before = rn.getPossibleSettlements(p1, true);
		assertTrue(before.contains(c), "expected initial possible settlements to contain the node");

		// place a building at c for player 2
		rn.newBuilding(c, p2);

		Set<Coordinate> after = rn.getPossibleSettlements(p1, true);
		// the occupied coordinate must not be possible anymore
		assertFalse(after.contains(c), "occupied node should be excluded from possible settlements");
		// the available-set should shrink
		assertTrue(after.size() < before.size(), "expected fewer possible settlements after occupation");
	}

	@Test
	public void testGetPossibleRoads_includesAdjacentToPlayerBuilding() {
		RoadNetwork rn = new RoadNetwork();
		Player p = new Player(0, Colors.PLAYER1_COLOR);

		Coordinate c = new Coordinate(0, 0, 0);

		// Give the player a settlement at c
		Settlement s = new Settlement(p, c, new ArrayList<>());
		p.addBuilding(s);

		Set<RoadNetwork.Road> possible = rn.getPossibleRoads(p);

		assertFalse(possible.isEmpty(), "expected some possible roads adjacent to player's building");
		// every road should have one endpoint equal to the building coordinate and owner equal to player
		boolean allAdjacent = possible.stream().allMatch(r -> (r.getCoord1().equals(c) || r.getCoord2().equals(c)) && r.getOwner().equals(p));
		assertTrue(allAdjacent, "every suggested road should be adjacent to the player's building and owned by the player in the candidate");
	}

	@Test
	public void testGetLongestPath_chainOfRoadsReturnsLength() {
		RoadNetwork rn = new RoadNetwork();
		Player p = new Player(0, Colors.PLAYER1_COLOR);

		// Build a chain c0 - c1 - c2 - c3 (3 roads) owned by player p
		Coordinate c0 = new Coordinate(0, 0, 0);
		Coordinate c1 = new Coordinate(1, 0, 0);
		Coordinate c2 = new Coordinate(2, 0, 0);
		Coordinate c3 = new Coordinate(3, 0, 0);

		rn.newRoad(p, c0, c1);
		rn.newRoad(p, c1, c2);
		rn.newRoad(p, c2, c3);

		int longest = rn.getLongestPath(p);

		// longest path should equal number of roads along the longest chain (3)
		assertEquals(3, longest, "expected longest path length to equal the number of connected roads in chain");
	}

}
