package render;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image bgImage;

    public BackgroundPanel(Image bg) {
        bgImage = bg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
