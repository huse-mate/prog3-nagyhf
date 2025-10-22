package game.buildings;

import java.util.List;

import game.*;

public class Settlement extends Building {
    public Settlement(Player owner, Coordinate coord, List<Tile> neighbours){
        super(owner, coord, neighbours);
    }

    @Override
    public void addResource(Resource r) {
        owner.addResource(r, 1);
    }

}
