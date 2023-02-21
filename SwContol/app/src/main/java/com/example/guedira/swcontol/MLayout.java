package com.example.guedira.swcontol;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by mouka on 1/9/2018.
 */
public class MLayout extends View {


    public MLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Here we actually handle the touch event (e.g. if the action is ACTION_MOVE,
        // scroll this container).
        // This method will only be called if the touch event was intercepted in
        // onInterceptTouchEvent

        //dispatchTouchEvent(ev);
        getParent().requestDisallowInterceptTouchEvent(true);
        return false;
    }













    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }







}
