package com.nhlstenden.amazonsimulatie.controllers;

import com.nhlstenden.amazonsimulatie.models.EdgeWeighted;
import com.nhlstenden.amazonsimulatie.models.NodeWeighted;

import java.util.*;

public class GraphController {
    /** lijst met nodes in de graaf */
    private Set<NodeWeighted> nodes;

    /** lijst met nodes in een array */
    private NodeWeighted[][] nodeArray = new NodeWeighted[32][32];

    /** constructor van de graaf, maakt een lege lijst met nodes aan */
    public GraphController(){
        nodes = new HashSet<>();
    }

    /** @return de array van nodes */
    public NodeWeighted[][] getNodeArray(){
        return nodeArray;
    }

    /** een functie die gebruikt wordt om de node graaf te maken */
    public void populateGraph(){
        for (int i = 2; i <= 31; i++){
            for (int j = 2; j <= 31; j++){
                this.nodeArray[i][j] = new NodeWeighted("(" + i + ";" + j + ")", i, j);
            }
        }

        for (int i = 2; i <= 31; i++){
            for (int j = 2; j <= 31; j++){
                if (i == 2 || i == 5 || i == 8|| i == 11|| i == 14|| i == 17|| i == 20 || (i >=23 && i<= 30)){
                    this.addEdge(nodeArray[i][j], nodeArray[i+1][j], 1);
                }
                if (!(j == 3 || j == 6 || j == 9 || j == 12 || j == 15|| j == 18 || j == 21 || j == 24) && j <= 30){
                    this.addEdge(nodeArray[i][j], nodeArray[i][j+1], 1);
                }
            }
        }
    }

    /**
     * voegt een edge toe aan de graaf tussen twee nodes
     * @param source node A.
     * @param destination node B.
     * @param weight gewicht/afstand tussen twee nodes.
     */
    public void addEdge(NodeWeighted source, NodeWeighted destination, int weight){
        nodes.add(source);
        nodes.add(destination);

        addEdgeHelper(source, destination, weight);

        if (source != destination){
            addEdgeHelper(destination, source, weight);
        }
    }

    /**
     * helpt de addEdge functie door de edges toe te voegen aan de lijst van de nodes.
     * @param a begin node
     * @param b eind node
     * @param weight gewicht/afstand tussen nodes
     */
    public void addEdgeHelper(NodeWeighted a, NodeWeighted b, int weight){
        for (EdgeWeighted edge : a.getEdges()){
            if (edge.getSource() == a && edge.getDestination() == b){
                edge.setWeight(weight);
                return;
            }
        }

        a.getEdges().add(new EdgeWeighted(a, b, weight));
    }

    /**
     * dijkstra algoritme om het kortste pad tussen twee nodes te vinden
     * @param start start node
     * @param end eind node
     * @return een hashmap met het kortste pad tussen de twee gegeven nodes
     */
    public HashMap<NodeWeighted, NodeWeighted> dijkstraShortestPath(NodeWeighted start, NodeWeighted end) {
        HashMap<NodeWeighted, NodeWeighted> path = new HashMap<>();
        path.put(start, null);

        HashMap<NodeWeighted, Integer> shortestPathMap = new HashMap<>();

        for (NodeWeighted node : nodes) {
            if (node == start){
                shortestPathMap.put(start, 0);
            } else {
                shortestPathMap.put(node, Integer.MAX_VALUE);
            }
        }
        for (EdgeWeighted edge : start.getEdges()) {
            shortestPathMap.put(edge.getDestination(), edge.getWeight());
            path.put(edge.getDestination(), start);
        }

        start.visit();

        while (true) {
            NodeWeighted currentNode = closestReachableUnvisited(shortestPathMap);

            if (currentNode == null){
                unvisitNodes();
                return path;
            }

            if (currentNode == end){
                printPath(path, shortestPathMap, start, end);
                unvisitNodes();
                return path;
            }
            currentNode.visit();

            for (EdgeWeighted edge: currentNode.getEdges()) {
                if (edge.getDestination().isVisited() || edge.getDestination().isOccupied()){
                    continue;
                }

                if (shortestPathMap.get(currentNode) + edge.getWeight() < shortestPathMap.get(edge.getDestination())){
                    shortestPathMap.put(edge.getDestination(), shortestPathMap.get(currentNode) + edge.getWeight());
                    path.put(edge.getDestination(), currentNode);
                }
            }
        }
    }

    /**
     * een functie om de node te vinden die het dichtste bij is en ook berijkbaar is.
     * @param shortestPathMap map met alle nodes
     * @return de node die het dichtste bij is, berijkbaar is en niet bezet is.
     */
    private NodeWeighted closestReachableUnvisited(HashMap<NodeWeighted, Integer> shortestPathMap){

        int shortestDistance = Integer.MAX_VALUE;
        NodeWeighted closestReachableNode = null;

        for (NodeWeighted node : nodes){
            if (node.isVisited() || node.isOccupied()){
                continue;
            }

            int currentDistance = shortestPathMap.get(node);
            if (currentDistance == Integer.MAX_VALUE){
                continue;
            }

            if (currentDistance < shortestDistance){
                shortestDistance = currentDistance;
                closestReachableNode= node;
            }
        }
        return closestReachableNode;
    }

    /**
     * print het kortste pad dat tussen twee nodes zit
     * @param path het pad van nodes tussen start en eind
     * @param shortestPathMap de mpa met nodes en hun kosten
     * @param start begin node
     * @param end eind node
     */
    private void printPath(HashMap<NodeWeighted, NodeWeighted> path, HashMap<NodeWeighted, Integer> shortestPathMap, NodeWeighted start, NodeWeighted end){
        System.out.println("het pad met de kortste afstand tussen " + start.getName() + " en " + end.getName() + " is:");

        NodeWeighted child = end;
        String nodePath = end.getName();
        while (true){
            NodeWeighted parent = path.get(child);
            if (parent == null){
                break;
            }
            nodePath = parent.getName() + " " + nodePath;
            child = parent;
        }
        System.out.println(nodePath);
        System.out.println("dit pad kost: " + shortestPathMap.get(end));
    }

    /**
     * een functie om alle nodes naar unvisited te zetten na het dijkstra algoritme.
     */
    private void unvisitNodes(){
        for (NodeWeighted node : nodes){
            node.unvisit();
        }
    }

}
