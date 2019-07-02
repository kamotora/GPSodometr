package com.practica.gpsodometr;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;

import androidx.core.app.NotificationCompat;

import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.repository.ActionRep;

import java.util.HashMap;
import java.util.Random;

//TODO: обработать нажатие

public class MyNotification {
    private final static String ACTION_1 = "DeleteActionRealm";
    private final static String CHANNEL_ID = "Actions";
    private final static String EXTRA_NAME = "ID";
    private final static String NOTIFICATION_CHANNEL_NAME = "Уведомления об отслеживаемых действиях";
    private final static String NOTIFICATION_CHANNEL_DESCRIPTION = "Показывать уведомления о тех действиях, которые вы добавляли в случае, если Вы проехали нужное кол-во километров";

    private NotificationCompat.Builder builder;
    private static NotificationManager notificationManager = null;
    private static Context context;
    private static HashMap<Integer, Action> actionHashMap = null;

    public MyNotification(Context context) {
        MyNotification.context = context;
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        } else {
            builder.setLights(Color.RED, 1000, 1000);
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        actionHashMap = new HashMap<>();
    }

    public int show(Action action) {

        int notificationID = new Random().nextInt(100000);
        actionHashMap.put(notificationID, action);
        Intent action1Intent = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_1)
                .putExtra(EXTRA_NAME, notificationID);

        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);
        //addAction(new NotificationCompat.Action(R.drawable.ic_launcher,
        //                        "Action 1", action1PendingIntent))
        Notification notification =
                builder.setContentTitle(action.getName())
                        .setContentText("Вы уже проехали " + action.getKilometers() + "км , пора сделать \"" + action.getName() + "\"")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Начиная с даты " + action.getDateStart() + " вы уже проехали " + action.getKilometers() + "км , пора сделать \"" + action.getName() + "\"! " +
                                        "Нажмите на это уведомление, чтобы перестать отслеживать \"" + action.getName() + "\""))
                        .setContentIntent(action1PendingIntent)
                        .build();
        notificationManager.notify(notificationID, notification);
        return notificationID;
    }


    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (ACTION_1.equals(action)) {
                final Integer key = intent.getIntExtra(EXTRA_NAME, 0);
                final Action act = actionHashMap.get(key);
                if (act != null) {
                    //TODO:ошибка, если приложение не запущено
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ActionRep.delete(act);
                            actionHashMap.remove(key);
                            System.out.println("Удалено");
                        }
                    });
                }
            }
        }
    }
}
