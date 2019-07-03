package com.practica.gpsodometr.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.practica.gpsodometr.Msg;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.repository.ActionRep;
import com.practica.gpsodometr.servicies.MyLocationListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public class settingsActivity extends AppCompatActivity{

    int DIALOG_DATE = 1;
    int myYear = 2019;
    int myMonth = 06;
    int myDay = 30;

    ArrayAdapter<String> adapter;
    final ArrayList<String> tasks = new ArrayList<>();
    Button btn;
    TextView minSpeed;
    TableLayout table;
    LayoutInflater inflaer;

    SharedPreferences mSettings = null;
    //Название файла с настройками
    static final String SETTING_FILENAME = "settings";
    //Название сохраняемой настройки в файле
    static final String SETTING_MINSPEED_NAME = "minSpeed";
    //в км/ч
    static final Integer DEFAULT_MIN_SPEED = 20;
    //Формат даты
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        table = (TableLayout)findViewById(R.id.tableresult);
        inflaer = LayoutInflater.from(this);
        btn = (Button)findViewById(R.id.addWork);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showDialog(settingsActivity.this);
            }
        });
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
                        intent = new Intent(settingsActivity.this, profileActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(settingsActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        }).build();

        minSpeed = (TextView)findViewById(R.id.minSpeed);
        //listWork = (ListView)findViewById(R.id.listWork);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tasks);

//        listWork.setAdapter(adapter);

        mSettings  = getSharedPreferences(SETTING_FILENAME, Context.MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Загружаем ранее сохранённые настройки
        //Загружаем мин.скорость
        Object edit = findViewById(R.id.minSpeed);
        if(edit instanceof EditText) {
            ((EditText) edit).setText(String.format(Locale.getDefault(), "%d", mSettings.getInt(SETTING_MINSPEED_NAME, MyLocationListener.DEFAULT_MIN_SPEED)));
        }


        //Вывод существующих действий
        //TODO: учитывать также текущее значение, ещё не добавленное в базу
        ConcurrentHashMap<Action, Double> list = MainActivity.getActionsAndKm();
        if(list != null) {
            for (Action action : list.keySet()) {
                Double km = list.get(action);
                tasks.add(action.toString() + String.format(" Осталось: %1$,.2f км", km));
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Сохраняем настройки
        //Сохраняем мин скорость
        SharedPreferences.Editor settingEditor = mSettings.edit();
        final String str = ((TextView) findViewById(R.id.minSpeed)).getText().toString();
        if(!str.trim().isEmpty())
            settingEditor.putInt(SETTING_MINSPEED_NAME, Integer.parseInt(str));
        Msg.showMsg(str);
        settingEditor.apply();
        tasks.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    //Для кнопки "Добавить"
    public void onClick(View v){
        /*CustomDialogFragment dialog = new CustomDialogFragment();
        dialog.show(getSupportFragmentManager(), "custom");
*/
        final EditText text1 = new EditText(this);
        text1.setHint("Минимальная скорость");
        AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity.this);
        builder.setTitle("Минимальная скорость")
                .setCancelable(false)
                .setView(text1)
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                minSpeed.setText(text1.getText().toString());
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Назад",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        switch(v.getId()){
            case R.id.addWork:


        }
    }

    public void showDialog(settingsActivity activity){
        final Dialog dialog = new Dialog(activity);

        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        final TextInputLayout errorOfWork = dialog.findViewById(R.id.typeOfWorklogin);
        final TextInputLayout errorOfKilometrs = dialog.findViewById(R.id.typeOfKilo);
        final TextInputLayout errorOfDate = dialog.findViewById(R.id.typeOfDateStart);
        final TextView typeOfWork = dialog.findViewById(R.id.typeOfWork);
        final TextView kilometrs = dialog.findViewById(R.id.kilometrs);
        final TextView tvDate = dialog.findViewById(R.id.dataOfStart);



        Button btnOk = (Button)dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Проверка на допустимость
                String name = typeOfWork.getText().toString();
                Double kilometers = 0.0;
                Date date = null;
                //Если строка пустая или только из пробелов, ошибка
                if(name.trim().isEmpty()){
                    Msg.showMsg("Тип работы не может быть пустой строкой");
                    errorOfWork.setErrorEnabled(true);
                    errorOfWork.setError(getResources().getString(R.string.typeOfWorkError));
                    errorOfKilometrs.setError("");
                    errorOfDate.setError("");
                    return;
                }

                //Если не удалось спарсить данные, ошибка
                try{
                    kilometers = Double.parseDouble(kilometrs.getText().toString());
                    date = DATE_FORMAT.parse(tvDate.getText().toString());
                    System.out.println(date);

                }catch (NumberFormatException parseDoubExcept){
                    Msg.showMsg("Кол-во километров содержит недопустимое число");
                    errorOfKilometrs.setErrorEnabled(true);
                    errorOfKilometrs.setError(getResources().getString(R.string.kilometrsError));
                    errorOfWork.setError("");
                    errorOfDate.setError("");
                    return;
                }catch (ParseException parseDateExcept){
                    Msg.showMsg("Дата должна быть в формате дд/мм/гггг");
                    errorOfDate.setErrorEnabled(true);
                    errorOfDate.setError(getResources().getString(R.string.tvDate));
                    errorOfKilometrs.setError("");
                    errorOfWork.setError("");
                    return;
                }
                //Сохранение события, если всё норм
                ActionRep.add(new Action(name,date,kilometers));


                String str = typeOfWork.getText().toString() + " " + kilometrs.getText().toString() + " "  + tvDate.getText().toString();
                TableRow tr = (TableRow)inflaer.inflate(R.layout.tableforsettings,null);
                TextView tv = (TextView) tr.findViewById(R.id.col1);
                tv.setText(typeOfWork.getText().toString());
                tv = (TextView) tr.findViewById(R.id.col2);
                tv.setText(kilometrs.getText().toString());
                tv = (TextView) tr.findViewById(R.id.col3);
                tv.setText(tvDate.getText().toString());

                table.addView(tr);

                tasks.add(0,str);
                adapter.notifyDataSetChanged();
                typeOfWork.setText("");
                kilometrs.setText("");
                tvDate.setText("");

                dialog.dismiss();
            }
        });

        Button btnCanc = (Button)dialog.findViewById(R.id.btnClose);
        btnCanc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });



        dialog.show();
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
                tvDate.setText("0"+myDay + "0" + myMonth + "" + myYear);
            else if (myDay < 10)
                tvDate.setText("0"+myDay + "" + myMonth + "" + myYear);
            else if(myMonth < 10)
                tvDate.setText(myDay + "0" + myMonth + "" + myYear);
            else
                tvDate.setText(myDay + "" + myMonth + "" + myYear);
        }
    };
*/
}