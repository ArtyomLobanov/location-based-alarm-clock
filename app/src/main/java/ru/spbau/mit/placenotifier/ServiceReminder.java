package ru.spbau.mit.placenotifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.NOTIFICATION_SERVICE;

@SuppressWarnings("ALL")
public class ServiceReminder {

    private Timer reminder;
    private TimerTask task;
    private Handler handler;
    private AlarmManager manager;
    private static final long MILLISEC_IN_MINUTE = 60000;

    ServiceReminder(Activity main) {
        manager = new AlarmManager(main);
        reminder = new Timer();
        handler = new Handler();
        task = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Cursor result = manager.getAlarms();
                        sendNotification(main, result);
                    }
                });
            }
        };
        reminder.schedule(task, 0, MILLISEC_IN_MINUTE);
    }

    public void sendNotification(Activity main, Cursor result) {
        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(main)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Alarm")
                .setContentText("do something somewhere")
                .setTicker("alarm!!!");
        Notification not = builder.build();
        not.defaults = Notification.DEFAULT_ALL;
        Intent resultIntent = new Intent(main, main.getClass());
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        main,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr =
                (NotificationManager) main.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, not);
    }


}
