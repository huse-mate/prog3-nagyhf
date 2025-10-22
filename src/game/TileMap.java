package game;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;
import java.util.List;

public class TileMap implements Iterable<Tile> {
    private HashMap<Coordinate, Tile> tiles = new HashMap<>();

    private final Random random = new Random();

    private static final int NEIGHBOR_OFFSETS[][][] = {
        {{0,0},{0,+1},{-1,+1}},
        {{0,0},{0,+1},{+1,0}}
    };


    public TileMap(){
        ArrayList<Integer> numsToGenerate = new ArrayList<>(Arrays.asList(
                8,3,6,10,5,4,2,9,6,9,5,12,3,11,10,11,4,8
        ));
        ArrayList<Resource> typesToGenerate = new ArrayList<>(Arrays.asList(
                Resource.BRICK, Resource.BRICK, Resource.BRICK,
                Resource.ORE, Resource.ORE, Resource.ORE,
                Resource.WOOD, Resource.WOOD, Resource.WOOD, Resource.WOOD,
                Resource.WOOL, Resource.WOOL, Resource.WOOL, Resource.WOOL,
                Resource.WHEAT, Resource.WHEAT, Resource.WHEAT, Resource.WHEAT
        ));

        int r1 = 0;
        int r2 = 0;
        
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                if (x==0 && y==0) continue;
                if (-2 <= (x+y) && (x+y) <= 2) {
                    r2 = random.nextInt(typesToGenerate.size());
                    tiles.put(new Coordinate(x, y, -1), new Tile(numsToGenerate.remove(r1), typesToGenerate.remove(r2)));
                }
            }
        }
        tiles.put(new Coordinate(0, 0, -1), new Tile(7,Resource.DESERT));
    }

    public Tile get(Coordinate c){
        return tiles.get(c);
    }

    public List<Tile> getNeighbouringTiles(Coordinate loc) {
        int corner = loc.getCorner();
        ArrayList<Tile> neighbours = new ArrayList<>();
        int baseX = loc.getX();
        int baseY = loc.getY();
        for(int i=0;i<3;i++){
            Coordinate c = new Coordinate(baseX + NEIGHBOR_OFFSETS[corner][i][0], baseY + NEIGHBOR_OFFSETS[corner][i][1], -1);
            if(tiles.containsKey(c)){
                neighbours.add(tiles.get(c));
            }
        }
        return neighbours;
    }

    @Override
    public java.util.Iterator<Tile> iterator() {
        return tiles.values().iterator();
    }

    public Iterable<java.util.Map.Entry<Coordinate, Tile>> entries() {
        return tiles.entrySet();
    }
}
