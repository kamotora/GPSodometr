package com.practica.gpsodometr.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.model.Stat;
import com.practica.gpsodometr.data.repository.StatRep;
import com.practica.gpsodometr.servicies.MyLocationListener;

import java.util.Date;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity{

    public final int REQUEST_CODE_PERMISSION_GPS = 1;
    private static LocationManager locationManager = null;

    //Обработчик событий от gps
    private MyLocationListener locationListener = null;
    private static Realm realm = null;
    private static Stat todayStat = null;

    //Spinner spinner = (Spinner)findViewById(R.id.action_bar_spinner);
    //String selected = spinner.getSelectedItem().toString();

    //String [] spin_array = getResources().getStringArray(R.array.interval);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new Drawer().withActivity(this).withToolbar(toolbar).withActionBarDrawerToggle(true).withHeader(R.layout.drawer_header).addDrawerItems(
                new PrimaryDrawerItem().withName("Профиль").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                new PrimaryDrawerItem().withName("Настройки").withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(2),
                new PrimaryDrawerItem().withName("Результат").withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3)
        ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem){
                Intent intent;
                switch(drawerItem.getIdentifier()){
                    case 1:
                        intent = new Intent(MainActivity.this, profileActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, settingsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        }).build();

        //Инициализация бд
        Realm.init(this);
        realm = Realm.getDefaultInstance();

        //Работа с гпс
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);

        //Вывод всех записей из бд
        for (Stat stat : realm.where(Stat.class).findAll())
            System.out.println(stat);
        Msg.initial(this);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerProviders();
        //Проверяем, вдруг есть что-то сохранённое
        if (todayStat == null) {
            todayStat = StatRep.findByDate(new Date());
        }
        if (todayStat != null) {
            locationListener.setKilometers(todayStat.getKilometers());
            showDistance(todayStat.getKilometers());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearProviders();

        //Если ещё нет записи на сегодня, создаём
        //Сохраняем пройденное расстояние
        if (todayStat == null) {
            if (locationListener.getKilometers() > 0) {
                todayStat = new Stat(locationListener.getKilometers());
                StatRep.add(todayStat);
            }
        } else
            StatRep.updateKm(todayStat, locationListener.getKilometers());
    }


    @Override
    //Ответ пользователя на запрос прак
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_GPS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Права есть
                    registerProviders();
                    //showDistance();
                } else {
                    //Пользователь запретил доступ к GPS
                    Msg.showMsg("Нет доступа к GPS.Разрешите доступ к вашему местоположению, иначе работа приложения невозможна");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void registerProviders() {
        //Проверка прав
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Если прав нет, запросим
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_PERMISSION_GPS);
            return;
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Msg.showMsg("Включите GPS");

        HandlerThread t = new HandlerThread("locationListener");
        t.start();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5.0f, locationListener, t.getLooper());
    }

    private void clearProviders(){
        locationManager.removeUpdates(locationListener);
    }

    //Обновление данных на экране
    public void showDistance(double kilometers) {
        final TextView distanceText = findViewById(R.id.distance);
        String distanceStr = String.format("%1$,.2f км", kilometers);
        distanceText.setText(distanceStr);
    }

    public static Realm getRealm() {
        return realm;
    }
}
