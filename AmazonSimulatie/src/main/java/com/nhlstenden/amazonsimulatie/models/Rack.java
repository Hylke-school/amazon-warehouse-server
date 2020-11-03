package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

class Rack implements Object3D, Updatable {
    private UUID uuid;

    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    public Rack() {
        this(0, 0, 0, 0, 0, 0);
    }
    public Rack(double x, double z){
        this(x,0,z,0,0,0);
    }
    public Rack(double x, double y, double z){
        this(x,y,z,0,0,0);
    }
    public Rack(double x, double z, double rotationX, double rotationZ){
        this(x,0,z,rotationX,0,rotationZ);
    }
    public Rack(double x, double y, double z, double rotationX, double rotationY, double rotationZ){
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.uuid = UUID.randomUUID();
    }

    /**
     * checks if the rack has been picked up by a robot, by checking its Y value
     * @return boolean, true if the rack is on a robot
     */
    public boolean isBusy(){
        return this.getY() == 0.15;
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public String getUUID() {
        return this.uuid.toString();
    }

    @Override
    public String getType() {
        /*
         * Dit onderdeel wordt gebruikt om het type van dit object als stringwaarde terug
         * te kunnen geven. Het moet een stringwaarde zijn omdat deze informatie nodig
         * is op de client, en die verstuurd moet kunnen worden naar de browser. In de
         * javascript code wordt dit dan weer verder afgehandeld.
         */
        return Rack.class.getSimpleName().toLowerCase();
    }

    @Override
    public double getX() {
        return this.x;
    }

    public void setX(double x){
        this.x = x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    public void setY(double y){
        this.y = y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    public void setZ(double z){
        this.z = z;
    }

    @Override
    public double getRotationX() {
        return this.rotationX;
    }

    public void setRotationX(double rotationX){
        this.rotationX = rotationX;
    }

    @Override
    public double getRotationY() {
        return this.rotationY;
    }

    public void setRotationY(double rotationY){
        this.rotationY = rotationY;
    }

    @Override
    public double getRotationZ() {
        return this.rotationZ;
    }

    public void setRotationZ(double rotationZ){
        this.rotationZ = rotationZ;
    }
}
