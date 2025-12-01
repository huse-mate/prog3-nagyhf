package render;

import javax.swing.*;

import io.GameIO;
import game.*;
import save.*;
import render.MainFrame.Scene;

import java.awt.*;
import java.nio.file.Path;

public class GameScene extends BackgroundPanel{
    
    private MainFrame frame;
    private Game game;

    private MaterialsPanel materialsPanel;
    private GamePanel gamePanel;
    private DicePanel dicePanel;
    private PlayersPanel playersPanel;
    private TradePanel tradePanel;

    // width reserved for left/right side panels; avoids negative insets
    private static final int SIDEWIDTH = 450;

    public GameScene(MainFrame frame, Game game) {
        super(new ImageIcon(new java.io.File("assets/table.png").getAbsolutePath()).getImage());
        materialsPanel = new MaterialsPanel();
        gamePanel = new GamePanel(frame);
        dicePanel = new DicePanel();
        // wire end-turn button to notify the game's end-turn waiter
        this.game = game;
        this.frame = frame;
        playersPanel = new PlayersPanel(frame, game);
        tradePanel = new TradePanel(game.getPlayers());

        setLayout(new BorderLayout());

        JPanel gameContainer = new JPanel(new GridBagLayout());
        gameContainer.add(gamePanel);
        gameContainer.setOpaque(false);
        

        add(setupTop(), BorderLayout.NORTH);
        add(setupRightSide(), BorderLayout.EAST);
        add(setupLeftSide(), BorderLayout.WEST);
        add(setupBottom(), BorderLayout.SOUTH);
        add(gameContainer, BorderLayout.CENTER);
    }

    public void updateStatus(){
        game.getPlayers().forEach( player -> {
            playersPanel.updateStatus(player);
        });
        playersPanel.highlightCurrentPlayer(game.getCurrentPlayer());
        materialsPanel.updateStatus(game.getCurrentPlayer());
        tradePanel.updateStatus(game.getCurrentPlayer());
        gamePanel.repaint();
        repaint();
    }

    public void gameOver(Player winner) {
        frame.setWinner(winner);
        frame.showScene(Scene.WIN);
    }

    public void saveGame(){
        try {
            SaveManager.save(Path.of("save/prev.json"), game);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadPreviousGame(){
        try {
            GameState loadedState = SaveManager.load(Path.of("save/prev.json"));
            game = Game.fromState(loadedState);
            GameIO.setGame(game);
            tradePanel.setPlayers(game.getPlayers());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateStatus();
        startGameThread(false);
    }

    public void startNewGame() {
        GameIO.setGame(game);
        setGame(game);

        game.getTileMap().get(new Coordinate(0, 0, -1)).addThief();
        startGameThread(true);
        
    }

    public void setGame(Game game){
        gamePanel.setGame(game);
        dicePanel.setEndTurnListener(() -> {
            // notify the currently-running turn to finish
            GameIO.notifyEndTurn();
            // start the next player's turn off the EDT so we don't block UI
            new Thread(game::turn, "Game-Turn-Thread").start();
        });
        dicePanel.setSaveListener(this::saveGame);
    }

    public void startGameThread(boolean newGame){
        Thread gameThread = new Thread(() -> {
            // run the startup placement sequence
            if (newGame) {
                game.gameStartSequence();
            }
            // after the startup sequence finishes, start the first normal turn
            // run on the same background thread so the EDT remains responsive
            game.turn();
        }, "Game-Thread");
        gameThread.start();
    }

    public void gameStartSequenceBegin() {
        gamePanel.setInStartSequence(true);
    }
    
    public void gameStartSequenceEnd() {
        gamePanel.setInStartSequence(false);
    }


    public int waitForDiceThrow() {
        try {
            return dicePanel.takeThrow();
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

    public void setTradingEnabled(boolean enabled) {
        if (tradePanel != null) {
            tradePanel.setEnabled(enabled);
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

    public void setSaveButtonEnabled(boolean enabled) {
        if (dicePanel != null) {
            dicePanel.setSaveButtonEnabled(enabled);
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
        int insets = Math.max(0, (SIDEWIDTH - dicePanel.getPreferredSize().width) / 2);
        dice.insets = new Insets(0, insets, 0, insets);


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
        int insets = Math.max(0, (SIDEWIDTH - tradePanel.getPreferredSize().width) / 2);
        trade.insets = new Insets(0, insets, 0, insets);

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
