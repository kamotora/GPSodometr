package com.practica.gpsodometr.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.practica.gpsodometr.Log;
import com.practica.gpsodometr.MyNotification;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.Helper;
import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.repository.ActionRep;
import com.practica.gpsodometr.servicies.MyApplication;
import com.practica.gpsodometr.servicies.MyLocationListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.RealmResults;

public class SettingsActivity extends AppCompatActivity {
    Button btn, quest;
    TextView minSpeed;
    //TableLayout table;
    //LayoutInflater inflaer;


    Typeface tf1;//Для Букв
    Typeface tf2;//Для Цифр

    RecyclerView listWork;
    private MyAdapter listAdapter;


    SharedPreferences mSettings = null;
    //Название файла с настройками
    public static final String SETTING_FILENAME = "settings";
    //Название сохраняемой настройки в файле
    public static final String SETTING_MINSPEED_NAME = "minSpeed";

    private MyApplication myApplication = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myApplication = (MyApplication) getApplicationContext();
        myApplication.setSettingsActivity(this);


        tf1 = Typeface.createFromAsset(getAssets(), "Geometria-Bold.ttf");
        tf2 = Typeface.createFromAsset(getAssets(), "PFAgoraSlabPro Bold.ttf");

        listWork = (RecyclerView)findViewById(R.id.listWork);
        listWork.setHasFixedSize(true);
        listWork.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new MyAdapter();
        listWork.setAdapter(listAdapter);

        //loadDate();
        //table = (TableLayout) findViewById(R.id.tableresult);
        //inflaer = LayoutInflater.from(this);
        btn = (Button) findViewById(R.id.addWork);
        btn.setTypeface(tf1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(SettingsActivity.this);
            }
        });
        quest = (Button) findViewById(R.id.question);
        quest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAsk(SettingsActivity.this);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new Drawer().withActivity(this).withToolbar(toolbar).withActionBarDrawerToggle(true).withHeader(R.layout.drawer_header).addDrawerItems(
                new PrimaryDrawerItem().withName("Профиль").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                new PrimaryDrawerItem().withName("Настройки").withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(2),
                new PrimaryDrawerItem().withName("Результат").withIcon(FontAwesome.Icon.faw_eye).withIdentifier(3)
        ).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                Intent intent;
                if (drawerItem == null)
                    return;
                switch (drawerItem.getIdentifier()) {
                    case 1:
                        intent = new Intent(SettingsActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        break;
                    case 3:
                        intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                        break;
                    default:
                        break;
                }
            }
        }).build();

        minSpeed = (TextView) findViewById(R.id.minSpeed);

        mSettings = getSharedPreferences(SETTING_FILENAME, Context.MODE_PRIVATE);

        //Все работы и сколько км осталось для каждой
        ConcurrentHashMap<Action, Double> allActions = myApplication.getActionsAndKm();
        if (allActions != null) {
            for (Action action : allActions.keySet()) {
                Double km = allActions.get(action);
                if (km != null)
                    addRow(action, km);
                else
                    //Если отслеживание начнётся в будущем
                    //Осталось столько, сколько всего км
                    addRow(action, action.getKilometers());
            }
        }
*/
    }

    private void loadDate(Action action/*, Double leftKm*/){
        listAdapter.setItems(action);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Загружаем ранее сохранённые настройки
        //Загружаем мин.скорость
        Object edit = findViewById(R.id.minSpeed);
        if (edit instanceof EditText) {
            ((EditText) edit).setText(String.format(Locale.getDefault(), "%d", mSettings.getInt(SETTING_MINSPEED_NAME, MyLocationListener.DEFAULT_MIN_SPEED)));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Сохраняем настройки
        //Сохраняем мин скорость
        SharedPreferences.Editor settingEditor = mSettings.edit();
        final String str = ((TextView) findViewById(R.id.minSpeed)).getText().toString();
        if (!str.trim().isEmpty()) {
            //Если введеная мин скорость отличается
            //Сохраняем и изменяем в расчётах
            int newMinSpeed = Integer.parseInt(str);
            if (newMinSpeed != myApplication.getLocationListener().getMinSpeed()) {
                settingEditor.putInt(SETTING_MINSPEED_NAME, newMinSpeed);
                myApplication.getLocationListener().setMinSpeed(newMinSpeed);
            }
        }
        settingEditor.apply();
        //tasks.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void showDialog(SettingsActivity activity) {
        final Dialog dialog = new Dialog(activity);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        final TextInputLayout errorOfWork = dialog.findViewById(R.id.typeOfWorklogin);
        final TextInputLayout errorOfKilometrs = dialog.findViewById(R.id.typeOfKilo);
        final TextInputLayout errorOfDate = dialog.findViewById(R.id.typeOfDateStart);
        final TextView typeOfWork = dialog.findViewById(R.id.typeOfWork);
        final TextView kilometrs = dialog.findViewById(R.id.kilometrs);
        final TextView tvDate = dialog.findViewById(R.id.dataOfStart);


        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Проверка на допустимость
                String name = typeOfWork.getText().toString();
                Double kilometers = 0.0;
                Date date = null;
                //Если строка пустая или только из пробелов, ошибка
                if (name.trim().isEmpty()) {
                    errorOfWork.setErrorEnabled(true);
                    errorOfWork.setError(getResources().getString(R.string.typeOfWorkError));
                    errorOfKilometrs.setError("");
                    errorOfDate.setError("");
                    return;
                }

                //Если не удалось спарсить данные, ошибка
                try {
                    kilometers = Helper.stringToKm(kilometrs.getText().toString());
                    date = Helper.getDateFromString(tvDate.getText().toString());
                    if (kilometers == null || kilometers > 1_000_000)
                        throw new NumberFormatException("Многовато");
                    if (date == null)
                        throw new ParseException("Неверный формат даты", 0);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(date);
                    if (calendar.get(Calendar.YEAR) < 2019 || calendar.get(Calendar.YEAR) > 2100)
                        throw new ParseException("Некорректная дата", 0);


                } catch (NumberFormatException parseDoubExcept) {
                    errorOfKilometrs.setErrorEnabled(true);
                    errorOfKilometrs.setError(getResources().getString(R.string.kilometrsError));
                    errorOfWork.setError("");
                    errorOfDate.setError("");
                    return;
                } catch (ParseException parseDateExcept) {
                    errorOfDate.setErrorEnabled(true);
                    errorOfDate.setError(parseDateExcept.getMessage());
                    errorOfKilometrs.setError("");
                    errorOfWork.setError("");
                    return;
                }
                //Сохранение действия, если всё норм
                //И сразу посчитаем, сколько осталось км
                //Добавим в список для отслеживания, если надо
                Action action = new Action(name, date, kilometers);
                Double km = ActionRep.countForOneAction(action);
                ActionRep.add(action);
                if (km != null)
                    myApplication.getActionsAndKm().put(action, km);
                //addRow(action, km);
                loadDate(action);
                dialog.dismiss();
            }
        });

        Button btnCanc = (Button) dialog.findViewById(R.id.btnClose);
        btnCanc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    /**
     * Добавить новую строку в таблицу
     **/
    /*public void addRow(final Action action, Double leftKm) {
        final TableRow tr = (TableRow) inflaer.inflate(R.layout.tableforsettings, null);
        final TextView tvName = (TextView) tr.findViewById(R.id.col1);
        final TextView tvDate = (TextView) tr.findViewById(R.id.col2);
        final TextView tvKm = (TextView) tr.findViewById(R.id.col3);
        final TextView tvLeftKm = (TextView) tr.findViewById(R.id.col4);

        tvName.setTypeface(tf1);
        tvDate.setTypeface(tf2);
        tvKm.setTypeface(tf2);
        tvLeftKm.setTypeface(tf2);
        /**
         * Обработка удаления с таблицы
         */
       /* tr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tr.setBackgroundResource(R.color.colorAccent);
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Удалить данную работу?")
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Ищем запись в базе на основе строки
                                        Action actionForDelete = ActionRep.findAction(
                                                new Action(tvName.getText().toString(),
                                                        Helper.getDateFromString(tvDate.getText().toString()), Helper.stringToKm(tvKm.getText().toString())
                                                ));
                                        //Если запись не найдена, возможно, удалена с помощью уведомления
                                        //Или что-то пошло не так
                                        if (actionForDelete == null) {
                                            Log.v("ERROR при удалении работы из таблицы");
                                        } else {
                                            myApplication.getActionsAndKm().remove(actionForDelete);
                                            ActionRep.delete(actionForDelete);
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
*/
        //Добавление в таблицу и бд
  /*      tvName.setText(action.getName());
        tvDate.setText(Helper.getDateStringInNeedFormat(action.getDateStart()));
        tvKm.setText(Helper.kmToString(action.getKilometers()));
        if (leftKm != null) {
            tvLeftKm.setText(Helper.kmToString(leftKm));
            //вдруг
            Action managedObject = ActionRep.findAction(action);
            if (leftKm <= 0 && managedObject != null)
                MyNotification.getInstance(this).show(managedObject);
        } else
            tvLeftKm.setText(Helper.kmToString(action.getKilometers()));
        table.addView(tr);
    }
*/
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    public void showAsk(SettingsActivity activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage("Чтобы удалить работу, необходимо долговременное нажатие");

        AlertDialog alert = dialog.create();
        alert.show();
    }

    /**
     * Обновление таблицы
     */
    public void updateTable() {
    }

/*
    //Для выпадающего календарика
    public void inClick(View view){
        showDialog(DIALOG_DATE);
    }

    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_DATE){
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack ,myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            myYear = year;
            myMonth = month;
            myDay = day;
            if(myDay < 10 && myMonth < 10)
                tvDate.setText(String.format(Locale.getDefault(), "0%d0%d%d", myDay, myMonth, myYear));
            else if (myDay < 10)
                tvDate.setText(String.format(Locale.getDefault(), "0%d%d%d", myDay, myMonth, myYear));
            else if(myMonth < 10)
                tvDate.setText(String.format(Locale.getDefault(), "%d0%d%d", myDay, myMonth, myYear));
            else
                tvDate.setText(String.format(Locale.getDefault(), "%d%d%d", myDay, myMonth, myYear));
        }
    };
*/
}
