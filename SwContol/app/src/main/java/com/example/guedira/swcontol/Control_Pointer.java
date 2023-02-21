package com.example.guedira.swcontol;

import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * Created by mouka on 12/25/2017.
 */
public class Control_Pointer {
    private String pointer_action;
    private float pointer_x;
    private float pointer_y;
    private float pointer_velocity;
    private float pointer_size;
    private float pointer_pressure;
    private boolean clutched;
    private CursorService cursorServ;
    private VelocityTracker v_Tracker=VelocityTracker.obtain();







    void initialize(){
        this.setClutched(false);
        this.setPointer_action("nothing");
        this.setPointer_x(0);
        this.setPointer_y(0);
        this.setPointer_pressure(0);
        this.setPointer_size(0);
        this.setPointer_velocity(0);
    }


    void clutch(){
        this.setClutched(true);
    }


    void unclutch(){
        this.setClutched(false);
    }


    public float getPointer_x() {
        return pointer_x;
    }

    public void setPointer_x(float pointer_x) {
        this.pointer_x = pointer_x;
    }

    public float getPointer_y() {
        return pointer_y;
    }

    public void setPointer_y(float pointer_y) {
        this.pointer_y = pointer_y;
    }

    public float getPointer_velocity() {
        return pointer_velocity;
    }

    public void setPointer_velocity(float pointer_velocity) {
        this.pointer_velocity = pointer_velocity;
    }

    public float getPointer_size() {
        return pointer_size;
    }

    public void setPointer_size(float pointer_size) {
        this.pointer_size = pointer_size;
    }

    public float getPointer_pressure() {
        return pointer_pressure;
    }

    public void setPointer_pressure(float pointer_pressure) {
        this.pointer_pressure = pointer_pressure;
    }

    public boolean isClutched() {
        return clutched;
    }

    public void setClutched(boolean clutched) {
        this.clutched = clutched;
    }

    public String getPointer_action() {
        return pointer_action;
    }

    public void setPointer_action(String pointer_action) {
        this.pointer_action = pointer_action;
    }

    public CursorService getCursorServ() {
        return cursorServ;
    }

    public void setCursorServ(CursorService cursorServ) {
        this.cursorServ = cursorServ;
    }


    public void SpasmFilter(float threshold){
        if(this.getPointer_velocity()>=threshold)this.unclutch();
    }


    public VelocityTracker getV_Tracker() {
        return v_Tracker;
    }



    public boolean AboveSpasmLimit(float threshold){
        return this.pointer_velocity>threshold;
    }


    public void ComputeVelocity(MotionEvent eve, int[] idx, int n_of_pointers, float ppi){
        float ret=0;
        v_Tracker.addMovement(eve);
        v_Tracker.computeCurrentVelocity(1000);
        for (int i=0;i<n_of_pointers;i++){
            ret+=(float)Math.sqrt((double)(Math.pow(v_Tracker.getXVelocity(idx[i]),2)+Math.pow(v_Tracker.getYVelocity(idx[i]),2)));
        }
        v_Tracker.clear();
        this.setPointer_velocity((float)((ret*2.54)/(ppi*3*n_of_pointers)));
    }





    public void setV_Tracker(VelocityTracker v_Tracker) {
        this.v_Tracker = v_Tracker;
    }
}
