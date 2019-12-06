package com.jdhr.autoclick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();

    private String action = "action_click";
    private int currentState = 0, state_stop = 0;
    private int state_running = 1;

    private EditText etTime;
    private EditText etCoordinates;
    private Button btnStart;

    private int notificationId = 0x1000;
    private MyBroadcastReceiver receiver;
    private RemoteViews remoteViews;
    private Intent serviceIntent;
    private NotificationManager notificationManager;
    private Notification notification;

    private ArrayList<Point> coordinateList = new ArrayList<>();
    private String[] timeSpans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        registerMyReceiver();
    }


    private void assignViews() {
        etTime = findViewById(R.id.etTime);
        etCoordinates = findViewById(R.id.etCoordinates);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> showNoticeNotification());
    }

    private void showNoticeNotification() {
        String coordinateStr = etCoordinates.getText().toString();
        Log.i(TAG, "showNoticeNotification: " + coordinateStr);
        if (TextUtils.isEmpty(coordinateStr)) {
            Toast.makeText(this, "请输入坐标！", Toast.LENGTH_LONG).show();
            return;
        }
        String delays = etTime.getText().toString();
        if (TextUtils.isEmpty(delays)) {
            Toast.makeText(this, "请输入点击间隔时间！", Toast.LENGTH_LONG).show();
            return;
        }

        String[] coor = coordinateStr.split(",");
        int indexSize = coor.length / 2;//两个数字一个坐标点

        for (int i = 0; i < indexSize; i++) {
            coordinateList.add(new Point(Integer.parseInt(coor[i * 2]), Integer.parseInt(coor[i * 2 + 1])));
            Log.i(TAG, "showNoticeNotification: " + coordinateList.get(coordinateList.size() - 1).toString());
        }

        timeSpans = delays.split(",");

        Toast.makeText(this, "请打开对应页面在下拉菜单中进行操作！", Toast.LENGTH_LONG).show();

        if (null == notification)
            initNotification();
        showMyNotification();
    }

    private void initNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_click);

        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tvService, pi);

        Notification.Builder builder = new Notification.Builder(this);
        String id = "clickService";
        if (Utils.isVersionAbove(Build.VERSION_CODES.O)) {
            NotificationChannel channel = new NotificationChannel(id, "模拟点击", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(id);
        }
        builder.setTicker("通知")
                .setContentTitle("模拟点击")
                .setContentText("开始")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setAutoCancel(false)
                .setDeleteIntent(PendingIntent.getBroadcast(this, 1, new Intent("action_show_notification"), PendingIntent.FLAG_UPDATE_CURRENT));

        notification = builder.build();
    }

    private void registerMyReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(action);
        intentFilter.addAction("action_show_notification");
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != receiver) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        stopMyService();
        notificationManager.cancel(notificationId);
    }


    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: ");
            String action = intent.getAction();
            switch (action) {
                case "action_click":
                    opService();
                    showMyNotification();
                    break;
                case "action_show_notification":
                    showMyNotification();
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 操作 service
     */
    private void opService() {
        if (currentState == state_stop) {
            startMyService();
            currentState = state_running;
        } else if (currentState == state_running) {
            stopMyService();
            currentState = state_stop;
        }
    }

    /**
     * 显示通知
     */
    private void showMyNotification() {
        if (currentState == state_stop) {
            remoteViews.setTextViewText(R.id.tvService, "开始");
        } else if (currentState == state_running) {
            remoteViews.setTextViewText(R.id.tvService, "停止");
        }
        notificationManager.notify(notificationId, notification);
    }

    private void startMyService() {
        serviceIntent = new Intent(this, MyService.class);
        serviceIntent.setPackage(getPackageName());
        serviceIntent.putParcelableArrayListExtra("coordinates", coordinateList);
        serviceIntent.putExtra("delay", timeSpans);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(serviceIntent);
        else
            startService(serviceIntent);
    }

    private void stopMyService() {
        stopService(serviceIntent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: " + event.getRawX() + ">>>" + event.getRawY());
        return super.onTouchEvent(event);
    }
}
