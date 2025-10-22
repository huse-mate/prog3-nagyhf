package game;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import IO.GameIO;
import game.buildings.Building;
import game.buildings.City;
import game.buildings.Settlement;

public class Player {
    private int id;
    private EnumMap<Resource, Integer> resources;
    private int cardCount;
    private ArrayList<Building> buildings;
    private Random rand;

    public Player(int i){
        id = i;
        cardCount = 0;
        resources = new EnumMap<>(Resource.class);
        buildings = new ArrayList<>();
        for (Resource r : Resource.values()) {
            resources.put(r, 0);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player p = (Player) obj;
        return id == p.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }




    public void addResource(Resource r, int n){
        resources.put(r, resources.get(r) + n);
        cardCount += n;
    }

    public Resource removeRandomResource() {
        int i = rand.nextInt(cardCount);
        for (Resource r : Resource.values()) {
            int resourceCount = resources.get(r);
            if(resourceCount > i){
                resources.put(r,resources.get(r)-1);
                cardCount--;
                return r;
            }
            i-=resourceCount;
        }
        return Resource.DESERT;
    }

    public void thiefAction(){
        if(cardCount >= 7){
            int toRemove = cardCount/2;
            Map<Resource, Integer> toThrow = GameIO.chooseToThrow(this, toRemove);
            for (Resource r : Resource.values()) {
                resources.put(r,resources.get(r) - toThrow.get(r));
            }
        }
    }


    public boolean canBuildSettlement(){
        return  resources.get(Resource.BRICK)>=1 &&
                resources.get(Resource.WOOD)>=1 && 
                resources.get(Resource.WOOL)>=1 && 
                resources.get(Resource.WHEAT)>=1;
    }

    public boolean canBuildCity(){
        boolean hasSettlement = false;
        for (Building b : buildings) {
            if(b.getClass() == Settlement.class){
                hasSettlement = true;
                break;
            }
        }
        return  resources.get(Resource.WHEAT)>=2 &&
                resources.get(Resource.ORE)>=3 &&
                hasSettlement;
    }

    public boolean canBuildRoad(){
        return  resources.get(Resource.BRICK)>=1 &&
                resources.get(Resource.WOOD)>=1;
    }

    public void addBuilding(Building buildingToAdd){
        if(buildingToAdd.getClass() == City.class){
            for (Building b : buildings) {
                if(b.equals(buildingToAdd)){
                    buildings.remove(b);
                    break;
                }
            }
        }
        buildings.add(buildingToAdd);
    }

    
}
