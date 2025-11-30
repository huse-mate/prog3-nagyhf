package game;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import game.buildings.Building;
import game.buildings.City;
import game.buildings.Settlement;
import render.Colors;

public class Player {
    private int id;
    private Colors color;
    private Random rand = new Random();

    private ArrayList<Building> buildings = new ArrayList<>();
    private EnumMap<Resource, Integer> resources = new EnumMap<>(Resource.class);

    private int cardCount = 0;
    private int devCardCount = 0;
    private int points = 0;

    private int knigthsPlayed = 0;
    private int maxRoadLength = 0;

    private int freeRoadCards = 0;
    private int knightCards = 0;
    private int victoryPointCards = 0;

    private int settlementInventory = 0;
    private int roadInventory = 0;
    private int cityInventory = 0;

    public Player(int i, Colors c){
        id = i;
        color = c;
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

    @Override
    public String toString(){
        return "Player " + (id+1);
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

    public final int getResourceCount(Resource r){
        return resources.get(r);
    }

    public final int getDevCardCount(){
        return devCardCount;
    }

    public final int getScore(){
        return points;
    }

    public final int getKnightsPlayed(){
        return knigthsPlayed;
    }

    public void addKnightsPlayed() {
        knigthsPlayed++;
    }

    public final int getMaxRoadLength(){
        return maxRoadLength;
    }

    public final int getDevCardCount(String type){
        switch(type){
            case "KNIGHT":
                return knightCards;
            case "POINT":
                return victoryPointCards;
            case "ROAD":
                return freeRoadCards;
            default:
                return 0;
        }
    }

    public void removeRoadBuildingCard(){
        if(freeRoadCards > 0){
            freeRoadCards--;
            devCardCount--;
        }
    }

    public void removePointCard(){
        if(victoryPointCards > 0){
            victoryPointCards--;
            devCardCount--;
        }
    }

    public void removeKnightCard(){
        if(knightCards > 0){
            knightCards--;
            devCardCount--;
        }
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

    public void addPointCard(){
        victoryPointCards++;
        devCardCount++;
    }

    public void addKnightCard(){
        knightCards++;
        devCardCount++;
    }

    public void addFreeRoadCard(){
        freeRoadCards++;
        devCardCount++;
    }

    public void setKnightsPlayed(int knightsPlayed) {
        this.knigthsPlayed = knightsPlayed;
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

    public int getSettlementInventory(){
        return settlementInventory;
    }
    public int getCityInventory(){
        return cityInventory;
    }
    public void removeCityInventory(){
        if(cityInventory > 0)
            cityInventory--;
    }
    public void removeSettlementInventory(){
        if(settlementInventory > 0)
            settlementInventory--;
    }
    public void removeRoadInventory(){
        if(roadInventory > 0)
            roadInventory--;
    }
    public int getRoadInventory(){
        return roadInventory;
    }

    public void addSettlementForStart(){
        settlementInventory++;
    }

    public void addCityForStart(){
        cityInventory++;
    }

    public void addRoadForStart(){
        roadInventory++;
    }

    public void thiefSteal(){
        if(cardCount > 7){
            int toRemove = cardCount/2;
            for (int i = 0; i < toRemove; i++) {
                removeRandomResource();
            }
        }
    }


    public boolean canBuyDevCard(){
        return  (resources.get(Resource.ORE)>=1 &&
                resources.get(Resource.WOOL)>=1 && 
                resources.get(Resource.WHEAT)>=1);
    }

    public boolean canBuildSettlement(){
        return  (resources.get(Resource.BRICK)>=1 &&
                resources.get(Resource.WOOD)>=1 && 
                resources.get(Resource.WOOL)>=1 && 
                resources.get(Resource.WHEAT)>=1) 
                || settlementInventory > 0;
    }

    public boolean canBuildCity(){
        boolean hasSettlement = false;
        for (Building b : buildings) {
            if(b.getClass() == Settlement.class){
                hasSettlement = true;
                break;
            }
        }
        return  (resources.get(Resource.WHEAT)>=2 &&
                resources.get(Resource.ORE)>=3 &&
                hasSettlement) || cityInventory > 0;
    }

    public boolean canBuildRoad(){
        return  (resources.get(Resource.BRICK)>=1 &&
                resources.get(Resource.WOOD)>=1)
                || roadInventory > 0;
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
