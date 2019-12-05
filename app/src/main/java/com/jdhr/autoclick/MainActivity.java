package com.jdhr.autoclick;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String action = "action_click";
    private int currentState = 0, state_stop = 0;
    private int state_running = 1;
    private LinearLayout llTime;
    private EditText etTime;
    private LinearLayout llCoordinate;
    private EditText etCoordinates;
    private TextView tvInfo;
    private Button btnStart;

    private int notificationId = 0x1000;
    private MyBroadcastReceiver receiver;
    private RemoteViews remoteViews;
    private Intent serviceIntent;
    private NotificationManager notificationManager;
    private Notification notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        registerMyReceiver();
    }


    private void assignViews() {
        llTime = findViewById(R.id.llTime);
        etTime = findViewById(R.id.etTime);
        llCoordinate = findViewById(R.id.llCoordinate);
        etCoordinates = findViewById(R.id.etCoordinates);
        tvInfo = findViewById(R.id.tvInfo);
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> showNoticeNotification());
    }

    private void showNoticeNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);

        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_click);

        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tvService, pi);

        String id = "clickService";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
                .setAutoCancel(false);

        notification = builder.build();
        notificationManager.notify(notificationId, notification);

    }

    private void registerMyReceiver() {
        receiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(action);
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

    private String TAG = getClass().getSimpleName();

    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: ");
            if (currentState == state_stop) {
                remoteViews.setTextViewText(R.id.tvService, "停止");
                ArrayList<Point> coornidates = new ArrayList<>();
                coornidates.add(new Point(200, 200));
                coornidates.add(new Point(400, 400));
                startMyService(coornidates, 1);
                currentState = state_running;
                notificationManager.notify(notificationId, notification);
            } else if (currentState == state_running) {
                remoteViews.setTextViewText(R.id.tvService, "开始");
                stopMyService();
                currentState = state_stop;
                notificationManager.notify(notificationId, notification);
            }
        }
    }

    private void startMyService(ArrayList<Point> coordinates, int timeDelay) {
        serviceIntent = new Intent(this, MyService.class);
        serviceIntent.setPackage(getPackageName());
        serviceIntent.putParcelableArrayListExtra("coordinates", coordinates);
        serviceIntent.putExtra("delay", timeDelay);
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
        Log.i(TAG, "onTouchEvent: " + event.getRawX() +">>>"+event.getRawY());
        return super.onTouchEvent(event);
    }
}
