package com.practica.gpsodometr.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.practica.gpsodometr.Data.Model.Stat;
import com.practica.gpsodometr.Data.Repository.StatRep;
import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.Servicies.Listener;

import java.time.LocalDate;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    //Пройденное за сегодня расстояние
    private double kilometers = 0.0;

    public final int REQUEST_CODE_PERMISSION_GPS = 1;
    private static LocationManager locationManager = null;
    private Listener locationListener = null;
    private static Realm realm = null;
    private static Stat todayStat = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        // Приложение запущено впервые или восстановлено из памяти?
        //TODO: добавить выборку из бд, если на сегодня уже есть запись
        /*
        if (savedInstanceState == null)   // приложение запущено впервые
        {
            //kilometers = 0.0;    // инициализация суммы счета нулем
            // другой код
        } else // приложение восстановлено из памяти
        {
            // инициализация суммы счета сохраненной в памяти суммой
            //kilometers = savedInstanceState.getDouble("kilometers");
        }
        */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new Listener();
        for (Stat stat : realm.where(Stat.class).findAll())
            System.out.println(stat);
        Msg.initial(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.putDouble("kilometers", kilometers);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerProviders();
        //Если расстояние за сегодня = 0
        //Проверяем, вдруг есть что-то сохранённое
        if (kilometers < 0.0001) {
            if (todayStat == null) {
                todayStat = StatRep.findByDate(LocalDate.now());
            }
            if (todayStat != null) {
                kilometers = todayStat.getKilometers();
            }
        }
        watchKilometers();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearProviders();

        if (todayStat == null) {
            LocalDate todayDate = LocalDate.now();
            todayStat = new Stat(todayDate.getYear(), todayDate.getMonthValue(), todayDate.getDayOfMonth(), kilometers);
            StatRep.add(todayStat);
        } else
            StatRep.updateKm(todayStat, kilometers);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_GPS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    registerProviders();
                    watchKilometers();
                } else {
                    Msg.showMsg("Нет доступа к GPS.Разрешите доступ к вашему местоположению, иначе работа приложения невозможна");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void registerProviders() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION_GPS);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        //locationManager.requestLocationUpdates(NETWORK_PROVIDER, 10000, 10, locationListener);
        //locationManager.requestLocationUpdates(PASSIVE_PROVIDER, 5000, 10, locationListener);
    }

    private void clearProviders(){
        locationManager.removeUpdates(locationListener);
    }

    //Обновление данных на экране
    private void watchKilometers() {
        final TextView distanceText = findViewById(R.id.distance);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double newKm = locationListener.updateDistance();
                kilometers += newKm;
                String distanceStr = String.format("Пройдено сегодня: %1$,.2f км", kilometers);
                distanceText.setText(distanceStr);

                //TODO: сделать обновление показаний при смене позиции, а не по таймеру
                handler.postDelayed(this, 5000);
            }
        });
    }

    public static Realm getRealm() {
        return realm;
    }
}
