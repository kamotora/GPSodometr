package com.practica.gpsodometr.data;

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
            System.out.println("Ошибка при получении текущей даты");
        }
        return null;
    }

    public static Date stringToDate(final String strDate) {
        try {
            return dateFormat.parse(strDate);
        } catch (java.text.ParseException exp) {
            System.out.println("Ошибка при получении даты из строки. Строка = " + strDate);
        }
        return null;
    }
    /**
     * @return формат даты,использующийся в приложении
     */
    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public static String dateToString(Date date) {
        return dateFormat.format(date);
    }

    /**
     * @return километры в виде строки в нужном формате
     **/
    public static String kmToString(double kilometers) {
        return String.format(Locale.US, "%1$,.2f", kilometers);
    }

    public static Double stringToKm(String strKm) {
        try {
            //Удаляем всё кроме цифр и точки
            strKm = strKm.replaceAll("[^.0-9]", "");
            return Double.parseDouble(strKm);
        } catch (NumberFormatException exp) {
            System.out.println("Ошибка в stringToKm. Строка = " + strKm + "\nexp = " + exp);
        }
        return null;
    }
}
