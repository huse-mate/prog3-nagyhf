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



	
}