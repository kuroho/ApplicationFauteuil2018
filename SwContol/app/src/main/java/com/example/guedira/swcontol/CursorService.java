package com.example.guedira.swcontol;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.VelocityTracker;
import android.view.MotionEvent;


/**
 * Created by Christian on 3/24/17.
 */

class CursorService {

    // Current speed of the cursor in px/s
    private int _speed;
    private VelocityTracker _tracker = null;
    private boolean _speedHasBeenTooHigh = false;
    private int spasm_threshold_factor=3;
    private int _pointerId;
    private float pictocm;



    public CursorService(){
        _speed=0;
        _tracker = null;
        _speedHasBeenTooHigh = false;
        spasm_threshold_factor=3;
        _pointerId=0;
        pictocm=300;
    }




    public CursorService(Display d){
        this();
        setPictocm(300);
        DisplayMetrics dpm=new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(dpm);
            setPictocm(dpm.density);
        }


    }

    public void configureTracker(MotionEvent event) {
        if (this._tracker == null) {
            this._tracker = VelocityTracker.obtain();
        } else {
            this._tracker.clear();
        }
        this._tracker.addMovement(event);

        this._pointerId = event.getPointerId(event.getActionIndex());
    }

    public int computeSpeed(MotionEvent event, int ptr) {
        this._speed=0;
        configureTracker(event);
        if(this!=null && event!=null){

            this._tracker.addMovement(event);
            this._tracker.computeCurrentVelocity(1000);
            float speedX = VelocityTrackerCompat.getXVelocity(this._tracker, ptr);
            float speedY = VelocityTrackerCompat.getYVelocity(this._tracker, ptr);
            this._speed = (int) Math.sqrt(Math.pow(speedX, 2) + Math.pow(speedY, 2));
        }
        return this._speed;
    }

    public void unsetTracker() {
        if(this!=null){
            this._tracker.clear();
            this._tracker.recycle();
            this._tracker = null;
        }
    }

    public boolean aboveLimit() {
        if (this._speed > 2*this.getPictocm()* getSpasm_threshold_factor()) {
            _speedHasBeenTooHigh = true;
            return true;
        } else if (_speedHasBeenTooHigh) {
            return true;
        }
        return false;
    }

    public void unlock() {
        _speedHasBeenTooHigh = false;
    }

    public int getSpasm_threshold_factor() {
        return spasm_threshold_factor;
    }

    public void setSpasm_threshold_factor(int spasm_threshold_factor) {
        this.spasm_threshold_factor = spasm_threshold_factor;
    }


    public float getPictocm() {
        return pictocm;
    }

    public void setPictocm(float pictocm) {
        this.pictocm = pictocm;
    }


}
