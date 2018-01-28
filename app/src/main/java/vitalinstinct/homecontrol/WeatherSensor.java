package vitalinstinct.homecontrol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.android.things.contrib.driver.bmx280.Bmx280SensorDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.content.ContentValues.TAG;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Reece on 21/11/2017, vitalinstinct.homecontrol, HomeControl2
 */

public class WeatherSensor {

    private SensorManager mSensorManager;
    private Bmx280SensorDriver mEnvironmentalSensorDriver;
    SensorManager.DynamicSensorCallback mDynamicSensorCallback;
    private float temperature, pressure, cpu_temp;

    public WeatherSensor(Context context)
    {
        sensorCreate();
        mSensorManager = ((SensorManager) context.getSystemService(SENSOR_SERVICE));
        try {
            mEnvironmentalSensorDriver = new Bmx280SensorDriver("I2C1");
            mSensorManager.registerDynamicSensorCallback(mDynamicSensorCallback);
            mEnvironmentalSensorDriver.registerTemperatureSensor();
            mEnvironmentalSensorDriver.registerPressureSensor();
            Log.d(TAG, "Initialized I2C BMP280");
        } catch (IOException e) {
            throw new RuntimeException("Error initializing BMP280", e);
        }
    }


    public float getTemperature() {
        cpu_temp = getCpuTemp();
        return (cpu_temp-temperature);
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }


    public void sensorCreate()
    {
        mDynamicSensorCallback
                = new SensorManager.DynamicSensorCallback() {
            @Override
            public void onDynamicSensorConnected(android.hardware.Sensor sensor) {
                if (sensor.getType() == android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE) {
                    // Our sensor is connected. Start receiving temperature data.
                    mSensorManager.registerListener(mTemperatureListener, sensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                } else if (sensor.getType() == android.hardware.Sensor.TYPE_PRESSURE) {
                    // Our sensor is connected. Start receiving pressure data.
                    mSensorManager.registerListener(mPressureListener, sensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                }
            }

            @Override
            public void onDynamicSensorDisconnected(android.hardware.Sensor sensor) {
                super.onDynamicSensorDisconnected(sensor);
                sensorCreate();
            }
        };
    }

    private SensorEventListener mTemperatureListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            temperature = event.values[0];
           // Log.d(TAG, "temp changed: " + temperature);
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
         //   Log.d(TAG, "accuracy changed: " + accuracy);
        }
    };

    private SensorEventListener mPressureListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            pressure = event.values[0];
            // Log.d(TAG, "pressure changed: " + pressure);
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {
           // Log.d(TAG, "accuracy changed: " + accuracy);
        }
    };

    public float getCpuTemp() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = reader.readLine();
            float temp = Float.parseFloat(line) / 1000.0f;

            return temp;

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

}
