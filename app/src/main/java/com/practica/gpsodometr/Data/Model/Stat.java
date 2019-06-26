package com.practica.gpsodometr.Data.Model;

import io.realm.BuildConfig;
import io.realm.RealmObject;
import io.realm.annotations.Required;


//Статистика пройденных км за день
public class Stat extends RealmObject {

    @Required
    private Integer year;
    @Required
    private Integer month;
    @Required
    private Integer day;
    private Double kilometers;

    public Stat() {
    }

    public Stat(Integer year, Integer month, Integer day, Double kilometers) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.kilometers = kilometers;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        if (BuildConfig.DEBUG && !(month > 0 && month < 13)) {
            throw new AssertionError();
        }
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        if (BuildConfig.DEBUG && !(day > 0 && day < 32)) {
            throw new AssertionError();
        }
        this.day = day;
    }

    public Double getKilometers() {
        return kilometers;
    }

    public void setKilometers(Double kilometers) {
        this.kilometers = kilometers;
    }

    @Override
    public String toString() {
        return String.format("Дата: %d:%d:%d, километры = %f", day, month, year, kilometers);
    }
}
