package com.practica.gpsodometr.servicies;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;

import com.practica.gpsodometr.MyNotification;
import com.practica.gpsodometr.activities.MainActivity;
import com.practica.gpsodometr.activities.SettingsActivity;
import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.model.Stat;
import com.practica.gpsodometr.data.repository.ActionRep;
import com.practica.gpsodometr.data.repository.StatRep;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.Realm;

public class MyApplication extends Application {
    //Обработчик событий от gps
    private MyLocationListener locationListener = null;
    private LocationManager locationManager = null;

    //Action - действие, Double - оставшееся кол-во км.
    private ConcurrentHashMap<Action, Double> actionsAndKm = null;


    //Кол-во километров на сегодня
    private double kilometers = 0;
    //запись из бд на сегодня
    private Stat todayStat = null;

    private MainActivity mainActivity = null;
    private SettingsActivity settingsActivity = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        //Работа с гпс
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);

        //Если есть сохранённая минимальная скорость
        //Сообщаем это MyLocationListener
        SharedPreferences mSettings = getSharedPreferences(SettingsActivity.SETTING_FILENAME, Context.MODE_PRIVATE);
        if (mSettings.contains(SettingsActivity.SETTING_MINSPEED_NAME)) {
            locationListener.setMinSpeed(mSettings.getInt(SettingsActivity.SETTING_MINSPEED_NAME, MyLocationListener.DEFAULT_MIN_SPEED));
        }


        //Получаем список всех отслеживаемых действий и сколько осталось км
        actionsAndKm = ActionRep.countForEveryKilometersLeft();
        //Проверяем, вдруг есть сохранённая информация на сегодня

        if (todayStat == null) {
            todayStat = StatRep.findByDate(new Date());
        }
        if (todayStat != null) {
            kilometers = todayStat.getKilometers();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public MyLocationListener getLocationListener() {
        return locationListener;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public ConcurrentHashMap<Action, Double> getActionsAndKm() {
        return actionsAndKm;
    }

    public Stat getTodayStat() {
        return todayStat;
    }

    /**
     * Обновление пройденной дистанции
     * Сообщить MainActivity, что нужно отобразить дистанцию
     * Пересчёт, сколько осталось километров,  для каждой остлеживаемой работы actionsAndKm
     */
    public void addDistance(double deltaDistance) {
        kilometers += deltaDistance;
        if (todayStat == null) {
            todayStat = new Stat(kilometers);
            StatRep.add(todayStat);
        } else
            StatRep.updateKm(todayStat, kilometers);

        if (mainActivity != null)
            mainActivity.showDistance(kilometers);

        // Пересчитаем, сколько осталось км
        if (actionsAndKm == null || actionsAndKm.isEmpty())
            return;
        for (Action key : actionsAndKm.keySet()) {
            Double newValue = actionsAndKm.get(key);
            if (newValue == null) {
                actionsAndKm.remove(key);
                continue;
            }
            newValue -= deltaDistance;
            actionsAndKm.put(key, newValue);

            if (newValue <= 0) {
                MyNotification.getInstance(this).show(key);
                //Перестаём отслеживать, т.к. уже проехали столько, сколько нужно
                actionsAndKm.remove(key);
            }
        }

    }

    //Ин-фа за сегодня была удалена
    public void todayStatWasDeleted() {
        todayStat = null;
        kilometers = 0;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public SettingsActivity getSettingsActivity() {
        return settingsActivity;
    }

    public void setSettingsActivity(SettingsActivity settingsActivity) {
        this.settingsActivity = settingsActivity;
    }
}
