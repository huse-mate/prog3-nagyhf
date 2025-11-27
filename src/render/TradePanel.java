package render;

import java.awt.*;

import javax.swing.*;
import java.util.List;
import game.Player;

public class TradePanel extends JPanel {

    private BankTradePanel bankTradePanel;
    private PlayerTradePanel playerTradePanel;

    public TradePanel(List<Player> players) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        bankTradePanel = new BankTradePanel();
        playerTradePanel = new PlayerTradePanel(players);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        add(bankTradePanel, gbc);
        gbc.gridy = 1;
        add(playerTradePanel, gbc);
    }


    @Override
    public void setEnabled(boolean enabled) {
        bankTradePanel.setEnabled(enabled);
        playerTradePanel.setEnabled(enabled);
    }

}
