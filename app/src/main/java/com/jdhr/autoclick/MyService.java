package com.jdhr.autoclick;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyService extends IntentService {

    private String TAG = getClass().getSimpleName();
    private ArrayList<Point> coordinates;
    private String[] timeSpans;
    private boolean doContinue = true;
    private String[] commands;

    private int notificationId = 10;
    private Notification notification;
    private NotificationManager notificationManager;

    public MyService() {
        this("");
    }

    public MyService(String name) {
        super(name);
    }


    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        Bundle bundle = intent.getExtras();
        Log.i(TAG, "onStart: ");
        if (bundle != null) {
            this.coordinates = bundle.getParcelableArrayList("coordinates");
            this.timeSpans = bundle.getStringArray("delay");
            this.commands = new String[coordinates.size()];
            for (int i = 0; i < coordinates.size(); i++) {
                commands[i] = "input tap " + coordinates.get(i).x + " " + coordinates.get(i).y;
            }
        }
        showNotification();
    }

    private void showNotification() {
        if (Utils.isVersionAbove(Build.VERSION_CODES.O)) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String channelId = "clickService";
            Notification.Builder builder = new Notification.Builder(this, channelId);
            NotificationChannel channel = new NotificationChannel(channelId, "模拟点击", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setTicker("通知")
                    .setContentText("模拟点击")
                    .setContentTitle("服务正在运行")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setAutoCancel(false);

            notification = builder.build();
            startForeground(notificationId, notification);
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
        while (doContinue) {
            for (int i = 0; i < commands.length; i++) {
                execShellCmd(commands[i]);
                Log.i(TAG, "onHandleIntent: command = " + commands[i]);
                SystemClock.sleep((timeSpans == null || timeSpans.length <= i) ? 2000 : Integer.parseInt(timeSpans[i]) * 1000);
//                sendBroadcast(notiIntent);//发送一个显示notification的广播，以免被清除后无法停止点击
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service onDestroy: ");
        super.onDestroy();
        doContinue = false;
        notificationManager.cancel(notificationId);
        stopForeground(true);
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            Process process = Runtime.getRuntime().exec(cmd);
            // 获取输出流
//            OutputStream outputStream = process.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//            dataOutputStream.writeBytes(cmd);
//            dataOutputStream.flush();
//            dataOutputStream.close();
//            outputStream.close();
            process.waitFor();
        } catch (Throwable t) {
            Log.i(TAG, "execShellCmd: " + t.getMessage());
            t.printStackTrace();
        }
    }

}
