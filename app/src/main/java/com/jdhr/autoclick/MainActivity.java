package com.jdhr.autoclick;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout llTime;
    private EditText etTime;
    private LinearLayout llCoordinate;
    private EditText etCoordinates;
    private TextView tvInfo;
    private Button btnStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
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
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_click);

//        remoteViews.setOnClickPendingIntent(R.id.tvService, );
        String id = "clickService";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, "模拟点击", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(id);
        }
        builder.setContentTitle("模拟点击")
                .setContentText("模拟点击")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round));


        manager.notify(10, builder.build());

    }

}
