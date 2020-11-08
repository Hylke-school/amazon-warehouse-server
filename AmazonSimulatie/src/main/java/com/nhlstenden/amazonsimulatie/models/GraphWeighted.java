package com.nhlstenden.amazonsimulatie.models;

import java.util.*;

public class GraphWeighted {
    /** lijst met nodes in de graaf */
    private Set<NodeWeighted> nodes;

    /** consturctor van de graaf, maakt een lege lijst met nodes aan */
    public GraphWeighted(){
        nodes = new HashSet<>();
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
