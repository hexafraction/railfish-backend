package edu.cooper.hack.railfish.backend;

import java.util.Arrays;
import java.util.HashSet;

public class Station {
    String desc;
    String name;
    String[] routes;
    NodeSet.Node routeNodes;
    int ordinal;
    Line line;
    public Station(String name, String desc, String... routes) {
        this.name = name;
        this.routes = routes;
        this.desc = desc;
    }

    public static Station st(String name, String desc, String... routes) {
        Station st = new Station(name, desc, routes);
        return st;
    }

    HashSet<Station> transfers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        return name != null ? name.equals(station.name) : station.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
