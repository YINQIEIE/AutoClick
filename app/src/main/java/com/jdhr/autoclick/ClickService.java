package com.jdhr.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ClickService extends AccessibilityService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        //指定监听包名
        serviceInfo.packageNames = new String[]{"com.android.packageinstaller", "com.jdhr.autoclick"};
        setServiceInfo(serviceInfo);
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int height = dm.heightPixels;
//        int width = dm.widthPixels;
//        int pointX, pointY;
//        pointX = width / 3 + 23;
//        if (height <= 1920) {
//            pointY = height - 30;
//        } else {
//            pointY = height - 160;
//        }
//        Point position = new Point(pointX, pointY);
//        GestureDescription.Builder builder;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            builder = new GestureDescription.Builder();
//            Path p = new Path();
//            p.moveTo(position.x, position.y);
//            builder.addStroke(new GestureDescription.StrokeDescription(p, 0L, 100L));
//            GestureDescription gesture = builder.build();
//            dispatchGesture(gesture, new AccessibilityService.GestureResultCallback() {
//                @Override
//                public void onCompleted(GestureDescription gestureDescription) {
//                    super.onCompleted(gestureDescription);
////                    Logger.d2file("onCompleted: 完成..........");
//                }
//
//                @Override
//                public void onCancelled(GestureDescription gestureDescription) {
//                    super.onCancelled(gestureDescription);
////                    Logger.d2file("onCompleted: 取消..........");
//                }
//            }, null);
//
//        }

    }

    @Override
    public void onInterrupt() {

    }
}
