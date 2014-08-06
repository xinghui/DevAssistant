package com.xinghui.devassistant;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class WatchingService extends Service {

    ActivityManager mActivityManager;

    WindowManager mWindowManager;
    WindowManager.LayoutParams params;

    FloatWindowView mFloatWindowView;
    TextView mPackageName;
    TextView mActivityName;

    private static boolean running = false;

    public static boolean isRunning() {
        return running;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    showWindow((ComponentName) msg.obj);
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mFloatWindowView = (FloatWindowView) LayoutInflater.from(this).inflate(R.layout.float_window, null);
            initParams();
            mFloatWindowView.setWindowManagerLayoutParams(params);
            mWindowManager.addView(mFloatWindowView, params);
            mPackageName = (TextView) mFloatWindowView.findViewById(R.id.package_name);
            mActivityName = (TextView) mFloatWindowView.findViewById(R.id.activity_name);
        }

        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        loopGetTask();

        return super.onStartCommand(intent, flags, startId);
    }

    private void initParams() {
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888;

//        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.flags = WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;

        params.x = 0;
        params.y = 0;

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void loopGetTask() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    ActivityManager.RunningTaskInfo amrt = mActivityManager.getRunningTasks(2).get(0);
                    ComponentName cn = amrt.topActivity;

                    if(!cn.getClassName().equals(mActivityName.getText())){
                        handler.obtainMessage(0, cn).sendToTarget();
                        Log.i("xinghui","PackageName : " + cn.getPackageName() + "\n" + "ClassName : " + cn.getClassName());
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void showWindow(ComponentName cn) {
        mPackageName.setText(cn.getPackageName());
        mActivityName.setText(cn.getClassName() == null ? "null" : cn.getClassName());
    }

    private void hideWindow() {
        running = false;
        mWindowManager.removeView(mFloatWindowView);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        hideWindow();
        super.onDestroy();
    }
}
