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
     * Поиск действия action в бд
     *
     * @return если нашли, управляемый объект Action, иначе null
     */
    public static Action findAction(Action action) {
        Realm realm = Realm.getDefaultInstance();
        //return realm.copyFromRealm(action);
        return realm.where(Action.class).equalTo("dateStart", action.getDateStart()).and().equalTo("kilometers", action.getKilometers()).and().equalTo("name", action.getName()).findFirst();
    }

    /**
     * Для каждого действия Action посчитать кол-во километров, которое осталось для его наступления
     * Если действие будет отслеживаться в будущем(позже, чем сегодня), добавлять в res не будем
     *
     * @return список из пар, где Action - действие, Double - оставшееся кол-во км. Если дейстий нет, null
     */
    public static ConcurrentHashMap<Action, Double> countForEveryKilometersLeft() {
        RealmResults<Action> actions = Realm.getDefaultInstance().where(Action.class).findAll().sort("dateStart");
        if (actions.isEmpty())
            return null;

        //Получить статистику, начиная с самой прошлой даты
        //Первая дата - самая ближайшая
        //Первое дейстие - самое старое(дата начала отслеживания самая маленькая)
        RealmResults<Stat> statistics = StatRep.getDays(actions.get(0).getDateStart()).sort("date", Sort.DESCENDING);
        ConcurrentHashMap<Action, Double> res = new ConcurrentHashMap<>();

        Double sum = 0.0;
        for (int i = actions.size() - 1, j = 0; i >= 0; i--) {
            Action action = actions.get(i);
            //Если action должен отслеживаться в будущем, переходим к следующему
            if (action.getDateStart().after(new Date()))
                continue;
            try {
                //Если дата действия <= даты из статистики
                //Прибавляем к сумме и переходим к следующему дню(более раннему)
                while (j < statistics.size() && !statistics.get(j).getDate().before(action.getDateStart()))
                    sum += statistics.get(j++).getKilometers();
            } catch (ArrayIndexOutOfBoundsException exp) {
                System.out.println("Ошибка в " + ActionRep.class + " \nExcept = " + exp);
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
        RealmResults<Stat> stats = StatRep.getDays(action.getDateStart());
        if (stats == null)
            return null;
        for (Stat stat : stats) {
            sum += stat.getKilometers();
        }
        return action.getKilometers() - sum;
    }

    public static RealmResults<Action> getAll() {
        return Realm.getDefaultInstance().where(Action.class).findAll();
    }
}
