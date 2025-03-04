/**
 * The Truck script is the logic behind the truck.
 * It communicates with the LoadaingBay to check what it's supossed to do.
 * Furthermore is it an 3D-object and can be updated by the world
 * 
 * @author  Berber Bouma
 * @version 1.0
 * @since   2020-11-08
 */
package com.nhlstenden.amazonsimulatie.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.UUID;

public class Truck implements Object3D, Updatable {
    public static final String LOADING_STATUS = "loading_status";
    public static final String TRUCK_AVAILABLE = "truck_available";
    public static final int TRUCK_CAPACITY = 30;

    private UUID uuid;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    // Position in the world
    private double x = 0;
    private double y = 0;
    private double z = -10;

    // The location of the loading bay on one axis
    private double destLocation = 0;

    private double bayLocation;
    private double roadLocation;

    // Amount of packages in the truck
    private int packageAmount = 0;

    private boolean isLoading = false;
    private boolean atRoad = false;
    private boolean bayInControl = false;

    /**
     * Constructor
     * @param x            The x location of the robot
     * @param bayLocation  The location of the loading bay (z axis)
     * @param roadLocation The location of the road (z axis)
     */
    public Truck(double x, double bayLocation, double roadLocation) {
        this.x = x;
        this.bayLocation = bayLocation;
        this.roadLocation = roadLocation;
        this.destLocation = bayLocation;
        this.uuid = UUID.randomUUID();
    }

    /**
     * Add a propertyChangeListener (LoadingBayController) so it can comunicate with
     * it
     * 
     * @param pcl Object that listens when there are changes
     */
    public void addObserver(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    /**
     * Check if you've reached your destination, if so, send a message to
     * LoadingBayController
     * 
     * @param destinationZ the location of your destination on one axis
     * @return true: You've reached your destination false: You haven't reached your
     *         destination
     */
    private boolean reachedDestination(double destinationZ) {
        if (Math.abs(z - destinationZ) <= 0.25) {
            if (destinationZ == bayLocation) {
                isLoading = true;
            } else {
                isLoading = true;
                atRoad = true;
            }

            return true;
        }

        return false;
    }

    /**
     * Remove a packages from the truck
     * 
     * @return true, when the truck is emtpy. Otherwise false
     */
    private boolean emptyTheTruck() {
        packageAmount--;

        if (packageAmount <= 0) {
            packageAmount = 0;

            return true;
        }

        return false;
    }

    /**
     * Empty the truck, when the truck is empty refill truck with new racks and
     * drive towards loading bay
     */
    public void truckReachedRoad() {
        if (emptyTheTruck()) {
            packageAmount = TRUCK_CAPACITY; // quick refill of the truck
            destLocation = bayLocation; // send back towards the bay

            atRoad = false;
            isLoading = false;
        }
    }

    /**
     * Empty the truck (new racks for the warehouse) Send a message to the loading
     * bay to start filling the truck with packages
     */
    public void truckReachedLoadingBay() {
        if (emptyTheTruck()) {
            // Send message to bay, reached location available for refill
            bayInControl = true;
            pcs.firePropertyChange(TRUCK_AVAILABLE, 0, TRUCK_CAPACITY);
        }
    }

    /**
     * Send the truck back towards the road by setting a new destination Set loading
     * and baycontrol to false
     */
    public void sendTruckToRoad() {
        destLocation = roadLocation;
        isLoading = false;
        bayInControl = false;
    }

    /**
     * Add package to truck if the truck is full, send a message to the
     * LoadingBayController
     */
    public void addPackage() {
        packageAmount++;

        if (packageAmount >= TRUCK_CAPACITY) {
            packageAmount = TRUCK_CAPACITY;
            pcs.firePropertyChange(LOADING_STATUS, 0, packageAmount);
        }
    }

    /**
     * Update function of the truck, Check if the truck is loading or is at the
     * destination location
     */
    @Override
    public boolean update() {
        if (isLoading) {
            if (atRoad) {
                truckReachedRoad();
            } else {
                // extra check for when the bay is filling the truck
                if (bayInControl == false) {
                    truckReachedLoadingBay();
                }
            }
        } else {
            if (reachedDestination(destLocation) == false) {
                if (destLocation - z > 0) {
                    this.z += 0.20;
                } else {
                    this.z -= 0.20;
                }
            }
        }

        return true;
    }

    @Override
    public String getUUID() {
        return uuid.toString();
    }

    @Override
    public String getType() {
        return Truck.class.getSimpleName().toLowerCase();
    }

    /**
     * Return the x position
     */
    @Override
    public double getX() {
        return this.x;
    }

    /**
     * Return the y position
     */
    @Override
    public double getY() {
        return this.y;
    }

    /**
     * Return the z position
     */
    @Override
    public double getZ() {
        return this.z;
    }

    /**
     * Return the x rotation
     * Always 0 because the truck only needs to "drive" forward and backward
     */
    @Override
    public double getRotationX() {
        return 0;
    }

    /**
     * Return the y rotation
     * Always 0 because the truck only needs to "drive" forward and backward
     */
    @Override
    public double getRotationY() {
        return 0;
    }

    /**
     * Return the z rotation
     * Always 0 because the truck only needs to "drive" forward and backward
     */
    @Override
    public double getRotationZ() {
        return 0;
    }
}
