package com.practica.gpsodometr.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.practica.gpsodometr.R;

import java.util.ArrayList;

public class settingsActivity extends AppCompatActivity/* implements View.OnClickListener*/{

    int DIALOG_DATE = 1;
    int myYear = 2019;
    int myMonth = 06;
    int myDay = 30;
    TextInputLayout errorOfWork;
    TextInputLayout errorOfKilometrs;
    TextInputLayout errorOfDate;
    TextView typeOfWork;
    TextView kilometrs;
    TextView tvDate;
    ListView listWork;
    ArrayAdapter<String> adapter;
    final ArrayList<String> tasks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        errorOfWork = (TextInputLayout) findViewById(R.id.typeOfWorklogin);
        errorOfKilometrs = (TextInputLayout) findViewById(R.id.kilometrslogin);
        errorOfDate = (TextInputLayout) findViewById(R.id.dateOfStartlogin);
        tvDate = (TextView)findViewById(R.id.dateOfStart);
        typeOfWork = (TextView)findViewById(R.id.typeOfWork);
        kilometrs = (TextView)findViewById(R.id.kilometrs);
        typeOfWork.setOnFocusChangeListener((View.OnFocusChangeListener)this);
        kilometrs.setOnFocusChangeListener((View.OnFocusChangeListener)this);
        tvDate.setOnFocusChangeListener((View.OnFocusChangeListener)this);

        listWork = (ListView)findViewById(R.id.listWork);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,tasks);

        listWork.setAdapter(adapter);
    }


    //Для кнопки "Добавить"
    public void onClick(View v){
        if(typeOfWork.getText().toString().isEmpty()){
            errorOfWork.setErrorEnabled(true);
            errorOfWork.setError(getResources().getString(R.string.typeOfWorkError));
        }
        else
            if (kilometrs.getText().toString().isEmpty()){
                errorOfKilometrs.setErrorEnabled(true);
                errorOfKilometrs.setError(getResources().getString(R.string.kilometrsError));
            }
            else
                if(tvDate.getText().toString().isEmpty()){
                    errorOfDate.setErrorEnabled(true);
                    errorOfDate.setError(getResources().getString(R.string.tvDate));
                }
                else{
                    String str = typeOfWork.getText().toString() + " " + kilometrs.getText().toString() + " "  + tvDate.getText().toString();
                    tasks.add(0,str);
                    adapter.notifyDataSetChanged();
                    typeOfWork.setText("");
                    kilometrs.setText("");
                    tvDate.setText("");
                }
    }

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
            tvDate.setText(myDay + "/" + myMonth + "/" + myYear);
        }
    };
}
