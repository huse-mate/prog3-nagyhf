package render;
import javax.swing.*;

import game.Tile;
import io.GameIO;

import java.awt.*;

public class TileButton extends JButton {
    private int radius;
    private Tile tile;

    TileButton(Tile tile, int radius){
        super(String.valueOf(tile.getNum()));
        this.tile = tile;
        this.radius = radius;
        setFocusPainted(false);       // no focus outline
        setBorderPainted(false);      // no rectangular border
        setContentAreaFilled(false);  // we'll paint the shape manually
        setOpaque(false);
        setEnabled(false);
        setVisible(true);

        addActionListener((event) -> {
            if (tile.getThief())
                return;
            GameIO.addThief(tile);
            GameIO.thiefMovementEnd();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setFont(new Font("Arial", Font.BOLD, radius));

        if(tile.getThief()) {
            g2.setColor(Color.BLACK);
        } else {
            g2.setColor(Colors.TILE_NUMBER_COLOR.val);
        }
        g2.fillOval(0, 0, radius * 2, radius * 2);

        if(isEnabled() && !tile.getThief()) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.BLACK);
        }
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(0, 0, radius * 2, radius * 2);

        if(!tile.getThief()) {
            if(tile.getNum() < 10)
                g2.drawString(String.valueOf(tile.getNum()), radius - 8, (int)(radius * 1.5) - 5);
            else
                g2.drawString(String.valueOf(tile.getNum()), (radius - 4) / 2, (int)(radius * 1.5) - 5);
        }
        
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }


}
