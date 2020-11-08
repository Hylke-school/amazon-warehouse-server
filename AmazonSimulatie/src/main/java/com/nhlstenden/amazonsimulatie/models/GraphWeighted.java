package com.nhlstenden.amazonsimulatie.models;

import java.util.*;

public class GraphWeighted {
    private Set<NodeWeighted> nodes;

    public GraphWeighted(){
        nodes = new HashSet<>();
    }

    public void addEdge(NodeWeighted source, NodeWeighted destination, int weight){
        nodes.add(source);
        nodes.add(destination);

        addEdgeHelper(source, destination, weight);

        if (source != destination){
            addEdgeHelper(destination, source, weight);
        }
    }

    public void addEdgeHelper(NodeWeighted a, NodeWeighted b, int weight){
        for (EdgeWeighted edge : a.getEdges()){
            if (edge.getSource() == a && edge.getDestination() == b){
                edge.setWeight(weight);
                return;
            }
        }

        a.getEdges().add(new EdgeWeighted(a, b, weight));
    }


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
                return path;
            }

            if (currentNode == end){
                printPath(path, shortestPathMap, start, end);
                unvisitNodes();
                return path;
            }
            currentNode.visit();

            for (EdgeWeighted edge: currentNode.getEdges()) {
                if (edge.getDestination().isVisited()){
                    continue;
                }

                if (shortestPathMap.get(currentNode) + edge.getWeight() < shortestPathMap.get(edge.getDestination())){
                    shortestPathMap.put(edge.getDestination(), shortestPathMap.get(currentNode) + edge.getWeight());
                    path.put(edge.getDestination(), currentNode);
                }
            }
        }
    }

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

    private void printPath(HashMap<NodeWeighted, NodeWeighted> path, HashMap<NodeWeighted, Integer> shortestPathMap, NodeWeighted start, NodeWeighted end){
        System.out.println("The path with the smallest weight between " + start.getName() + " and " + end.getName() + " is:");

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
        System.out.println("the path costs: " + shortestPathMap.get(end));
    }

    private void unvisitNodes(){
        for (NodeWeighted node : nodes){
            node.unvisit();
        }
    }

}
