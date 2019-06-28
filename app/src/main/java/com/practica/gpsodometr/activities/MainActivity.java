package com.practica.gpsodometr.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.model.Stat;
import com.practica.gpsodometr.data.repository.StatRep;
import com.practica.gpsodometr.servicies.MyLocationListener;

import java.util.Date;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity{

    //Пройденное за сегодня расстояние
    private double kilometers = 0.0;

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
                new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                new PrimaryDrawerItem().withName(R.string.drawer_item_free_play).withIcon(FontAwesome.Icon.faw_gamepad),
                new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(2),
                new SectionDrawerItem().withName(R.string.drawer_item_settings),
                new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false),
                new DividerDrawerItem(),
                new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(1)
        ).build();
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        // Приложение запущено впервые или восстановлено из памяти?
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
        locationListener = new MyLocationListener();
        //Вывод всех записей из бд
        for (Stat stat : realm.where(Stat.class).findAll())
            System.out.println(stat);
        Msg.initial(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
                todayStat = StatRep.findByDate(new Date());
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

        //Если ещё нет записи на сегодня, создаём
        //Сохраняем пройденное расстояние
        if (todayStat == null) {
            todayStat = new Stat(kilometers);
            StatRep.add(todayStat);
        } else
            StatRep.updateKm(todayStat, kilometers);
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
                    watchKilometers();
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

        //TODO: Looper
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Msg.showMsg("Включите GPS");


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, locationListener);
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
                //Сколько прошли с прошлого запроса
                double newKm = locationListener.updateDistance();
                //Добавляем к общему расстоянию
                kilometers += newKm;
                String distanceStr = String.format("%1$,.2f км", kilometers);
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
