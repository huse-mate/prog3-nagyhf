package render;

import javax.swing.*;

import game.Resource;
import io.GameIO;

import java.awt.*;
import java.util.ArrayList;


public class BankTradePanel extends JPanel {

    private final JComboBox<Resource> givenResourceSelector;
    private final JComboBox<Resource> receivedResourceSelector;

    private final JButton tradeButton;

    public BankTradePanel() {
        ArrayList<Resource> resourceList = new ArrayList<>();
        for (Resource r : Resource.values()) {
            if (r != Resource.DESERT) {
                resourceList.add(r);
            }
        }
        
        setLayout(new BorderLayout(0, 5));
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, false));
        setBackground(Colors.TRADE_PANEL_BACKGROUND_COLOR.val);


        // --- Create Components ---
        givenResourceSelector = new JComboBox<>(resourceList.toArray(new Resource[0]));
        receivedResourceSelector = new JComboBox<>(resourceList.toArray(new Resource[0]));

        JLabel giveLabel = new JLabel("Give:");
        JLabel receiveLabel = new JLabel("Receive:");

        tradeButton = new JButton("Trade!");
        
        tradeButton.addActionListener(event -> 
            GameIO.attemptTradeWithBank(getGivenResource(), getReceivedResource())
        );


        // --- Layout Panels ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.BOTH;
        int row = 0;

        
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 4;
        formPanel.add(new JLabel("Trade with Bank (4:1)"), gbc);

        gbc.gridwidth = 1;
        row++;
        
        // GIVE side
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(giveLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(givenResourceSelector, gbc);

        // RECEIVE side
        gbc.gridx = 2; gbc.gridy = row;
        formPanel.add(receiveLabel, gbc);

        gbc.gridx = 3;
        formPanel.add(receivedResourceSelector, gbc);

        // BUTTON row
        gbc.gridx = 0; gbc.gridy = ++row;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(tradeButton, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    // --- Getters ---

    public Resource getGivenResource(){
        return (Resource) givenResourceSelector.getSelectedItem();
    }

    public Resource getReceivedResource(){
        return (Resource) receivedResourceSelector.getSelectedItem();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        tradeButton.setEnabled(enabled);
    }
}
