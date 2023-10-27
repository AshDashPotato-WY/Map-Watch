package com.example.myapplication;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float lastX, lastY, lastZ;
    private TextView movementTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movementTextView = findViewById(R.id.movementTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float deltaX = lastX - x;
        float deltaY = lastY - y;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (Math.abs(deltaX) > 5) {
                if (deltaX < 0) {
                    try {
                        displayMovement("RIGHT");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        displayMovement("LEFT");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            if (Math.abs(deltaY) > 5) {
                if (deltaY < 0) {
                    try {
                        displayMovement("DOWN");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        displayMovement("UP");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        lastX = x;
        lastY = y;
        lastZ = z;
    }

    private void displayMovement(String movement) throws InterruptedException {

//        String serverIP = "192.168.1.27"; // Replace with the IP address of your Android device
//        int serverPort = 9876; // The port should match the server's listening port

        movementTextView.setText(movement);

        if (Objects.equals(movement, "RIGHT")){


            String message = "-800"; // This is the padding value that you want to send


            updclient Udpclient = new updclient(message);

            Udpclient.start();

            Udpclient.join();

        } else{


            String message = "100"; // This is the padding value that you want to send

            updclient Udpclient = new updclient(message);

            Udpclient.start();

            Udpclient.join();

        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this demo.
    }
}


//package com.example.myapplication;
//
//import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.util.Objects;
//
//public class MainActivity extends AppCompatActivity implements SensorEventListener {
//
//    private SensorManager sensorManager;
//    private Sensor accelerometer;
//
//    private float lastX, lastY, lastZ;
//    private TextView movementTextView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        movementTextView = findViewById(R.id.movementTextView);
//
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        sensorManager.unregisterListener(this);
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        float x = event.values[0];
//        float y = event.values[1];
//        float z = event.values[2];
//
//        float deltaX = lastX - x;
//        float deltaY = lastY - y;
//
//        if (Math.abs(deltaX) > Math.abs(deltaY)) {
//            if (Math.abs(deltaX) > 5) {
//                if (deltaX < 0) {
//                    displayMovement("RIGHT");
//                } else {
//                    displayMovement("LEFT");
//                }
//            }
//        } else {
//            if (Math.abs(deltaY) > 5) {
//                if (deltaY < 0) {
//                    displayMovement("DOWN");
//                } else {
//                    displayMovement("UP");
//                }
//            }
//        }
//
//        lastX = x;
//        lastY = y;
//        lastZ = z;
//    }
//
//    private void displayMovement(String movement) {
//        String serverIP = "192.168.1.27";
//        int serverPort = 9876;
//
//        movementTextView.setText(movement);
//
//        String message = Objects.equals(movement, "RIGHT") ? "800" : "800";
//
//        new SendUDPPacketTask().execute(serverIP, String.valueOf(serverPort), message);
//    }
//
//    private static class SendUDPPacketTask extends AsyncTask<String, Void, Void> {
//        @Override
//        protected Void doInBackground(String... params) {
//            try {
//                InetAddress serverAddress = InetAddress.getByName(params[0]);
//                int serverPort = Integer.parseInt(params[1]);
//                String message = params[2];
//
//                try (DatagramSocket socket = new DatagramSocket()) {
//                    byte[] buffer = message.getBytes();
//                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
//                    socket.send(packet);
//                    System.out.println("Padding value sent to the server: " + message);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Not used in this demo.
//    }
//}
//
//
//
