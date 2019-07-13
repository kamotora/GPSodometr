package com.practica.gpsodometr.servicies;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.practica.gpsodometr.Log;

import java.util.Date;
import java.util.Locale;

public class MyLocationListener implements LocationListener {
    private static Location lastLocation = null;
    //Минимальная скорость в м/с

    private int minSpeed = 0;
    public static final int DEFAULT_MIN_SPEED = 20;
    private static final double MILISECONDS_TO_HOURS = 3.6e+6;
    private static final double METERS_TO_KILOMETERS = 1000;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private MyApplication context;

    public MyLocationListener(MyApplication context) {
        this.context = context;
        //Если скорость не задана ранее, пусть будет по-умолчанию
        if (minSpeed == 0) {
            minSpeed = DEFAULT_MIN_SPEED;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (lastLocation == null) {
            lastLocation = location;
        }

        //Считаем скорость
        final double deltaTime = (location.getTime() - lastLocation.getTime()) / MILISECONDS_TO_HOURS;
        final double deltaDistance = location.distanceTo(lastLocation) / METERS_TO_KILOMETERS;
        final double curSpeed = deltaDistance / deltaTime;
        if (Double.isNaN(curSpeed))
            return;
        Log.v(String.format(Locale.getDefault(), "Время : %s Скорость по рассчётам = %f км/ч; Скорость по gps = %f км/ч; мин скорость = %d км/ч; Расстояние = %f км; Время = %f ч"
                , new Date(location.getTime()), curSpeed, location.getSpeed(), minSpeed, deltaDistance, deltaTime));

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                context.printCurSpeed(curSpeed);
            }
        });

        //Если скорость больше, прибавляем пройденное расстояние
        if (curSpeed > minSpeed) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    context.addDistance(deltaDistance);
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
        //Message.showMsg(provider + " включен");
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Message.showMsg(provider + " отключён");
    }


    public void setMinSpeed(int minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getMinSpeed() {
        return minSpeed;
    }
}
