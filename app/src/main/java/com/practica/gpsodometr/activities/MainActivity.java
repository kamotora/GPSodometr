package com.practica.gpsodometr.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.practica.gpsodometr.Log;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.adapters.MainAdapter;
import com.practica.gpsodometr.data.model.SimpleItemTouchHelper;
import com.practica.gpsodometr.data.model.Stat;
import com.practica.gpsodometr.data.repository.StatRep;
import com.practica.gpsodometr.servicies.MyApplication;
import com.practica.gpsodometr.servicies.MyLocationListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity{

    public final int REQUEST_CODE_PERMISSION_GPS = 1;

    static int kol;

    //Обработчик событий от gps
    private MyLocationListener locationListener = null;
    private LocationManager locationManager = null;
    private MyApplication myApplication;
    Typeface tf1;//Для Букв
    Typeface tf2;//Для Цифр

    RecyclerView listResult;
    MainAdapter adapter;
    //private AdapterForMain listAdapter;
    ItemTouchHelper.Callback callback;

    /*
    TableLayout table;*/
    Spinner spinDay;
    LayoutInflater inflaer;
    ArrayList<Stat> tests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tests = new ArrayList<>();

        myApplication = (MyApplication) getApplicationContext();

        myApplication.setMainActivity(this);

        spinDay = (Spinner)findViewById(R.id.action_bar_spinner);

        inflaer = LayoutInflater.from(this);

        listResult = (RecyclerView)findViewById(R.id.listResult);
        listResult.setHasFixedSize(true);
        listResult.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MainAdapter(StatRep.findByDateAll(new Date()), myApplication);
        adapter.onAttachedToRecyclerView(listResult);

        listResult.setAdapter(adapter);

        //listAdapter = new AdapterForMain(myApplication);
        //listResult.setAdapter(listAdapter);

        callback = new SimpleItemTouchHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(listResult);

        tf1 = Typeface.createFromAsset(getAssets(),"Geometria-Bold.ttf");
        tf2 = Typeface.createFromAsset(getAssets(),"PFAgoraSlabPro Bold.ttf");

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
                if (drawerItem == null)
                    return;
                switch(drawerItem.getIdentifier()){
                    case 1:
                        intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                        break;
                    default:
                        break;
                }
            }
        }).build();

        spinDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //TODO: можно добавить строку : всего пройдено за неделю,месяц км =
                //listAdapter.clearItems();
                //За сегодня
                if(position == 0){
                    adapter.updateData(StatRep.findByDateAll(new Date()));
                    adapter.notifyDataSetChanged();
                }
                //За неделю
                if(position == 1){
                    tests.clear();
                    //Получаем дату - 1 неделя и выводим все данные, начиная с той даты
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, -7);
                    RealmResults<Stat> statsOfWeek = StatRep.getDays(cal.getTime()).sort("date", Sort.DESCENDING);
                    adapter.updateData(statsOfWeek);
                    adapter.notifyDataSetChanged();

                }
                //Аналогично за месяц
                if(position == 2){
                    tests.clear();
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.add(Calendar.MONTH, -1);
                    //new Date(new Date().getTime() - 2628000000);
                    //listAdapter.clearItems();
                    RealmResults<Stat> statsOfMonth = StatRep.getDays(cal.getTime()).sort("date", Sort.DESCENDING);
                    adapter.updateData(statsOfMonth);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        kol = 0;

        //Вывод всех записей из бд(отладка)
        for (Stat stat : Realm.getDefaultInstance().where(Stat.class).findAll())
            Log.v(stat.toString());

        //Добавить запись(отладка)
        //StatRep.add(new Stat(2019,7,12,4.0));


        locationListener = myApplication.getLocationListener();
        locationManager = myApplication.getLocationManager();


        registerProviders();
        //Конец метода onCreate()*/
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearProviders();
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

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast toast = Toast.makeText(this,
                    "Включите GPS",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
            //Message.showMsg("Включите GPS");
        }

        //Обрабатываем события от GPS в отдельном потоке
        HandlerThread t = new HandlerThread("locationListener");
        t.start();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2.0f, locationListener, t.getLooper());
    }

    private void clearProviders() {
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Просмотр ответа пользователя на запрос доступа к геолокации (доступ дан или нет)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_GPS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Права есть
                    registerProviders();
                    //updateDistance();
                } else {
                    //Пользователь запретил доступ к GPS
                    //Message.showMsg("Нет доступа к GPS.Разрешите доступ к вашему местоположению, иначе работа приложения невозможна");
                    Toast toast = Toast.makeText(this,
                            "Нет доступа к GPS.Разрешите доступ к вашему местоположению, иначе работа приложения невозможна",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

    /**
     * Если удаляли значение на сегодня, нужно вывести новое
     */
    public void needUpdateTodayInfo() {
        adapter.updateData(StatRep.findByDateAll(new Date()));
        adapter.notifyDataSetChanged();
    }
}
