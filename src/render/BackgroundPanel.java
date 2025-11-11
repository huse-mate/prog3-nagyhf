package render;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private final transient Image bgImage = new ImageIcon(new java.io.File("assets/table.png").getAbsolutePath()).getImage();

    public BackgroundPanel(){}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
