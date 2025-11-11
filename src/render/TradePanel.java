package render;

import javax.swing.*;
import java.awt.*;


public class TradePanel extends JPanel{
    private final transient Image tradeImage = new ImageIcon(new java.io.File("assets/dice.png").getAbsolutePath()).getImage();

    public TradePanel(){
        setPreferredSize(new Dimension(200,200));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (tradeImage != null) {
            g.drawImage(tradeImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
