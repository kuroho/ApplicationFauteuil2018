package com.example.guedira.swcontol;

/**
 * Created by guedira on 08/04/2016.
 */
public class Proprty {
    //interface
    int quadrants;
    float neutralZoneSize;
    char confi;//c for circular, r for rectangular
    int posi;//0->center, 1->top, -1-> bottom
    String neutral_color;
    int [][]color_choice;//8x4(RBGA)
    String bg_color;
    int speed_level;
    int precision;
    private boolean ofset;
    private boolean spasm_filter;
    private boolean spasm_filter_on_release;
    private float spasm_threshold;
    private char touch_Strategy;
    private boolean tmp_accel;
    private boolean tmp_decel;
    private int accel_ratio;
    private int decel_ratio;

    int speedincrement;

    public int getSpeedincrement() {
        return speedincrement;
    }

    public void setSpeedincrement(int speedincrement) {
        this.speedincrement = speedincrement;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    boolean analogspeed;

    public boolean isAnalogdirection() {
        return analogdirection;
    }

    public void setAnalogdirection(boolean analogdirection) {
        this.analogdirection = analogdirection;
    }

    public boolean isAnalogspeed() {
        return analogspeed;
    }

    public void setAnalogspeed(boolean analogspeed) {
        this.analogspeed = analogspeed;
    }

    boolean analogdirection;


    public char getSc_rotate() {
        return sc_rotate;
    }

    public void setSc_rotate(char sc_rotate) {
        this.sc_rotate = sc_rotate;
    }

    char sc_rotate;





    //modalities
    boolean vi;//for vibration
    boolean vib_continuous;
    boolean vib_on_reg_change;

    public boolean isVisFbk() {
        return visFbk;
    }

    public void setVisFbk(boolean visFbk) {
        this.visFbk = visFbk;
    }

    boolean snd;//for sound feedback
    boolean snd_continuous;
    boolean visFbk;
    boolean snd_on_reg_change;

    public boolean isSnd_on_reg_change() {
        return snd_on_reg_change;
    }

    public void setSnd_on_reg_change(boolean snd_on_reg_change) {
        this.snd_on_reg_change = snd_on_reg_change;
    }

    //control
    boolean speed_control;
    boolean dir_control;


    boolean bugmd;

    public boolean isBugmd() {
        return bugmd;
    }

    public boolean isVib_on_reg_change() {
        return vib_on_reg_change;
    }

    public void setVib_on_reg_change(boolean vib_on_reg_change) {
        this.vib_on_reg_change = vib_on_reg_change;
    }

    public void setBugmd(boolean bugmd) {
        this.bugmd = bugmd;
    }
    //default constructor


    public Proprty(){

    }

    //getters and setters


    public String getBg_color() {
        return bg_color;
    }

    public void setBg_color(String bg_color) {
        this.bg_color = bg_color;
    }

    public String getNeutral_color() {
        return neutral_color;
    }

    public void setNeutral_color(String neutral_color) {
        this.neutral_color = neutral_color;
    }



    public boolean isVib_continuous() {
        return vib_continuous;
    }

    public void setVib_continuous(boolean vib_continuous) {
        this.vib_continuous = vib_continuous;
    }

    public boolean isDir_control() {
        return dir_control;
    }

    public void setDir_control(boolean dir_control) {
        this.dir_control = dir_control;
    }

    public int getSpeed_level() {
        return speed_level;
    }

    public void setSpeed_level(int speed_level) {
        this.speed_level = speed_level;
    }

    public boolean isSpeed_control() {

        return speed_control;
    }

    public void setSpeed_control(boolean speed_control) {
        this.speed_control = speed_control;
    }

    public boolean isSnd_continuous() {

        return snd_continuous;
    }

    public void setSnd_continuous(boolean snd_continuous) {
        this.snd_continuous = snd_continuous;
    }

    public boolean isSnd() {

        return snd;
    }

    public void setSnd(boolean snd) {
        this.snd = snd;
    }

    public boolean isVi() {

        return vi;
    }

    public void setVi(boolean vi) {
        this.vi = vi;
    }

    public int[][] getColor_choice() {

        return color_choice;
    }

    public void setColor_choice(int[][] color_choice) {
        this.color_choice = color_choice;
    }

    public int getPosi() {

        return posi;
    }

    public void setPosi(int posi) {
        this.posi = posi;
    }

    public float getNeutralZoneSize() {

        return neutralZoneSize;
    }

    public void setNeutralZoneSize(float neutralZoneSize) {
        this.neutralZoneSize = neutralZoneSize;
    }

    public char getConfi() {

        return confi;
    }

    public void setConfi(char confi) {
        this.confi = confi;
    }

    public int getQuadrants() {
        return quadrants;
    }

    public void setQuadrants(int quadrants) {
        this.quadrants = quadrants;
    }


    public boolean isOfset() {
        return ofset;
    }

    public void setOfset(boolean ofset) {
        this.ofset = ofset;
    }

    public boolean isSpasm_filter() {
        return spasm_filter;
    }

    public void setSpasm_filter(boolean spasm_filter) {
        this.spasm_filter = spasm_filter;
    }

    public boolean isSpasm_filter_on_release() {
        return spasm_filter_on_release;
    }

    public void setSpasm_filter_on_release(boolean spasm_filter_on_release) {
        this.spasm_filter_on_release = spasm_filter_on_release;
    }

    public char getTouch_Strategy() {
        return touch_Strategy;
    }

    public void setTouch_Strategy(char touch_Strategy) {
        this.touch_Strategy = touch_Strategy;
    }

    public boolean isTmp_accel() {
        return tmp_accel;
    }

    public void setTmp_accel(boolean tmp_accel) {
        this.tmp_accel = tmp_accel;
    }

    public boolean isTmp_decel() {
        return tmp_decel;
    }

    public void setTmp_decel(boolean tmp_decel) {
        this.tmp_decel = tmp_decel;
    }

    public float getSpasm_threshold() {
        return spasm_threshold;
    }

    public void setSpasm_threshold(float spasm_threshold) {
        this.spasm_threshold = spasm_threshold;
    }

    public int getAccel_ratio() {
        return accel_ratio;
    }

    public void setAccel_ratio(int accel_ratio) {
        this.accel_ratio = accel_ratio;
    }

    public int getDecel_ratio() {
        return decel_ratio;
    }

    public void setDecel_ratio(int decel_ratio) {
        this.decel_ratio = decel_ratio;
    }
}
