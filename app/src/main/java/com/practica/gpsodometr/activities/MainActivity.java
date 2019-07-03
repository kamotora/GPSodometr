package com.practica.gpsodometr.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.MyNotification;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.Helper;
import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.model.Stat;
import com.practica.gpsodometr.data.repository.ActionRep;
import com.practica.gpsodometr.data.repository.StatRep;
import com.practica.gpsodometr.servicies.MyLocationListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.Realm;
import io.realm.Sort;


public class MainActivity extends AppCompatActivity{

    public final int REQUEST_CODE_PERMISSION_GPS = 1;
    private static LocationManager locationManager = null;
    //Кол-во километров на сегодня
    private static double kilometers = 0;
    static int kol;
    //Обработчик событий от gps
    private MyLocationListener locationListener = null;
    private static Stat todayStat = null;


    Typeface tf1;//Для Букв
    Typeface tf2;//Для Цифр

    //Action - действие, Double - оставшееся кол-во км.
    private static ConcurrentHashMap<Action, Double> actionsAndKm = null;

    TableLayout table;
    Spinner spinDay;
    LayoutInflater inflaer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        table = (TableLayout)findViewById(R.id.tableresult);
        spinDay = (Spinner)findViewById(R.id.action_bar_spinner);

        inflaer = LayoutInflater.from(this);

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
                        intent = new Intent(MainActivity.this, profileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, settingsActivity.class);
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
                cleanTable(table);
                //За сегодня
                if(position == 0){
                    addRow(new Date(), kilometers);
                }
                //За неделю
                if(position == 1){
                    //Получаем дату - 1 неделя и выводим все данные, начиная с той даты
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.add(Calendar.DAY_OF_MONTH, -7);
                    //new Date(new Date().getTime() - 604800000);

                    //Если в базе на сегодня нет, но в программе есть же
                    if (todayStat == null)
                        addRow(new Date(), kilometers);
                    for (Stat stat : StatRep.getDays(cal.getTime()).sort("date", Sort.DESCENDING)) {
                        //Для даты на сегодня берём более актуальное значение
                        if ((Helper.getDateWithothTime(new Date()).compareTo(stat.getDate())) == 0)
                            addRow(new Date(), kilometers);
                        else
                            addRow(stat.getDate(), stat.getKilometers());
                    }

                }
                //Аналогично за месяц
                if(position == 2){
                    Calendar cal = GregorianCalendar.getInstance();
                    cal.add(Calendar.MONTH, -1);
                    //new Date(new Date().getTime() - 2628000000);
                    if (todayStat == null)
                        addRow(new Date(), kilometers);
                    for (Stat stat : StatRep.getDays(cal.getTime()).sort("date", Sort.DESCENDING)) {
                        if ((Helper.getDateWithothTime(new Date()).compareTo(stat.getDate())) == 0)
                            addRow(new Date(), kilometers);
                        else
                            addRow(stat.getDate(), stat.getKilometers());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //Инициализация бд
        Realm.init(this);
        kol = 0;
        //Работа с гпс
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(this);

        //Вывод всех записей из бд(отладка)
        //for (Stat stat : realm.where(Stat.class).findAll())
        //    System.out.println(stat);
        Msg.initial(this);

        //Если есть сохранённая минимальная скорость
        //Сообщаем это MyLocationListener
        SharedPreferences mSettings = getSharedPreferences(settingsActivity.SETTING_FILENAME, Context.MODE_PRIVATE);
        if (mSettings.contains(settingsActivity.SETTING_MINSPEED_NAME)) {
            MyLocationListener.setMinSpeed(mSettings.getInt(settingsActivity.SETTING_MINSPEED_NAME, MyLocationListener.DEFAULT_MIN_SPEED));
        }

        //Получаем список всех отслеживаемых действий и сколько осталось км
        actionsAndKm = ActionRep.countForEveryKilometersLeft();

        //Добавить запись(отладка)
        //StatRep.add(new Stat(2019,6,24,10.0));

        //Проверяем, вдруг есть сохранённая информация на сегодня
        if (todayStat == null) {
            todayStat = StatRep.findByDate(new Date());
        }
        if (todayStat != null) {
            kilometers = todayStat.getKilometers();
        }
        addRow(new Date(), kilometers);

        //Конец метода onCreate()
    }

    public void cleanTable(TableLayout table){
        int childCount = table.getChildCount();
        if(childCount > 1){
            table.removeViews(1, childCount - 1);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerProviders();
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
            if (kilometers > 0) {
                todayStat = new Stat(kilometers);
                StatRep.add(todayStat);
            }
        } else
            StatRep.updateKm(todayStat, kilometers);
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

        //Обрабатываем события от GPS в отдельном потоке
        HandlerThread t = new HandlerThread("locationListener");
        t.start();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5.0f, locationListener, t.getLooper());
    }

    private void clearProviders(){
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Добавить строку в таблицу
     */
    public void addRow(Date date, double km) {
        final TableRow tr = (TableRow) inflaer.inflate(R.layout.table_row, null);
        final TextView dateView = tr.findViewById(R.id.col1);
        final TextView kmView = tr.findViewById(R.id.col2);

        dateView.setTypeface(tf2);
        kmView.setTypeface(tf2);
        /**
         * Обработка удаления из таблицы
         */
        tr.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                tr.setBackgroundResource(R.color.colorAccent);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Удалить пробег за этот день?")
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Date dateForDelete = Helper.getDateFromString(dateView.getText().toString());
                                        Stat statForDelete = StatRep.findByDate(dateForDelete);
                                        //Если нужно удалить данные за сегодня и запись в бд есть
                                        if (statForDelete != null && statForDelete == todayStat) {
                                            StatRep.delete(todayStat);
                                            todayStat = null;
                                            kilometers = 0;
                                        } else if (statForDelete != null) {
                                            StatRep.delete(statForDelete);
                                        }
                                        //Если нужно удалить данные за сегодня и записи в бд нет
                                        else if (dateForDelete.compareTo(Helper.getDateWithothTime(new Date())) == 0) {
                                            kilometers = 0;
                                        } else {
                                            System.out.println("ERROR !!! При удалении статистики");
                                        }
                                        table.removeView(tr);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        tr.setBackgroundResource(R.color.back);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        //Добавление
        dateView.setText(Helper.getDateStringInNeedFormat(date));
        kmView.setText(Helper.kmToString(km));
        table.addView(tr);
        kol += 1;
    }

    /**
     * Обновление данных на экране
     * Обновление оставшихся км
     */
    public void showDistance(double newKm) {
        kilometers += newKm;
        //final TextView distanceText = findViewById(R.id.distance);
        //distanceText.setText(distanceStr);
        //TODO:!!!
        //Берём первую строку(нулевая строка - заголовок)
        TableRow tr = (TableRow) table.getChildAt(1);
        TextView kmView = (TextView) tr.getVirtualChildAt(1);
        //TextView kmView = (TextView) tr.findViewById(R.id.col2);
        kmView.setText(Helper.kmToString(kilometers));

        // Для наших действий учитываем недавно пройденное расстояние, которого ещё нет в базе
        //TODO: возможно, нужно в отдельный поток
        if (actionsAndKm == null || actionsAndKm.isEmpty())
            return;
        for (Action key : actionsAndKm.keySet()) {
            Double newValue = actionsAndKm.get(key) - newKm;
            actionsAndKm.put(key, newValue);
            if (newValue <= 0) {
                MyNotification.getInstance(this).show(key);
                //Перестаём отслеживать, т.к. уже проехали столько, сколько нужно
                actionsAndKm.remove(key);
            }
        }

    }


    public static ConcurrentHashMap<Action, Double> getActionsAndKm() {
        if (actionsAndKm == null)
            actionsAndKm = new ConcurrentHashMap<>();
        return actionsAndKm;
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

}
