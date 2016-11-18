package edu.cooper.hack.railfish.backend;

import java.util.*;

public class NodeSet {
    HashMap<String, Node> allNodes = new HashMap<>();

    static class Node {
        HashSet<Node> neighbours = new HashSet<>();
        Node prev;
        // e.g. LEX-14-N
        String nameAndService;
        String name;
        String service;
        Station stationRef;
        Node nextOnLine;
        Node prevOnLine;
        boolean queued;
        double dist;
        @Override public String toString(){
            return nameAndService;
        }
    }
    // ascending means uptown or east
    double calcDirectTime(String service, boolean ascending, int ord1, int ord2){
        if(Character.isDigit(service.charAt(0))){
            // todo overrides
            return 0.5+(1.5*Math.abs(ord2-ord1));
        } else {
            // todo overrides
            return 0.5+(2.5*Math.abs(ord2-ord1));
        }
    }

    static HashMap<String, Integer> costOverrides = new HashMap<>();


    // n1 to n2
    double calcCost(Node n1, Node n2){
        //System.out.println("Eval: "+RouteMap.toSegment(n1, n2));
        if(costOverrides.containsKey(n2.nameAndService+"-"+n1.nameAndService)){
            // This is very what? Inefficient. But if I made it efficient I would need to be crafty.
            // And you will have trouble reading it. And you will what? Cry.
            System.out.println("Overriding: "+n2.nameAndService+"-"+n1.nameAndService+":"+costOverrides.get(n2.nameAndService+"-"+n1.nameAndService));
            return costOverrides.get(n2.nameAndService+"-"+n1.nameAndService);
        }
        if(n1.service.equals("0") || n2.service.equals("0")) return 5.25;
//        if(n1.name.equals("LEX23")&&n2.name.equals("LEX28")) return 9999;
//        if(n2.name.equals("LEX23")&&n1.name.equals("LEX28")) return 9999;
        Line l1 = n1.stationRef.line;
        Line l2 = n2.stationRef.line;
        if(n1.service==n2.service){
            return calcDirectTime(n1.service, n2.stationRef.ordinal, n1.stationRef.ordinal);
        } else if(RouteMap.isSameTrack(n1.service, n2.service)){
            return 8; // 8 minutes between trains
        } else if(l1==l2){
            return 4; // amortized, still sucky.
        } else return RouteMap.transferTimes.get(n1.name+"-"+n2.name);
    }

    private double calcDirectTime(String service, int ord, int ord2) {
        return calcDirectTime(service, ord2>ord, ord, ord2);
    }

    List<Node> pathfind(Node src, Node dst){
        for(Node n : allNodes.values()){
            n.prev = null;
            n.queued = false;
            n.dist = Double.MAX_VALUE;
        }

        PriorityQueue<Node> queue = new PriorityQueue<>((a, b)->Double.compare(a.dist, b.dist));
        queue.add(dst);
        dst.dist = 0;
        while(!queue.isEmpty()){
            Node n = queue.poll();
            if(n==src) break;
            for(Node nd : n.neighbours){
                pathfindCheckAndAdvance(queue, n, nd);
            }
            pathfindCheckAndAdvance(queue, n, n.nextOnLine);
            pathfindCheckAndAdvance(queue, n, n.prevOnLine);

        }
        List<Node> path = new ArrayList<>();
        Node t = src;
        do{
            path.add(t);
            t = t.prev;
            if(t==null) return null;
        } while (t!=dst);

        path.add(dst);
        return path;
    }

    private void pathfindCheckAndAdvance(PriorityQueue<Node> queue, Node n, Node nd) {
        if(n==null||nd==null) return;
        double alt = n.dist + calcCost(n, nd); // THIS IS CONSTANT WHAT? TIME.
        if(alt < nd.dist){// SURPRISED? YOU SHOOOODN'T BE SURPRISED
            if(nd.queued){
                queue.remove(nd); // THIS IS WHAT? OK.
            }
            nd.dist = alt;
            nd.prev = n;
            queue.add(nd);
            nd.queued = true;
        }
    }
}
