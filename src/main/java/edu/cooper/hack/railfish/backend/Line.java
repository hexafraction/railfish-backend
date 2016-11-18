package edu.cooper.hack.railfish.backend;

public class Line {
    String name;
    Station[] stations;
    public Line(String name, Station... stations){
        this.name = name;
        this.stations = stations;
        int ord = 0;
        for(Station s : this.stations){
            s.ordinal = ord++;
            s.line = this;
        }
    }
}
