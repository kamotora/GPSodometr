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

import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.Servicies.Listener;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity {

    public final int REQUEST_CODE_PERMISSION_GPS = 1;
    private static LocationManager locationManager = null;
    private Listener locationListener = null;
    private double kilometers = 0.0;
    private static Realm realm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        //RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").build();
        realm = Realm.getDefaultInstance();
        // Приложение запущено впервые или восстановлено из памяти?
        //TODO: добавить выборку из бд, если на сегодня уже есть запись
        if (savedInstanceState == null)   // приложение запущено впервые
        {
            kilometers = 0.0;    // инициализация суммы счета нулем
            // другой код
        } else // приложение восстановлено из памяти
        {
            // инициализация суммы счета сохраненной в памяти суммой
            kilometers = savedInstanceState.getDouble("kilometers");
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new Listener();
        Msg.initial(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putDouble("kilometers", kilometers);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerProviders();
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
        //TODO: добавить сохранение в бд при завершении работы
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

    private void watchKilometers() {
        final TextView distanceText = findViewById(R.id.distance);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String distanceStr = String.format("Километры: %1$,.2f", locationListener.getKilometers());
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
