package com.practica.gpsodometr.data.repository;

import com.practica.gpsodometr.data.ParseDate;
import com.practica.gpsodometr.data.model.Stat;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class StatRep {
    public static void add(Stat stat) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(stat);
        realm.commitTransaction();
    }

    public static void updateKm(Stat stat, final double newKm) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        stat.setKilometers(newKm);
        realm.commitTransaction();
    }

    public static void delete(Stat stat) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        stat.deleteFromRealm();
        realm.commitTransaction();
    }

    /**
     * Найти информацию за день @param date
     *
     * @return найденная информация или null
     */
    public static Stat findByDate(Date date) {
        Realm realm = Realm.getDefaultInstance();
        date = ParseDate.parse(date);
        return realm.where(Stat.class).equalTo("date", date)
                .findFirst();
    }


    /**
     * Получить статистику начиная с определённого дня date
     *
     * @return список информации по каждому дню,начиная с date
     */
    public static RealmResults<Stat> getDays(Date date) {
        Realm realm = Realm.getDefaultInstance();

        date = ParseDate.parse(date);
        if (date == null)
            return null;
        RealmQuery<Stat> query = realm.where(Stat.class).greaterThanOrEqualTo("date", date);

        return query.findAll();
    }
}
