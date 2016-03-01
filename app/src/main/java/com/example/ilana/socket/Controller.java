package com.example.ilana.socket;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class Controller extends Activity implements SensorEventListener {
    /*initialization*/
    Sensor accelerometer;
    SensorManager sm;
    TextView acceleration;
    final static int NORMALAIZE = 5;
    final Object lock = new Object();
    double startTime = System.currentTimeMillis();
    double delay =0;
    double boundaryYbottom = 4.5;
    double boundaryYup = 5.5;
    double boundaryXbottom = -1;
    double boundaryXup = 1;
    /*Control the amount of messages in a certain period*/
    int delayTimer = 1000;


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

        //gyro configuration
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView) findViewById(R.id.acceleration);

        /*Up button*/
        final ImageButton upButton = (ImageButton) this.findViewById(R.id.up);
        upButton.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int z = 10;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                /*Sending a command that influence z axis - up*/
                    ConnectTask.mTcpClient.sendMessage("moveby 0 0 " + z + "\n");
                }
                return true;
            }
        });

        /*Down button*/
        final ImageButton downButton = (ImageButton) this.findViewById(R.id.down);
        downButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int z = -10;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    /*Sending a command that influence z axis - down*/
                    ConnectTask.mTcpClient.sendMessage("moveby 0 0 " + z + "\n");
                }
                return true;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (lock) {
            delay = System.currentTimeMillis() - startTime;
            System.out.println(delay);

            /*Handling with accelerometer*/
            acceleration.setText("X: " + event.values[0] +
                    "\nY: " + event.values[1]);

            /*Calculate the value that x and y should be for sending to the server */
            int x = calcX(event) * NORMALAIZE;
            int y = calcY(event) * NORMALAIZE;

        /*Handling with timer*/
        if(delay < delayTimer) {return;}
            else {
            delay = 0;
            startTime = System.currentTimeMillis();

            /*Sending the command*/
            ConnectTask.mTcpClient.sendMessage("moveby " + x + " " + y + " 0\n");
            return;
        }
    }
}
        @Override
        public void onAccuracyChanged (Sensor sensor,int accuracy){

    }

    /*Calc y axis*/
    public int calcY(SensorEvent event) {
        int y = (int)event.values[0] ; /* y get the value from the accelerometer */
        if (y > boundaryYup) {
            y = (y - 5) * 4;/*Calculation of growth of y*/
            return -y;
        } else if (y < boundaryYbottom) {
            return -y;
        } else {
            return 0;
        }
    }

    /*Calc x axis*/
    public int calcX(SensorEvent event) {
        int x = (int)event.values[1]; /*x get the value from the accelerometer*/
        if (x > boundaryXup || x < boundaryXbottom) {
            x *= 2; /*Calculation of growth of x */
            return x;
        } else {
            return 0;
        }
    }
}

