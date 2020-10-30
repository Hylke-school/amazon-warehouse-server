package com.nhlstenden.amazonsimulatie.models;

import java.util.UUID;

/*
 * Deze class stelt een robot voor. Hij impelementeerd de class Object3D, omdat het ook een
 * 3D object is. Ook implementeerd deze class de interface Updatable. Dit is omdat
 * een robot geupdate kan worden binnen de 3D wereld om zich zo voort te bewegen.
 */
class Robot implements Object3D, Updatable {
    private UUID uuid;

    private double x = 0;
    private double y = 0;
    private double z = 0;

    private double rotationX = 0;
    private double rotationY = 0;
    private double rotationZ = 0;

    private Rack child;

    public Robot() {
        this(0, 1, 0, 0, 0, 0);
    }
    public Robot(double x, double z){
        this(x,1,z,0,0,0);
    }
    public Robot(double x, double y, double z){
        this(x,y,z,0,0,0);
    }
    public Robot(double x, double z, double rotationX, double rotationZ){
        this(x,1,z,rotationX,0,rotationZ);
    }
    public Robot(double x, double y, double z, double rotationX, double rotationY, double rotationZ){
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        this.uuid = UUID.randomUUID();
    }

    /*
     * Deze update methode wordt door de World aangeroepen wanneer de
     * World zelf geupdate wordt. Dit betekent dat elk object, ook deze
     * robot, in de 3D wereld steeds een beetje tijd krijgt om een update
     * uit te voeren. In de updatemethode hieronder schrijf je dus de code
     * die de robot steeds uitvoert (bijvoorbeeld positieveranderingen). Wanneer
     * de methode true teruggeeft (zoals in het voorbeeld), betekent dit dat
     * er inderdaad iets veranderd is en dat deze nieuwe informatie naar de views
     * moet worden gestuurd. Wordt false teruggegeven, dan betekent dit dat er niks
     * is veranderd, en de informatie hoeft dus niet naar de views te worden gestuurd.
     * (Omdat de informatie niet veranderd is, is deze dus ook nog steeds hetzelfde als
     * in de view)
     */
    @Override
    public boolean update() {
        this.y = 0.15;
        if(child != null){
            child.setX(this.x);
            child.setY(this.y+1.65);
            child.setZ(this.z);
            child.setRotationX(this.rotationX);
            child.setRotationY(this.rotationY);
            child.setRotationZ(this.rotationZ);
        }
        return true;
    }

    public void setChild(Rack child){
        this.child = child;
    }

    public void removeChild(){
        if(child != null){
            child.setY(1.5);
        }
        child = null;
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
        return Robot.class.getSimpleName().toLowerCase();
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