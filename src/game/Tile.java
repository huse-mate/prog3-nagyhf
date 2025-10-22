package game;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import game.buildings.Building;
import game.buildings.Settlement;

public class Tile {
    private int num;
    private Resource resource;
    private ArrayList<Building> buildings;
    private boolean thief; 

    public Tile(int n, Resource r){
        num = n;
        resource = r;
        buildings = new ArrayList<>();
        if(r == Resource.DESERT){
            thief = true;
        } else {
            thief = false;
        }
    }

    public int getNum() {
        return num;
    }
    public Resource getType() {
        return resource;
    }
    public boolean getThief(){
        return thief;
    }

    public void giveResources(){
        for (Building b : buildings) {
            if(!thief){
                b.addResource(resource);
            }
        }
    }

    public void addBuilding(Building buildToAdd){
        if(buildToAdd.getClass() == Settlement.class){
            buildings.add(buildToAdd);
        } else {    
            for (Building b : buildings) {
                if(b.equals(buildToAdd)){
                    buildings.remove(b);
                    buildings.add(buildToAdd);
                    return;
                }
            }
        }
    }


    public Set<Player> addThief(Player curPlayer){
        thief = true;
        Set<Player> canStealFrom = new HashSet<>();
        for (Building b : buildings) {
            Player owner = b.getOwner();
            if(curPlayer != owner){
                canStealFrom.add(owner);
            }
        }
        return canStealFrom;
    }
    public void removeThief(){
        thief = false;
    }
}
