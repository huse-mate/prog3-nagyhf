import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import game.*;
import game.buildings.*;
import render.Colors;

public class PlayerTest {

	@Test
	public void testBuyDevCard_deductsResources_and_incrementsDevCount() {
		Game game = new Game();
		Player cur = game.getCurrentPlayer();

		// give resources required for a dev card
		game.debugGiveResources(cur.getId(), Resource.WOOL, 1);
		game.debugGiveResources(cur.getId(), Resource.WHEAT, 1);
		game.debugGiveResources(cur.getId(), Resource.ORE, 1);

		int beforeDev = cur.getDevCardCount();
		int beforeWool = cur.getResourceCount(Resource.WOOL);

		game.buyDevCard(cur);

		// dev card count should increase by 1
		assertEquals(beforeDev + 1, cur.getDevCardCount());

		// resources consumed
		assertEquals(beforeWool - 1, cur.getResourceCount(Resource.WOOL));
		assertEquals(0, cur.getResourceCount(Resource.WHEAT));
		assertEquals(0, cur.getResourceCount(Resource.ORE));
	}

	@Test
	public void testPlayerDevCardAddRemoveBehavior() {
		Player p = new Player(0, Colors.PLAYER1_COLOR);

		assertEquals(0, p.getDevCardCount());

		p.addKnightCard();
		assertEquals(1, p.getDevCardCount());
		assertEquals(1, p.getDevCardCount("KNIGHT"));

		p.addFreeRoadCard();
		assertEquals(2, p.getDevCardCount());
		assertEquals(1, p.getDevCardCount("ROAD"));

		p.addPointCard();
		assertEquals(3, p.getDevCardCount());
		assertEquals(1, p.getDevCardCount("POINT"));

		// removals
		p.removeKnightCard();
		assertEquals(2, p.getDevCardCount());
		p.removeRoadBuildingCard();
		assertEquals(1, p.getDevCardCount());
		p.removePointCard();
		assertEquals(0, p.getDevCardCount());
	}

	@Test
	public void testUseVictoryPointAndRoadBuilding_inGame() {
		Game game = new Game();
		Player cur = game.getCurrentPlayer();

		// give a point card and use it
		cur.addPointCard();
		int beforePoints = cur.getScore();
		game.useVictoryPointCard(cur);
		assertEquals(beforePoints + 1, cur.getScore());

		// give a road-building card and use it
		cur.addFreeRoadCard();
		int beforeInventory = cur.getRoadInventory();
		game.useRoadBuildingCard(cur);
		// road building adds two roads to inventory
		assertEquals(beforeInventory + 2, cur.getRoadInventory());
	}

}
