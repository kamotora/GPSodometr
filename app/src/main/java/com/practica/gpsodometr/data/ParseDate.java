package com.practica.gpsodometr.data;

import com.practica.gpsodometr.Msg;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseDate {
    //Из date отбрасываем время, оставляем только дату
    public static Date parse(final Date date) {
        try {
            Date res = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).parse(SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(date));
            return res;
        } catch (java.text.ParseException exp) {
            Msg.showMsg("Ошибка при получении текущей даты");
        }
        return null;
    }
}
