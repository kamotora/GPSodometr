package com.practica.gpsodometr.data.model;

public class PairActionAndKilometers {
    public final Action action;
    public Double leftKilometers;

    public PairActionAndKilometers(Action action, Double leftKilometers) {
        this.action = action;
        this.leftKilometers = leftKilometers;
    }

}
