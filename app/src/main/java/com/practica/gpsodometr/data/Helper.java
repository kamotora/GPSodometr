package com.practica.gpsodometr.data;

import com.practica.gpsodometr.Msg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    //Из date отбрасываем время, оставляем только дату
    public static Date getDateWithothTime(final Date date) {
        try {
            return dateFormat.parse(dateFormat.format(date));
        } catch (java.text.ParseException exp) {
            Msg.showMsg("Ошибка при получении текущей даты");
        }
        return null;
    }

    public static Date getDateFromString(final String strDate) {
        try {
            return dateFormat.parse(strDate);
        } catch (java.text.ParseException exp) {
            Msg.showMsg("Ошибка при получении даты из строки. Строка = " + strDate);
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

    /**
     * @return километры в виде строки в нужном формате
     **/
    public static String kmToString(double kilometers) {
        return String.format(Locale.getDefault(), "%1$,.2f", kilometers);
    }

    public static Double stringToKm(String strKm) {
        try {
            return Double.parseDouble(strKm.replace(",", "."));
        } catch (NumberFormatException exp) {
            Msg.showMsg("Ошибка в stringToKm. Строка = " + strKm + "\nexp = " + exp);
        }
        return null;
    }
}
