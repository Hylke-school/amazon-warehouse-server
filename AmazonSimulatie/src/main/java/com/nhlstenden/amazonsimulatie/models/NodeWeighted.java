package com.nhlstenden.amazonsimulatie.models;

import java.util.LinkedList;

public class NodeWeighted {

    private int n;
    private int x;
    private int y;
    private String name;
    private boolean visited;
    private boolean occupied;
    private LinkedList<EdgeWeighted> edges;

    public NodeWeighted(int n, String name,int x,int y){
        this.n = n;
        this.x = x;
        this.y = y;
        this.name = name;
        visited = false;
        occupied = false;
        edges = new LinkedList<>();
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public LinkedList<EdgeWeighted> getEdges(){
        return edges;
    }

    public String getName(){
        return name;
    }

    public boolean isVisited(){
        return visited;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void visit(){
        visited = true;
    }

    public void unvisit(){
        visited = false;
    }

    public void toggleOccupation() {
        occupied = !occupied;
    }
}
