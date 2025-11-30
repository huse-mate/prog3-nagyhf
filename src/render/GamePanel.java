package render;
import javax.swing.*;

import game.*;
import game.buildings.Building;
import game.buildings.RoadNetwork;
import game.buildings.RoadNetwork.Road;
import game.buildings.Settlement;
import io.GameIO;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class GamePanel extends JPanel {

    private MainFrame frame;
	private Game game;
	private HashMap<Coordinate, BuildingButton> buildingButtonMap;
	private HashMap<RoadNetwork.Road, RoadButton> roadButtonMap;
	private HashMap<Tile, TileButton> tileButtonMap;
	private boolean inStartSequence = false;

	private static final int SCREEN_SIZE = 900;
	private static final int TILE_SIZE = 200;
	private static final int TILE_GAP = 2;
	private static final int TILE_BUTTON_SIZE = 60;

	private static final int BUILDING_RADIUS = 15;
	private static final int[] BUILDING_OFFSET_X = { 0, (int)(TILE_SIZE*0.43301)};
	private static final int[] BUILDING_OFFSET_Y = { -TILE_SIZE/2, -TILE_SIZE/2+45}; // ezt lehetne rendes matekkal szamolni

    public GamePanel(MainFrame frame) {
		this.frame = frame;
		this.game = null;
		
    }

	public void setGame(Game game) {
		setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
		setBackground(Colors.MAP_BACKGROUND_COLOR.val);
		setBorder(BorderFactory.createLineBorder(Colors.MAP_BORDER_COLOR.val, 2));
		setLayout(null);
		
		this.game = game;
		buildingButtonMap = new HashMap<>();
		createBuildingButtons();
		roadButtonMap = new HashMap<>();
		createRoadButtons();
		tileButtonMap = new HashMap<>();
		createTileButtons(game.getTileMap());
	}

	public void setInStartSequence(boolean inStartSequence) {
		this.inStartSequence = inStartSequence;
	}

	public void resetBuildings() {
		for (BuildingButton bb : buildingButtonMap.values()) {
			bb.reset();
		}
	}

    @Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (game != null) {
			Graphics2D g2 = (Graphics2D) g;
			java.awt.geom.AffineTransform old = g2.getTransform();
			g2.translate(getWidth() / 2.0, getHeight() / 2.0);
			g2.scale(1, -1);
			renderMap(g2, game.getTileMap());
			g2.setTransform(old);

			resetPossibleCities();
			resetRoads();

			resetBuildings();
			renderBuildings();
			renderPossibleSettlements(inStartSequence);
			renderPossibleUpgrades();
			renderRoads();
			renderPossibleRoads();
		}
		
	}

    private void renderMap(Graphics2D g, TileMap map){
		final int tileSizeY = TILE_SIZE;
		final int tileSizeX = (int)(TILE_SIZE*0.86602);

		final int tileOffsetX = -tileSizeX/2;
		final int tileOffsetY = -tileSizeY/2;
		
		final double tileGapX = (double) tileSizeX + (double) TILE_GAP;
		final double tileGapY = (tileSizeY * 0.75) + TILE_GAP;


		for (Entry<Coordinate, Tile> entry : map.entries()) {
			Coordinate coord = entry.getKey();

			double px = tileGapX * (coord.getX() + coord.getY() / 2.0);
			double py = tileGapY * coord.getY();
			int drawTileX = (int) Math.round(px) + tileOffsetX;
			int drawTileY = (int) Math.round(py) + tileOffsetY;


			Tile t = entry.getValue();
			g.drawImage(getTileTexture(t), drawTileX, drawTileY, tileSizeX, tileSizeY, this);
		}
	}

	private void createBuildingButtons(){
		HashMap<Coordinate, Player> buildingMap = new HashMap<>(game.getBuildingMap());
		for (Coordinate c : buildingMap.keySet()) {
			Coordinate screenCoord = convertToScreenCoord(c);
			BuildingButton building = new BuildingButton(screenCoord.getX(), screenCoord.getY(), BUILDING_RADIUS);
			buildingButtonMap.put(c, building);

			building.addActionListener(e -> {
				if(building.isPlaceable()) {
					if(inStartSequence) 
						GameIO.notifyStarterPlacement();
					game.newBuilding(game.getCurrentPlayer(), Building.Types.SETTLEMENT, c);
				} else if(building.isUpgradeable()) {
					game.newBuilding(game.getCurrentPlayer(), Building.Types.CITY, c);
				}
			});

			add(building);
		}

	}

	private void createRoadButtons(){
		for (RoadNetwork.Road road : game.getAllRoads()) {
			Coordinate c1 = road.getCoord1();
			Coordinate c2 = road.getCoord2();
			Coordinate screenCoord1 = convertToScreenCoord(c1);
			Coordinate screenCoord2 = convertToScreenCoord(c2);
			RoadButton roadButton = new RoadButton(screenCoord1.getX(), screenCoord1.getY(), screenCoord2.getX(), screenCoord2.getY(), c1, c2);
			roadButtonMap.put(road, roadButton);

			roadButton.addActionListener(e -> {
				if(roadButton.isPossible()) {
					if(inStartSequence) 
						GameIO.notifyStarterPlacement();
					game.newRoad(game.getCurrentPlayer(), roadButton.getCoord1(), roadButton.getCoord2());
				}
			});

			add(roadButton);
		}
	}

	private void createTileButtons(TileMap map){  
		for (Entry<Coordinate, Tile> entry : map.entries()) {
			Coordinate c = entry.getKey();
			Coordinate screenCoord = convertToScreenCoord(c);
			TileButton tileButton = new TileButton(entry.getValue(), TILE_BUTTON_SIZE/2);
			tileButton.setBounds(screenCoord.getX()- TILE_BUTTON_SIZE/2, screenCoord.getY()- TILE_BUTTON_SIZE/2, TILE_BUTTON_SIZE, TILE_BUTTON_SIZE);
			add(tileButton);
			tileButtonMap.put(entry.getValue(), tileButton);
		}
	}

	/**
	 * Enable or disable all BuildingButton and RoadButton components on this panel.
	 */
	public void setBuildingEnabled(boolean enabled) {
		for (BuildingButton bb : buildingButtonMap.values()) {
			bb.setEnabled(enabled);
		}
		for (RoadButton rb : roadButtonMap.values()) {
			rb.setEnabled(enabled);
		}
	}

	public void thiefMovementStart() {
		for (Map.Entry<Tile, TileButton> entry : tileButtonMap.entrySet()) {
			TileButton tb = entry.getValue();
			tb.setEnabled(true);
		}
		setBuildingEnabled(false);
		frame.getGameScene().setEndTurnEnabled(false);
	}

	public void thiefMovementEnd() {
		for (Map.Entry<Tile, TileButton> entry : tileButtonMap.entrySet()) {
			TileButton tb = entry.getValue();
			tb.setEnabled(false);
		}
	}

	private Coordinate convertToScreenCoord(Coordinate coord){
		// Use same tile sizing and spacing as renderMap so positions match exactly
		final int tileSizeY = TILE_SIZE;
		final int tileSizeX = (int)(TILE_SIZE*0.86602);

		final double tileGapX = (double) tileSizeX + (double) TILE_GAP;
		final double tileGapY = (tileSizeY * 0.75) + TILE_GAP;

		// compute map-space center for this axial coordinate (same as in renderMap)
		double px = tileGapX * (coord.getX() + coord.getY() / 2.0);
		double py = tileGapY * coord.getY();

		// convert from map-space (where paintComponent translates origin to center
		// and flips Y with scale(1,-1)) to component screen coordinates
		int screenX = (int) Math.round(getWidth() / 2.0 + px);
		int screenY = (int) Math.round(getHeight() / 2.0 - py);

		// Return a Coordinate where corner can be unused (set to 0)
		switch (coord.getCorner()) {
			case 0:
				return new Coordinate(
					screenX + SCREEN_SIZE / 2 + BUILDING_OFFSET_X[0],
					screenY + SCREEN_SIZE / 2 + BUILDING_OFFSET_Y[0],
					0
				);
			case 1:
				return new Coordinate(
					screenX + SCREEN_SIZE / 2 + BUILDING_OFFSET_X[1],
					screenY + SCREEN_SIZE / 2 + BUILDING_OFFSET_Y[1],
					0
				);
			default:
				return new Coordinate(SCREEN_SIZE / 2 + screenX, SCREEN_SIZE / 2 + screenY, 0);
		}
	}

	private void renderBuildings(){
		for (Player p : game.getPlayers()) {
			p.getBuildings().forEach(b -> {
				Coordinate c = b.getCoordinate();
				BuildingButton bb = buildingButtonMap.get(c);
				if (b.getClass() == Settlement.class) {
					bb.setSettlement(p);
				} else {
					bb.setCity(p);
				}
			});
		}
		repaint();
	}

	private void renderPossibleSettlements(boolean start){
		if(!game.getCurrentPlayer().canBuildSettlement()) return;
		game.getPossibleSettlements(start).forEach( c -> {
			BuildingButton bb = buildingButtonMap.get(c);
			bb.makePlaceable();
		});
		repaint();
	}


	private void renderPossibleUpgrades(){
		if(!game.getCurrentPlayer().canBuildCity()) return;
		game.getCurrentPlayer().getBuildings().forEach((b) -> {
			if(b.getClass() == Settlement.class){
				Coordinate c = b.getCoordinate();
				BuildingButton bb = buildingButtonMap.get(c);
				bb.makeUpgradeable();
			}
		});
		repaint();
	}

	private void resetPossibleCities(){
		for (BuildingButton bb : buildingButtonMap.values()) {
			if (!bb.isUpgradeable()) continue;
			bb.makeUnUpgradeable();
		}
		repaint();
	}

	private void renderRoads(){

		for(RoadNetwork.Road road : game.getRoads()){
			RoadButton rb = roadButtonMap.get(road);
			if (rb == null) continue;
			
			rb.makePlaced(road.getOwner());
		}
		repaint();
	}

	private void resetRoads(){
		for (RoadButton rb : roadButtonMap.values()) {
			if (rb.isPlaced()) continue;
			rb.makeEmpty();
		}
	}

	private void renderPossibleRoads(){
		if(!game.getCurrentPlayer().canBuildRoad()) return;
		game.getPossibleRoads().forEach( road -> {
			RoadButton rb = roadButtonMap.get(road);
			rb.makePossible();
		});
		repaint();
	}




	private Image getTileTexture(Tile t){
		Image tileImage;
		switch (t.getType()) {
			case Resource.DESERT:
				tileImage = new ImageIcon(new java.io.File("assets/tileDesert.png").getAbsolutePath()).getImage();
				break;
			case Resource.BRICK:
				tileImage = new ImageIcon(new java.io.File("assets/tileBrick.png").getAbsolutePath()).getImage();
				break;
			case Resource.ORE:
				tileImage = new ImageIcon(new java.io.File("assets/tileRock.png").getAbsolutePath()).getImage();
				break;
			case Resource.WOOD:
				tileImage = new ImageIcon(new java.io.File("assets/tileWood.png").getAbsolutePath()).getImage();
				break;
			case Resource.WOOL:
				tileImage = new ImageIcon(new java.io.File("assets/tileGrass.png").getAbsolutePath()).getImage();
				break;
			case Resource.WHEAT:
				tileImage = new ImageIcon(new java.io.File("assets/tileWheat.png").getAbsolutePath()).getImage();
				break;
			default:
				tileImage = new ImageIcon(new java.io.File("assets/tile.png").getAbsolutePath()).getImage();
				break;
		}
		return tileImage;
	}
}
