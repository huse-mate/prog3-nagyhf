package game;
public class Coordinate {
    private final int x;
    private final int y;
    private final int corner;

    public Coordinate(int x, int y, int corner){
        this.x = x;
        this.y = y;
        this.corner = corner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCorner(){
        return corner;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate c = (Coordinate) obj;
        return x == c.x && y == c.y && corner == c.corner;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y, corner);
    }
}
