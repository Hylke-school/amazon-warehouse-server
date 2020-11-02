package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

class Truck implements Object3D, Updatable {
    private UUID uuid;

    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    public Truck() {
        this(0, 0, 0, 0, 0, 0);
    }
    public Truck(double x, double z){
        this(x,0,z,0,0,0);
    }
    public Truck(double x, double y, double z){
        this(x,y,z,0,0,0);
    }
    public Truck(double x, double z, double rotationX, double rotationZ){
        this(x,0,z,rotationX,0,rotationZ);
    }
    public Truck(double x, double y, double z, double rotationX, double rotationY, double rotationZ){
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.uuid = UUID.randomUUID();

    }

    @Override
    public boolean update() {
        this.x = 32.5;
        if(this.z>10){
            this.z -= 1;
        }
        else{
            this.z = 50;
        }
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
        return Truck.class.getSimpleName().toLowerCase();
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public double getRotationX() {
        return this.rotationX;
    }

    @Override
    public double getRotationY() {
        return this.rotationY;
    }

    @Override
    public double getRotationZ() {
        return this.rotationZ;
    }
}
