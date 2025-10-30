package game;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import IO.GameIO;
import game.buildings.Building;
import game.buildings.City;
import game.buildings.Settlement;
import render.Colors;

public class Player {
    private int id;
    private EnumMap<Resource, Integer> resources;
    private int cardCount;
    private int devCardCount;
    private int points;
    private int knigthsPlayed;
    private int maxRoadLength;
    private ArrayList<Building> buildings;
    private Colors color;
    private Random rand;

    public Player(int i, Colors c){
        id = i;
        cardCount = 0;
        devCardCount = 0;
        points = 0;
        knigthsPlayed = 0;
        maxRoadLength = 0;
        color = c;
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


    public List<Building> getBuildings() {
        return buildings;
    }

    public final int getId() {
        return id;
    }

    public final int getCardCount() {
        return cardCount;
    }

    public final int getDevCardCount(){
        return devCardCount;
    }

    public final int getScore(){
        return points;
    }

    public final int getKnightCount(){
        return knigthsPlayed;
    }

    public final int getMaxRoadLength(){
        return maxRoadLength;
    }

    public final Colors getColor(){
        return color;
    }

    public void setMaxRoadLength(int l){
        maxRoadLength = l;
    }

    public void addPoints(int x){
        points += x;
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

    public void thiefSteal(){
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
