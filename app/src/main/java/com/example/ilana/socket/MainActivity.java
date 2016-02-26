package com.example.ilana.socket;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends Activity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);



        ImageButton send = (ImageButton) findViewById(R.id.send_button);//Connect button

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText ip = (EditText) findViewById(R.id.insert_ip); //Get ip from EditText
                EditText port = (EditText) findViewById(R.id.insert_port); //Get port from EditText
                Intent i = new Intent(MainActivity.this, Controller.class); //Passing variables to Controller activity
                i.putExtra("ip", ip.getText().toString());
                i.putExtra("port", port.getText().toString());
                MainActivity.this.startActivity(i); //Go to Controller Activity
            }
        });

    }

}

