package game;
import java.util.ArrayList;

import game.buildings.*;

public class Tile {
    private int num;
    private Resource resource;
    private ArrayList<Building> buildings;
    private boolean thief; 

    public Tile(int n, Resource r){
        num = n;
        resource = r;
        buildings = new ArrayList<>();
        thief = false;
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

    /**
     * Give resources to all buildings on this tile if there is no thief
     */
    public void giveResources(){
        for (Building b : buildings) {
            if(!thief){
                b.addResource(resource);
            }
        }
    }

    /**
     * Add a building to the tile's list of buildings
     * not intended to be used directly, use Game.newBuilding() instead
     * @param buildToAdd the building to add
     */
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


    public void addThief(){
        thief = true;
    }
    public void removeThief(){
        thief = false;
    }
}
