package com.practica.gpsodometr.Data.Model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Day extends RealmObject {
    @Required
    private Date date;
    private double kilometers;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getKilometers() {
        return kilometers;
    }

    public void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }
}
