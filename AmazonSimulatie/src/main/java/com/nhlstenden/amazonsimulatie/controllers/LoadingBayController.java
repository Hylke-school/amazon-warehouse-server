/**
 * The LoadingBayController script is a way to let the robots and trucks work together.
 * By using a central script to check and control the trucks to prevent unnecessary code
 * 
 * @author  Berber Bouma
 * @version 1.0
 * @since   2020-11-08
 */
package com.nhlstenden.amazonsimulatie.controllers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import com.nhlstenden.amazonsimulatie.models.Truck;

public class LoadingBayController implements Runnable, PropertyChangeListener {
    public List<Truck> truckList = new ArrayList<Truck>();

    private List<Truck> atBay = new ArrayList<Truck>();

    private double bayLocation = -15;
    private double roadLocation = -30;

    private int rackAmount = 0;
    private int packageAmount = 0;

    public LoadingBayController() {
        new Thread(this).start();

        addTruck(10);
        addTruck(20);
    }

    /**
     * "Update" function of the LoadingBayController
     * Checks if there are packages that can be put into the truck
     */
    @Override
    public void run() {
        while (true) {

            if (packageAmount > 0) {
                loadPackageOnTruck();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Add a new truck to the scene
     * Set the location on the x axis, de location of the bay and road (z-axis)
     * Set the listener of the truck to this object.
     * Add the truck to the list of trucks in the game
     * @param x
     */
    private void addTruck(double x) {
        Truck newTruck = new Truck(x, bayLocation, roadLocation);
        newTruck.addObserver(this);

        truckList.add(newTruck);
    }

    /**
     * Check if there are trucks to load packages into
     *      yes, add the package to the truck.
     *           Remove a package from the loading bay
     */
    private void loadPackageOnTruck() {
        if (atBay.size() > 0) {
            atBay.get(0).addPackage();
            packageAmount--;
        }
    }

    /**
     * When there is a robot to pick up a rack, call this function
     */
    public void loadRackOnRobot() {
        rackAmount--;
        //TODO: Make the robot's pick up a package when there is one
    }

    /**
     * When there is a robot that brings a package to the loading bay, call this function
     */
    public void loadPackageOffRobot() {
        packageAmount++;
        //TODO: Make the robot drop packages too the loadingbay
    }

    public int getAtBaySize() {
        return atBay.size();
    }

    /**
     * When the truck reached the loading bay, add it to the list of available trucks 
     * @param truck truck that is now available
     * @param deliveredRacks the amount of racks the truck delivered to the bay
     */
    private void truckAvailable(Truck truck, int deliveredRacks) {
        rackAmount += deliveredRacks;

        if(atBay.contains(truck) == false) {
            atBay.add(truck);
        } 
    }

    /**
     * When the truck is full of packages, send it back on the road
     * Remove it from the list of available trucks
     * @param truck truck that is full, and gets send off
     */
    private void sendTruckToRoad(Truck truck) {
        if (atBay.contains(truck)) {
            atBay.remove(truck);
        }

        truck.sendTruckToRoad();
    }

    /**
     * Receive messages from senders
     * When there arived a truck at the bay, and this one is empty, call truckAvailable
     * When the truck is full of packages, send the truck back on the road
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Truck sendingTruck = (Truck) evt.getSource();

        if (evt.getPropertyName() == Truck.TRUCK_AVAILABLE) {
            truckAvailable(sendingTruck, (int)evt.getNewValue());
        }

        else if (evt.getPropertyName() == Truck.LOADING_STATUS) {
            sendTruckToRoad(sendingTruck);
        }
    }
}
