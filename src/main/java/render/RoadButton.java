package render;

import java.awt.*;

import javax.swing.*;

import game.Coordinate;
import game.Player;
import game.buildings.RoadNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoadButton extends JButton {
    
    private static final int THICKNESS = 12;
    private static final int TOLERANCE = 5;
    private int x1, y1, x2, y2;
    private State state;
    private Player owner;
    private Polygon shape;
    private Coordinate coord1, coord2;

    private enum State {
        EMPTY,
        POSSIBLE,
        PLACED
    }

    public static List<Coordinate> perpendicularPointsAtT(
        double x1, double y1, double x2, double y2,
        double t, double D) {

        double dx = x2 - x1;
        double dy = y2 - y1;
        double len = Math.hypot(dx, dy);

        // Base point on the line at parameter t
        double px = x1 + t * dx;
        double py = y1 + t * dy;

        // Unit perpendicular vector (-dy, dx) / len
        double nx = -dy / len;
        double ny =  dx / len;

        // Two points offset by +D and -D
        double xPlus  = px + D * nx;
        double yPlus  = py + D * ny;
        double xMinus = px - D * nx;
        double yMinus = py - D * ny;

        return Arrays.asList(
            new Coordinate((int)xPlus, (int)yPlus, 0),
            new Coordinate((int)xMinus, (int)yMinus, 0)
        );
    }


    public RoadButton(int x1, int y1, int x2, int y2, Coordinate c1, Coordinate c2){
        super();
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        setBounds(0,0,900,900);
        List<Coordinate>pointsFromFirst = perpendicularPointsAtT(x1, y1, x2, y2, 0, THICKNESS / 2);
        List<Coordinate>pointsFromSecond = perpendicularPointsAtT(x1, y1, x2, y2, 1, THICKNESS / 2);

        shape = new Polygon();
        shape.addPoint(pointsFromFirst.get(0).getX(), pointsFromFirst.get(0).getY());
        shape.addPoint(pointsFromSecond.get(0).getX(), pointsFromSecond.get(0).getY());
        shape.addPoint(pointsFromSecond.get(1).getX(), pointsFromSecond.get(1).getY());
        shape.addPoint(pointsFromFirst.get(1).getX(), pointsFromFirst.get(1).getY());

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.coord1 = c1;
        this.coord2 = c2;
        this.state = State.EMPTY;

        
    }

    public boolean isPlaced() {
        return state == State.PLACED;
    }

    public boolean isPossible() {
        return state == State.POSSIBLE;
    }

    public void makePlaced(Player p) {
        this.state = State.PLACED;
        this.owner = p;
        
    }

    public void makeEmpty() {
        this.state = State.EMPTY;
    }

    public void makePossible() {
        this.state = State.POSSIBLE;
    }

    public Coordinate getCoord1() {
        return coord1;
    }
    public Coordinate getCoord2() {
        return coord2;
    }

    @Override
    public boolean contains(int x, int y) {
        if(state == State.POSSIBLE){
            return shape.contains(x, y);
        }
        return false;
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setStroke(new BasicStroke(2f));
        
        switch (state) {
            case State.EMPTY:
                break;
            case State.POSSIBLE:
                if (getModel().isRollover()) {
                    g2.setColor(Colors.PRESSED_ROAD_COLOR.val);
                } else {
                    g2.setColor(Colors.POSSIBLE_ROAD_COLOR.val);
                }
                g2.fill(shape);
                g2.setColor(Color.BLACK);
                g2.draw(shape);
                break;
            case State.PLACED:
                g2.setColor(owner.getColor().val);
                g2.fill(shape);
                g2.setColor(Color.BLACK);
                g2.draw(shape);
                break;
            default:
                break;
        }
        

        g2.dispose();
    }

    @Override
    public String toString(){
        return "RoadButton{" +
            ", state=" + state +
            ", owner=" + owner +
            '}';
    }
}