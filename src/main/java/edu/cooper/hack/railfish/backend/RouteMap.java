package edu.cooper.hack.railfish.backend;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static edu.cooper.hack.railfish.backend.Station.st;

public class RouteMap {

    static Line[] lines;
    static HashMap<String, Integer> transferTimes = new HashMap<>();

    static HashMap<String, Station> stations = new HashMap<>();
    static Line lexington = new Line("LEX", st("LEXSPRING", "6"), st("LEXBLEECKER", "6"), st("LEXASTOR", "6"), st("LEX14", "4", "5", "6"), st("LEX23", "6"),
            st("LEX28", "6"), st("LEX33", "6"), st("LEX42", "4", "5", "6"));
    static Line canarsie = new Line("CAN", st("CAN8", "L"), st("CAN6", "L"), st("CANUNION", "L"));
    static Line eighth = new Line("8TH", st("8THSPRING", "C", "E"), st("8THW4", "A", "C", "E"), st("8TH14", "A", "C", "E"), st("8TH23", "C", "E"),
            st("8TH34", "A", "C", "E"), st("8TH42", "A", "C", "E"), st("8TH50", "C", "E"));
    static Line seventh = new Line("7TH", st("7THHOUSTON", "1"), st("7THCHRISTOPHER", "1"), st("7TH14", "1", "2", "3"), st("7TH18", "1"),
            st("7TH23", "1"), st("7TH28", "1"), st("7TH34", "1", "2", "3"), st("7TH42", "1", "2", "3"), st("7TH50", "1", "2", "3"));
    static Line sixth = new Line("6TH", st("6THLAFAYETTE", "B", "D", "F", "M"), st("6THW4", "B", "D", "F", "M"), st("6TH14", "F", "M"),
            st("6TH23", "F", "M"), st("6TH34", "B", "D", "F", "M"), st("6TH42", "B", "D", "F", "M"), st("6TH47", "B", "D", "F", "M"));
    static Line broadway = new Line("BWY", st("BWYPRINCE", "R", "W"), st("BWY8", "R", "W"), st("BWY14", "N", "Q", "R", "W"), st("BWY23", "R", "W"),
            st("BWY28", "R", "W"), st("BWY34", "N", "Q", "R", "W"), st("BWY42", "N", "Q", "R", "W"), st("BWY49", "N", "Q", "R", "W"));
    static Line flushing = new Line("FLU", st("FLU34", "7"), st("FLUTIMES", "7"), st("FLU5", "7"), st("FLUGRAND", "7"));
    static Line shuttle = new Line("SHU", st("SHUTIMES", "S"), st("SHUGRAND", "S"));

    private static HashSet<String> sameTrackTransfers = new HashSet<>();

    static NodeSet nst = new NodeSet();
    static HashMap<String, String[]> multisets = new HashMap<>();
    static {
        lines = new Line[]{lexington, canarsie, eighth, seventh, sixth, broadway, flushing, shuttle};
        buildMaps();

        transfer("LEX14", "CANUNION");
        transfer("LEXBLEECKER", "6THLAFAYETTE", 7);
        transfer("LEX42", "SHUGRAND");
        transfer("LEX42", "FLUGRAND");
        transfer("6TH42", "FLU5", 10);
        transfer("6TH14", "CAN6");
        transfer("CAN6", "6TH14");
        transfer("7TH42", "BWY42");
        transfer("7TH42", "SHUTIMES");
        transfer("7TH42", "FLUTIMES");
        transfer("7TH14", "CAN6", 7);
        transfer("BWY42", "SHUTIMES");
        transfer("BWY42", "FLUTIMES");
        transfer("BWY34", "6TH34");
        transfer("BWY14", "LEX14");
        transfer("BWY14", "CANUNION");
        transfer("8THW4", "6THW4");
        transfer("8TH14", "CAN8");
        transfer("8TH42", "7TH42", 10);
        transfer("8TH42", "BWY42", 10);
        transfer("8TH42", "SHUTIMES", 10);
        transfer("8TH42", "FLUTIMES", 10);
        transfer("SHUTIMES", "FLUTIMES");
        transfer("SHUGRAND", "FLUGRAND");

        multiset("COOPER", "BWY80", "LEXASTOR0");
        multiset("PENN", "8TH340", "7TH340");
        multiset("UNIONSQ", "LEX140", "BWY140", "CANUNION");
        multiset("14TH8TH", "8TH140", "CAN80");
        multiset("WASHSQ", "6THW40", "8THW40");
        multiset("GCT", "LEX420", "FLUGRAND", "SHUGRAND");
        multiset("HERALDSQ", "6TH340", "BWY340");
        multiset("TIMESSQ", "7TH420", "BWY420", "SHUTIMES", "FLUTIMES");
        multiset("14TH6TH", "6TH140", "CAN60");
        multiset("LAFLEX", "6THLAFAYETTE", "LEXBLEECKER");

        sameTrackTransfers.add("45");
        sameTrackTransfers.add("54");
        sameTrackTransfers.add("CE");
        sameTrackTransfers.add("EC");
        sameTrackTransfers.add("23");
        sameTrackTransfers.add("32");
        sameTrackTransfers.add("BD");
        sameTrackTransfers.add("DB");
        sameTrackTransfers.add("FM");
        sameTrackTransfers.add("MF");
        sameTrackTransfers.add("NQ");
        sameTrackTransfers.add("QN");
        sameTrackTransfers.add("RW");
        sameTrackTransfers.add("WR");
        buildNodeSet();
        //printSummary();
    }


    private static void multiset(String placename, String... stations) {
        multisets.put(placename, stations);
    }


    static void printSummary() {
        System.out.println("===PRINTING ROUTE SUMMARY===");
        for (Line l : lines) {
            System.out.println(String.format("%s line:", l.name));
            System.out.println(String.format("%d stop(s):", l.stations.length));
            for (Station s : l.stations) {
                System.out.println(String.format("* %s (%s)%s", s.name, StringUtils.join(s.routes, '•'),
                        s.transfers.isEmpty() ? "" : ", transfer to " + StringUtils.join(s.transfers.stream().map(t -> t.name).toArray(), ", ")));
            }

            System.out.println();
            System.out.println();
        }
    }

    static void buildMaps() {
        for (Line l : lines) {
            for (Station s : l.stations) {
                stations.put(s.name, s);
            }
        }
    }

    public static void main(String[] args) {
        printSummary();
        List<NodeSet.Node> path = pathfind("COOPER", "PENN");
        System.out.println(evalCost(path)+":"+path);
        System.out.println(getJSON("PENN", "COOPER"));
        System.out.println(getJSON("LEXASTOR0", "LEX230"));
        NodeSet.costOverrides.put("BWY14R-BWY23R", 4000);
        NodeSet.costOverrides.put("BWY23W-BWY28W", 4000);
        System.out.println(getJSON("BWY140", "BWY280"));
    }

    static List<NodeSet.Node> pathfind(String src, String dst){
        String[] srcs = multisets.getOrDefault(src, new String[]{src});
        String[] dsts = multisets.getOrDefault(dst, new String[]{dst});
        double bestCost = Double.MAX_VALUE;
        List<NodeSet.Node> best = null;
        for(String src_ : srcs){
            for(String dst_ : dsts){
                List<NodeSet.Node> candidate = nst.pathfind(nst.allNodes.get(src_), nst.allNodes.get(dst_));
                double cost = evalCost(candidate);
                if(cost<bestCost){
                    bestCost = cost;
                    best = candidate;
                }
            }
        }
        return best;
    }

    static String getJSON(String src, String dst) {
        try {
            dst = dst.trim();
            src = src.trim();
            List<NodeSet.Node> path = RouteMap.pathfind(src, dst);
            StringBuilder json = new StringBuilder();
            json.append("{\"result\":\"ok\", \"error\":\"\", \"path\" : [");
            ArrayList<String> elements = new ArrayList<>();
            for (int i = 1; i < path.size(); i++) {

                NodeSet.Node prev = path.get(i - 1);
                NodeSet.Node cur = path.get(i);
                elements.add(toSegment(prev, cur));
            }
            json.append(StringUtils.join(elements, ","));
            json.append("]}");
            return json.toString();
        } catch(Exception e){
            return String.format("{\"result\":\"error\", \"error\":\"%s\"}",
                    StringEscapeUtils.escapeJavaScript(e.toString()+":"+e.getMessage()+"//"+
                            StringUtils.join(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toArray(), "//")));
        }
    }

    static String toSegment(NodeSet.Node prev, NodeSet.Node cur) {
        if(prev.stationRef!=null && cur.stationRef!=null && prev.stationRef.line==cur.stationRef.line){
            if(prev.stationRef.ordinal > cur.stationRef.ordinal){
                NodeSet.Node temp = cur;
                cur = prev;
                prev = temp;
            }
        } else if(prev.stationRef != null && cur.stationRef != null){
            if(prev.stationRef.line.name.compareTo(cur.stationRef.line.name)>0){
                NodeSet.Node temp = cur;
                cur = prev;
                prev = temp;
            }
        }
        return "\"" + prev.name + "-" + cur.name + "-" + (cur.service.equals(prev.service) ? cur.service : "X") + "\"";
    }

    private static double evalCost(List<NodeSet.Node> candidate) {
        double sum = 0;
        for(int i = 1; i < candidate.size(); i++){
            sum+=nst.calcCost(candidate.get(i-1), candidate.get(i));
        }
        return sum;
    }

    static void transfer(String st1, String st2, int time) {
        transferTimes.put(st1 + "-" + st2, time);
        transferTimes.put(st2 + "-" + st1, time);
        stations.get(st1).transfers.add(stations.get(st2));
        stations.get(st2).transfers.add(stations.get(st1));
    }

    static void transfer(String st1, String st2) {
        transfer(st1, st2, 5);
    }


    static void buildNodeSet() {
        for (Line l : lines) {
            // do a front to back walk.
            HashSet<String> services = new HashSet<>();
            for (Station s : l.stations) {
                for (String svc : s.routes) {
                    services.add(svc);
                }
            }
            for (String svc : services) {
                System.err.println("Building the linkages for " + svc);
                NodeSet.Node prev = null;
                NodeSet.Node cur = null;
                for (Station s : l.stations) {
                    if (Arrays.stream(s.routes).anyMatch(pSvc -> pSvc.equals(svc))) {

                        boolean isBMT = Character.isAlphabetic(svc.charAt(0));
                        prev = cur;
                        cur = new NodeSet.Node();
                        cur.nameAndService = s.name + svc;
                        System.err.println("Storing " + cur.nameAndService);
                        cur.name = s.name;
                        cur.service = svc;
                        cur.stationRef = s;
                        nst.allNodes.put(cur.nameAndService, cur);
                        if (prev != null) {
                            //prev.neighbours.add(cur);
                            //cur.neighbours.add(prev);
                            prev.nextOnLine = cur;
                            cur.prevOnLine = prev;
                        }
                    }
                }
            }
        }
        for (Line l : lines) {
            for (Station s : l.stations) {
                crossLinkStations(s, s);
            }
        }
        for (Line l : lines) {
            for (Station s : l.stations) {
                for (Station txd : s.transfers) {
                    crossLinkStations(s, txd);
                }
            }
        }

        for(Line l : lines){
            for(Station s : l.stations){
                NodeSet.Node stationNode = new NodeSet.Node();
                stationNode.name = s.name;
                stationNode.service = "0";
                stationNode.nameAndService = s.name+"0";
                nst.allNodes.put(s.name+"0", stationNode);
                for(String service : s.routes){
                    NodeSet.Node nd = nst.allNodes.get(s.name+service);
                    nd.neighbours.add(stationNode);
                    stationNode.neighbours.add(nd);
                }
            }
        }
    }

    private static void crossLinkStations(Station s1, Station s2) {
        for (String sv1 : s1.routes) {
            for (String sv2 : s2.routes) {
                NodeSet.Node n1 = nst.allNodes.get(s1.name + sv1);
                NodeSet.Node n2 = nst.allNodes.get(s2.name + sv2);
                n1.neighbours.add(n2);
                n2.neighbours.add(n1);
            }
        }
    }


    static boolean isSameTrack(String sv, String sv2) {
        String c1 = sv + sv2;
        return (sameTrackTransfers.contains(c1));
    }


}

