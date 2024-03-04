package com.example.myapplication;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.SpeechRecognizer;
import android.util.Log;
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
    private SpeechRecognizer speechRecognizer;
    private String voiceCommand = "";

    private long lastGestureTime = 0;
    private static final long GESTURE_DEBOUNCE_MS = 500;

    private FileWriter acceWriter;

    private FileWriter gyroWriter;
    private File acceFile;
    private File gyroFile;

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/model-test";

    long startTime = System.nanoTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movementTextView = findViewById(R.id.movementTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);


        createFileForData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(gyroscopeListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
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
        try {
            if (gyroWriter != null) {
                gyroWriter.flush(); // Flush data when the app is paused
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sensorManager.unregisterListener(accelerometerListener);
        sensorManager.unregisterListener(gyroscopeListener);
//        startVoiceRecognition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flushAndCloseWriter(acceWriter);
        flushAndCloseWriter(gyroWriter);
    }

    private void createFileForData() {
        acceFile = createFile("acceData.txt");
        gyroFile = createFile("gyroData.txt");

        try {
            acceWriter = new FileWriter(acceFile, false); // false to overwrite
            gyroWriter = new FileWriter(gyroFile, false); // false to overwrite
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
            long timeDuration = (System.nanoTime() - startTime) / 1000000;
            try {
                if (acceWriter != null) {
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
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used in this demo.
        }
    };

    // Gyroscope sensor
    SensorEventListener gyroscopeListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // event.values contain the rate of rotation around the x, y, and z axis.
            float x = event.values[0]; // Angular speed around x-axis
            float y = event.values[1]; // Angular speed around y-axis
            float z = event.values[2]; // Angular speed around z-axis

            // print out in a gyroscopeData.txt
            long timeDuration = (System.nanoTime() - startTime) / 1000000;
            try {
                if (gyroWriter != null) {
                    String data = x + " " + y + " " + z + " " + timeDuration+ "\n";
                    Log.d("Gyroscope Sensor", data);
                    gyroWriter.append(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


//    private void addValue(ArrayList<Float> values, float newValue) {
//        if (values.size() >= SMOOTHING_WINDOW_SIZE) {
//            values.remove(0);
//        }
//        values.add(newValue);
//    }
//
//    private float getAverage(ArrayList<Float> values) {
//        float sum = 0;
//        for (Float value : values) {
//            sum += value;
//        }
//        return values.isEmpty()? 0 : sum/values.size();
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        // Add the latest sensor values to the smoothing buffers
//        addValue(xValues, event.values[0]);
//        addValue(yValues, event.values[1]);
//        addValue(zValues, event.values[2]);
//
//        // Calculate the average (smoothed) values
//        float x = getAverage(xValues);
//        float y = getAverage(yValues);
//        float z = getAverage(zValues);
//
//        float deltaX = lastX - x;
//        float deltaY = lastY - y;
//        float deltaZ = lastZ - z;
//
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastGestureTime > GESTURE_DEBOUNCE_MS) {
//            // Your gesture detection logic
//            if (Math.abs(deltaX) > Math.abs(deltaY)) {
//                if (Math.abs(deltaX) > 5) {
//                    if (deltaX < 0) {
//                        try {
//                            displayMovement("RIGHT");
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    } else {
//                        try {
//                            displayMovement("LEFT");
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            } else {
//                if (Math.abs(deltaY) > 5) {
//                    if (deltaY < 0) {
//                        try {
//                            displayMovement("DOWN");
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    } else {
//                        try {
//                            displayMovement("UP");
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                }
//            }
//
//            lastGestureTime = currentTime;
//        }
//
//        lastX = x;
//        lastY = y;
//        lastZ = z;
//    }

    private void displayMovement(String movement) throws InterruptedException {

        movementTextView.setText(movement);

    }


}


