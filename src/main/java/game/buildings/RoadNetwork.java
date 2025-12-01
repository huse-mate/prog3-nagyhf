package game.buildings;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game.Coordinate;
import game.Player;

public class RoadNetwork {
    private ArrayList<Road> adjList;
    private HashMap<Coordinate, Player> roadNodes;

    private static final int[][] MISSED_ROADNODES = {
        {-3,0,1}, {-3,1,1}, {-3,2,1}, {3,-3,0}, {3,-2,0}, {3,-1,0}
    };

    private static final int[][][] BUILDING_NEIGHBOUR_TRANSFORM = {
        {{0,0,1}, {0,0,-1}},
        {{-1,0,1}, {1,0,-1}},
        {{-1,1,1}, {1,-1,-1}},
    };

    public static class Road {
        private Player owner;
        private Coordinate c1;
        private Coordinate c2;

        public Road(Player owner, Coordinate c1, Coordinate c2){
            this.c1 = c1;
            this.c2 = c2;
            this.owner = owner;
        }
        
        public Coordinate getCoord1(){
            return c1;
        }
        public Coordinate getCoord2(){
            return c2;
        }
        public Player getOwner(){
            return owner;
        }

        @Override
        public boolean equals(Object o){
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Road e = (Road) o;
            return (c1.equals(e.c1) && c2.equals(e.c2)) || (c1.equals(e.c2) && c2.equals(e.c1));
        }

        @Override
        public int hashCode() {
            return c1.hashCode() ^ c2.hashCode();
        }

        @Override
        public String toString(){
            return "Road{" +
                "owner=" + owner +
                ", c1=" + c1 +
                ", c2=" + c2 +
                '}';
        }
    }



    public RoadNetwork(){
        adjList = new ArrayList<>();
        roadNodes = new HashMap<>();
        
        for (int x = -2; x <= 2; x++) {
            for (int y = -3; y <= 2; y++) {
                if (-3 <= (x + y) && (x + y) <= 2) {
                    roadNodes.put(new Coordinate(x, y, 0), null);
                    roadNodes.put(new Coordinate(x, y, 1), null);
                }
            }
        }
        for (int[] c : MISSED_ROADNODES) {
            roadNodes.put(new Coordinate(c[0], c[1], c[2]), null);
        }
    }
    
    public Map<Coordinate, Player> getBuildingMap(){
        return roadNodes;
    }

    public List<Road> getRoads(){
        return adjList;
    }

    /**
     * 
     * @return all possible roads in the network, regardless of ownership
     * The returned roads have null owners
     * 
     */
    
    public List<Road> getAllRoads(){
        Set<Road> allRoads = new HashSet<>();
        for(Coordinate c : roadNodes.keySet()){
            allRoads.addAll(getRoadsFromHere(c, null));
        }
        return new ArrayList<>(allRoads);
    }

    /**
     * Add a new road to the network for player p between coordinates c1 and c2
     * not intended to be used directly, use Game.newRoad() instead
     */
    public void newRoad(Player p, Coordinate c1, Coordinate c2){
        adjList.add(new Road(p,c1,c2));
    }

    /**
     * Add a building to the road network at coordinate c for player p
     * not intended to be used directly, use Game.newBuilding() instead
     */
    public void newBuilding(Coordinate c, Player p){
        roadNodes.put(c, p);
    }

    /**
     * Get all neighbouring nodes to coordinate c
     * @param c the coordinate to get neighbours for
     * @return a list of neighbouring coordinates
     */
    private List<Coordinate> getTransforms(Coordinate c){
        List<Coordinate> transforms = new ArrayList<>();
        for (int[][] transform : BUILDING_NEIGHBOUR_TRANSFORM) {
            Coordinate destination = new Coordinate(c.getX() + transform[c.getCorner()][0], c.getY() + transform[c.getCorner()][1], c.getCorner() + transform[c.getCorner()][2]);
            if(roadNodes.containsKey(destination))
                transforms.add(destination);
        }
        return transforms;
    }

    /**
     * Get all possible roads from coordinate c for player p
     * @param c the coordinate to get roads from
     * @param p the player to own the roads
     * @return a list of possible roads from c for player p
     */
    private List<Road> getRoadsFromHere(Coordinate c, Player p){
        List<Road> roadsFrom = new ArrayList<>();
        getTransforms(c).forEach( dest -> {
            Road newRoad = new Road(p, c, dest);
            if(!adjList.contains(newRoad)) 
                roadsFrom.add(newRoad);
        });
        return roadsFrom;
    }

    /**
     * Get all possible settlement locations for player p
     * @param p the player to get settlement locations for
     * @param start whether this is the starting placement phase
     * @return a set of possible settlement coordinates for player p, if start is true, all road nodes are considered
     */
    public Set<Coordinate> getPossibleSettlements(Player p, boolean start){
        Set<Coordinate> possibleSettlements;
        if(start){
            possibleSettlements = new HashSet<>(roadNodes.keySet());
        } else {
            possibleSettlements = new HashSet<>();
            for (Road road : adjList) {
                if(road.getOwner().equals(p)){
                    possibleSettlements.add(road.getCoord1());
                    possibleSettlements.add(road.getCoord2());
                }
            }
        }
        for (var entry : roadNodes.entrySet()) {
            Coordinate c = entry.getKey();
            boolean hasBuilding = entry.getValue() != null;
            if(hasBuilding){
                possibleSettlements.remove(c);
                getTransforms(c).forEach(possibleSettlements::remove);
            }
        }
        return possibleSettlements;
    }

    /**
     * Get all possible roads for player p
     * @param p the player to get possible roads for
     * @return a set of possible roads for player p
     */
    public Set<Road> getPossibleRoads(Player p){
        HashSet<Road> possibleRoads = new HashSet<>();
        List<Building> buildings = p.getBuildings();
        for (Building b : buildings) {
            getRoadsFromHere(b.coordinate, p).forEach(possibleRoads::add);
        }
        for (Road road : adjList) {
            if (road.getOwner().equals(p)) {
                getRoadsFromHere(road.getCoord1(), p).forEach(possibleRoads::add);
                getRoadsFromHere(road.getCoord2(), p).forEach(possibleRoads::add);
            }
        }
        return possibleRoads;
    }


    /**
     * Get all nodes connected owned by player p
     * @param p the player to get nodes for
     * @return a set of coordinates connected by roads owned by player p
     */
    private Set<Coordinate> getPlayerNodes(Player p){
        Set<Coordinate> playerNodes = new HashSet<>();
        for (Road road : adjList) {
            if(road.getOwner().equals(p)){ 
                playerNodes.add(road.getCoord1());
                playerNodes.add(road.getCoord2());
            }
        }
        return playerNodes;
    }

    /**
     * Get all roads owned by player p
     * @param p the player to get roads for
     * @return a list of roads owned by player p
     */
    private List<Road> getPlayerRoads(Player p) {
        List<Road> playerRoads = new ArrayList<>();
        for (Road road : adjList) {
            if(road.getOwner().equals(p)){  
                playerRoads.add(road);
            }
        }
        return playerRoads;
    }

    /**
     * Get all neighbouring nodes to coordinate c, along the given roads
     * @param c the coordinate to get neighbours for
     * @param roads the list of roads to consider
     * @return a map of neighbouring coordinates and the roads connecting them to c
     */
    public Map<Coordinate, Road> getNeighbouringNodes(Coordinate c, List<Road> roads){
        Map<Coordinate, Road> neighbours = new HashMap<>();
        for (Road road : roads) {
            if(road.getCoord1().equals(c)){
                neighbours.put(road.getCoord2(), road);
            } else if(road.getCoord2().equals(c)){
                neighbours.put(road.getCoord1(), road);
            }
        }
        return neighbours;
    }

    /**
     * Get the longest path starting from coordinate c, along the given roads
     * @param c the coordinate to start from
     * @param roads the list of roads to consider
     * @return the length of the longest path from c
     */
    public int longestPathFrom(Coordinate c, List<Road> roads){
        HashMap<Coordinate, Integer> distances = new HashMap<>();
        HashSet<Coordinate> visited = new HashSet<>();
        HashMap<Coordinate, Coordinate> prevNode = new HashMap<>();
        HashMap<Coordinate, Road> roadHere = new HashMap<>();
        Deque<Coordinate> stack = new ArrayDeque<>();
        stack.add(c);
        distances.put(null,-1);
        prevNode.put(c,null);
        while(!stack.isEmpty()){
            Coordinate node = stack.removeLast();
            roads.remove(roadHere.get(node));
            distances.put(node,distances.get(prevNode.get(node))+1);
            if(!visited.contains(node)){
                visited.add(node);
                getNeighbouringNodes(node, roads).forEach( (n, r) -> {
                    stack.add(n);
                    prevNode.put(n,node);
                    roadHere.put(n,r);
                });
            }
        }

        int farthestNode = 0;
        for (Integer dist : distances.values()) {
            farthestNode = Math.max(farthestNode, dist);
        }
        return farthestNode;
    }

    /**
     * Get the longest path owned by player p
     * @param p the player to get the longest path for
     * @return the length of the longest path owned by player p
     */
    public int getLongestPath(Player p){
        Set<Coordinate> playerNodes = getPlayerNodes(p);
        int maxRoadLength = 0;
        for (Coordinate node : playerNodes) {
            List<Road> playerRoads = getPlayerRoads(p);
            int curLength = longestPathFrom(node, playerRoads);
            maxRoadLength = Math.max(maxRoadLength, curLength);
        }
        return maxRoadLength;
    }

}
