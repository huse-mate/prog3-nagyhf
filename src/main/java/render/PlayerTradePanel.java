package render;

import javax.swing.*;

import game.Resource;
import io.GameIO;
import game.Player;

import java.awt.*;
import java.util.Map;
import java.util.List;


public class PlayerTradePanel extends JPanel {

    private final Map<Resource, JSpinner> giveResourceSpinner;
    private final Map<Resource, JSpinner> receiveResourceSpinner;
    private final Map<Resource, JLabel> giveResourceLabels;
    private final Map<Resource, JLabel> receiveResourceLabels;
    private final JComboBox<Player> traderInput;

    private final JButton tradeButton;

    public PlayerTradePanel(List<Player> players) {
        giveResourceSpinner = new java.util.EnumMap<>(Resource.class);
        receiveResourceSpinner = new java.util.EnumMap<>(Resource.class);
        giveResourceLabels = new java.util.EnumMap<>(Resource.class);
        receiveResourceLabels = new java.util.EnumMap<>(Resource.class);
        traderInput = new JComboBox<>(players.toArray(new Player[0]));
        
        setLayout(new BorderLayout(0, 5));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));
        setBackground(Colors.TRADE_PANEL_BACKGROUND_COLOR.val);


        // --- Create Components ---
        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;
            giveResourceLabels.put(r, new JLabel(r.name()));
            receiveResourceLabels.put(r, new JLabel(r.name()));
            giveResourceSpinner.put(r, new JSpinner(new SpinnerNumberModel(0, 0, 99, 1)));
            receiveResourceSpinner.put(r, new JSpinner(new SpinnerNumberModel(0, 0, 99, 1)));
        }
        tradeButton = new JButton("Trade!");
        
        tradeButton.addActionListener(event -> 
            GameIO.attemptTrade(getTrader(), getGiveResource(), getReceiveResource())
        );


        // --- Layout Panels ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.BOTH;
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("Trade with Player:"), gbc);
        gbc.gridx = 2;
        formPanel.add(traderInput, gbc);

        gbc.gridwidth = 1;
        int resourceStartRow = ++row;
        // GIVE side
        row = resourceStartRow;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Give:"), gbc);

        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;
            gbc.gridy = ++row;
            gbc.gridx = 0;
            formPanel.add(giveResourceLabels.get(r), gbc);
            gbc.gridx = 1;
            formPanel.add(giveResourceSpinner.get(r), gbc);
        }

        // RECEIVE side
        row = resourceStartRow;
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(new JLabel("Receive:"), gbc);

        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;
            gbc.gridy = ++row;
            gbc.gridx = 2;
            formPanel.add(receiveResourceLabels.get(r), gbc);
            gbc.gridx = 3;
            formPanel.add(receiveResourceSpinner.get(r), gbc);
        }

        // BUTTON row
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(tradeButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    // --- Getters ---
    public Map<Resource, Integer> getGiveResource(){
        Map<Resource, Integer> giveMap = new java.util.EnumMap<>(Resource.class);
        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;
            giveMap.put(r, (Integer) giveResourceSpinner.get(r).getValue());
        }
        return giveMap;
    }

    public Map<Resource, Integer> getReceiveResource(){
        Map<Resource, Integer> receiveMap = new java.util.EnumMap<>(Resource.class);
        for (Resource r : Resource.values()) {
            if (r == Resource.DESERT) continue;
            receiveMap.put(r, (Integer) receiveResourceSpinner.get(r).getValue());
        }
        return receiveMap;
    }

    public void setPlayers(List<Player> players) {
        traderInput.removeAllItems();
        for (Player p : players) {
            traderInput.addItem(p);
        }
    }

    public Player getTrader(){
        return (Player) traderInput.getSelectedItem();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tradeButton.setEnabled(enabled);
    }
}
