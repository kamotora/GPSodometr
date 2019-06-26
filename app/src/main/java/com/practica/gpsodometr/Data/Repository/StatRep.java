package com.practica.gpsodometr.Data.Repository;

import com.practica.gpsodometr.Activities.MainActivity;
import com.practica.gpsodometr.Data.Model.Stat;

import java.time.LocalDate;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class StatRep {
    public static void add(Stat stat) {
        Realm realm = MainActivity.getRealm();
        realm.beginTransaction();
        realm.insert(stat);
        realm.commitTransaction();
    }

    public static void updateKm(Stat stat, final double newKm) {
        Realm realm = MainActivity.getRealm();
        realm.beginTransaction();
        stat.setKilometers(newKm);
        realm.commitTransaction();
    }

    public static void delete(Stat stat) {
        Realm realm = MainActivity.getRealm();
        realm.beginTransaction();
        stat.deleteFromRealm();
        realm.commitTransaction();
    }

    //Поиск по дд.мм.гг
    public static Stat findByDate(LocalDate date) {
        Realm realm = MainActivity.getRealm();
        int curYear = date.getYear();
        int curMonth = date.getMonthValue();
        int curDay = date.getDayOfMonth();
        Stat res = realm.where(Stat.class).equalTo("year", curYear)
                .and().equalTo("month", curMonth)
                .and().equalTo("day", curDay)
                .findFirst();
        return res;
    }

    public static Stat getTodayStat() {
        LocalDate todayDate = LocalDate.now();
        return findByDate(todayDate);
    }

    //Получить статистику начиная с определённого дня
    public RealmResults<Stat> getDays(LocalDate date) {
        Realm realm = MainActivity.getRealm();
        int curYear = date.getYear();
        int curMonth = date.getMonthValue();
        int curDay = date.getDayOfMonth();

        //select from stat where year > curYear or (year = curYear and month > curMonth or (month = curMonth and day >= curDay));
        RealmQuery<Stat> query = realm.where(Stat.class).greaterThan("year", curYear).or();
        query.beginGroup().equalTo("year", curYear).and().greaterThan("month", curMonth).or();
        query.beginGroup().equalTo("month", curMonth).and().greaterThanOrEqualTo("day", curDay).endGroup().endGroup();

        RealmResults<Stat> res = query.findAll();
        return res;
    }
}
