package com.practica.gpsodometr.data.repository;

import com.practica.gpsodometr.activities.MainActivity;
import com.practica.gpsodometr.data.ParseDate;
import com.practica.gpsodometr.data.model.Stat;

import java.util.Date;

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
    public static Stat findByDate(Date date) {
        Realm realm = MainActivity.getRealm();
        date = ParseDate.parse(date);
        Stat res = realm.where(Stat.class).equalTo("date", date)
                .findFirst();
        return res;
    }


    //Получить статистику начиная с определённого дня
    public static RealmResults<Stat> getDays(Date date) {
        Realm realm = MainActivity.getRealm();

        date = ParseDate.parse(date);

        RealmQuery<Stat> query = realm.where(Stat.class).greaterThanOrEqualTo("date", date);

        return query.findAll();
    }
}
