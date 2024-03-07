package com.example.myapplication;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
import java.util.Objects;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;

import java.util.List;

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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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


        movementTextView.setText(movement);

        if (Objects.equals(movement, "RIGHT")){


            String message = "-800"; // This is the padding value that you want to send
            findConnectedNodeAndSendMessage(message); // send message via bluetooth


        } else{


            String message = "100"; // This is the padding value that you want to send
            findConnectedNodeAndSendMessage(message); // send message via bluetooth


        }

    }

    private void findConnectedNodeAndSendMessage(String message) {
        Task<List<Node>> nodeListTask = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
        nodeListTask.addOnSuccessListener(new OnSuccessListener<List<Node>>() {
            @Override
            public void onSuccess(List<Node> nodes) {
                for (Node node : nodes) {
                    if (node.isNearby()) { // Optional: Check if the node is nearby
                        sendMessage(node.getId(), message);
                        Log.d("Padding value sent to the server: ", message);
                        Log.d("Node the watch sends to: ", node.getDisplayName());
                        break;
                    }
                }
            }
        });
    }

    private void sendMessage(String nodeId, String message) {
        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(nodeId, "/motion_path", message.getBytes());

        sendMessageTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                // Message was sent successfully
                Log.d("Padding value sent to the server: ", message);
            }
        });

        sendMessageTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure in sending message
                Log.e("MessageSender", "Message failed to send", e);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this demo.
    }
}


