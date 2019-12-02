package com.jdhr.autoclick;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Point;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.OutputStream;

public class MyService extends IntentService {

    private Point[] coordinates;
    private int[] timeSpans;
    private boolean doContinue = true;
    private String[] commands;

    public MyService(String name, Point[] coordinates, int[] timeSpans) {
        super(name);
        this.coordinates = coordinates;
        this.timeSpans = timeSpans;
        this.commands = new String[coordinates.length];
        for (int i = 0; i < coordinates.length; i++) {
            commands[i] = "adb shell input tap " + coordinates[i].x + " " + coordinates[i].y;
        }
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (doContinue) {
            for (int i = 0; i < coordinates.length; i++) {

            }
        }
    }

    @Override
    public void onDestroy() {
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
            Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
