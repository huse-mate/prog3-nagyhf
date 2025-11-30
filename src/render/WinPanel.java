package render;

import java.awt.*;

import javax.swing.*;
import game.Player;


public class WinPanel extends BackgroundPanel {
    
    private JLabel winnerLabel;

    public WinPanel() {
        super(new ImageIcon(new java.io.File("assets/winBg.jpg").getAbsolutePath()).getImage());
        winnerLabel = new JLabel("Winner: ");
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        winnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setLayout(new BorderLayout());
        add(winnerLabel, BorderLayout.CENTER);
    }

    public void setWinner(Player winner) {
        winnerLabel.setText("Winner: " + winner);
    }
}