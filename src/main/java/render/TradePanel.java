package render;

import java.awt.*;

import javax.swing.*;

import game.Player;

import java.util.List;

public class TradePanel extends JPanel {

    private BankTradePanel bankTradePanel;
    private PlayerTradePanel playerTradePanel;
    private DevCardPanel devCardPanel;

    public TradePanel(List<Player> players) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        bankTradePanel = new BankTradePanel();
        playerTradePanel = new PlayerTradePanel(players);
        devCardPanel = new DevCardPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        add(bankTradePanel, gbc);
        gbc.gridy = 1;
        add(playerTradePanel, gbc);
        gbc.gridy = 2;
        add(devCardPanel, gbc);
    }


    @Override
    public void setEnabled(boolean enabled) {
        bankTradePanel.setEnabled(enabled);
        playerTradePanel.setEnabled(enabled);
        devCardPanel.setEnabled(enabled);
    }

    public void updateStatus(Player currentPlayer) {
        devCardPanel.updateStatus(currentPlayer);
    }

    public void setPlayers(List<Player> players) {
        playerTradePanel.setPlayers(players);
    }

}
