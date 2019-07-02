package com.practica.gpsodometr.data.model;

import androidx.annotation.NonNull;

import com.practica.gpsodometr.data.ParseDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.BuildConfig;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 *    Действие
 *    Если начиная с даты dateStart проехали больше, чем kilometers
 *    Пользователя нужно оповестить, что нужно выполнить действие(Нужно заменить масло и т.п.)
*/
public class Action extends RealmObject {
    @Required
    //Название события
    private String name;

    @Required
    //С какой даты начинать отслеживание
    private Date dateStart;

    @Required
    //Нужно проехать км
    private Double kilometers;

    public Action() {
    }

    public Action(String name, Date dateStart, Double kilometers) {
        setDateStart(dateStart);
        setName(name);
        setKilometers(kilometers);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (BuildConfig.DEBUG && !(name.trim().isEmpty())) {
            throw new AssertionError();
        }
        this.name = name;
    }

    @NonNull
    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = ParseDate.parse(dateStart);
    }

    public Double getKilometers() {
        return kilometers;
    }

    public void setKilometers(Double kilometers) {
        if (BuildConfig.DEBUG && !(kilometers >= 0)) {
            throw new AssertionError();
        }
        this.kilometers = kilometers;
    }

    @Override
    public String toString() {
        String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(this.dateStart);
        return name + " " + date + " " + kilometers;
    }

}
