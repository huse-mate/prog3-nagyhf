package render;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import game.Player;

import java.awt.*;

public class PlayerStatusPanel extends JPanel {

    private JLabel scoreLabel;
    private JLabel cardCountLabel;
    private JLabel devCardCountLabel;
    private JLabel longestRoadLabel;
    private JLabel knightsPlayedLabel;

    public PlayerStatusPanel(Player p) {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(300, 80));
        Border line = BorderFactory.createLineBorder(p.getColor().val, 2);
        TitledBorder border = BorderFactory.createTitledBorder(line, "P" + (p.getId()+1));
        border.setTitleColor(Colors.TITLE_COLOR.val);
        setBorder(border);
        
        setBackground(Colors.PLAYERPANEL_COLOR.val);
        

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10); // padding
        
        scoreLabel = new JLabel("Score: 0");
        cardCountLabel = new JLabel("Cards: 0");
        devCardCountLabel = new JLabel("Dev. Cards: 0");
        longestRoadLabel = new JLabel("Longest Road: 0");
        knightsPlayedLabel = new JLabel("Knights Played: 0");



        // --- Score (top row, merged across both columns) ---
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2; // spans across 2 columns
        add(scoreLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Card Count ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(cardCountLabel, gbc);

        // --- Development Cards ---
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(longestRoadLabel, gbc);

        // --- Longest Road ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(devCardCountLabel, gbc);

        // --- Knights Played ---
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(knightsPlayedLabel, gbc);
    }


    public void updateStatus(int score, int cardCount, int devCardCount, int maxRoadLength, int knightCount){
        scoreLabel.setText("Score: " + score);
        cardCountLabel.setText("Cards: " + cardCount);
        devCardCountLabel.setText("Dev. Cards: " + devCardCount);
        longestRoadLabel.setText("Longest Road: " + maxRoadLength);
        knightsPlayedLabel.setText("Knights Played: " + knightCount);
    }

    public void highlightPlayer(){
        setBackground(Colors.HIGHLIGHTED_PLAYERPANEL_COLOR.val);
    }

    public void unhighlightPlayer(){
        setBackground(Colors.PLAYERPANEL_COLOR.val);
    }
}