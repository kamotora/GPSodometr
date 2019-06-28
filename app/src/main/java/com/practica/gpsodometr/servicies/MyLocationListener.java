package com.practica.gpsodometr.servicies;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.practica.gpsodometr.Msg;

public class MyLocationListener implements LocationListener {
    private static double distanceInMeters = 0;
    private static Location lastLocation = null;
    //Минимальная скорость в м/с
    private final double minSpeedInMetersPerSecond = 20.0 / 3.6;
    @Override
    public void onLocationChanged(Location location) {
        if (lastLocation == null) {
            lastLocation = location;
        }
        //TODO: скорость возвращается не всегда
        if (location.hasSpeed()) {
            if (location.getSpeed() > minSpeedInMetersPerSecond) {
                distanceInMeters += location.distanceTo(lastLocation);
                Msg.showMsg(Double.toString(distanceInMeters));
            } else
                Msg.showMsg("Скорость меньше минимальной " + location.getSpeed());
        } else
            Msg.showMsg("Не удалось определить скорость");
        lastLocation = location;
    }

    @Override
    @Deprecated
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Msg.showMsg(provider + "enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Msg.showMsg(provider + "disabled");
    }

    public double updateDistance() {
        double res = distanceInMeters / 1000.00;
        distanceInMeters = 0;
        return res;
    }
}
