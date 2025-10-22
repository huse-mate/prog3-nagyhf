package game.buildings;

import java.util.ArrayList;
import java.util.List;

import game.Coordinate;
import game.Player;
import game.Resource;
import game.Tile;

public abstract class Building {
    public enum Types {
        SETTLEMENT,
        CITY;
    }

    protected Player owner;
    protected Coordinate coordinate;
    protected ArrayList<Tile> neighbouringTiles;

    protected Building(Player owner, Coordinate coord, List<Tile> neighbours){
        this.owner = owner;
        this.coordinate = coord;
        this.neighbouringTiles = new ArrayList<>(neighbours);
    }

    public Player getOwner() {
        return owner;
    }

    public void addResource(Resource r){}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Building)) return false;
        Building b = (Building) obj;
        return owner.equals(b.owner) && coordinate.equals(b.coordinate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(owner, coordinate);
    }

    
}
