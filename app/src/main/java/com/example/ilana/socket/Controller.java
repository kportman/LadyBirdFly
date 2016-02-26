package com.example.ilana.socket;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Timer;

public class Controller extends Activity implements SensorEventListener {
    Sensor accelerometer;
    SensorManager sm;
    TextView acceleration;
    final static int NORMALAIZE = 5;
    final Object lock = new Object();
    //Integer count = 0;
    double startTime = System.currentTimeMillis();
    double delay =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controller);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        //Get ip and port from MainActivity
        Bundle i = getIntent().getExtras();
        String ip = i.getString("ip");
        String port = i.getString("port");


        //Open tcp socket
        new ConnectTask(ip, port).execute("");
        if (ConnectTask.mTcpClient == null) {
            return;
        }

        //gyro conficuration
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView) findViewById(R.id.acceleration);

        //Buttons
        final ImageButton upButton = (ImageButton) this.findViewById(R.id.up);
        upButton.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int z = 10;
                // PRESSED
                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    ConnectTask.mTcpClient.sendMessage("moveby 0 0 " + z + "\n");

                }
                return true;

            }
        });

        final ImageButton downButton = (ImageButton) this.findViewById(R.id.down);
        downButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int z = -10;
                // PRESSED
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    ConnectTask.mTcpClient.sendMessage("moveby 0 0 " + z + "\n");
                }
                return true;
            }
        });
    }

    // up button

       /* String str = "moveby 0 -200 0\n";
        byte[] buf;
        try {
            buf = str.getBytes("UTF-8");
            ConnectTask.mTcpClient.sendMessage(buf);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (lock) {
            delay = System.currentTimeMillis() - startTime;
            System.out.println(delay);

            //count++;
            acceleration.setText("X: " + event.values[0] +
                    "\nY: " + event.values[1]);

            int x = calcX(event) * NORMALAIZE;
            int y = calcY(event) * NORMALAIZE;


        if(delay < 1000) {return;}
            else {
            delay = 0;
            startTime = System.currentTimeMillis();

            ConnectTask.mTcpClient.sendMessage("moveby " + x + " " + y + " 0\n");
            return;
        }



    }

}




        @Override
        public void onAccuracyChanged (Sensor sensor,int accuracy){

    }


    public int calcY(SensorEvent event) {
        int y = (int)event.values[0] ;
        if (y > 5.5) {
            y = (y - 5) * 4;
            return -y;
        } else if (y < 4.5) {
           // y += -10;
            return -y;
        } else {
            return 0;
        }
    }

    public int calcX(SensorEvent event) {
        int x = (int)event.values[1];
        if (x > 1 || x < -1) {
            x *= 2;
            return x;
        } else {
            return 0;
        }
    }

}

