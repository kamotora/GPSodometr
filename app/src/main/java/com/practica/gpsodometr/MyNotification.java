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
import android.util.SparseArray;

import androidx.core.app.NotificationCompat;

import com.practica.gpsodometr.data.Helper;
import com.practica.gpsodometr.data.model.Action;
import com.practica.gpsodometr.data.repository.ActionRep;

import java.util.Random;

/**
 * Показ уведомлений
 **/

public class MyNotification {
    //Дейстие по удалению Action из базы при нажатии на уведомление
    private final static String ACTION_1 = "DeleteActionRealm";
    private final static String CHANNEL_ID = "Actions";
    private final static String EXTRA_NAME = "ID";
    private final static String NOTIFICATION_CHANNEL_NAME = "Уведомления об отслеживаемых действиях";
    private final static String NOTIFICATION_CHANNEL_DESCRIPTION = "Показывать уведомления о тех действиях, которые вы добавляли в случае, если Вы проехали нужное кол-во километров";

    private NotificationCompat.Builder builder;
    private static NotificationManager notificationManager = null;
    //здесь для каждого ID уведомления Action, для которого он вызван
    private static SparseArray<Action> actionHashMap = null;
    //TODO:убрать static
    private static Context context;
    private static MyNotification init = null;

    private MyNotification(Context context) {
        MyNotification.context = context;
        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true);

        //Если версия позволяем, создаём канал
        //Иначе без него
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
        actionHashMap = new SparseArray<>();
    }

    public static MyNotification getInstance(Context context) {
        if (init == null)
            init = new MyNotification(context);
        return init;
    }

    /**
     * Показать уведомление
     *
     * @return id уведомления
     */
    public int show(Action action) {
        //Если уже показали уведомление по этому action
        //Не надо ещё раз
        if (actionHashMap.indexOfValue(action) >= 0) {
            System.out.println("Такое уведомление уже есть");
            return 0;
        }
        Random random = new Random();
        int notificationID = random.nextInt(100000);
        //Вдруг уведомление с таким id уже есть
        while (actionHashMap.get(notificationID) != null) {
            notificationID = random.nextInt(100000);
        }

        //Сохраняем для дальнейшей обработки удаления по нажатию
        actionHashMap.put(notificationID, action);
        Intent action1Intent = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_1)
                .putExtra(EXTRA_NAME, notificationID);

        PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                action1Intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification =
                builder.setContentTitle(action.getName())
                        .setContentText("Вы уже проехали " + action.getKilometers() + "км , пора сделать \"" + action.getName() + "\"")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Начиная с даты " + Helper.getDateStringInNeedFormat(action.getDateStart()) + " вы проехали " + action.getKilometers() + "км , пора сделать \"" + action.getName() + "\"! " +
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

        //Обработка нажатия на уведомление
        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (ACTION_1.equals(action)) {
                final int key = intent.getIntExtra(EXTRA_NAME, 0);
                final Action act = actionHashMap.get(key);
                if (act != null) {
                    //TODO:ошибка, если приложение не запущено(ui потока нету)
                    //TODO:обновлять таблицу в настройках
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ActionRep.delete(act);
                            } catch (Exception exp) {
                                Message.showMsg("Ошибка. Возможно, вы уже удалили эту работу");
                            }
                            actionHashMap.remove(key);
                            System.out.println("Удалено");
                        }
                    });
                }
            }
        }


    }
}
