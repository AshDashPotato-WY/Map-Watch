package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private float lastX, lastY, lastZ;
    private TextView movementTextView;

    private long lastGestureTime = 0;
    private static final long GESTURE_DEBOUNCE_MS = 500;

    private FileWriter acceWriter;

    private FileWriter gyroWriter;
    private File acceFile;
    private File gyroFile;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/model-test";

    long startTime = System.nanoTime();

    private boolean isButtonStart = false; // click button once or twice
    private Button startButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movementTextView = findViewById(R.id.movementTextView);
        startButton = findViewById(R.id.startButton);
        // click button to start recording sensor data and click again to stop
        startButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                if (isButtonStart) {
                    Log.d("BUTTONS", "User tapped the button: stop");
                    // To Do: stop recording
                    sensorManager.unregisterListener(accelerometerListener);
                    startButton.setText("Start");
                }
                else {
                    Log.d("BUTTONS", "User tapped the button: start");
                    // start recording
                    sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    startButton.setText("Stop");
                }
                isButtonStart = !isButtonStart;
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        // sensorManager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        // write sensor data in a file then export it later for testing purpose
        createFileForData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        // sensorManager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (acceWriter != null) {
                acceWriter.flush(); // Flush data when the app is paused
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // pause the activity
        if (isButtonStart) {
            sensorManager.unregisterListener(accelerometerListener);
            isButtonStart = false;
        }

//        sensorManager.unregisterListener(gyroscopeListener);
//        startVoiceRecognition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flushAndCloseWriter(acceWriter);
//        flushAndCloseWriter(gyroWriter);
    }


    private void createFileForData() {
        acceFile = createFile("acceData.txt");
//        gyroFile = createFile("gyroData.txt");

        try {
            acceWriter = new FileWriter(acceFile, false); // false to overwrite
//            gyroWriter = new FileWriter(gyroFile, false); // false to overwrite
        } catch (IOException e) {
            Log.e("ERROR", "File writer creation error", e);
        }
    }

    private File createFile(String fileName) {
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyAppFolder");
        if (!folder.exists() && !folder.mkdirs()) {
            Log.e("ERROR", "Cannot create folder: " + folder.getAbsolutePath());
            return null;
        }

        File file = new File(folder, fileName);
        try {
            if (!file.exists() && !file.createNewFile()) {
                Log.e("ERROR", "Cannot create file: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            Log.e("ERROR", "Cannot create file: " + file.getAbsolutePath(), e);
        }
        return file;
    }

    private void flushAndCloseWriter(FileWriter writer) {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    SensorEventListener accelerometerListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isButtonStart) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float deltaX = lastX - x;
                float deltaY = lastY - y;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (Math.abs(deltaX) > 5) {
                        if (deltaX < 0) {
                            try {
                                displayMovement("Forward");
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (deltaX >= 0) {
                            try {
                                displayMovement("Backward");
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

                // write data to file
                long currentTimeMillis = System.currentTimeMillis();
                long timeDuration = currentTimeMillis - lastGestureTime;
                try {
                    if (acceWriter != null && lastGestureTime != 0) {
                        String data = x + " " + y + " " + z + " " + timeDuration + "\n";
                        Log.d("Accelerometer Sensor", data);
                        acceWriter.append(data);
                        // fileWriter.flush(); // ensure data is written to the file
//                String time = "Time Duration: " + timeDuration + "\n\n";
//                System.out.print(time);
//                fileWriter.write(time);
//                fileWriter.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                lastGestureTime = currentTimeMillis;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used in this demo.
        }
    };

    private void displayMovement(String movement) throws InterruptedException {
        movementTextView.setText(movement);
    }

}


