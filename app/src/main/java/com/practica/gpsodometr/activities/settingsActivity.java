package com.practica.gpsodometr.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.practica.gpsodometr.MyNotification;
import com.practica.gpsodometr.R;
import com.practica.gpsodometr.data.Helper;
import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.repository.ActionRep;
import com.practica.gpsodometr.servicies.MyLocationListener;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import io.realm.RealmResults;

public class settingsActivity extends AppCompatActivity{
    Button btn;
    TextView minSpeed;
    TableLayout table;
    LayoutInflater inflaer;

    SharedPreferences mSettings = null;
    //Название файла с настройками
    static final String SETTING_FILENAME = "settings";
    //Название сохраняемой настройки в файле
    static final String SETTING_MINSPEED_NAME = "minSpeed";
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

        mSettings  = getSharedPreferences(SETTING_FILENAME, Context.MODE_PRIVATE);

        //Отслеживаемые работы
        ConcurrentHashMap<Action, Double> list = MainActivity.getActionsAndKm();
        //Все работы(list + которые будут отслеживаться в будущем)
        RealmResults<Action> allActions = ActionRep.getAll();
        if (allActions != null) {
            for (Action action : allActions) {
                Double km = list.get(action);
                if (km != null)
                    addRow(action, km);
                else
                    //Если отслеживание начнётся в будущем
                    addRow(action, action.getKilometers());
            }
        }

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
            if (newMinSpeed != MyLocationListener.getMinSpeed()) {
                settingEditor.putInt(SETTING_MINSPEED_NAME, newMinSpeed);
                MyLocationListener.setMinSpeed(newMinSpeed);
            }
        }
        settingEditor.apply();
        //tasks.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                    errorOfWork.setErrorEnabled(true);
                    errorOfWork.setError(getResources().getString(R.string.typeOfWorkError));
                    errorOfKilometrs.setError("");
                    errorOfDate.setError("");
                    return;
                }

                //Если не удалось спарсить данные, ошибка
                try{
                    kilometers = Double.parseDouble(kilometrs.getText().toString());
                    date = Helper.getDateFormat().parse(tvDate.getText().toString());

                }catch (NumberFormatException parseDoubExcept){
                    errorOfKilometrs.setErrorEnabled(true);
                    errorOfKilometrs.setError(getResources().getString(R.string.kilometrsError));
                    errorOfWork.setError("");
                    errorOfDate.setError("");
                    return;
                }catch (ParseException parseDateExcept){
                    errorOfDate.setErrorEnabled(true);
                    errorOfDate.setError(getResources().getString(R.string.tvDate));
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
                    MainActivity.getActionsAndKm().put(action, km);
                addRow(action, km);
                //String str = typeOfWork.getText().toString() + " " + kilometrs.getText().toString() + " "  + tvDate.getText().toString();


                //tasks.add(0,str);
                //adapter.notifyDataSetChanged();
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

    /**
     * Добавить новую строку в таблицу
     **/
    public void addRow(Action action, Double leftKm) {
        final TableRow tr = (TableRow) inflaer.inflate(R.layout.tableforsettings, null);
        final TextView tvName = (TextView) tr.findViewById(R.id.col1);
        final TextView tvDate = (TextView) tr.findViewById(R.id.col2);
        final TextView tvKm = (TextView) tr.findViewById(R.id.col3);
        final TextView tvLeftKm = (TextView) tr.findViewById(R.id.col4);
        /**
         * Обработка удаления с таблицы
         */
        tr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tr.setBackgroundResource(R.color.colorAccent);
                AlertDialog.Builder builder = new AlertDialog.Builder(settingsActivity.this);
                builder.setTitle("Удалить данную работу?")
                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Ищем запись в базе на основе строки
                                        Action actionForDelete = ActionRep.findAction(
                                                new Action(tvName.getText().toString(),
                                                        Helper.getDateFromString(tvDate.getText().toString()),
                                                        Helper.stringToKm(tvKm.getText().toString())));
                                        //Если запись не найдена, возможно, удалена с помощью уведомления
                                        //Или что-то пошло не так
                                        if (actionForDelete == null) {
                                            System.out.println("ERROR при удалении работы из таблицы");
                                        } else
                                            ActionRep.delete(actionForDelete);
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

        //Добавление в таблицу и бд
        tvName.setText(action.getName());
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
