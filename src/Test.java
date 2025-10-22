import game.*;
import game.buildings.Building;

public class Test {
    public static void main(String[] args){
        TileMap tileMap = new TileMap();
        Game game = new Game(tileMap);
        game.newBuilding(Building.Types.SETTLEMENT, new Coordinate(2, -3, 0));
        game.newBuilding(Building.Types.CITY, new Coordinate(0, 1, 0));

    }
}
