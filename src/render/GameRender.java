package render;


import javax.swing.*;
import java.util.Map.*;

import game.Coordinate;
import game.Resource;
import game.Tile;
import game.TileMap;
import game.*;

import java.awt.*;


public class GameRender extends JPanel {
	private Game game;
	private java.awt.CardLayout layout;
	private javax.swing.JPanel root;

	private final transient Image NUMBER_IMAGE = new ImageIcon(new java.io.File("assets/number.png").getAbsolutePath()).getImage();
	private static final int NUMBER_SIZE = 70;
	private static final int TILE_SIZE = 200;
	private static final int TILE_GAP = 2;

	

	public GameRender(Game game, java.awt.CardLayout l,javax.swing.JPanel r) {
		setPreferredSize(new Dimension(1920, 1080));
		setBackground(Colors.BACKGROUND_COLOR.val);
		layout = l;
		root = r;
		this.game = game;
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int width = getWidth();
        int height = getHeight();
        g2.translate(width / 2, height / 2);
        g2.scale(1, -1);
		g2.setFont(new Font("NerdFont", Font.PLAIN, 30));

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// renderMap(g2);
		// renderBuildings(g2);
		// renderRoads(g2);
	}



	private void renderMap(Graphics2D g, TileMap map){
		final int tileSizeY = TILE_SIZE;
		final int tileSizeX = (int)(TILE_SIZE*0.86602);

		final int tileOffsetX = -tileSizeX/2;
		final int tileOffsetY = -tileSizeY/2;
		final int numberOffset = -NUMBER_SIZE/2;
		
		final double tileGapX = (double) tileSizeX + (double) TILE_GAP;
		final double tileGapY = (tileSizeY * 0.75) + TILE_GAP;


		for (Entry<Coordinate, Tile> entry : map.entries()) {
			Coordinate coord = entry.getKey();

			double px = tileGapX * (coord.getX() + coord.getY() / 2.0);
			double py = tileGapY * coord.getY();
			int drawTileX = (int) Math.round(px) + tileOffsetX;
			int drawTileY = (int) Math.round(py) + tileOffsetY;

			int drawNumberX = (int) Math.round(px) + numberOffset;
			int drawNumberY = (int) Math.round(py) + numberOffset;

			Tile t = entry.getValue();
			
			
			g.drawImage(getTileTexture(t), drawTileX, drawTileY, tileSizeX, tileSizeY, this);
			g.drawImage(getNumberTexture(t), drawNumberX, drawNumberY, NUMBER_SIZE, NUMBER_SIZE, this);

		}
	}

	private Coordinate convertToScreenCoord(Coordinate c){

		return c;
	}

	private void renderBuildings(Graphics2D g){
		
	}

	private void renderRoads(Graphics2D g){
		
	}

	public void renderDiceThrow(int dice){
		// TODO
	}








	private Image getNumberTexture(Tile t){
		Image numberImage;
		switch (t.getNum()) {
			case 2:
				numberImage = new ImageIcon(new java.io.File("assets/number2.png").getAbsolutePath()).getImage();
				break;
			case 3:
				numberImage = new ImageIcon(new java.io.File("assets/number3.png").getAbsolutePath()).getImage();
				break;
			case 4:
				numberImage = new ImageIcon(new java.io.File("assets/number4.png").getAbsolutePath()).getImage();
				break;
			case 5:
				numberImage = new ImageIcon(new java.io.File("assets/number5.png").getAbsolutePath()).getImage();
				break;
			case 6:
				numberImage = new ImageIcon(new java.io.File("assets/number6.png").getAbsolutePath()).getImage();
				break;
			case 7:
				numberImage = new ImageIcon().getImage();
				break;
			case 8:
				numberImage = new ImageIcon(new java.io.File("assets/number8.png").getAbsolutePath()).getImage();
				break;
			case 9:
				numberImage = new ImageIcon(new java.io.File("assets/number9.png").getAbsolutePath()).getImage();
				break;
			case 10:
				numberImage = new ImageIcon(new java.io.File("assets/number10.png").getAbsolutePath()).getImage();
				break;
			case 11:
				numberImage = new ImageIcon(new java.io.File("assets/number11.png").getAbsolutePath()).getImage();
				break;
			case 12:
				numberImage = new ImageIcon(new java.io.File("assets/number12.png").getAbsolutePath()).getImage();
				break;
			default:
				numberImage = new ImageIcon(new java.io.File("assets/number.png").getAbsolutePath()).getImage();
				break;
		}
		return numberImage;
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