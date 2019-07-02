package com.practica.gpsodometr.data.repository;


import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.model.Stat;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

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

    public static void delete(Action action) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        action.deleteFromRealm();
        realm.commitTransaction();
    }

    /**
     * Для каждого действия Action посчитать кол-во километров, которое осталось для его наступления
     * Если действие будет отслеживаться в будущем(позже, чем сегодня), добавлять в res не будем
     *
     * @return список из пар, где Action - действие, Double - оставшееся кол-во км. Если событий нет, null
     */
    public static ConcurrentHashMap<Action, Double> countForEveryKilometersLeft() {
        RealmResults<Action> actions = Realm.getDefaultInstance().where(Action.class).findAll().sort("dateStart");
        if (actions.isEmpty())
            return null;
        //Получить статистику, начиная с самой прошлой даты
        //Первая дата - самая ближайшая
        RealmResults<Stat> statistics = StatRep.getDays(actions.get(0).getDateStart()).sort("date", Sort.DESCENDING);
        ConcurrentHashMap<Action, Double> res = new ConcurrentHashMap<>();

        Double sum = 0.0;
        for (int i = actions.size() - 1, j = 0; i >= 0; i--) {
            Action action = actions.get(i);
            if (action.getDateStart().after(new Date()))
                continue;
            try {
                //Если отсчёт события ведется с более ранней даты
                //Прибавляем к сумме и переходим к следующему дню(более раннему)
                while (j < statistics.size() && !statistics.get(j).getDate().before(action.getDateStart()))
                    sum += statistics.get(j++).getKilometers();
            } catch (ArrayIndexOutOfBoundsException exp) {
                //TODO:...
            }
            res.put(action, action.getKilometers() - sum);
        }
        return res;
    }

    /**
     * Для 1 действия Action посчитать кол-во километров, которое осталось для наступления события
     * Если событие будет отслеживаться в будущем(позже, чем сегодня), вернем null
     */
    public static Double countForOneAction(Action action) {
        if (action == null || action.getDateStart().after(new Date()))
            return null;

        Double sum = 0.0;
        for (Stat stat : StatRep.getDays(action.getDateStart())) {
            sum += stat.getKilometers();
        }
        return sum;
    }
}
