package render;

import javax.swing.*;
import java.awt.*;


public class DicePanel extends JButton {
    private final transient Image diceImage = new ImageIcon(new java.io.File("assets/dice.png").getAbsolutePath()).getImage();
    private final transient java.util.Random rand = new java.util.Random();

    private boolean canThrowDice;
    private int value = 0;
    // synchronization helpers for a blocking take
    private final transient Object throwLock = new Object();
    private transient boolean hasNewThrow = false;

    public DicePanel(){
        setPreferredSize(new Dimension(200,200));
        setFocusPainted(false);       // no focus outline
        setBorderPainted(false);      // no rectangular border
        setContentAreaFilled(false);  // we'll paint the shape manually
        setOpaque(false);
        addActionListener( e -> {
            if(!canThrowDice) return;
            this.value = rand.nextInt(6) + rand.nextInt(6) + 2;
            canThrowDice = false;
            synchronized (throwLock) {
                hasNewThrow = true;
                throwLock.notifyAll();
            }
            
            repaint();
        });
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        if(canThrowDice) {
            if (getModel().isRollover()) {
                g2.setColor(Colors.PRESSED_SETTLEMENT_COLOR.val);
            } else {
                g2.setColor(Colors.POSSIBLE_SETTLEMENT_COLOR.val);
            }
        } else {
            g2.setColor(Colors.MAP_BACKGROUND_COLOR.val);
        }
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        g2.drawString(String.valueOf(value), getWidth() / 2 - 12, getHeight() / 2 +12);
        // if (diceImage != null) {
        //     g.drawImage(diceImage, 0, 0, getWidth(), getHeight(), this);
        // }
    }
}
