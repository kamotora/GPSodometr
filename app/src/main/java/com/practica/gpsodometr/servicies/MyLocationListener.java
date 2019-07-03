package com.practica.gpsodometr.servicies;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.activities.MainActivity;

import java.util.Locale;

public class MyLocationListener implements LocationListener {
    private static Location lastLocation = null;
    //Минимальная скорость в м/с

    private static int minSpeed = 0;
    public static final int DEFAULT_MIN_SPEED = 20;
    private static final double MILISECONDS_TO_HOURS = 3.6e+6;
    private static final double METERS_TO_KILOMETERS = 1000;

    private MainActivity mainActivity;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public MyLocationListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;

        //Если скорость не задана ранее, пусть будет по-умолчанию
        if (minSpeed == 0.01) {
            minSpeed = DEFAULT_MIN_SPEED;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (lastLocation == null) {
            lastLocation = location;
        }

        //Считаем скорость
        double deltaTime = (location.getTime() - lastLocation.getTime()) / MILISECONDS_TO_HOURS;
        final double deltaDistance = location.distanceTo(lastLocation) / METERS_TO_KILOMETERS;

        System.out.println(String.format(Locale.getDefault(), "Скорость по рассчётам = %f км/ч; Скорость по gps = %f км/ч; мин скорость = %d км/ч; Расстояние = %f км; Время = %f ч"
                , deltaDistance / deltaTime, location.getSpeed(), minSpeed, deltaDistance, deltaTime));

        //Если скорость больше, прибавляем пройденное расстояние
        if (deltaDistance / deltaTime > minSpeed) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mainActivity.showDistance(deltaDistance);
                }
            });
        }
        lastLocation = location;
    }

    @Override
    @Deprecated
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Msg.showMsg(provider + " включен");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Msg.showMsg(provider + " отключён");
    }


    public static void setMinSpeed(int minSpeed) {
        MyLocationListener.minSpeed = minSpeed;
    }

    public static int getMinSpeed() {
        return minSpeed;
    }
}
