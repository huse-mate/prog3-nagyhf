package game.buildings;

import java.util.List;
import game.*;

public class City extends Building {
        public City(Player owner, Coordinate coord, List<Tile> neighbours){
            super(owner, coord, neighbours);
        }

        @Override
        public void addResource(Resource r) {
            owner.addResource(r, 2);
        }
    }
