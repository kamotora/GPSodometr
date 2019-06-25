package com.practica.gpsodometr;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.practica.gpsodometr.Activities.MainActivity;

public class Msg {
    private static Context context = null;
    private Msg(MainActivity activity){
        context = activity.getApplicationContext();
    }
    public static void initial(MainActivity activity){
        if(context == null && activity != null){
            new Msg(activity);
        }
    }
    public static void showMsg(String text) {
        //создаём и отображаем текстовое уведомление
        if(context == null)
            return;
        Toast toast = Toast.makeText(context,
                text,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }
}
