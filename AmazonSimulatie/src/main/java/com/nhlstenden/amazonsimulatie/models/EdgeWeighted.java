package com.nhlstenden.amazonsimulatie.models;

public class EdgeWeighted {

    private NodeWeighted source;
    private NodeWeighted destination;
    private int weight;

    public EdgeWeighted(NodeWeighted s, NodeWeighted d, int w){
        source = s;
        destination = d;
        weight = w;
    }

    public NodeWeighted getSource(){
        return source;
    }

    public NodeWeighted getDestination(){
        return destination;
    }

    public int getWeight(){
        return weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public String toString(){
        return String.format("(%s -> %s, %d)", source.getName(), destination.getName(), weight);
    }

}
