package com.nhlstenden.amazonsimulatie.models;

import java.util.LinkedList;

public class NodeWeighted {

    /** x coordinaat */
    private int x;
    /** y coordinaat */
    private int y;
    /** naam van de node */
    private String name;
    /** boolean om bij te houden of de node al gevisit is */
    private boolean visited;
    /** boolean om bij te houden of de edge bezet is */
    private boolean occupied;
    /** lijst met alle edges van de node */
    private LinkedList<EdgeWeighted> edges;

    /**
     * constructor voor de node
     * @param name naam van de node
     * @param x x coordinaat node
     * @param y y coordinaat node
     */
    public NodeWeighted(String name,int x,int y){
        this.x = x;
        this.y = y;
        this.name = name;
        visited = false;
        occupied = false;
        edges = new LinkedList<>();
    }

    /**
     *
     * @return x coordinaat node
     */
    public int getX(){
        return x;
    }

    /**
     *
     * @return y coordinaat node
     */
    public int getY(){
        return y;
    }

    /**
     *
     * @return lijst met edges van de node
     */
    public LinkedList<EdgeWeighted> getEdges(){
        return edges;
    }

    /**
     *
     * @return naam van de node
     */
    public String getName(){
        return name;
    }

    /**
     *
     * @return returned of de node al gevisit is
     */
    public boolean isVisited(){
        return visited;
    }

    /**
     *
     * @return returned of de node bezet is
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * zet de visited variable in de node naar true
     */
    public void visit(){
        visited = true;
    }

    /**
     * zet de visited variable in de node naar false
     */
    public void unvisit(){
        visited = false;
    }

    /**
     * zet de occupied variable naar true als deze false is, en vice versa.
     */
    public void toggleOccupation() {
        occupied = !occupied;
    }
}
