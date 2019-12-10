package com.jdhr.autoclick;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

public class ClickService extends AccessibilityService {

    private final String TAG = getClass().getSimpleName();
    private GestureDescription gestureDescription;
    private Point point;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        serviceInfo.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        //指定监听包名
        serviceInfo.packageNames = new String[]{"com.android.packageinstaller", "com.yq.eie", "com.android.chrome", "com.eg.android.AlipayGphone"};
        setServiceInfo(serviceInfo);
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        int childCount = rootNode.getChildCount();
        AccessibilityNodeInfo childNode = null;
        boolean hasTarget = false;
        for (int i = 0; i < childCount; i++) {
            childNode = rootNode.getChild(i);
            if (childNode == null) {
                continue;
            }
            if ("继续玩".equals(childNode.getText())) {
                hasTarget = true;
                break;
            }
        }
        if (Utils.isVersionAbove(Build.VERSION_CODES.N)) {
            if (hasTarget) {
                Rect targetRect = new Rect();
                childNode.getBoundsInScreen(targetRect);
                Point point = new Point((targetRect.right + targetRect.left) / 2, (targetRect.bottom + targetRect.top) / 2);
                Log.i(TAG, "onAccessibilityEvent: " + point.toString());
                doDispatchGesture(point, 500);

            } else {
                if (point == null) {
                    point = new Point(600, 1000);
                }
                doDispatchGesture(point, 1000);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void doDispatchGesture(Point point, int pressTime) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path p = new Path();
        p.moveTo(point.x, point.y);
        builder.addStroke(new GestureDescription.StrokeDescription(p, 0, pressTime));
        gestureDescription = builder.build();
        dispatchGesture(gestureDescription, new MyGestureResultCallback(), null);
    }

    @Override
    public void onInterrupt() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyGestureResultCallback extends GestureResultCallback {

    }
}
