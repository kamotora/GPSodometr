package com.practica.gpsodometr.Data.Repository;

import com.practica.gpsodometr.Activities.MainActivity;
import com.practica.gpsodometr.Data.Model.Day;

import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class DayRepository {
    public void add(Date date, double km){
        Realm realm = MainActivity.getRealm();
        realm.beginTransaction();

        Day day = realm.createObject(Day.class);

        day.setDate(date);
        day.setKilometers(km);
        realm.commitTransaction();
    }

    public void update(Day day){

    }

    public void delete(Day day){

    }

    public Day findByDate(Date date){
        return null;
    }

    //Получить статистику начиная с определённого дня
    public List<Day> getDays(Date startDate){
        //TODO: добавить выборку
        return null;
    }
}
