package com.example.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.io.File;

import android.os.Bundle;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView textView_acc, textView_gyro, textView_mag, textInfo;
    private String fileName_acc = "/acc.txt";
    private String fileName_gyro = "/gyro.txt";
    private String fileName_mag = "/mag.txt";
    private float sensorX_acc, sensorY_acc, sensorZ_acc;
    private float sensorX_gyro, sensorY_gyro, sensorZ_gyro;
    private float sensorX_mag, sensorY_mag, sensorZ_mag;
    final Handler handler = new Handler();
    private boolean wait_time = true;

    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //textInfo = findViewById(R.id.text_info);

        // Get an instance of the TextView
        textView_acc = findViewById(R.id.text_view_acc);
        textView_gyro = findViewById(R.id.text_view_gyro);
        textView_mag = findViewById(R.id.text_view_mag);

        //save file
        Button button_start = findViewById(R.id.button_start);
        button_start.setText("Start");
        Button button_save = findViewById(R.id.button_save);
        button_save.setText("Save");

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start recording the sensor data
                flag = 1;
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save the sensor data
                flag = 0;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Listener
        Sensor acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if(acc != null){
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_UI);
        }
        else{
            String ns = "No Support";
            textView_acc.setText(ns);
        }
        if(gyro != null){
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_UI);
        }
        else{
            String ns = "No Support";
            textView_gyro.setText(ns);
        }
        if(mag != null){
            sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_UI);
        }
        else{
            String ns = "No Support";
            textView_mag.setText(ns);
        }
    }

    // enter the code to unlock!
    @Override
    protected void onPause() {
        super.onPause();
        // Listenerを解除
        sensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
//        Log.d("debug","onSensorChanged");

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorX_acc = event.values[0];
            sensorY_acc = event.values[1];
            sensorZ_acc = event.values[2];

            String strTmp_acc = String.format(Locale.US, "Accelerometer\n " +
                    " X: %f\n Y: %f\n Z: %f",sensorX_acc, sensorY_acc, sensorZ_acc);
            textView_acc.setText(strTmp_acc);

            //showInfo(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            sensorX_gyro = event.values[0];
            sensorY_gyro = event.values[1];
            sensorZ_gyro = event.values[2];

            String strTmp_gyro = String.format(Locale.US, "Gyroscope\n " +
                    " X: %f\n Y: %f\n Z: %f",sensorX_gyro, sensorY_gyro, sensorZ_gyro);
            textView_gyro.setText(strTmp_gyro);

            //showInfo(event);
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            sensorX_mag = event.values[0];
            sensorY_mag = event.values[1];
            sensorZ_mag = event.values[2];

            String strTmp_mag = String.format(Locale.US, "Magnetic Field\n " +
                    " X: %f\n Y: %f\n Z: %f",sensorX_mag, sensorY_mag, sensorZ_mag);
            textView_mag.setText(strTmp_mag);

            //showInfo(event);
        }
        //save
       if(flag == 1){
           String str_acc;
           String str_gyro;
           String str_mag;
           String filePath;
           String sdf1;

           File file = new File(this.getExternalFilesDir(null) + "/Notes");
           if(!file.exists()){
               file.mkdirs();
           }
           filePath = String.valueOf(file);


           sdf1 = getTimelocal();
           // writing the accuracy values.
           try {
                //Log.i("FILE_PATH", LOCAL_FILE);// check the output file location with this.
               //create a directory, filepath+filenameacc.txt
               FileOutputStream fos = new FileOutputStream(filePath + fileName_acc, true);//true ; add more
               OutputStreamWriter osw = new OutputStreamWriter(fos,"Shift-JIS");//Excel SHIFT_JIS is good.
               BufferedWriter bw = new BufferedWriter(osw);
               str_acc = sensorX_acc + "," + sensorY_acc + "," + sensorZ_acc;
               bw.write(sdf1 +"," + str_acc+"\n");
               bw.flush();
               bw.close();
               Log.d("acc", str_acc);
           } catch (IOException e) {
               e.printStackTrace();
           }
           //gyro
           try {
               FileOutputStream fos = new FileOutputStream(filePath + fileName_gyro,true);//true ; add mode
               OutputStreamWriter osw = new OutputStreamWriter(fos,"Shift-JIS");//Excel ; SHIFT JIS is a good mode.
               BufferedWriter bw = new BufferedWriter(osw);
               str_gyro = sensorX_gyro + "," + sensorY_gyro + "," + sensorZ_gyro;
               bw.write(sdf1 +"," + str_gyro+"\n");
               bw.flush();
               bw.close();
               Log.d("gyro", str_gyro);
           } catch (IOException e) {
               e.printStackTrace();
           }
           //mag
           try {
               FileOutputStream fos = new FileOutputStream(filePath + fileName_mag,true);//true
               OutputStreamWriter osw = new OutputStreamWriter(fos,"Shift-JIS");//Excel
               BufferedWriter bw = new BufferedWriter(osw);
               str_mag = sensorX_mag + "," + sensorY_mag + "," + sensorZ_mag;
               bw.write(sdf1 +"," + str_mag+"\n");
               bw.flush();
               bw.close();
               Log.d("mag", str_mag);
           } catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // display various information of the sensor
    private void showInfo(SensorEvent event){
        // Sensor Name
        StringBuffer info = new StringBuffer("Name: ");
        info.append(event.sensor.getName());
        info.append("\n");

        // Vendor Name
        info.append("Vendor: ");
        info.append(event.sensor.getVendor());
        info.append("\n");

        // Model Number
        info.append("Type: ");
        info.append(event.sensor.getType());
        info.append("\n");

        // the minimum delay
        int data = event.sensor.getMinDelay();
        info.append("Mindelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // maximum delay
        data = event.sensor.getMaxDelay();
        info.append("Maxdelay: ");
        info.append(String.valueOf(data));
        info.append(" usec\n");

        // Report Mode
        data = event.sensor.getReportingMode();
        String stinfo = "unknown";
        if(data == 0){
            stinfo = "REPORTING_MODE_CONTINUOUS";
        }else if(data == 1){
            stinfo = "REPORTING_MODE_ON_CHANGE";
        }else if(data == 2){
            stinfo = "REPORTING_MODE_ONE_SHOT";
        }
        info.append("ReportingMode: ");
        info.append(stinfo);
        info.append("\n");

        // Maximum Range
        info.append("MaxRange: ");
        float fData = event.sensor.getMaximumRange();
        info.append(String.valueOf(fData));
        info.append("\n");

        // Resolution
        info.append("Resolution: ");
        fData = event.sensor.getResolution();
        info.append(String.valueOf(fData));
        info.append(" m/s^2\n");

        // Power
        info.append("Power: ");
        fData = event.sensor.getPower();
        info.append(String.valueOf(fData));
        info.append(" mA\n");

        //textInfo.setText(info);
    }
    //log The time measurement output.
    public static String getTimelocal(){
        java.text.DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSS", Locale.US);//EXCELで編集しやすいようにこのフォーマット
        df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        return df.format(new Date());
    }

    public void wait_time(){
        wait_time = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                wait_time = true;
            }
        }, 100);

    }
}
