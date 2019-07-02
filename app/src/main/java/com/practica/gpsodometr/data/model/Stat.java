package com.practica.gpsodometr.data.model;

import androidx.annotation.NonNull;

import com.practica.gpsodometr.data.ParseDate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import io.realm.BuildConfig;
import io.realm.RealmObject;
import io.realm.annotations.Required;


//Статистика пройденных км за день
public class Stat extends RealmObject {
    @Required
    //Дата без времени
    private Date date;
    //Кол-во километров
    private Double kilometers;

    public Stat() {
    }

    //Создать запись с текущей датой
    public Stat(Double kilometers) {
        setDate(new Date());
        setKilometers(kilometers);
    }

    public Stat(int year, int month, int day, Double kilometers) {
        //Месяц начинается с нуля
        GregorianCalendar calendar = new GregorianCalendar(year, month - 1, day);
        setDate(calendar.getTime());
        setKilometers(kilometers);
    }

    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = ParseDate.parse(date);
    }

    @NonNull
    public Double getKilometers() {
        return kilometers;
    }

    public void setKilometers(Double kilometers) {
        if (BuildConfig.DEBUG && !(kilometers >= 0)) {
            throw new AssertionError();
        }
        this.kilometers = kilometers;
    }

    @Override
    public String toString() {
        String date = new SimpleDateFormat("dd.MM.yyyy").format(this.date);
        return "Дата: " + date + ", километры = " + kilometers;
    }


    //обьекты равны, если равны их даты
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stat stat = (Stat) o;
        return date.equals(stat.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
