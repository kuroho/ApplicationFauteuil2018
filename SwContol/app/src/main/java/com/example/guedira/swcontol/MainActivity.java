package com.example.guedira.swcontol;

//from bt app
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import static android.graphics.Color.*;
import static java.lang.Math.*;



public class MainActivity extends AppCompatActivity {

    //int ev_count = 0;

    float prev_x = 0;
    float prev_y = 0;
    static int control_resolution = 255;
    int[] prev;
    float [] default_center;

    int n_of_pointers=0;
    Control_Pointer main_control_pointer;
    int[] pointer_order;
    float[] oldpointer;
    int old_id=0;
    Button btButton;

    MLayout glayout;

    //indicator when a button is being recalibrated
    int calibrated_button = 0; //1 for fwd, 2 for bwd, 3 for rgt, 4 for lft
    int moved_button = 0; //1 for fwd, 2 for bwd, 3 for rgt, 4 for lft
    int scaled_button = 0; //1 for fwd, 2 for bwd, 3 for rgt, 4 for lft
    float o_o_x;//to remediate the x offset in button callibration
    float o_o_y;//to remediate the y offset in button callibration
    float oldfwdtop;
    float oldfwdleft;
    //from bt app
    static BluetoothSocket mmSocket;
    static BluetoothDevice mmDevice;
    static ConnectThread mConnectThread;
    InputStream mmInStream;
    static OutputStream mmOutStream;
    public static Proprty perty = new Proprty();
    static BluetoothAdapter mBluetoothAdapter;
    int cur_octant, prev_octant;
    static int move_x = 0x7F;
    static int move_y = 0x7F;
    static float x_max;
    static float y_max;
    static byte sendcount = 0x01;
    char l = 'e';
    //represents the area covered by the user's finger
    static float thumb = (float) 125;
    MediaPlayer alerter;
    //MediaPlayer alert_bip;
    MediaPlayer bloppy;




    boolean bt_connect=false;


    int temporal_a_factor=50;
    int temporal_d_factor=50;
    static int ltapbutton;

    boolean calibrating = false;


    Button calib_phase;
    //button to record user actions
    //Button rcb;
    //this app
    static float r;
    static float alpha;
    static float prev_r;
    static float prev_alpha;
    static final RectF oval = new RectF();
    static RectF visual_oval= new RectF();
    static final RectF fwd_oval = new RectF();
    static final RectF bwd_oval = new RectF();
    static final RectF rgt_oval = new RectF();
    static final RectF lft_oval = new RectF();
    static final RectF sel_oval = new RectF();
    static final RectF run_oval = new RectF();
    int control_region=0;
    int old_control_region=-1;

    float x = 0;
    float y = 0;
    TextView tvx;
    TextView tvy;
    TextView dec;
    static TextView stat;
    Button alert_btn;
    ImageView visfbk;
    float[] ett;
    int[] etto;

    Vibrator vib;
    static float refsize1 = 0;
    static float refsize3 = 0;
    float refsize2 = 0;
    boolean oriflag = true;
    Button setting;
    ImageView btbt;
    boolean first_bt=true;
    SharedPreferences SP;
    Bitmap bg;
    Bitmap fg;
    Animation anim = new AlphaAnimation(0.0f, 1.0f);
    Animation animb = new AlphaAnimation(0.0f, 1.0f);


    //Painting material
    //Paint white_plain_paint, grey_plain_paint, black_plain_paint, black_stroke_paint ;

    Paint white_plain_paint=new Paint();
    Paint black_plain_paint=new Paint();
    Paint grey_plain_paint=new Paint();
    Paint black_stroke_paint=new Paint();

    //resolution in pixel per inch
    float ppin;


    final Handler handler = new Handler();
    boolean cflag = false;
    int received = 0;

    TimerTask timertsk = new TimerTask() {
        int fist = 1;
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    if(bt_connect){
                        if (received == 0) {
                            cflag = false;
                            do {
                                if (fist == 1) {
                                    if (perty.isSnd()) {
                                        fist = 0;
                                    }
                                }
                                btbt.setColorFilter(Color.WHITE);
                                btbt.startAnimation(animb);
                                setBT();
                            } while (mmDevice == null);
                        } else {
                            fist = 1;
                            cflag = true;
                            //if (alert_bip.isPlaying()) alert_bip.pause();
                            if (animb.isInitialized()) animb.cancel();
                            btbt.bringToFront();
                            btbt.setImageAlpha(255);
                            btbt.setColorFilter(BLUE);
                        }
                        received = 0;
                    }else{
                        resetConnection();
                        if (animb.isInitialized()) animb.cancel();
                        btbt.setColorFilter(Color.WHITE);
                    }
                }
            });
        }
    };
    Timer timr = new Timer(true);




    float max_x_calib = 900, min_x_calib = 0, max_y_calib = 1080, min_y_calib = 0, ctr_x = 450, ctr_y = 540;
    boolean firstlaunch = true;
    float pasp, pas;
    //Display dis = getWindowManager().getDefaultDisplay();



    /*********************
     * painting
     ***************************/



    //to paint circular interface
    static void paintInterfaceCircular(Canvas cn, RectF ov,float thmb, float n_radius, Paint white_plain, Paint grey_plain) {


        cn.drawCircle(ov.centerX(), ov.centerY(), (float) (ov.width() * 0.5) + thmb, grey_plain);
        cn.drawCircle(ov.centerX(), ov.centerY(), (float) (ov.width() * 0.5), white_plain);
        cn.drawCircle(ov.centerX(), ov.centerY(), n_radius, grey_plain);
        cn.drawCircle(ov.centerX(), ov.centerY(), n_radius / 10, white_plain);

    }



    static void paintInterfaceButtons(Canvas cn, RectF fo, RectF ba, RectF ri, RectF le, boolean ca, int ca_b, Paint white_plain, Paint black_stroke) {


        Paint redy = new Paint();
        redy.setStyle(Paint.Style.FILL);
        redy.setColor(RED);
        Paint greeny = new Paint();
        greeny.setStyle(Paint.Style.FILL);
        greeny.setColor(GREEN);
        Paint bluey = new Paint();
        bluey.setStyle(Paint.Style.FILL);
        bluey.setColor(BLUE);
        Paint yellowy = new Paint();
        yellowy.setStyle(Paint.Style.FILL);
        yellowy.setColor(YELLOW);

        cn.drawRect(fo, greeny);
        cn.drawRect(ba, yellowy);
        cn.drawRect(ri, bluey);
        cn.drawRect(le, bluey);



        float fo_r = 20;
        float ba_r = 20;
        float ri_r = 20;
        float le_r = 20;


        if (ca) {
            if (ca_b == 1) {

                cn.drawRect(fo.left - fo_r, fo.top - fo_r, fo.left + fo_r, fo.top + fo_r, white_plain);
                cn.drawRect(fo.left - fo_r, fo.top - fo_r, fo.left + fo_r, fo.top + fo_r, black_stroke);


                cn.drawRect(fo.right - fo_r, fo.top - fo_r, fo.right + fo_r, fo.top + fo_r, white_plain);
                cn.drawRect(fo.right - fo_r, fo.top - fo_r, fo.right + fo_r, fo.top + fo_r, black_stroke);


                cn.drawRect(fo.left - fo_r, fo.bottom - fo_r, fo.left + fo_r, fo.bottom + fo_r, white_plain);
                cn.drawRect(fo.left - fo_r, fo.bottom - fo_r, fo.left + fo_r, fo.bottom + fo_r, black_stroke);


                cn.drawRect(fo.right - fo_r, fo.bottom - fo_r, fo.right + fo_r, fo.bottom + fo_r, white_plain);
                cn.drawRect(fo.right - fo_r, fo.bottom - fo_r, fo.right + fo_r, fo.bottom + fo_r, black_stroke);


                cn.drawCircle(fo.centerX(), fo.centerY(), fo_r, white_plain);
                cn.drawCircle(fo.centerX(), fo.centerY(), fo_r, black_stroke);

            } else if (ca_b == 2) {
                cn.drawRect(ba.left - ba_r, ba.top - ba_r, ba.left + ba_r, ba.top + ba_r, white_plain);
                cn.drawRect(ba.left - ba_r, ba.top - ba_r, ba.left + ba_r, ba.top + ba_r, black_stroke);


                cn.drawRect(ba.right - ba_r, ba.top - ba_r, ba.right + ba_r, ba.top + ba_r, white_plain);
                cn.drawRect(ba.right - ba_r, ba.top - ba_r, ba.right + ba_r, ba.top + ba_r, black_stroke);


                cn.drawRect(ba.left - ba_r, ba.bottom - ba_r, ba.left + ba_r, ba.bottom + ba_r, white_plain);
                cn.drawRect(ba.left - ba_r, ba.bottom - ba_r, ba.left + ba_r, ba.bottom + ba_r, black_stroke);


                cn.drawRect(ba.right - ba_r, ba.bottom - ba_r, ba.right + ba_r, ba.bottom + ba_r, white_plain);
                cn.drawRect(ba.right - ba_r, ba.bottom - ba_r, ba.right + ba_r, ba.bottom + ba_r, black_stroke);


                cn.drawCircle(ba.centerX(), ba.centerY(), ba_r, white_plain);
                cn.drawCircle(ba.centerX(), ba.centerY(), ba_r, black_stroke);


            } else if (ca_b == 3) {
                cn.drawRect(ri.left - ri_r, ri.top - ri_r, ri.left + ri_r, ri.top + ri_r, white_plain);
                cn.drawRect(ri.left - ri_r, ri.top - ri_r, ri.left + ri_r, ri.top + ri_r, black_stroke);


                cn.drawRect(ri.right - ri_r, ri.top - ri_r, ri.right + ri_r, ri.top + ri_r, white_plain);
                cn.drawRect(ri.right - ri_r, ri.top - ri_r, ri.right + ri_r, ri.top + ri_r, black_stroke);


                cn.drawRect(ri.left - ri_r, ri.bottom - ri_r, ri.left + ri_r, ri.bottom + ri_r, white_plain);
                cn.drawRect(ri.left - ri_r, ri.bottom - ri_r, ri.left + ri_r, ri.bottom + ri_r, black_stroke);


                cn.drawRect(ri.right - ri_r, ri.bottom - ri_r, ri.right + ri_r, ri.bottom + ri_r, white_plain);
                cn.drawRect(ri.right - ri_r, ri.bottom - ri_r, ri.right + ri_r, ri.bottom + ri_r, black_stroke);


                cn.drawCircle(ri.centerX(), ri.centerY(), ri_r, white_plain);
                cn.drawCircle(ri.centerX(), ri.centerY(), ri_r, black_stroke);

            } else if (ca_b == 4) {
                cn.drawRect(le.left - le_r, le.top - le_r, le.left + le_r, le.top + le_r, white_plain);
                cn.drawRect(le.left - le_r, le.top - le_r, le.left + le_r, le.top + le_r, black_stroke);


                cn.drawRect(le.right - le_r, le.top - le_r, le.right + le_r, le.top + le_r, white_plain);
                cn.drawRect(le.right - le_r, le.top - le_r, le.right + le_r, le.top + le_r, black_stroke);


                cn.drawRect(le.left - le_r, le.bottom - le_r, le.left + le_r, le.bottom + le_r, white_plain);
                cn.drawRect(le.left - le_r, le.bottom - le_r, le.left + le_r, le.bottom + le_r, black_stroke);


                cn.drawRect(le.right - le_r, le.bottom - le_r, le.right + le_r, le.bottom + le_r, white_plain);
                cn.drawRect(le.right - le_r, le.bottom - le_r, le.right + le_r, le.bottom + le_r, black_stroke);


                cn.drawCircle(le.centerX(), le.centerY(), le_r, white_plain);
                cn.drawCircle(le.centerX(), le.centerY(), le_r, black_stroke);
            }


        }

    }

    static void paintInterfaceScrolling(Canvas cn, RectF se, RectF ru,RectF fo, RectF ba, RectF ri, RectF le, boolean ca, int ca_b, Paint white_plain, Paint black_stroke) {


        Paint redy = new Paint();
        redy.setStyle(Paint.Style.FILL);
        redy.setColor(RED);
        Paint greeny = new Paint();
        greeny.setStyle(Paint.Style.FILL);
        greeny.setColor(GREEN);
        Paint bluey = new Paint();
        bluey.setStyle(Paint.Style.FILL);
        bluey.setColor(BLUE);
        Paint yellowy = new Paint();
        yellowy.setStyle(Paint.Style.FILL);
        yellowy.setColor(YELLOW);

        cn.drawRect(fo, redy);
        cn.drawRect(ba, bluey);
        cn.drawRect(ri, bluey);
        cn.drawRect(le, bluey);
        cn.drawRect(se, yellowy);
        cn.drawRect(ru, greeny);

        float se_r = 20;
        float ru_r = 20;


    }



    /*******************************
     * Bluetooth Connection
     ************************/

    private class ConnectThread extends Thread {
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            if (mmSocket != null && !mmSocket.isConnected()) {
                try {
                    mmSocket.connect();
                } catch (IOException connectException) {
                    try {
                        mmSocket.close();
                    } catch (Exception eException) {
                        eException.printStackTrace();
                    }
                    return;
                }
            }
            if(mmSocket!=null){
                ConnectedThread mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    public void setBT() {

        resetConnection();
        //from bt app

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(MainActivity.this, "Your device does not support bluetooth!", Toast.LENGTH_SHORT).show();

        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    mmDevice = device;
                }
            } else {
                Toast.makeText(MainActivity.this, "No paired device found!", Toast.LENGTH_SHORT).show();
            }
        }
        do{
            mConnectThread = new ConnectThread(mmDevice);
        }while (mmDevice==null);

        if(mConnectThread!=null){
            mConnectThread.start();
        }
    }


    private class ConnectedThread extends Thread {
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                if(mmSocket!=null){
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            if (mmOutStream != null) {
                move_x=127;
                move_y=127;
            }

        }


        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            ///////////////

            /////////////////////////////

            while (bt_connect) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == 48) {
                            received = 1;
                            int mocx;
                            int mocy;
                            if (perty.isOfset()) {
                                if (move_y > 128) mocx = move_x + 6;
                                else if (move_y < 127) mocx = move_x - 3;
                                else mocx = move_x;
                            } else {
                                mocx = move_x;
                            }
                            mocy = move_y;
                            if (mocx > 255) mocx = 255;
                            if (mocx < 0) mocx = 0;
                            if (mmOutStream != null && cflag) {
                                int x1 = mocx % 16;
                                int x2 = (mocx - x1) / 16;
                                int y1 = mocy % 16;
                                int y2 = (mocy - y1) / 16;
                                try {
                                    if (perty.getSpeed_level() == 1)
                                        mmOutStream.write(sendcount + 0x80);
                                    else if (perty.getSpeed_level() == 2)
                                        mmOutStream.write(sendcount + 0x90);
                                    else if (perty.getSpeed_level() == 3)
                                        mmOutStream.write(sendcount + 0xA0);
                                    else if (perty.getSpeed_level() == 4)
                                        mmOutStream.write(sendcount + 0xB0);
                                    mmOutStream.write(x1 + 0xC0);
                                    mmOutStream.write(x2 + 0xD0);
                                    mmOutStream.write(y1 + 0xE0);
                                    mmOutStream.write(y2 + 0xF0);
                                    sendcount++;
                                    if (sendcount == 16) sendcount = 0;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }

        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {

            }

        }

    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;

            switch (msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    break;
            }
        }
    };




    private void resetConnection() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mmInStream != null) {
            try {
                mmInStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mmInStream = null;
        }

        if (mmOutStream != null) {
            try {
                mmOutStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mmOutStream = null;
        }

        if (mmSocket != null) {
            mmSocket = null;
        }
        if (mmDevice != null) {
            mmDevice = null;
        }
    }


    /***************
     * system
     ********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Creation of app
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Display dis = getWindowManager().getDefaultDisplay();
        DisplayMetrics dpm=new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            dis.getRealMetrics(dpm);
            ppin=(float)(dpm.densityDpi);
        }
        main_control_pointer=new Control_Pointer();
        pointer_order=new int[50];
        default_center=new float[2];
        oldpointer=new float[2];
        oldpointer[0]=-1;
        oldpointer[1]=-1;
        prev_x = 0;
        prev_y = 0;
        prev_alpha = 0;
        prev_r = 0;
        prev = new int[2];
        prev[1] = 127;
        prev[0] = 127;
        ett = new float[2];
        etto = new int[2];
        etto[0] = 127;
        etto[1] = 127;
        cur_octant = 0;
        prev_octant = 0;

        ltapbutton = 0;

        btbt = (ImageView) findViewById(R.id.btimg);
        tvx = (TextView) findViewById(R.id.xView);
        tvy = (TextView) findViewById(R.id.yView);
        dec = (TextView) findViewById(R.id.decText);
        alert_btn = (Button) findViewById(R.id.alert_button);
        stat = (TextView) findViewById(R.id.statusView);
        setting = (Button) findViewById(R.id.prefs);
        visfbk = (ImageView) findViewById(R.id.vis_fbk);
        glayout=(MLayout) findViewById(R.id.MLT);
        btButton=(Button) findViewById(R.id.BTconnect);
        btButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getActionMasked()==MotionEvent.ACTION_DOWN)
                    bt_connect=!bt_connect;

                if(bt_connect){
                    setBT();
                    btButton.setTextColor(BLUE);
                    if(first_bt) {
                        timr.schedule(timertsk, 0, 500);
                        first_bt=false;
                    }

                }else{
                    resetConnection();
                    btButton.setTextColor(WHITE);
                }

                return true;
            }
        });




        calib_phase = (Button) findViewById(R.id.caliphase);
        calib_phase.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    calibrating = !calibrating;
                    if (calibrating) {
                        calib_phase.setTextColor(Color.parseColor("RED"));
                    } else {
                        calib_phase.setTextColor(Color.parseColor("WHITE"));
                        calibrated_button = 0;
                        moved_button = 0;
                        scaled_button = 0;
                    }
                    loadActivity();
                }
                return false;
            }
        });
        alerter = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
        bloppy = MediaPlayer.create(getApplicationContext(), R.raw.blop);



        loadActivity();

        x = oval.centerX();
        y = oval.centerY();


        oldfwdtop = fwd_oval.top;
        oldfwdleft = fwd_oval.left;



    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        MotionEvent OO7=MotionEvent.obtain(ev);

        if(OO7.getActionMasked()==MotionEvent.ACTION_CANCEL)
        OO7.setAction(MotionEvent.ACTION_MOVE);
        super.dispatchTouchEvent(OO7);

        return true;
    }


    public void loadActivity() {


        final String MyPREFERENCES = "MyPrefs";


        //animation //// vectors from: http://www.freepik.com/free-photos-vectors/turn
        anim.setDuration(250); //blinking time
        anim.setStartOffset(1);//offset to start of animation
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        //animation ////
        animb.setDuration(300); //blinking time
        animb.setStartOffset(5);//offset to start of animation
        animb.setRepeatMode(Animation.REVERSE);
        animb.setRepeatCount(Animation.INFINITE);

        btbt.setBackgroundResource(R.drawable.ic_bluetooth_24dp);




        //preferences loading

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        if (SP.getString("spd", "1").compareToIgnoreCase("4") == 0) perty.setSpeed_level(4);
        else if (SP.getString("spd", "1").compareToIgnoreCase("3") == 0) perty.setSpeed_level(3);
        else if (SP.getString("spd", "1").compareToIgnoreCase("2") == 0) perty.setSpeed_level(2);
        else perty.setSpeed_level(1);

        perty.setConfi(SP.getString("interfaceconfig", "c").charAt(0));

        perty.setTouch_Strategy(SP.getString("tch_s", "F").charAt(0));

        perty.setSpasm_filter(SP.getBoolean("spsm_fltr", false));
        perty.setSpasm_threshold(Integer.parseInt(SP.getString("fltr_val", "20")));

        perty.setSpasm_filter_on_release(SP.getBoolean("spsm_on_release", false));

        perty.setNeutral_color("GREY");
        perty.setSpeedincrement(SP.getInt("sp_increment", 127));

        if (SP.getString("quad", "8").compareToIgnoreCase("8") == 0) perty.setQuadrants(8);
        else perty.setQuadrants(4);

        perty.setBg_color("BLACK");

        if (SP.getString("pos", "Center").compareToIgnoreCase("Up") == 0) perty.setPosi(1);
        else if (SP.getString("pos", "Center").compareToIgnoreCase("Down") == 0) perty.setPosi(-1);
        else if (SP.getString("pos", "Center").compareToIgnoreCase("Center") == 0) perty.setPosi(0);
        else if (SP.getString("pos", "Center").compareToIgnoreCase("Free") == 0) perty.setPosi(3);
        else perty.setPosi(2);
        perty.setVi(SP.getBoolean("en_vib", true));
        perty.setSnd(SP.getBoolean("en_snd", true));

        perty.setVisFbk(SP.getBoolean("en_visfbk", true));

        perty.setAnalogspeed(SP.getBoolean("analog_speed_ctr", true));

        perty.setAnalogdirection(SP.getBoolean("analog_dir_ctr", true));

        perty.setSnd_continuous(SP.getBoolean("cont_snd", false));

        perty.setOfset(SP.getBoolean("offs", false));

        perty.setVib_on_reg_change(SP.getBoolean("vibonch", false));

        perty.setSnd_on_reg_change(SP.getBoolean("sndonch", false));

        perty.setVib_continuous(SP.getBoolean("cont_vib", false));

        perty.setDir_control(SP.getBoolean("analog_dir_ctr", false));

        perty.setSpeed_control(SP.getBoolean("analog_speed_ctr", false));

        perty.setBugmd(SP.getBoolean("bug", false));
        if (perty.isBugmd()) {
            tvx.setVisibility(View.VISIBLE);
            tvy.setVisibility(View.VISIBLE);
            dec.setVisibility(View.VISIBLE);
            stat.setVisibility(View.VISIBLE);
        } else {
            tvx.setVisibility(View.GONE);
            tvy.setVisibility(View.GONE);
            dec.setVisibility(View.GONE);
            stat.setVisibility(View.GONE);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        x_max = oval.width() - 2 * perty.getNeutralZoneSize();
        y_max = oval.height() - 2 * perty.getNeutralZoneSize();

        //calculate dimensions
        Display disp = getWindowManager().getDefaultDisplay();
        Point sz = new Point();
        disp.getSize(sz);
        final int width = sz.x;
        final int height = sz.y;
        if (firstlaunch) {
            if (width < height) {
                refsize1 = width;
                refsize2 = height;
                max_x_calib = width;
                min_x_calib = 0;
                max_y_calib = height;
                min_y_calib = 0;
                refsize3 = refsize1 - thumb;
                oriflag = true;//orientation portrait
            } else {
                refsize1 = height;
                refsize2 = width;
                max_x_calib = height;
                min_x_calib = 0;
                max_y_calib = width;
                min_y_calib = 0;
                refsize3 = refsize1 - thumb;
                oriflag = false;//orientation landscape
            }
        }

        if (SP.getString("ratrat", "Golden").compareToIgnoreCase("Golden") == 0)
            perty.setNeutralZoneSize((float) (refsize3 / (2.618 * 2)));
        else if (SP.getString("ratrat", "Golden").compareToIgnoreCase("1/2") == 0)
            perty.setNeutralZoneSize(refsize3 / (2 * 2));
        else if (SP.getString("ratrat", "Golden").compareToIgnoreCase("1/3") == 0)
            perty.setNeutralZoneSize(refsize3 / (3 * 2));
        else if (SP.getString("ratrat", "Golden").compareToIgnoreCase("1/4") == 0)
            perty.setNeutralZoneSize(refsize3 / (4 * 2));
        else if (SP.getString("ratrat", "Golden").compareToIgnoreCase("Custom") == 0) {
            perty.setNeutralZoneSize((float) ((refsize3 / 200) * SP.getInt("custom_ratio", 30)));
        }

        perty.setPrecision(SP.getInt("control_prec", 8));


        //Drawing material
        bg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        fg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas cnv = new Canvas(bg);
        final Canvas dircnv = new Canvas(bg);
        refsize3 = refsize1 - thumb;
        if (perty.getPosi() == 0) {
            ctr_x = width / 2;
            ctr_y = height / 2;
        } else if (perty.getPosi() == -1) {//on the top
            ctr_x = width / 2;
            ctr_y = refsize1;
        } else if (perty.getPosi() == 1) {
            ctr_x = width / 2;
            ctr_y = height - refsize1;
        }

        if (firstlaunch) {
            fwd_oval.set(ctr_x - 100, ctr_y - 500, ctr_x + 100, ctr_y - 300);
            bwd_oval.set(ctr_x - 100, ctr_y + 300, ctr_x + 100, ctr_y + 500);
            rgt_oval.set(ctr_x + 100, ctr_y - 100, ctr_x + 300, ctr_y + 100);
            lft_oval.set(ctr_x - 300, ctr_y - 100, ctr_x - 100, ctr_y + 100);
            //sel_oval.set(ctr_x - 300, ctr_y - 100, ctr_x + 300, ctr_y + 800);
            //run_oval.set(ctr_x - 300, ctr_y - 100, ctr_x + 400, ctr_y + 800);
            firstlaunch = false;
        }

        oval.set(ctr_x - refsize3 / 2, ctr_y - refsize3 / 2, ctr_x + refsize3 / 2, ctr_y + refsize3 / 2);
        x = oval.centerX();
        y = oval.centerY();

        //goodpaints
        white_plain_paint.setColor(WHITE);
        black_plain_paint.setColor(BLACK);
        grey_plain_paint.setColor(GRAY);
        black_stroke_paint.setColor(BLACK);


        white_plain_paint.setStyle(Paint.Style.FILL);
        black_plain_paint.setStyle(Paint.Style.FILL);
        grey_plain_paint.setStyle(Paint.Style.FILL);
        black_stroke_paint.setStyle(Paint.Style.STROKE);





        ////Paints
        final Paint pnt = new Paint();





        pnt.setColor(Color.parseColor("WHITE"));
        pnt.setStyle(Paint.Style.FILL);

        //////paint for neutral zone
        Paint n_pnt = new Paint();
        n_pnt.setColor(Color.parseColor(perty.getNeutral_color()));
        Paint bg_pnt = new Paint();
        bg_pnt.setColor(Color.parseColor(perty.getBg_color()));
        //paint background color
        cnv.drawRect(0, 0, width, height, bg_pnt);
        //paint the interface
        if (perty.getConfi() == 'c')
            paintInterfaceCircular(cnv, oval, (float) (thumb / 2.0), perty.getNeutralZoneSize(), white_plain_paint, grey_plain_paint);
        else if (perty.getConfi() == 'b')
            paintInterfaceButtons(cnv, fwd_oval, bwd_oval, rgt_oval, lft_oval, calibrating, calibrated_button,white_plain_paint,black_stroke_paint);
        //neutral zone
        else if (perty.getConfi() == 's'){
            paintInterfaceScrolling(cnv, sel_oval, run_oval, fwd_oval, bwd_oval, rgt_oval, lft_oval, calibrating, calibrated_button,white_plain_paint,black_stroke_paint);
        }

        pnt.setStyle(Paint.Style.FILL_AND_STROKE);
        pnt.setStrokeWidth(2);
        pnt.setColor(Color.BLACK);
        final RelativeLayout rl = (RelativeLayout) findViewById(R.id.arya);
        rl.setBackground(new BitmapDrawable(bg));
        alerter.setLooping(true);








        setting.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent itn = new Intent(MainActivity.this, InterfaceSettingsActivity.class);
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                    startActivity(itn);
                return true;
            }
        });



        alert_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        // Write your code to perform an action on down
                        alerter.start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // Write your code to perform an action on continuous touch move
                        break;
                    case MotionEvent.ACTION_UP:
                        // Write your code to perform an action on touch up
                        if (alerter.isPlaying()) {
                            alerter.pause();
                        }
                        break;
                }
                return true;
            }
        });








        rl.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean p_active = true;
                if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                    int jou = 0;
                    if (event.getHistorySize() > 0)
                        if (dist_point(event.getHistoricalX(event.getActionIndex(), jou), event.getHistoricalY(event.getActionIndex(), jou), event.getX(event.getActionIndex()), event.getY(event.getActionIndex())) < 1) {
                            p_active = false;
                        }
                }
                if (p_active) {
                    float delta_x_old_oval = 0;
                    float delta_y_old_oval = 0;

                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            n_of_pointers = 1;
                            pointer_order[0] = event.getPointerId(0);
                            main_control_pointer.clutch();

                            if (!calibrating && perty.getPosi() == 3)
                                oval.offsetTo(event.getX() - oval.height() / 2, event.getY() - oval.width() / 2);
                            visual_oval = new RectF(oval);
                            default_center[0] = oval.centerX();
                            default_center[1] = oval.centerY();
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            pointer_order[n_of_pointers] = event.getPointerId(event.getActionIndex());
                            n_of_pointers++;
                            if (perty.getTouch_Strategy() == 'C') {
                                delta_x_old_oval = (event.getX(event.getActionIndex()) - main_control_pointer.getPointer_x()) / n_of_pointers;
                                delta_y_old_oval = (event.getY(event.getActionIndex()) - main_control_pointer.getPointer_y()) / n_of_pointers;
                            } else {
                                delta_x_old_oval = 0;
                                delta_y_old_oval = 0;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_POINTER_UP:
                            old_id = -1;
                            int tii = event.getPointerId(event.getActionIndex());
                            for (int ju = 0; ju < n_of_pointers; ju++) {
                                if (pointer_order[ju] == tii) {
                                    for (int tiu = ju; tiu < pointer_order.length - 1; tiu++) {
                                        pointer_order[tiu] = pointer_order[tiu + 1];
                                    }
                                    break;
                                }
                            }
                            n_of_pointers--;

                            if (perty.getTouch_Strategy() == 'C') {
                                delta_x_old_oval = (main_control_pointer.getPointer_x() - event.getX(event.getActionIndex())) / n_of_pointers;
                                delta_y_old_oval = (main_control_pointer.getPointer_y() - event.getY(event.getActionIndex())) / n_of_pointers;
                            } else {
                                delta_x_old_oval = 0;
                                delta_y_old_oval = 0;
                            }

                            break;
                        case MotionEvent.ACTION_UP:
                            main_control_pointer.setPointer_x(oval.centerX());
                            main_control_pointer.setPointer_y(oval.centerY());
                            oldpointer[0] = -1;
                            oldpointer[1] = -1;
                            main_control_pointer.unclutch();
                            control_region=-1;
                            n_of_pointers = 0;
                            temporal_a_factor = perty.getAccel_ratio();
                            temporal_d_factor = perty.getDecel_ratio();
                            oval.offsetTo(default_center[0] - oval.width() / 2, default_center[1] - oval.height() / 2);
                            delta_y_old_oval = 0;
                            delta_x_old_oval = 0;
                            old_id = -1;
                            control_region=-1;
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            n_of_pointers = 0;
                            main_control_pointer.unclutch();
                            control_region=-1;
                            oval.offsetTo(default_center[0] - oval.width() / 2, default_center[1] - oval.height() / 2);
                            delta_y_old_oval = 0;
                            delta_x_old_oval = 0;
                            oldpointer[0] = -1;
                            oldpointer[1] = -1;
                            control_region=-1;
                            break;
                        default:
                    }

                    oval.offset(delta_x_old_oval, delta_y_old_oval);
                    main_control_pointer = gravityMotion(event, perty.getTouch_Strategy(), pointer_order, n_of_pointers, main_control_pointer.isClutched(), oldpointer, old_id);
                    if (main_control_pointer.isClutched()) {
                        oldpointer[0] = main_control_pointer.getPointer_x();
                        oldpointer[1] = main_control_pointer.getPointer_y();
                    } else {
                        oldpointer[0] = -1;
                        oldpointer[1] = -1;
                    }
                    if (calibrating) {
                        handle_event_calibration(v, event, 0);

                    } else {
                        handle_event_steering(main_control_pointer, oval, perty.getConfi(), cnv, dircnv);
                    }
                }
                return true;
            }
        });


    }



    void handle_event_calibration(View v, MotionEvent event, int idx) {
        float fo_r2 = 50;
        float ba_r2 = 50;
        float ri_r2 = 50;
        float le_r2 = 50;
        ltapbutton = 0;
        float calib_control_x=  event.getX(idx);
        float calib_control_y=  event.getY(idx);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            //case MotionEvent.ACTION_POINTER_DOWN:
                ctr_y = calib_control_y;
                ctr_x = calib_control_x;
                if (perty.getConfi() == 'b') {// calibration is different for the button interface
                    //long tap to select the button to calibrate
                    if (calib_control_x >= fwd_oval.left && calib_control_x <= fwd_oval.right && calib_control_y >= fwd_oval.top && calib_control_y <= fwd_oval.bottom) {
                        calibrated_button = 1;
                        o_o_x = ctr_x - fwd_oval.left;
                        o_o_y = ctr_y - fwd_oval.top;
                        loadActivity();

                    } else if (calib_control_x >= bwd_oval.left && calib_control_x <= bwd_oval.right && calib_control_y >= bwd_oval.top && calib_control_y <= bwd_oval.bottom) {

                        calibrated_button = 2;
                        o_o_x = ctr_x - bwd_oval.left;
                        o_o_y = ctr_y - bwd_oval.top;
                        loadActivity();


                    } else if (calib_control_x >= rgt_oval.left && calib_control_x <= rgt_oval.right && calib_control_y >= rgt_oval.top && calib_control_y <= rgt_oval.bottom) {

                        calibrated_button = 3;
                        o_o_x = ctr_x - rgt_oval.left;
                        o_o_y = ctr_y - rgt_oval.top;
                        loadActivity();

                    } else if (calib_control_x >= lft_oval.left && calib_control_x <= lft_oval.right && calib_control_y >= lft_oval.top && calib_control_y <= lft_oval.bottom) {

                        calibrated_button = 4;
                        o_o_x = ctr_x - lft_oval.left;
                        o_o_y = ctr_y - lft_oval.top;
                        loadActivity();

                    }

                    //end of tap on button to calibrate

                    //here we set the flags for the calibration:
                    //moved button gets the button that we need to move: 1-forward, 2-backward, 3-right, 4-left
                    //scaled button gets the redimensioning of the button: left digit for the button, right digit for the corner to move
                    if (calibrated_button == 1) {//for forward button
                        if (calib_control_x >= fwd_oval.centerX() - fo_r2 && calib_control_x <= fwd_oval.centerX() + fo_r2 && calib_control_y >= fwd_oval.centerY() - fo_r2 && calib_control_y <= fwd_oval.centerY() + fo_r2) {//the down touch is on the center of the button
                            moved_button = 1;
                            scaled_button = 0;
                        } else if (calib_control_x >= fwd_oval.right - fo_r2 && calib_control_x <= fwd_oval.right + fo_r2 && calib_control_y >= fwd_oval.top - fo_r2 && calib_control_y <= fwd_oval.top + fo_r2) {//top right corner
                            moved_button = 0;
                            scaled_button = 11;
                        } else if (calib_control_x >= fwd_oval.left - fo_r2 && calib_control_x <= fwd_oval.left + fo_r2 && calib_control_y >= fwd_oval.top - fo_r2 && calib_control_y <= fwd_oval.top + fo_r2) {//to left corner
                            moved_button = 0;
                            scaled_button = 12;
                        } else if (calib_control_x >= fwd_oval.right - fo_r2 && calib_control_x <= fwd_oval.right + fo_r2 && calib_control_y >= fwd_oval.bottom - fo_r2 && calib_control_y <= fwd_oval.bottom + fo_r2) {//bottom right
                            moved_button = 0;
                            scaled_button = 13;
                        } else if (calib_control_x >= fwd_oval.left - fo_r2 && calib_control_x <= fwd_oval.left + fo_r2 && calib_control_y >= fwd_oval.bottom - fo_r2 && calib_control_y <= fwd_oval.bottom + fo_r2) {//bottom left
                            moved_button = 0;
                            scaled_button = 14;
                        }
                    } else if (calibrated_button == 2) {//for backward button
                        if (calib_control_x >= bwd_oval.centerX() - ba_r2 && calib_control_x <= bwd_oval.centerX() + ba_r2 && calib_control_y >= bwd_oval.centerY() - ba_r2 && calib_control_y <= bwd_oval.centerY() + ba_r2) {//the down touch is on the center of the button
                            moved_button = 2;
                        } else if (calib_control_x >= bwd_oval.right - fo_r2 && calib_control_x <= bwd_oval.right + fo_r2 && calib_control_y >= bwd_oval.top - fo_r2 && calib_control_y <= bwd_oval.top + fo_r2) {//top right corner
                            moved_button = 0;
                            scaled_button = 21;
                        } else if (calib_control_x >= bwd_oval.left - fo_r2 && calib_control_x <= bwd_oval.left + fo_r2 && calib_control_y >= bwd_oval.top - fo_r2 && calib_control_y <= bwd_oval.top + fo_r2) {//to left corner
                            moved_button = 0;
                            scaled_button = 22;
                        } else if (calib_control_x >= bwd_oval.right - fo_r2 && calib_control_x <= bwd_oval.right + fo_r2 && calib_control_y >= bwd_oval.bottom - fo_r2 && calib_control_y <= bwd_oval.bottom + fo_r2) {//bottom right
                            moved_button = 0;
                            scaled_button = 23;
                        } else if (calib_control_x >= bwd_oval.left - fo_r2 && calib_control_x <= bwd_oval.left + fo_r2 && calib_control_y >= bwd_oval.bottom - fo_r2 && calib_control_y <= bwd_oval.bottom + fo_r2) {//bottom left
                            moved_button = 0;
                            scaled_button = 24;
                        }
                    } else if (calibrated_button == 3) {//for right button
                        if (calib_control_x >= rgt_oval.centerX() - ri_r2 && calib_control_x <= rgt_oval.centerX() + ri_r2 && calib_control_y >= rgt_oval.centerY() - ri_r2 && calib_control_y <= rgt_oval.centerY() + ri_r2) {//the down touch is on the center of the button
                            moved_button = 3;
                        } else if (calib_control_x >= rgt_oval.right - fo_r2 && calib_control_x <= rgt_oval.right + fo_r2 && calib_control_y >= rgt_oval.top - fo_r2 && calib_control_y <= rgt_oval.top + fo_r2) {//top right corner
                            moved_button = 0;
                            scaled_button = 31;
                        } else if (calib_control_x >= rgt_oval.left - fo_r2 && calib_control_x <= rgt_oval.left + fo_r2 && calib_control_y >= rgt_oval.top - fo_r2 && calib_control_y <= rgt_oval.top + fo_r2) {//to left corner
                            moved_button = 0;
                            scaled_button = 32;
                        } else if (calib_control_x >= rgt_oval.right - fo_r2 && calib_control_x <= rgt_oval.right + fo_r2 && calib_control_y >= rgt_oval.bottom - fo_r2 && calib_control_y <= rgt_oval.bottom + fo_r2) {//bottom right
                            moved_button = 0;
                            scaled_button = 33;
                        } else if (calib_control_x >= rgt_oval.left - fo_r2 && calib_control_x <= rgt_oval.left + fo_r2 && calib_control_y >= rgt_oval.bottom - fo_r2 && calib_control_y <= rgt_oval.bottom + fo_r2) {//bottom left
                            moved_button = 0;
                            scaled_button = 34;
                        }

                    } else if (calibrated_button == 4) {//for left button
                        if (calib_control_x >= lft_oval.centerX() - le_r2 && calib_control_x <= lft_oval.centerX() + le_r2 && calib_control_y >= lft_oval.centerY() - le_r2 && calib_control_y <= lft_oval.centerY() + le_r2) {//the down touch is on the center of the button
                            moved_button = 4;
                        } else if (calib_control_x >= lft_oval.right - fo_r2 && calib_control_x <= lft_oval.right + fo_r2 && calib_control_y >= lft_oval.top - fo_r2 && calib_control_y <= lft_oval.top + fo_r2) {//top right corner
                            moved_button = 0;
                            scaled_button = 41;
                        } else if (calib_control_x >= lft_oval.left - fo_r2 && calib_control_x <= lft_oval.left + fo_r2 && calib_control_y >= lft_oval.top - fo_r2 && calib_control_y <= lft_oval.top + fo_r2) {//to left corner
                            moved_button = 0;
                            scaled_button = 42;
                        } else if (calib_control_x >= lft_oval.right - fo_r2 && calib_control_x <= lft_oval.right + fo_r2 && calib_control_y >= lft_oval.bottom - fo_r2 && calib_control_y <= lft_oval.bottom + fo_r2) {//bottom right
                            moved_button = 0;
                            scaled_button = 43;
                        } else if (calib_control_x >= lft_oval.left - fo_r2 && calib_control_x <= lft_oval.left + fo_r2 && calib_control_y >= lft_oval.bottom - fo_r2 && calib_control_y <= lft_oval.bottom + fo_r2) {//bottom left
                            moved_button = 0;
                            scaled_button = 44;
                        }
                    }
                    //end of flags for calibration


                } else {//calibration of circular  design
                    oval.set(ctr_x - refsize3 / 2, ctr_y - refsize3 / 2, ctr_x + refsize3 / 2, ctr_y + refsize3 / 2);
                }
                loadActivity();

                break;
            case MotionEvent.ACTION_MOVE:
                if (perty.getConfi() != 'b') {//calibration for circular design
                    float d = dist_point(ctr_x, ctr_y, calib_control_x, calib_control_y);
                    refsize1 = 2 * d;
                    refsize3 = refsize1 - thumb;
                    oval.set(ctr_x - refsize3 / 2, ctr_y - refsize3 / 2, ctr_x + refsize3 / 2, ctr_y + refsize3 / 2);
                    //button calibration
                    //repositioning for buttons
                } else if (moved_button == 1) {//forward buttonctr_x=fwd_oval.centerX();
                    ctr_y = fwd_oval.centerY();
                    o_o_x = ctr_x - fwd_oval.left;
                    o_o_y = ctr_y - fwd_oval.top;
                    RectF tmprec = new RectF(fwd_oval);
                    tmprec.offsetTo(calib_control_x - o_o_x, calib_control_y - o_o_y);
                    if (calib_control_x - o_o_x >= 0 && calib_control_x - o_o_x + fwd_oval.width() <= refsize1 && calib_control_y - o_o_y >= 0 && calib_control_y - o_o_y + fwd_oval.height() <= refsize2) {//if not touching screen boundaries
                        if (!(rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval))) {//if not touching each other
                            fwd_oval.offsetTo(tmprec.left, tmprec.top);
                            moved_button = 1;
                        }
                    }

                } else if (moved_button == 2) {
                    ctr_x = bwd_oval.centerX();
                    ctr_y = bwd_oval.centerY();
                    o_o_x = ctr_x - bwd_oval.left;
                    o_o_y = ctr_y - bwd_oval.top;
                    RectF tmprec = new RectF(bwd_oval);
                    tmprec.offsetTo(calib_control_x - o_o_x, calib_control_y - o_o_y);
                    if (calib_control_x - o_o_x >= 0 && calib_control_x - o_o_x + bwd_oval.width() <= refsize1 && calib_control_y - o_o_y >= 0 && calib_control_y - o_o_y + bwd_oval.height() <= refsize2)//if not touching screen boundaries
                        if (calib_control_x - o_o_x >= 0 && calib_control_x - o_o_x + fwd_oval.width() <= refsize1 && calib_control_y - o_o_y >= 0 && calib_control_y - o_o_y + fwd_oval.height() <= refsize2)//if not touching screen boundaries
                            if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                    rectangle_touch2(tmprec, rgt_oval) ||
                                    rectangle_touch2(tmprec, lft_oval))) {
                                bwd_oval.offsetTo(tmprec.left, tmprec.top);
                                moved_button = 2;
                            }
                } else if (moved_button == 3) {
                    ctr_x = rgt_oval.centerX();
                    ctr_y = rgt_oval.centerY();
                    o_o_x = ctr_x - rgt_oval.left;
                    o_o_y = ctr_y - rgt_oval.top;
                    RectF tmprec = new RectF(rgt_oval);
                    tmprec.offsetTo(calib_control_x - o_o_x, calib_control_y - o_o_y);
                    if (calib_control_x - o_o_x >= 0 && calib_control_x - o_o_x + rgt_oval.width() <= refsize1 && calib_control_y - o_o_y >= 0 && calib_control_y - o_o_y + rgt_oval.height() <= refsize2)//if not touching screen boundaries
                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, lft_oval))) {
                            rgt_oval.offsetTo(tmprec.left, tmprec.top);
                            moved_button = 3;
                        }

                } else if (moved_button == 4) {
                    ctr_x = lft_oval.centerX();
                    ctr_y = lft_oval.centerY();
                    o_o_x = ctr_x - lft_oval.left;
                    o_o_y = ctr_y - lft_oval.top;
                    RectF tmprec = new RectF(lft_oval);
                    tmprec.offsetTo(calib_control_x - o_o_x, calib_control_y - o_o_y);
                    if (calib_control_x - o_o_x >= 0 && calib_control_x - o_o_x + lft_oval.width() <= refsize1 && calib_control_y - o_o_y >= 0 && calib_control_y - o_o_y + lft_oval.height() <= refsize2)//if not touching screen boundaries
                        if (!(rectangle_touch2(lft_oval, fwd_oval) ||
                                rectangle_touch2(lft_oval, bwd_oval) ||
                                rectangle_touch2(lft_oval, rgt_oval))) {
                            lft_oval.offsetTo(tmprec.left, tmprec.top);
                        }
                    //end of repositioning

                    //start of resizing for buttons
                } else {
                    RectF tmprec = new RectF();

                    if (scaled_button == 11) {
                        if (calib_control_x > fwd_oval.left && calib_control_y < fwd_oval.bottom) {
                            tmprec.set(fwd_oval.left, calib_control_y, calib_control_x, fwd_oval.bottom);
                        }

                        if (!(rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {//if not touching each other
                            fwd_oval.set(tmprec);
                        }

                    } else if (scaled_button == 12) {
                        if (calib_control_x < fwd_oval.right && calib_control_y < fwd_oval.bottom)
                            tmprec.set(calib_control_x, calib_control_y, fwd_oval.right, fwd_oval.bottom);

                        if (!(rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {//if not touching each other
                            fwd_oval.set(tmprec);
                        }


                    } else if (scaled_button == 13) {
                        if (calib_control_x > fwd_oval.left && calib_control_y > fwd_oval.top)
                            tmprec.set(fwd_oval.left, fwd_oval.top, calib_control_x, calib_control_y);

                        if (!(rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {//if not touching each other
                            fwd_oval.set(tmprec);
                        }

                    } else if (scaled_button == 14) {
                        if (calib_control_x < fwd_oval.right && calib_control_y > fwd_oval.top)
                            tmprec.set(calib_control_x, fwd_oval.top, fwd_oval.right, calib_control_y);

                        if (!(rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {//if not touching each other
                            fwd_oval.set(tmprec);
                        }


                    } else if (scaled_button == 21) {
                        if (calib_control_x > bwd_oval.left && calib_control_y < bwd_oval.bottom)
                            tmprec.set(bwd_oval.left, calib_control_y, calib_control_x, bwd_oval.bottom);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            bwd_oval.set(tmprec);
                        }

                    } else if (scaled_button == 22) {
                        if (calib_control_x < bwd_oval.right && calib_control_y < bwd_oval.bottom)
                            tmprec.set(calib_control_x, calib_control_y, bwd_oval.right, bwd_oval.bottom);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            bwd_oval.set(tmprec);
                        }

                    } else if (scaled_button == 23) {
                        if (calib_control_x > bwd_oval.left && calib_control_y > bwd_oval.top)
                            tmprec.set(bwd_oval.left, bwd_oval.top, calib_control_x, calib_control_y);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            bwd_oval.set(tmprec);
                        }


                    } else if (scaled_button == 24) {
                        if (calib_control_x < bwd_oval.right && calib_control_y > bwd_oval.top)
                            tmprec.set(calib_control_x, bwd_oval.top, bwd_oval.right, calib_control_y);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            bwd_oval.set(tmprec);
                        }


                    } else if (scaled_button == 31) {
                        if (calib_control_x > rgt_oval.left && calib_control_y < rgt_oval.bottom)
                            tmprec.set(rgt_oval.left, calib_control_y, calib_control_x, rgt_oval.bottom);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            rgt_oval.set(tmprec);
                        }


                    } else if (scaled_button == 32) {
                        if (calib_control_x < rgt_oval.right && calib_control_y < rgt_oval.bottom)
                            tmprec.set(calib_control_x, calib_control_y, rgt_oval.right, rgt_oval.bottom);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            rgt_oval.set(tmprec);
                        }

                    } else if (scaled_button == 33) {
                        if (calib_control_x > rgt_oval.left && calib_control_y > rgt_oval.top)
                            tmprec.set(rgt_oval.left, rgt_oval.top, calib_control_x, calib_control_y);


                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            rgt_oval.set(tmprec);
                        }
                    } else if (scaled_button == 34) {
                        if (calib_control_x < rgt_oval.right && calib_control_y > rgt_oval.top)
                            tmprec.set(calib_control_x, rgt_oval.top, rgt_oval.right, calib_control_y);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, lft_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            rgt_oval.set(tmprec);
                        }

                    } else if (scaled_button == 41) {
                        if (calib_control_x > lft_oval.left && calib_control_y < lft_oval.bottom)
                            tmprec.set(lft_oval.left, calib_control_y, calib_control_x, lft_oval.bottom);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            lft_oval.set(tmprec);
                        }

                    } else if (scaled_button == 42) {
                        if (calib_control_x < lft_oval.right && calib_control_y < lft_oval.bottom)
                            tmprec.set(calib_control_x, calib_control_y, lft_oval.right, lft_oval.bottom);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            lft_oval.set(tmprec);
                        }

                    } else if (scaled_button == 43) {
                        if (calib_control_x > lft_oval.left && calib_control_y > lft_oval.top)
                            tmprec.set(lft_oval.left, lft_oval.top, calib_control_x, calib_control_y);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            lft_oval.set(tmprec);
                        }


                    } else if (scaled_button == 44) {
                        if (calib_control_x < lft_oval.right && calib_control_y > lft_oval.top)
                            tmprec.set(calib_control_x, lft_oval.top, lft_oval.right, calib_control_y);

                        if (!(rectangle_touch2(tmprec, fwd_oval) ||
                                rectangle_touch2(tmprec, bwd_oval) ||
                                rectangle_touch2(tmprec, rgt_oval) ||
                                rectangle_collapse(tmprec, 20))) {
                            lft_oval.set(tmprec);

                        }

                    }

                }
                //end of resizing for buttons
                //end of calibration for buttons

                loadActivity();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                moved_button = 0;
                scaled_button = 0;
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadActivity();
        //setBT();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
            //set us to non-modal, so that others can receive the outside touch events.
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

            //and watch for outside touch events too
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        }
    }


    //OnStop method logs the events in a log file
    @Override
    public void onStop() {
        super.onStop();

    }



    /************************control*******************************/



    void handle_event_steering(Control_Pointer ctr_ptr, RectF ctr_oval, char config,Canvas cnv,Canvas dircnv){
        if(ctr_ptr.isClutched()) {
            if (perty.isSpasm_filter()) {
                if (ctr_ptr.AboveSpasmLimit(perty.getSpasm_threshold())) {
                    ctr_ptr.unclutch();
                } else {
                    if (!perty.isSpasm_filter_on_release())
                        ctr_ptr.clutch();
                }

            }
        }

        float x_ctr=ctr_ptr.getPointer_x();
        float y_ctr=ctr_ptr.getPointer_y();
        if(ctr_ptr.isClutched()){
            float t_ett=ett[0];
            ett = toPolar(x_ctr, y_ctr, ctr_oval);
            if (perty.getConfi() == 'c')
                etto = toJoystick(ett[0], ett[1], ctr_oval, cnv, dircnv, perty.getPrecision(), perty.getSpeedincrement(),t_ett);
            else
                etto = toJoystick_but(x_ctr, y_ctr, fwd_oval, bwd_oval, rgt_oval, lft_oval);
            interpolate(prev, etto);
            move_x = etto[0];
            move_y = etto[1];
            cur_octant = getOctant(etto, ett, perty.getConfi(), perty.getQuadrants());
            //prt();
            //tvx.setText(Boolean.toString(main_control_pointer.isClutched()));
            if(visual_oval!=null && perty.getPosi()!=3)
                visualFeedback(main_control_pointer.getPointer_x(),main_control_pointer.getPointer_y(),visual_oval,perty.isVisFbk(), false, etto, ett, perty.getConfi(), perty.getQuadrants(), cur_octant, prev_octant, dircnv);
            else
                visualFeedback(main_control_pointer.getPointer_x(),main_control_pointer.getPointer_y(),oval,perty.isVisFbk(), false, etto, ett, perty.getConfi(), perty.getQuadrants(), cur_octant, prev_octant, dircnv);
            audioFeedback(perty.isSnd(), perty.isSnd_on_reg_change(), perty.isSnd_continuous(), etto, perty.getConfi(), perty.getQuadrants(), cur_octant, prev_octant);
            hapticFeedback(perty.isVi(), perty.isVib_on_reg_change(), perty.isVib_continuous(), etto, perty.getConfi(), perty.getQuadrants(), cur_octant, prev_octant);
            prev[0] = etto[0];
            prev[1] = etto[1];
            prev_octant = cur_octant;
        }else{
            ett[0] = 0;
            ett[1] = 0;
            etto[0] = 127;
            etto[1] = 127;
            move_x = 127;
            move_y = 127;
            //prt();
            //tvx.setText(Boolean.toString(main_control_pointer.isClutched()));
            prev[0] = move_x;
            prev[1] = move_y;
            cur_octant = getOctant(etto, ett, perty.getConfi(), perty.getQuadrants());
            visualFeedback(main_control_pointer.getPointer_x(),main_control_pointer.getPointer_y(),oval,perty.isVisFbk(), false, etto, ett, perty.getConfi(), perty.getQuadrants(), cur_octant, prev_octant, dircnv);
            audioFeedback(perty.isSnd(), perty.isSnd_on_reg_change(), perty.isSnd_continuous(), etto, perty.getConfi(), perty.getQuadrants(), cur_octant, prev_octant);
            hapticFeedback(perty.isVi(), perty.isVib_on_reg_change(), perty.isVib_continuous(), etto, perty.getConfi(), perty.getQuadrants(), cur_octant, prev_octant);
            prev_octant = cur_octant;
        }


    }




    //for control
    public float[] interpolate(int[]a,int[]b){
        float[] ret=new float[2];
        int x,y;
        //variables à nom générique
        int xmin,y_xmin,xmax,y_xmax;
        int ymin,x_ymin,ymax,x_ymax;
        x=a[0];
        y=a[1];
        //attribution des variables à nom générique
        if (a[0]<b[0]) {xmin =a[0]; xmax =b[0];    y_xmin=a[1]; y_xmax=b[1];}
        else       {xmin =b[0]; xmax =a[0];    y_xmin=b[1]; y_xmax=a[1];}

        if (a[1]<b[1]) {ymin =a[1]; ymax =b[1];    x_ymin=a[0]; x_ymax=b[0];}
        else       {ymin =b[1]; ymax =a[1];    x_ymin=b[0]; x_ymax=a[0];}

        //Cas d'une ligne horizontale
        if (ymin==ymax) {
            while (x!=b[0]){
                if(x>b[0])x--;
                else x++;
                move_x=x;
                move_y=y;
            }
        }
        //Cas d'une ligne verticale
        if (xmin==xmax) {
            while (y!=b[1]){
                if(y>b[1])y--;
                else y++;
                move_x=x;
                move_y=y;
            }
        }
        //Cas des diagonale
        if ((y_xmax-y_xmin==xmax-xmin)||(y_xmax-y_xmin==xmin-xmax)) {
            if(x-b[0]>0&&y-b[1]>0){
                while(x!=b[0]){
                    x--;
                    y--;
                    move_y=y;
                    move_x=x;
                }
            }else if(x-b[0]<0&&y-b[1]>0){
                while(x!=b[0]){
                    x++;
                    y--;
                    move_y=y;
                    move_x=x;
                }
            }else if(x-b[0]<0&&y-b[1]<0){
                while(x!=b[0]){
                    x++;
                    y++;
                    move_y=y;
                    move_x=x;
                }
            }else{
                while(x!=b[0]){
                    x--;
                    y++;
                    move_y=y;
                    move_x=x;
                }
            }
        }


        //Autres cas
        float t, c=(float)(y_xmax-y_xmin)/(xmax-xmin);

        if (valAbs(c)<1) {
            x = a[0];
            t = y_xmin;
            while (x != b[0]) {
                if (x > b[0]) x--;
                else x++;
                y = arrondi(t);
                if (y > b[1]) t -= c;
                else t += c;
                move_y = y;
                move_x = x;
            }


        } else {
            c=1/c;
            y=a[1];
            t=x_ymin;
            while ( y!=b[1])
            {
                if(y>b[1])y--;
                else y++;
                x=arrondi(t);
                if(x>b[0])t-=c;
                else t+=c;
                move_y=y;
                move_x=x;
            }
        }

        return ret;
    }




    Control_Pointer gravityMotion(MotionEvent eve, char t_st, int[] ord,int number_of_pointers, boolean cltch_b, float[] op,int o_i) {
        Control_Pointer ctr_ptr=new Control_Pointer();
        ctr_ptr.setClutched(cltch_b);
        int idx=0;
        int int_idx=0;
        int[]synth_ord=new int[1];
        ctr_ptr.setPointer_velocity(0);
        try {
            if (number_of_pointers==1){
                int_idx=0;
                idx=eve.findPointerIndex(ord[int_idx]);
                ctr_ptr.setPointer_y(eve.getY(idx));
                ctr_ptr.setPointer_x(eve.getX(idx));
                ctr_ptr.setPointer_size(eve.getSize(idx));
                ctr_ptr.setPointer_pressure(eve.getPressure(idx));
                ctr_ptr.setPointer_action(eve.toString());
                ctr_ptr.ComputeVelocity(eve,ord,1,ppin);
                old_id=ord[int_idx];
            }else if (t_st=='C'){
                ctr_ptr.setPointer_y(0);
                ctr_ptr.setPointer_x(0);
                ctr_ptr.setPointer_size(0);
                ctr_ptr.setPointer_pressure(0);
                ctr_ptr.ComputeVelocity(eve,ord,number_of_pointers,ppin);
                for(int s=0;s<number_of_pointers;s++){
                    if(eve.findPointerIndex(ord[s])!=-1) {
                        ctr_ptr.setPointer_y(ctr_ptr.getPointer_y() + eve.getY(eve.findPointerIndex(ord[s])));
                        ctr_ptr.setPointer_x(ctr_ptr.getPointer_x() + eve.getX(eve.findPointerIndex(ord[s])));
                        ctr_ptr.setPointer_size(ctr_ptr.getPointer_size() + eve.getSize(eve.findPointerIndex(ord[s])));
                        ctr_ptr.setPointer_pressure(ctr_ptr.getPointer_pressure() + eve.getPressure(eve.findPointerIndex(ord[s])));
                        ctr_ptr.setPointer_velocity(ctr_ptr.getPointer_velocity() + eve.findPointerIndex(ord[s]));
                    }
                }
                ctr_ptr.setPointer_y(ctr_ptr.getPointer_y()/number_of_pointers);
                ctr_ptr.setPointer_x(ctr_ptr.getPointer_x() / number_of_pointers);

                ctr_ptr.setPointer_action(eve.toString());
                ctr_ptr.setPointer_velocity(ctr_ptr.getPointer_velocity()/number_of_pointers);
            }else{
                if(eve.getActionMasked()==MotionEvent.ACTION_MOVE) {

                    if(old_id!=-1){
                        for(int ger=0;ger<ord.length;ger++){
                            if(ord[ger]==old_id)int_idx=ger;
                        }
                    }else{
                        if(op[0]!=-1 && op[1]!=-1) {
                            for (int o = 0; o < number_of_pointers; o++) {
                                if (dist_point(eve.getX(eve.findPointerIndex(ord[int_idx])), eve.getY(eve.findPointerIndex(ord[int_idx])), op[0], op[1]) >= dist_point(eve.getX(eve.findPointerIndex(ord[o])), eve.getY(eve.findPointerIndex(ord[o])), op[0], op[1])){
                                    int_idx=o;
                                    //idx = eve.findPointerIndex(ord[o]);
                                }
                            }
                        }
                    }
                    //tvx.setText(Integer.toString(ord[int_idx]));
                    idx=eve.findPointerIndex(ord[int_idx]);
                    synth_ord[0] = ord[int_idx];
                    ctr_ptr.setPointer_y(eve.getY(idx));
                    ctr_ptr.setPointer_x(eve.getX(idx));
                    ctr_ptr.setPointer_size(eve.getSize(idx));
                    ctr_ptr.setPointer_pressure(eve.getPressure(idx));
                    ctr_ptr.setPointer_action(eve.toString());
                    ctr_ptr.ComputeVelocity(eve, synth_ord, 1, ppin);
                    old_id=ord[int_idx];

                }else{
                    if(t_st=='F'){
                        int_idx=eve.findPointerIndex(ord[0]);
                    }else if(t_st=='L'){
                        if(number_of_pointers>1)
                            int_idx=number_of_pointers-1;
                    }else if(t_st=='l'){
                        int_idx=0;
                        for(int o=1;o<number_of_pointers;o++){
                            if(eve.getX(eve.findPointerIndex(ord[int_idx]))>=eve.getX(eve.findPointerIndex(ord[o]))) int_idx=o;
                        }
                    }else if(t_st=='r'){
                        int_idx=0;
                        for(int o = 1; o < number_of_pointers; o++) {
                            if (eve.getX(eve.findPointerIndex(ord[int_idx]))<=eve.getX(eve.findPointerIndex(ord[o]))) int_idx=o;
                        }
                    }else if(t_st=='u'){
                        int_idx=0;
                        for(int o=1;o<number_of_pointers;o++){
                            if(eve.getY(eve.findPointerIndex(ord[int_idx]))>=eve.getY(eve.findPointerIndex(ord[o])))  int_idx=o;
                        }
                    }else if(t_st=='b'){
                        int_idx=0;
                        for(int o=1;o<number_of_pointers;o++){
                            if(eve.getY(eve.findPointerIndex(ord[int_idx]))<=eve.getY(eve.findPointerIndex(ord[o])))  int_idx=o;
                        }
                    }
                    idx=eve.findPointerIndex(ord[int_idx]);
                    synth_ord[0] = ord[int_idx];
                    ctr_ptr.setPointer_y(eve.getY(idx));
                    ctr_ptr.setPointer_x(eve.getX(idx));
                    ctr_ptr.setPointer_size(eve.getSize(idx));
                    ctr_ptr.setPointer_pressure(eve.getPressure(idx));
                    ctr_ptr.setPointer_action(eve.toString());
                    ctr_ptr.ComputeVelocity(eve, synth_ord, 1, ppin);
                    old_id=ord[int_idx];
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
/*                    ctr_ptr.setPointer_y(oval.centerY());
                    ctr_ptr.setPointer_x(oval.centerX());
                    ctr_ptr.setPointer_size(0);
                    ctr_ptr.setPointer_pressure(0);
                    ctr_ptr.setPointer_action("up");
                    ctr_ptr.setPointer_velocity(0);


*/

            try{
                ctr_ptr.setPointer_y(eve.getY());
                ctr_ptr.setPointer_x(eve.getX());
                ctr_ptr.setPointer_size(eve.getSize());
                ctr_ptr.setPointer_pressure(eve.getPressure());
                ctr_ptr.setPointer_action(eve.toString());
                ctr_ptr.ComputeVelocity(eve,ord,1,ppin);
            }catch (Exception ex){
                ex.printStackTrace();
                ctr_ptr.setPointer_y(oval.centerY());
                ctr_ptr.setPointer_x(oval.centerX());
                ctr_ptr.setPointer_size(0);
                ctr_ptr.setPointer_pressure(0);
                ctr_ptr.setPointer_action("up");
                ctr_ptr.setPointer_velocity(0);
                ctr_ptr.unclutch();
                control_region=-1;
            }
            //ctr_ptr.unclutch();
        }




        return ctr_ptr;
    }






    //transforms touch coordinates to polar coordinates
    public float[]toPolar(float tx,float ty, RectF ov){
        float[] ret=new float[2];
        ret[0]=dist_point(tx,ty,ov.centerX(),ov.centerY());
        ret[1]=(float)Math.acos((double)((tx-ov.centerX())/ret[0]));
        if(ty>ov.centerY())
            ret[1]=(float)(2.0*PI-ret[1]);
        return ret;
    }


    //transforms the touch coordinates from polar to joystick reference
    public int[] toJoystick(float speedo,float angle, RectF ov, Canvas cv,Canvas hcnv, int direction_div,int speed_div, float old_speed){
        int[] ret=new int[2];
        float ns=perty.getNeutralZoneSize();
        float ndw = ov.width()*(float)0.5-ns;
        float ndh=ov.height()*(float)0.5-ns;
        float anglet=angle;
        float direct_divisions= (float) Math.pow(2,direction_div);
        pas=360/direct_divisions;
        //pasp=refsize1/(2*direct_divisions);
        //pasp=refsize3/(2*direct_divisions);
        float tp=(float)(angle/PI)*180;
        if(direction_div>=2) {
            if (direction_div >= 2) {
                for (int cb = 0; cb <= direct_divisions; cb++) {
                    if (tp < (cb + 0.5) * pas && tp > (cb - 0.5) * pas) {
                        anglet = pas * cb;
                        break;
                    }
                }
                angle = (float) (anglet * PI / 180);
            }
        }
        old_control_region=control_region;

        //in the upper thumb area
        if (speedo <= refsize1 * 0.5 && speedo > ov.width() * 0.5){
            speedo = ov.width() * (float) 0.5;
            control_region=3;//in the acceleration zone
        }else if(speedo<thumb+ns/2){
            if(speedo>ns) control_region=1;//in the deceleration zone
            else control_region=0;//in the neutral zone
        }else{

            control_region=2;//analog control of the speed
        }
        float velocity = speedo - ns;
        if(speed_div!=0) {
            pasp = (float) (ndw) / speed_div;
            for (int cb = 1; cb <= speed_div; cb++) {
                if (velocity <= cb * pasp && velocity > (cb-1) * pasp) {
                    velocity = pasp * cb;
                    break;
                }
            }
        }
        float xt;
        float yt;
        if(speedo<ns || speedo>ov.width()*(float)0.5){
            xt=ndw;
            yt=ndh;
        }else{
            xt= velocity * (float)cos(angle)+ndw;
            yt=velocity*(float)sin(angle)+ndh;
        }
        xt=xt*255/(2*ndw);
        yt=yt*255/(2*ndh);
        ret[0]=(int)xt;
        ret[1]=(int)yt;
        return ret;
    }


    //function transforms touch coordinates from screen referential to polar coordinates

    public int getOctant(int[] coordinates,float[] polCoordinates,char configuration,int q){
        int res;
            //case of circular
            res=0;
            if((coordinates[0]==127 || coordinates[0]==128) && (coordinates[1]==127 || coordinates[0]==128)){
                res=0;
            }else{
                if(q==4){
                    if (polCoordinates[1] < PI*0.25 || polCoordinates[1] > PI*1.75) {
                        //move right
                        res = 7;
                    } else if (polCoordinates[1] >= PI*0.25 && polCoordinates[1] <= PI*0.75) {
                        //move forward
                        res = 1;
                    } else if (polCoordinates[1] >PI*0.75 && polCoordinates[1] <PI*1.25) {
                        //move left
                        res = 3;
                    } else if (polCoordinates[1] >= PI*1.25 && polCoordinates[1] <= PI*1.75) {
                        //move backward
                        res = 5;
                    } else {
                        //not moving
                        res =0;
                    }
                }else{
                    if (polCoordinates[1] <= PI*0.125 || polCoordinates[1] >= PI*1.875) {
                        //move right
                        res=7;
                    } else if (polCoordinates[1] > PI*0.125 && polCoordinates[1] < PI*0.375) {
                        //move forward-right
                        res=8;
                    } else if (polCoordinates[1] >= PI*0.375 && polCoordinates[1] <= PI*0.625) {
                        //move forward
                        res=1;
                    } else if (polCoordinates[1] > PI*0.625 && polCoordinates[1] < PI*0.875) {
                        //move forward-left
                        res=2;
                    } else if (polCoordinates[1] >= PI*0.875 && polCoordinates[1] <= PI*1.125) {
                        //move left
                        res=3;
                    } else if (polCoordinates[1] > PI*1.125 && polCoordinates[1] < PI*1.375) {
                        //move backward-left
                        res=4;
                    } else if (polCoordinates[1] >= PI*1.375 && polCoordinates[1] <= PI*1.625) {
                        //move backward
                        res=5;
                    } else if (polCoordinates[1] > PI*1.625 && polCoordinates[1] < PI*1.875) {
                        //move backward-right
                        res=6;
                    } else {
                        //not moving
                        res=0;
                    }
                }
            }

        return res;
    }



    static int[] toJoystick_but(float a, float b, RectF fo,RectF ba,RectF ri,RectF le){

        int[] re=new int[2];

        if(a>=fo.left && a<=fo.right && b>=fo.top && b <=fo.bottom){//forward
            re[0]=127;
            re[1]=255;
        }else if(a>=ba.left && a<=ba.right && b>=ba.top && b <=ba.bottom){//backward
            re[0]=127;
            re[1]=0;
        }else if(a>=ri.left && a<=ri.right && b>=ri.top && b <=ri.bottom){//right
            re[0]=255;
            re[1]=127;
        }else if(a>=le.left && a<=le.right && b>=le.top && b <=le.bottom){//left
            re[0]=0;
            re[1]=127;
        }else{
            re[0]=127;
            re[1]=127;
        }

        return re;

    }



    /***********************feedback*******************************/
    public void visualFeedback(float cx, float cy, RectF v_ov ,boolean enabled,boolean continuous,int[] coordinates,float[] pol_coordinates,char configuration,int q, int currentOctant, int prevOctant, Canvas dcnv) {

        if (enabled) {
            //core for visual feedback
            if (perty.getConfi() == 'b') {
            }else{
                //set the blinking period proportional to the speed
                anim.setDuration((long) (100 * refsize3 / (pol_coordinates[0] + 1)));
                if (currentOctant != prevOctant) {
                    visfbk.setBackground(null);
                    anim.cancel();
                    if (currentOctant == 7) {
                        //move right
                        visfbk.setBackgroundResource(R.drawable.ic_rgt);
                        visfbk.startAnimation(anim);
                    } else if (currentOctant == 8) {
                        //move forward-right
                        visfbk.setBackgroundResource(R.drawable.arrow4);
                        visfbk.startAnimation(anim);
                    } else if (currentOctant == 1) {
                        //move forward
                        visfbk.setBackgroundResource(R.drawable.arrow1);
                        visfbk.startAnimation(anim);
                    } else if (currentOctant == 2) {
                        //move forward-left
                        visfbk.setBackgroundResource(R.drawable.arrow3);
                        visfbk.startAnimation(anim);
                    } else if (currentOctant == 3) {
                        //move left
                        visfbk.setBackgroundResource(R.drawable.ic_lft);
                        visfbk.startAnimation(anim);
                    } else if (currentOctant == 4) {
                        //move backward-left
                        visfbk.setBackgroundResource(R.drawable.arrow2);
                        visfbk.startAnimation(anim);
                    } else if (currentOctant == 5) {
                        //move backward
                        visfbk.setBackgroundResource(R.drawable.arrow5);
                        visfbk.startAnimation(anim);
                    } else if (currentOctant == 6) {
                        //move backward-right
                        visfbk.setBackgroundResource(R.drawable.arrow6);
                        visfbk.startAnimation(anim);
                    } else {
                        //not moving
                        visfbk.setBackground(null);
                        anim.cancel();
                    }
                }
            }
        }
        if(perty.getConfi()=='c'){
            //define angles
            if(perty.getTouch_Strategy()=='C' && perty.getPosi()==3)v_ov=new RectF(oval);

            float angle=pol_coordinates[1];
            float anglet=angle;
            int qqi=perty.getPrecision();
            float qqi_draw= (float) Math.pow(2,qqi);
            float pas_draw=360/qqi_draw;
            float tp=(float)(angle/PI)*180;
            if(qqi>=2) {
                for (int cb = 0; cb <= qqi_draw; cb++) {
                    if (tp < (cb + 0.5) * pas_draw && tp > (cb - 0.5) * pas_draw) {
                        anglet = pas_draw * cb;
                        break;
                    }
                }
                angle = anglet;
            }

            Paint p1=new Paint();
            p1.setColor(Color.parseColor("GREEN"));
            Paint pnt = new Paint();

            pnt.setColor(Color.parseColor("WHITE"));
            pnt.setStyle(Paint.Style.FILL);

            //////paint for neutral zone
            Paint n_pnt= new Paint();
            n_pnt.setColor(Color.parseColor(perty.getNeutral_color()));
            Display disp = getWindowManager().getDefaultDisplay();
            Point sz = new Point();
            disp.getSize(sz);
            dcnv.drawRect(0,0,sz.x,sz.y,black_plain_paint);
            paintInterfaceCircular(dcnv, v_ov,(float) (thumb/2.0),perty.getNeutralZoneSize(), white_plain_paint,grey_plain_paint);

            //define speed

            float qq_speed=perty.getSpeedincrement();


            float pasp_draw=(((v_ov.width()/2)-perty.getNeutralZoneSize())/qq_speed);

            if(qq_speed>0 && (!((coordinates[0]==127 || coordinates[0]==128 )&&(coordinates[1]==127 || coordinates[1]==128 )))) {
                pasp = (float)(v_ov.width() * 0.5-perty.getNeutralZoneSize()) / qq_speed;

                for(int cnc=(int)qq_speed;cnc>=1;cnc--){
                    RectF tmprec=new RectF(v_ov.centerX()-(cnc*pasp_draw)-perty.getNeutralZoneSize(),
                            v_ov.centerY()-(cnc*pasp_draw)-perty.getNeutralZoneSize(),
                            v_ov.centerX()+(cnc*pasp_draw)+perty.getNeutralZoneSize(),
                            v_ov.centerY()+(cnc*pasp_draw)+perty.getNeutralZoneSize());

                    float[] col_vals={50, 1, 1};
                    if(qq_speed>1){
                            col_vals[0]=(float)((-90*cnc)/(qq_speed-1))+(90*qq_speed/(qq_speed-1));
                    }

                    p1.setColor(HSVToColor(col_vals));
                    p1.setStyle(Paint.Style.FILL);
                    dcnv.drawArc(tmprec, (-angle) - pas_draw / 2, pas_draw, true, p1);
                    p1.setStyle(Paint.Style.STROKE);
                    dcnv.drawArc(tmprec, (-angle) - pas_draw/2, pas_draw, true, p1);
                }

                dcnv.drawCircle(v_ov.centerX(), v_ov.centerY(), perty.getNeutralZoneSize(), grey_plain_paint);
                dcnv.drawCircle(v_ov.centerX(), v_ov.centerY(), perty.getNeutralZoneSize() / 10, white_plain_paint);
            }
        }

    }


    public void audioFeedback(boolean enabled, boolean region,boolean continuous,int[] coordinates,char configuration, int q, int currentOctant, int prevOctant){
        if(enabled){
            if(continuous){
                if(currentOctant==prevOctant && currentOctant!=0) {
                    try{
                        if (bloppy.isPlaying()) bloppy.pause();
                        bloppy.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                if((currentOctant!=prevOctant)) {
                    if(region || (!region &&(currentOctant==0||prevOctant==0)))
                    {
                        try{
                            if (bloppy.isPlaying()) bloppy.pause();
                            bloppy.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public void hapticFeedback(boolean enabled,boolean region,boolean continuous,int[] coordinates,char configuration, int q, int currentOctant, int prevOctant){
        if(enabled){
            if((prevOctant==0 && currentOctant!=0)||(prevOctant!=0 && currentOctant==0)){
                vib.vibrate(90);
            }
            //core for haptic feedback
            if (continuous) {
                if (currentOctant == 0||currentOctant!=prevOctant) {//continuous vib config
                    vib.cancel();
                } else if(currentOctant==prevOctant){//not on boundaries
                    vib.vibrate(6000);
                }

            }else{
                if(currentOctant!=prevOctant && region)
                    vib.vibrate(90);
            }

        }

    }





    /*****************************utilities***************************************/



    //calculates the distance between 2 points given their respective coordinates
    public static float dist_point(float a, float b, float c, float d){
        return(float) Math.sqrt(Math.pow(a - c, 2) + Math.pow(b- d, 2));
    }


    //for button-configuration calibration

    public boolean rectangle_touch2(RectF r1, RectF r2){
        float xtt,ytt;
        boolean comp_result=false;

        for (ytt=r1.top;ytt<=r1.bottom && !comp_result;ytt++){
            for(xtt=r1.left;xtt<=r1.right && !comp_result;xtt++){
                comp_result=((ytt-r2.top>0 && r2.bottom-ytt>0 && r2.right-xtt>0 && xtt-r2.left>0));
            }
        }
        return comp_result;
    }


    public boolean rectangle_collapse(RectF r,float lim){
        return (r.bottom-r.top<lim || r.right-r.left<lim);
    }


    //math
    public float valAbs(float j){
        if(j<0)return -j;
        else return j;

    }


    public int arrondi(float j){
        return (int)(j+0.5);
    }



    //prompt
    public void prt(){
        if (perty.isBugmd()) {
            tvx.setText(Integer.toString(move_x));
            tvy.setText(Integer.toString(move_y));
        }
    }


    public void prt2(){
        if (perty.isBugmd()) {
            tvx.setText(Integer.toString(moved_button));
            tvy.setText(Integer.toString(scaled_button));
        }
    }




}