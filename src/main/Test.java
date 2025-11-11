package main;
import game.*;
import game.buildings.Building;

public class Test {
    public static void main(String[] args){
        Game game = new Game();
        game.newBuilding(Building.Types.SETTLEMENT, new Coordinate(3, -3, 0));
        game.newRoad(new Coordinate(3, -3, 0), new Coordinate(2, -3, 1));
        game.newRoad(new Coordinate(2, -3, 0), new Coordinate(2, -3, 1));

        game.getPossibleRoads().forEach(road -> {
            System.out.println("Possible road between " + road.getCoord1() + " and " + road.getCoord2());
        });
        game.getPossibleSettlements(false).forEach(coord -> {
            System.out.println("Possible settlement " + coord);
        });
        game.newRoad(new Coordinate(2, -3, 0),new Coordinate(1, -2, 1));
        game.newRoad(new Coordinate(1, -2, 0),new Coordinate(1, -2, 1));
        game.newRoad(new Coordinate(2, -2, 0),new Coordinate(1, -2, 1));
        game.newRoad(new Coordinate(2, -2, 0),new Coordinate(2, -2, 1));
        game.newRoad(new Coordinate(3, -2, 0),new Coordinate(2, -2, 1));
        game.newRoad(new Coordinate(3, -3, 0),new Coordinate(2, -2, 1));
        game.newRoad(new Coordinate(2, -2, 0),new Coordinate(1, -1, 1));
        game.newRoad(new Coordinate(1, -1, 0),new Coordinate(1, -1, 1));
    }
}
