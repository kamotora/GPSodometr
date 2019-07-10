package com.practica.gpsodometr;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class Message {
    private static Context context = null;

    public Message(Context context) {
        Message.context = context;
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
