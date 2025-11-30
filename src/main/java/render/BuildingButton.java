package render;

import javax.swing.*;

import game.Player;

import java.awt.*;

public class BuildingButton extends JButton {

    private Player owner;
    private State state = State.EMPTY;

    private enum State {
        EMPTY,
        PLACEABLE,
        SETTLEMENT,
        UPGRADEABLE,
        CITY;
    }
        
    public BuildingButton(int x, int y, int r){
        super();
        setFocusPainted(false);       // no focus outline
        setBorderPainted(false);      // no rectangular border
        setContentAreaFilled(false);  // we'll paint the shape manually
        setOpaque(false);
        setBounds(x-r, y-r, 2*r, 2*r);
        setVisible(true);
        owner = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        int diameter = Math.min(getWidth(), getHeight());

        switch (state) {
            case EMPTY:
                break;
            case PLACEABLE:
                if (getModel().isRollover()) {
                    g2.setColor(Colors.PRESSED_SETTLEMENT_COLOR.val);
                } else {
                    g2.setColor(Colors.POSSIBLE_SETTLEMENT_COLOR.val);
                }
                g2.fillOval(0, 0, diameter, diameter);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(0, 0, diameter, diameter);
                break;
            case SETTLEMENT:
                g2.setColor(owner.getColor().val);
                g2.fillOval(0, 0, diameter, diameter);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(0, 0, diameter, diameter);
                break;
            case UPGRADEABLE:
                if (getModel().isRollover()) {
                    g2.setColor(Colors.PRESSED_CITY_COLOR.val);
                } else {
                    g2.setColor(Colors.POSSIBLE_CITY_COLOR.val);
                }
                g2.fillRect(0, 0, diameter, diameter);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(0, 0, diameter, diameter);
                break;
            case CITY:
                g2.setColor(owner.getColor().val);
                g2.fillRect(0, 0, diameter, diameter);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(0, 0, diameter, diameter);
                break;
            default:
                break;
        }

        g2.dispose();
    }

    // --- Make clicks register only inside the circle ---
    @Override
    public boolean contains(int x, int y) {
        switch(state) {
            case EMPTY:
                break;
            case PLACEABLE:
                int radius = Math.min(getWidth(), getHeight()) / 2;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
            case SETTLEMENT:
                break;
            case UPGRADEABLE:
                return x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight();
            case CITY:
                break;
            default:
                break;
        }
        return false;
    }

    public void reset(){
        if(state == State.PLACEABLE){
            state = State.EMPTY;
        }
        if (state == State.UPGRADEABLE) {
            state = State.SETTLEMENT;
        }
    }

    public void setSettlement(Player p){
        owner = p;
        state = State.SETTLEMENT;
    }

    public void setCity(Player p){
        owner = p;
        state = State.CITY;
    }

    public void makeUpgradeable(){
        state = State.UPGRADEABLE;
    }

    public void makeUnUpgradeable(){
        state = State.SETTLEMENT;
    }

    public void makeEmpty(){
        state = State.EMPTY;
    }

    public void makePlaceable(){
        state = State.PLACEABLE;
    }

    public void upgrade(){
        state = State.CITY;
    }

    public boolean isPlaced(){
        return (state != State.PLACEABLE && state != State.EMPTY);
    }

    public boolean isPlaceable(){
        return state == State.PLACEABLE;
    }

    public boolean isUpgradeable(){
        return state == State.UPGRADEABLE;
    }
}
