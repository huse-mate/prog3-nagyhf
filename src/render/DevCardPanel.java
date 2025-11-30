package render;

import java.awt.*;
import javax.swing.*;

import game.Player;
import io.GameIO;

public class DevCardPanel extends JPanel {
    private static final int CARD_HEIGHT = 60;
    private static final int CARD_WIDTH = 40;

    private int knightCardCount = 0;
    private int roadBuildingCardCount = 0;
    private int victoryPointCardCount = 0;

    private JLabel knightCardLabel = new JLabel("0");
    private JLabel roadBuildingCardLabel = new JLabel("0");
    private JLabel victoryPointCardLabel = new JLabel("0");

    private JButton buyButton = new JButton();
    private JButton knightCardButton = new JButton();
    private JButton roadBuildingCardButton = new JButton();
    private JButton victoryPointCardButton = new JButton();

    

    public DevCardPanel() {
        setPreferredSize(new Dimension(300, 100));
        setBackground(Colors.MATERIALS_PANEL_BACKGROUND_COLOR.val);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));
        setStyles();

        buyButton.addActionListener(e -> GameIO.buyDevCard());

        knightCardButton.addActionListener(e -> {
            updateStatus(GameIO.getCurrentPlayer());
            if (knightCardCount > 0)
                GameIO.useDevCard("KNIGHT");
        });
        roadBuildingCardButton.addActionListener(e -> {
            updateStatus(GameIO.getCurrentPlayer());
            if (roadBuildingCardCount > 0)
                GameIO.useDevCard("ROAD");
        });
        victoryPointCardButton.addActionListener(e -> {
            updateStatus(GameIO.getCurrentPlayer());
            if (victoryPointCardCount > 0)
                GameIO.useDevCard("POINT");
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0; gbc.gridy = 0;

        add(buyButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(knightCardButton, gbc);
        gbc.gridx = 2; gbc.gridy = 0;
        add(roadBuildingCardButton, gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        add(victoryPointCardButton, gbc);
        

        gbc.gridx = 0; gbc.gridy = 1;
        add(Box.createGlue(), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(knightCardLabel, gbc);
        gbc.gridx = 2; gbc.gridy = 1;
        add(roadBuildingCardLabel, gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        add(victoryPointCardLabel, gbc);
    }

    @Override
    public void setEnabled(boolean enabled) {
        buyButton.setEnabled(enabled);
        knightCardButton.setEnabled(enabled);
        roadBuildingCardButton.setEnabled(enabled);
        victoryPointCardButton.setEnabled(enabled);
    }

    public void updateStatus(Player currentPlayer) {
        knightCardCount = currentPlayer.getDevCardCount("KNIGHT");
        roadBuildingCardCount = currentPlayer.getDevCardCount("ROAD");
        victoryPointCardCount = currentPlayer.getDevCardCount("POINT");

        knightCardLabel.setText(String.valueOf(knightCardCount));
        roadBuildingCardLabel.setText(String.valueOf(roadBuildingCardCount));
        victoryPointCardLabel.setText(String.valueOf(victoryPointCardCount));
        repaint();
    }

    private void setStyles() {
        ImageIcon buyIcon = new ImageIcon();
        buyIcon.setImage(
            new ImageIcon(new java.io.File("assets/iconDevCard.png").getAbsolutePath())
            .getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, 0));

        ImageIcon knightIcon = new ImageIcon();
        knightIcon.setImage(
            new ImageIcon(new java.io.File("assets/iconKnight.png").getAbsolutePath())
            .getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, 0));
    
        ImageIcon roadIcon = new ImageIcon();
        roadIcon.setImage(
            new ImageIcon(new java.io.File("assets/iconRoad.png").getAbsolutePath())
            .getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, 0));

        ImageIcon pointIcon = new ImageIcon();
        pointIcon.setImage(
            new ImageIcon(new java.io.File("assets/iconPoint.png").getAbsolutePath())
            .getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, 0));

        setButtonStyle(buyButton, buyIcon);
        setButtonStyle(knightCardButton, knightIcon);
        setButtonStyle(roadBuildingCardButton, roadIcon);
        setButtonStyle(victoryPointCardButton, pointIcon);
    }

    public void setButtonStyle(JButton button, ImageIcon icon) {
        button.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setIcon(icon);
    }
}
