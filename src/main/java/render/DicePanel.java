package render;

import javax.swing.*;
import java.awt.*;


public class DicePanel extends JPanel {
    private final transient java.util.Random rand = new java.util.Random();

    private JButton throwButton;
    private JButton endTurnButton;
    private JButton saveButton;

    private boolean canThrowDice;
    private int value = 0;
    private final transient Object throwLock = new Object();
    private transient boolean hasNewThrow = false;

    public DicePanel(){
        setOpaque(false);
        setLayout(new GridBagLayout());
        throwButton = new JButton("0");
        throwButton.setPreferredSize(new Dimension(200,200));
        endTurnButton = new JButton("End Turn");
        endTurnButton.setPreferredSize(new Dimension(200,80));

        throwButton.setFont(new Font("Arial", Font.BOLD, 72));
        
        throwButton.addActionListener( e -> {
            if(!canThrowDice) return;
            this.value = rand.nextInt(6) + rand.nextInt(6) + 2;
            canThrowDice = false;
            synchronized (throwLock) {
                hasNewThrow = true;
                throwLock.notifyAll();
            }
            repaint();
        });


        

        saveButton = new JButton("Save Game");
        saveButton.setPreferredSize(new Dimension(200,50));
        GridBagConstraints saveGbc = new GridBagConstraints();
        saveGbc.insets = new Insets(5, 0, 5, 0);
        saveGbc.gridx = 0;
        saveGbc.gridy = 0;
        add(saveButton, saveGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(throwButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(endTurnButton, gbc);
    }

    public void beginTurn() {
        canThrowDice = true;
    }

    public int takeThrow() throws InterruptedException {
        synchronized (throwLock) {
            while (!hasNewThrow) {
                throwLock.wait();
            }
            hasNewThrow = false;
            return value;
        }
    }

    /** Enable/disable the End Turn button from outside (e.g., after a throw). */
    public void setEndTurnEnabled(boolean enabled) {
        endTurnButton.setEnabled(enabled);
    }

    public void setDiceButtonEnabled(boolean enabled) {
        throwButton.setEnabled(enabled);
    }

    public void setSaveButtonEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
    }

    /**
     * Register a callback that runs when the End Turn button is pressed.
     */
    public void setEndTurnListener(Runnable listener) {
        endTurnButton.addActionListener(e -> listener.run());
    }

    public void setSaveListener(Runnable saveListener) {
        saveButton.addActionListener(e -> saveListener.run());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        throwButton.setText(Integer.toString(value));
    }
}
