package com.nhlstenden.amazonsimulatie.models;

public class EdgeWeighted {

    /** de node waar de edge begint */
    private NodeWeighted source;
    /** de node waar de edge eindigt*/
    private NodeWeighted destination;
    /** hoe ver de twee nodes uit elkaar liggen*/
    private int weight;

    /** constructor voor EdgeWeighted
     * @param s de source node
     * @param d de destination node
     * @param w het gewicht tussen de twee nodes*/
    public EdgeWeighted(NodeWeighted s, NodeWeighted d, int w){
        source = s;
        destination = d;
        weight = w;
    }

    /**
     *
     * @return source node
     */
    public NodeWeighted getSource(){
        return source;
    }

    /**
     *
     * @return destination node
     */
    public NodeWeighted getDestination(){
        return destination;
    }

    /**
     *
     * @return gewicht tussen de twee nodes
     */
    public int getWeight(){
        return weight;
    }

    /**
     *
     * @param weight gewicht tussen twee nodes
     */
    public void setWeight(int weight){
        this.weight = weight;
    }

    /**
     * maakt een string van de edge
     * @return een geformatte string wat de edge omschrijft.
     */
    public String toString(){
        return String.format("(%s -> %s, %d)", source.getName(), destination.getName(), weight);
    }

}
