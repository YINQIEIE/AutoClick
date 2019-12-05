package com.jdhr.autoclick;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyService extends IntentService {

    private String TAG = getClass().getSimpleName();
    private ArrayList<Point> coordinates;
    private int[] timeSpans;
    private boolean doContinue = true;
    private String[] commands;

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
            this.commands = new String[coordinates.size()];
            for (int i = 0; i < coordinates.size(); i++) {
                commands[i] = "input tap " + coordinates.get(i).x + " " + coordinates.get(i).y;
            }
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
        while (doContinue) {
            for (int i = 0; i < commands.length; i++) {
                execShellCmd(commands[i]);
                Log.i(TAG, "onHandleIntent: command = " + commands[i]);
                SystemClock.sleep(2000);
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "service onDestroy: ");
        super.onDestroy();
        doContinue = false;
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
