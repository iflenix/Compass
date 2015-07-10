package com.example.storm.compass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;


public class CompassActivity extends Activity {

    private SensorManager sensorManager;
    private float[] accelValues = new float[3];
    private float[] magValues = new float[3];
    CompassView mainCompassView;
    private int rotation;

    private SensorEventListener mySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelValues = sensorEvent.values;
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magValues = sensorEvent.values;
            }

             updateOrientation(calculateOrientation());

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_view);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mainCompassView = (CompassView) findViewById(R.id.compassView);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        rotation = display.getRotation();

        updateOrientation(new float[]{0, 0, 0});

    }

    private void updateOrientation(float[] values) {
        mainCompassView.setBearing(values[0]);
        mainCompassView.setPitch(values[1]);
        mainCompassView.setRoll(values[2]);

    }

    private float[] calculateOrientation() {
        float[] values = new float[3];
        float[] inR = new float[9];
        float[] outR = new float[9];

        SensorManager.getRotationMatrix(inR,null,accelValues,magValues);

        int x_axis = SensorManager.AXIS_X;
        int y_axis = SensorManager.AXIS_Y;

        switch (rotation) {
            case Surface.ROTATION_0: break;
            case Surface.ROTATION_90:
                x_axis = SensorManager.AXIS_Y;
                y_axis = SensorManager.AXIS_MINUS_Y;
                break;
            case Surface.ROTATION_180:
                y_axis  = SensorManager.AXIS_MINUS_Y;
                break;
            case Surface.ROTATION_270:
                x_axis = SensorManager.AXIS_MINUS_Y;
                y_axis = SensorManager.AXIS_X;
                break;
            default:break;
        }

        SensorManager.remapCoordinateSystem(inR,x_axis,y_axis,outR);
        SensorManager.getOrientation(outR, values);

        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        return values;


    }


    @Override
    protected void onResume() {
        sensorManager.registerListener(mySensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(mySensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
        super.onResume();
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(mySensorListener);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compass_view, menu);
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

    public void onButtonBearingClick(View view) {
        CompassView cv = (CompassView) this.findViewById(R.id.compassView);
        cv.setBearing((int) (Math.random() * 360));
    }
}
