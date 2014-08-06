package com.xinghui.devassistant;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by hugo on 2014/7/29.
 */
public class FloatWindowView extends LinearLayout {

    WindowManager mWindowManager;
    WindowManager.LayoutParams params;

    private static int STATUS_BAR_HEIGHT = 0;

    float downX = 0;
    float downY = 0;

    public FloatWindowView(Context context) {
        super(context);
    }

    public FloatWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public FloatWindowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWindowManagerLayoutParams(WindowManager.LayoutParams params) {
        this.mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        this.params = params;

        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            STATUS_BAR_HEIGHT = getResources().getDimensionPixelSize(resourceId);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float rawX = event.getRawX();
                float rawY = event.getRawY();

                params.x = (int) (rawX - downX);
                params.y = (int) (rawY - downY - STATUS_BAR_HEIGHT);
                mWindowManager.updateViewLayout(this,params);
                break;
        }
        return true;
    }
}
