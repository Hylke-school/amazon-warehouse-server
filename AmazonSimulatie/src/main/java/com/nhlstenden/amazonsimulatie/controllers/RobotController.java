package com.nhlstenden.amazonsimulatie.controllers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.nhlstenden.amazonsimulatie.models.*;

public class RobotController {
    private List<Robot> robotList;
    private List<Rack> rackList;
    private List<Rack> rackListToTruck;
    private boolean[][] rackLocations = new boolean[30][30];

    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /**
     * constructs object, sets rackLocations array to false
     * puts racks and robots in the world
     * @param populateRacksPercentage the percentage of rack slots to be filled
     * @param amountRobots the amount of robots to be added
     */
    public RobotController(int populateRacksPercentage, int amountRobots) {
        robotList = new ArrayList<>();
        rackList = new ArrayList<>();
        rackListToTruck = new ArrayList<>();

        for (int row = 0; row < rackLocations.length; row++) {
            for (int col = 0; col < rackLocations[row].length; col++) {
                rackLocations[row][col] = false;
            }
        }

        populateRobots(amountRobots);
        populateRacks(populateRacksPercentage);
    }

    /**
     * makes a robot, adds an observer, and adds it to the robotlist
     * @param x x location of the robot
     * @param z z location of the robot
     */
    public void addRobot(int x, int  z){
        Robot robot = new Robot(x,z);
        robotList.add(robot);
        updateRackLocations();
    }

    /**
     * makes a rack, adds an observer, and adds it to the racklist
     * @param x x location of the rack
     * @param z z location of the rack
     */
    private void addRack(int x, int z){
        Rack rack = new Rack(x,z);
        rackList.add(rack);
        updateRackLocations();
    }

    public Rack addRack(){
        int[] dropoffLocation = getDropoffLocation(false);
        if(dropoffLocation != null){
            Rack rack = new Rack(dropoffLocation[0],dropoffLocation[1],true);
            rack.setFromTruck(true);
            rackList.add(rack);
            updateRackLocations();
            return rack;
        }
        return null;
    }

    /**
     * removes rack from rackList, updates racklocations
     * @param rack rack to be removed
     */
    public void removeRack(Rack rack){
        try{
            rackList.remove(rack);
            rackListToTruck.add(rack);
            updateRackLocations();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    public void removeRackToTruck(Rack rack){
        try{
            rackListToTruck.remove(rack);
            updateRackLocations();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * adds the specified amount of robots to the robot spawning area, maximum of 58 robots
     * @param amountRobots amount of robots to be added
     */
    public void populateRobots(int amountRobots){
        for(int i = 1; i <= amountRobots; i++){
            if(i <= 29){
                addRobot(29,i);
            } else{
                addRobot(28, i-29);
            }
        }
    }

    /**
     * populates the grid randomly with storage racks, only in permitted areas
     * @param percentage percentage of slots to be filled with storage racks
     */
    public void populateRacks(int percentage){
        Random random = new Random();
        boolean checkgrid,checkrandom;
        for (int i = 2; i < 25; i++){
            for (int j = 2; j < 29; j++){
                checkrandom = random.nextInt(100)+1 <= percentage;
                checkgrid = !(i==4||i==7||i==10||i==13||i==16||i==19||i==22||j==15);
                if(checkrandom&&checkgrid){
                    addRack(i,j);
                }
            }
        }
    }

    /**
     * takes the first rack from the racklist, checks if any robots are available to pick it up, then picks it up
     * once the robot puts down the rack, delete the rack
     */
    public Rack pickupRack() {
        int[] dropoffLocation;
        int racklistsize = rackList.size();
        Rack rack = null;
        if(racklistsize != 0){
            rack = rackList.get(0);
            boolean fromTruck = rack.getFromTruck();
            boolean toTruck = rack.getToTruck();
            System.out.printf("pickupRack: fromTruck:%b toTruck:%b\n",fromTruck,toTruck);
            if(!fromTruck&&!toTruck){
                Robot robot = robotList.get(0);
                    if (!robot.isBusy() && !rack.isBusy()) {
                        dropoffLocation = getDropoffLocation(false);
                        if (dropoffLocation != null) {
                            if(robot.pickup(rack,dropoffLocation[0],dropoffLocation[1])){
                                rack.setToTruck(true);
                                removeRack(rack);

                        }
                    }
                }
            }
        }
        return rack;
    }

    public Rack pickupRackFromTruck(){
        int[] dropoffLocation;
        int racklistsize = rackList.size();
        Rack rack = null;
        if(racklistsize != 0){
            rack = rackList.get(0);
            boolean fromTruck = rack.getFromTruck();
            boolean toTruck = rack.getToTruck();
            System.out.printf("pickupRackFromTruck: fromTruck:%b toTruck:%b\n",fromTruck,toTruck);
            if(fromTruck&&!toTruck){
                Robot robot = robotList.get(0);
                    if (!robot.isBusy() && !rack.isBusy()) {
                        dropoffLocation = getDropoffLocation(true);
                        if (dropoffLocation != null) {
                            if(robot.pickup(rack, dropoffLocation[0], dropoffLocation[1])){
                                rack.setFromTruck(false);
                            }
                        }
                    }

            }
        }
        return rack;
    }

    public Rack pickupRackToTruck(){
        int racklistsize = rackListToTruck.size();
        Rack rack = null;
        if(racklistsize!=0){
            rack = rackListToTruck.get(0);
            System.out.printf("removing rack %s\n",rack);
            removeRackToTruck(rack);
        }
        return rack;
    }

    /**
     * goes through the racklist, checks locations, and sets racklocations accordingly
     */
    public void updateRackLocations(){
        int x,z;
        boolean hasChanged = false;
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                for(Rack rack : rackList){
                    x = (int)rack.getX();
                    z = (int)rack.getZ();
                    if(i==x&&j==z){
                        rackLocations[i][j] = true;
                        hasChanged = true;
                    }
                }
                for(Rack rack : rackListToTruck){
                    x = (int)rack.getX();
                    z = (int)rack.getZ();
                    if(i==x&&j==z){
                        rackLocations[i][j] = true;
                        hasChanged = true;
                    }
                }
                if(!hasChanged) rackLocations[i][j] = false;
            }
        }

    }

    /**
     * goes through the dropoff area, and checks if there are any racks there, if not, sets the dropofflocation to the first available slot
     * @return dropofflocation, with x and z
     */
    public int[] getDropoffLocation(boolean fromTruck) {
        int[] dropoffLocation = null;

        if(!fromTruck){
            outer: for(int i = 26; i < 29; i++){
                for(int j = 2; j < 29; j++){
                    if(!rackLocations[i][j]){
                        dropoffLocation = new int[2];
                        dropoffLocation[0] = i;
                        dropoffLocation[1] = j;
                        break outer;
                    }
                }
            }
        } else {
            boolean checkgrid;
            outer: for (int i = 2; i < 25; i++){
                for (int j = 2; j < 29; j++){
                    checkgrid = !(i==4||i==7||i==10||i==13||i==16||i==19||i==22||j==15);
                    if(checkgrid){
                        dropoffLocation = new int[2];
                        dropoffLocation[0] = i;
                        dropoffLocation[1] = j;
                        break outer;
                    }
                }
            }
        }

        return dropoffLocation;
    }

    //returns rackList
    public List<Rack> getRackList(){
        return rackList;
    }

    //returns robotList
    public List<Robot> getRobotList(){
        return robotList;
    }

}
