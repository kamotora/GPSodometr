package com.practica.gpsodometr.servicies;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.activities.MainActivity;

public class MyLocationListener implements LocationListener {
    private double kilometers = 0;
    private static Location lastLocation = null;
    //Минимальная скорость в м/с
    //TODO : сделать подгрузку из настроек.Найстройки могут меняться. Активити может быть дейстройд. Получить дефолтную или из файла есть есть запись
    private final double minSpeed = 20.0;
    private static final double MILISECONDS_TO_HOURS = 3.6e+6;
    private static final double METERS_TO_KILOMETERS = 1000;

    private MainActivity mainActivity = null;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public  MyLocationListener(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (lastLocation == null) {
            lastLocation = location;
        }

        double deltaTime = (location.getTime() - lastLocation.getTime()) / MILISECONDS_TO_HOURS;
        final double deltaDistance = location.distanceTo(lastLocation) /  METERS_TO_KILOMETERS;

        System.out.println(String.format("Скорость по рассчётам = %f км/ч; Скорость по gps = %f км/ч; Расстояние = %f км; Время = %f ч; Общеее расстояние = %f"
                ,deltaDistance / deltaTime, location.getSpeed(),deltaDistance,deltaTime,kilometers));

        if(deltaDistance / deltaTime > minSpeed){
            mainHandler.post( new Runnable() {
                @Override
                public void run() {
                    kilometers += deltaDistance;
                    mainActivity.showDistance(kilometers);
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

    public  void setKilometers(double kilometers) {
        this.kilometers = kilometers;
    }

    public double getKilometers() {
        return kilometers;
    }
}
