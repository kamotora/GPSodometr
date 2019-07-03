package com.practica.gpsodometr.data;

import com.practica.gpsodometr.Msg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ParseDate {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    //Из date отбрасываем время, оставляем только дату
    public static Date parse(final Date date) {
        try {
            return dateFormat.parse(dateFormat.format(date));
        } catch (java.text.ParseException exp) {
            Msg.showMsg("Ошибка при получении текущей даты");
        }
        return null;
    }

    /**
     * @return формат даты,использующийся в приложении
     */
    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public static String getDateStringInNeedFormat(Date date) {
        return dateFormat.format(date);
    }
}
