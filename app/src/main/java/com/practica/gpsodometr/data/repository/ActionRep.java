package com.practica.gpsodometr.data.repository;


import android.util.Pair;

import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.model.Stat;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ActionRep {
    public static void add(Action action) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(action);
        realm.commitTransaction();
    }

    public static RealmResults<Action> findAll() {
        return Realm.getDefaultInstance().where(Action.class).findAll();
    }

    public static List<Pair<Action, Double>> countForEveryKilometersLeft() {
        RealmResults<Action> actions = findAll().sort("dateStart");
        if (actions.isEmpty())
            return null;
        //Получить статистику, начиная с самой прошлой даты
        //Первая дата - самая ближайшая
        RealmResults<Stat> statistics = StatRep.getDays(actions.get(0).getDateStart()).sort("date", Sort.DESCENDING);
        List<Pair<Action, Double>> res = new ArrayList<>();

        Double sum = 0.0;
        for (int i = actions.size() - 1, j = 0; i >= 0; i--) {
            Action action = actions.get(i);
            try {
                while (!statistics.get(j).getDate().before(action.getDateStart()))
                    sum += statistics.get(j++).getKilometers();
            } catch (ArrayIndexOutOfBoundsException exp) {
                //TODO:...
            }
            res.add(new Pair<Action, Double>(action, action.getKilometers() - Double.valueOf(sum)));
        }
        return res;
    }
}
