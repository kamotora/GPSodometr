package com.practica.gpsodometr.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout layout;

    ListView listOfDate;
    ListView listOfWork;
    ListView listOfKilo;

    ArrayAdapter<String> adapterOfDate;
    ArrayAdapter<String> adapterOfWork;
    ArrayAdapter<String> adapterOfKilo;

    final ArrayList<String> tasksDate = new ArrayList<>();
    final ArrayList<String> tasksWork = new ArrayList<>();
    final ArrayList<String> tasksKilo = new ArrayList<>();

    public final int REQUEST_CODE_PERMISSION_GPS = 1;
    private static LocationManager locationManager = null;

    //Обработчик событий от gps
    private MyLocationListener locationListener = null;
    private static Realm realm = null;
    private static Stat todayStat = null;

    int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout)findViewById(R.id.listResults);

        listOfDate = (ListView)findViewById(R.id.date);
        listOfWork = (ListView)findViewById(R.id.typeOfWork);
        listOfKilo = (ListView)findViewById(R.id.kilometrs);

        adapterOfDate = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tasksDate);
        listOfDate.setAdapter(adapterOfDate);
        adapterOfWork = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tasksWork);
        listOfWork.setAdapter(adapterOfWork);
        adapterOfKilo = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tasksKilo);
        listOfKilo.setAdapter(adapterOfKilo);


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
    public void onClick(View v){
        //Для дат
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        Calendar c = Calendar.getInstance();
        String str = dateFormat.format(c.getTime());
        tasksDate.add(0,str);
        adapterOfDate.notifyDataSetChanged();

        //Для вида работы
        String strWork = "Что то";
        tasksWork.add(0,strWork);
        adapterOfWork.notifyDataSetChanged();

        //Для километров
        String strKilo = "2345";
        tasksKilo.add(0,strKilo);
        adapterOfKilo.notifyDataSetChanged();

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
