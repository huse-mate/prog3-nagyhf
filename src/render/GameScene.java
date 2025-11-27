package render;

import javax.swing.*;

import IO.GameIO;
import game.*;

import java.awt.*;

public class GameScene extends BackgroundPanel{
    
    private MainFrame frame;
    private Game game;

    private MaterialsPanel materialsPanel;
    private GamePanel gamePanel;
    private DicePanel dicePanel;
    private PlayersPanel playersPanel;
    private TradePanel tradePanel;

    public GameScene(MainFrame frame, Game game) {
        super();
        materialsPanel = new MaterialsPanel();
        gamePanel = new GamePanel(frame, game);
        dicePanel = new DicePanel();
        // wire end-turn button to notify the game's end-turn waiter
        this.game = game;
        dicePanel.setEndTurnListener(() -> {
            // notify the currently-running turn to finish
            GameIO.notifyEndTurn();
            // start the next player's turn off the EDT so we don't block UI
            new Thread(() -> game.turn(), "Game-Turn-Thread").start();
        });
        playersPanel = new PlayersPanel(frame, game);
        tradePanel = new TradePanel();

        setLayout(new BorderLayout());

        JPanel gameContainer = new JPanel(new GridBagLayout());
        gameContainer.add(gamePanel);
        gameContainer.setOpaque(false);
        add(gameContainer, BorderLayout.CENTER);

        add(setupTop(), BorderLayout.NORTH);

        add(setupRightSide(), BorderLayout.EAST);
        add(setupLeftSide(), BorderLayout.WEST);
        add(setupBottom(), BorderLayout.SOUTH);
    }

    public void updateStatus(){
        game.getPlayers().forEach( player -> {
            playersPanel.updateStatus(player);
        });
        playersPanel.highlightCurrentPlayer(game.getCurrentPlayer());
        materialsPanel.updateStatus(game.getCurrentPlayer());
    }


    public void beginTurn() {
        // disable building and endTurn while waiting for dice
        setBuildingEnabled(false);
        setEndTurnEnabled(false);
        setDiceButtonEnabled(true);
        repaint();
    }

    public int waitForDiceThrow() {
        try {
            int val = dicePanel.takeThrow();
            return val;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    public void thiefMovementStart() {
        if (gamePanel != null) {
            gamePanel.thiefMovementStart();
        }
    }

    public void thiefMovementEnd() {
        if (gamePanel != null) {
            gamePanel.thiefMovementEnd();
            setBuildingEnabled(true);
            setEndTurnEnabled(true);
        }
    }

     /** Enable/disable the End Turn button from outside (e.g., after a throw). */

    public void setEndTurnEnabled(boolean enabled) {
        if (dicePanel != null) {
            dicePanel.setEndTurnEnabled(enabled);
        }
    }

    public void setBuildingEnabled(boolean enabled) {
        if (gamePanel != null) {
            gamePanel.setBuildingEnabled(enabled);
        }
    }

    public void setDiceButtonEnabled(boolean enabled) {
        if (dicePanel != null) {
            dicePanel.setDiceButtonEnabled(enabled);
            if (enabled) {
                dicePanel.beginTurn();
            }
        }
    }

    private JPanel setupRightSide(){
        JPanel diceContainer = new JPanel(new GridBagLayout());
        diceContainer.setOpaque(false);
        GridBagConstraints dice = new GridBagConstraints();
        dice.gridx = 0;
        dice.gridy = 1;
        dice.weighty = 0.4;
        dice.anchor = GridBagConstraints.CENTER;
        dice.insets = new Insets(0, 0, 0, 150);

        GridBagConstraints rightFiller = new GridBagConstraints();
        rightFiller.gridx = 0;
        rightFiller.gridy = 1;
        rightFiller.weightx = 0.1;

        GridBagConstraints rightTop = new GridBagConstraints();
        rightTop.gridx = 0;
        rightTop.gridy = 2;
        rightTop.weighty = 0.3;
        rightTop.fill = GridBagConstraints.VERTICAL;

        GridBagConstraints rightBottom = new GridBagConstraints();
        rightBottom.gridx = 0;
        rightBottom.gridy = 0;
        rightBottom.weighty = 0.3;
        rightBottom.fill = GridBagConstraints.VERTICAL;
        
        diceContainer.add(Box.createVerticalStrut(0), rightTop);
        diceContainer.add(dicePanel, dice);
        diceContainer.add(Box.createVerticalStrut(0), rightBottom);

        return diceContainer;
    }

    public JPanel setupLeftSide(){
        JPanel tradeContainer = new JPanel(new GridBagLayout());
        tradeContainer.setOpaque(false);
        GridBagConstraints trade = new GridBagConstraints();
        trade.gridx = 0;
        trade.gridy = 1;
        trade.weighty = 0.4;
        trade.anchor = GridBagConstraints.CENTER;
        trade.insets = new Insets(0, 150, 0, 0);

        GridBagConstraints leftTop = new GridBagConstraints();
        leftTop.gridx = 0;
        leftTop.gridy = 2;
        leftTop.weighty = 0.3;
        leftTop.fill = GridBagConstraints.VERTICAL;

        GridBagConstraints leftBottom = new GridBagConstraints();
        leftBottom.gridx = 0;
        leftBottom.gridy = 0;
        leftBottom.weighty = 0.3;
        leftBottom.fill = GridBagConstraints.VERTICAL;

        tradeContainer.add(Box.createVerticalStrut(0), leftTop);
        tradeContainer.add(tradePanel, trade);
        tradeContainer.add(Box.createVerticalStrut(0), leftBottom);
        return tradeContainer;
    }

    public JPanel setupTop(){
        JPanel playersContainer = new JPanel(new GridBagLayout());
        playersContainer.setOpaque(false);
        GridBagConstraints topMiddle = new GridBagConstraints();
        topMiddle.gridx = 1;
        topMiddle.weightx = 0.8; // 80% of horizontal space
        topMiddle.fill = GridBagConstraints.HORIZONTAL;
        topMiddle.anchor = GridBagConstraints.CENTER;
        topMiddle.insets = new Insets(10, 0, 0, 0);
        
        GridBagConstraints topLeft = new GridBagConstraints();
        topLeft.gridx = 0;
        topLeft.weightx = 0.1; // 10% left margin
        topLeft.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints topRight = new GridBagConstraints();
        topRight.gridx = 2;
        topRight.weightx = 0.1; // 10% right margin
        topRight.fill = GridBagConstraints.HORIZONTAL;
        
        playersContainer.add(Box.createHorizontalStrut(0), topLeft);
        playersContainer.add(playersPanel, topMiddle);
        playersContainer.add(Box.createHorizontalStrut(0), topRight);
        return playersContainer;
    }

    public JPanel setupBottom(){
        JPanel materialsContainer = new JPanel(new GridBagLayout());
        materialsContainer.setOpaque(false);
        GridBagConstraints bottomMiddle = new GridBagConstraints();
        bottomMiddle.gridx = 1;
        bottomMiddle.weightx = 0.4;
        bottomMiddle.insets = new Insets(0, 0, 10, 0);
        bottomMiddle.fill = GridBagConstraints.HORIZONTAL;
        bottomMiddle.anchor = GridBagConstraints.CENTER;
        
        GridBagConstraints topLeft = new GridBagConstraints();
        topLeft.gridx = 0;
        topLeft.weightx = 0.3;
        topLeft.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints topRight = new GridBagConstraints();
        topRight.gridx = 2;
        topRight.weightx = 0.3;
        topRight.fill = GridBagConstraints.HORIZONTAL;
        
        materialsContainer.add(Box.createHorizontalStrut(0), topLeft);
        materialsContainer.add(materialsPanel, bottomMiddle);
        materialsContainer.add(Box.createHorizontalStrut(0), topRight);
        return materialsContainer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        playersPanel.highlightCurrentPlayer(game.getCurrentPlayer());
    }
}
