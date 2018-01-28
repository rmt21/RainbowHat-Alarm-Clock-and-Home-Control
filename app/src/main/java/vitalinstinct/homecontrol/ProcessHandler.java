package vitalinstinct.homecontrol;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Format;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Reece on 19/11/2017, vitalinstinct.homecontrol, HomeControl2
 */

public class ProcessHandler extends Handler {

    private BedLights bedLights;
    private APA102 ledBed;
    private SegmentDisplay segDisp;
    public AlarmClock alarmClock;
   // Clock clock;
    private WeatherSensor weatherSensor;
    static ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    Bundle send;
    Message msg;
    boolean dispTime, dispSensor;
    private int cTime, cStart, cChange;
    Colours colour;
    Button btnA, btnB, btnC;
    Gpio gbA, gbB, gbC;
    JSONSend jSend;
    JSONCreate jCreate;
    Buzzer buzzer;

    private enum DisplayMode {
        TEMPERATURE,
        TIME,
        ALARM,
        ALARMNOTIFY
    }
    private enum TouchMode {
        DISPLAY,
        ALARM,
        ALARMNOTIFY
    }

    private TouchMode cTouchMode;
    private DisplayMode cDisplayMode = DisplayMode.TIME;

    private int SEG_BRIGHTNESS = 5;
    private boolean SEGMENT_STATUS = true;
    String BED_IP_ADDRESS = "192.168.0.14";
    String LIGHT_DELAY = "250";
    String LIGHT_BRIGHTNESS = "25";

    public ProcessHandler(Looper looper, final Context context, final MainActivity UI) {
        super(looper);
        cTime = 0;
        cChange = 10;
        dispTime = true;
        bedLights = new BedLights();
        ledBed = new APA102(this);
        //clock = new Clock();
        segDisp = new SegmentDisplay();
        alarmClock = new AlarmClock(this);
        weatherSensor = new WeatherSensor(context);
        colour = new Colours();
        GPIO gpio = new GPIO();
        jSend = new JSONSend();
        jCreate = new JSONCreate();
        buzzer = new Buzzer();
        btnA = new Button(context);
        btnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send = new Bundle();
                msg = new Message();
                send.putString("type", "Button");
                send.putString("operation", "pressedA");
                msg.setData(send);
                handleMessage(msg);
            }
        });
        btnB = new Button(context);
        btnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send = new Bundle();
                msg = new Message();
                send.putString("type", "Button");
                send.putString("operation", "pressedB");
                msg.setData(send);
                handleMessage(msg);
            }
        });
        btnC = new Button(context);
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send = new Bundle();
                msg = new Message();
                send.putString("type", "Mode");
                send.putString("operation", "toggle");
                msg.setData(send);
                handleMessage(msg);
            }
        });

        GpioCallback gpioCallback = new GpioCallback() {
            @Override
            public boolean onGpioEdge(Gpio gpio) {
                if (gpio.getName().equals("BCM21"))
                {
                    btnA.performClick();
                }
                if (gpio.getName().equals("BCM20"))
                {
                    btnB.performClick();
                }if (gpio.getName().equals("BCM16"))
                {
                    btnC.performClick();
                }
                return super.onGpioEdge(gpio);
            }
        };

        gbA = gpio.btnA;
        gbB = gpio.btnB;
        gbC = gpio.btnC;
        try {
            gbA.registerGpioCallback(gpioCallback);
            gbB.registerGpioCallback(gpioCallback);
            gbC.registerGpioCallback(gpioCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runnable time = new Runnable() {
            @Override
            public void run() {
                chooseDisplay();
                if (cDisplayMode == DisplayMode.TIME) {
                    if (dispSensor == true) {
                        updateSensorDisp();
                    }
                    if (dispTime == true) {
                        updateTimeDisp();
                    }
                }
                if (cDisplayMode == DisplayMode.ALARM)
                {
                    // handled in code...
                }
            }
        };
        executor.scheduleWithFixedDelay(time, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        final Bundle input = msg.getData();

        if (input.get("type").equals("Alarm"))
        {
            if (input.get("operation").equals("true"))
            {
                cDisplayMode = DisplayMode.ALARMNOTIFY;
                buzzer.startBuzzer();
            }
        }
        if (input.get("type").equals("Button"))
        {
            if (input.get("operation").equals("pressedA"))
            {
                if (cDisplayMode == DisplayMode.ALARM)
                {
                    alarmClock.setAlarm("hours");
                }
            }
            if (input.get("operation").equals("pressedB"))
            {
                if (cDisplayMode == DisplayMode.ALARM)
                {
                    alarmClock.setAlarm("minutes");
                }
            }
        }
        if (input.get("type").equals("Mode"))
        {
            if (input.get("operation").equals("toggle"))
            {
                switch (cDisplayMode) {
                    case TIME:
                    {
                        cDisplayMode = DisplayMode.ALARM;
                        alarmClock.setAlarm("start");
                        break;
                    }
                    case ALARM:
                    {
                        alarmClock.setAlarm("save");
                        cDisplayMode = DisplayMode.TIME;
                        break;
                    }
                    case ALARMNOTIFY:
                    {
                        buzzer.stopBuzzer();
                        cDisplayMode = DisplayMode.TIME;
                        break;
                    }
                }
            }
        }

        if (input.get("type").equals("BedLights"))
        {
            ledBed.clearOperationStatus(true);
            bedLights.setLightsOn((boolean) input.get("status"));
            if (input.get("lastChange").equals("lightsOn")) {
                Log.d("STATUS", String.valueOf(bedLights.isLightsOn()));
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ledBed.simpleToggle(bedLights.isLightsOn());
                        try {
                            String value;
                            if (bedLights.isLightsOn() == true)
                            {
                                value = "on";
                            }
                            else
                            {
                                value = "off";
                            }
                            jSend.sendData(jCreate.createData(new String[]{value, BED_IP_ADDRESS, LIGHT_DELAY, LIGHT_BRIGHTNESS, "RGB"}), BED_IP_ADDRESS);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(runnable).start();
            }

            if (bedLights.isLightsOn() == true)
            {
                if (input.get("lastChange").equals("colour"))
                {
                    Runnable function = new Runnable() {
                        @Override
                        public void run() {
                          ledBed.setColour(colour.getColour((String)input.get("colour")));
                            try {
                                jSend.sendData(jCreate.createData(new String[]{(String)input.get("colour"), BED_IP_ADDRESS, LIGHT_DELAY, LIGHT_BRIGHTNESS, "RGB"}), BED_IP_ADDRESS);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    new Thread(function).start();
                }
                if (input.get("lastChange").equals("operation"))
                {
                    Runnable function = new Runnable() {
                        @Override
                        public void run() {
                            ledBed.setOperation(input.getString("operation"));
                            try {
                                jSend.sendData(jCreate.createData(new String[]{input.getString("operation"), BED_IP_ADDRESS, LIGHT_DELAY, LIGHT_BRIGHTNESS, "RGB"}), BED_IP_ADDRESS);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    new Thread(function).start();
                }
            }
        }
        if (input.get("type").equals("SegmentStatus")) {
            if (SEGMENT_STATUS == true)
            {
                SEGMENT_STATUS = false;
            }
            else
            {
                SEGMENT_STATUS = true;
            }
        }
        if (input.get("type").equals("Segment"))
        {
            if (input.get("operation").equals("display")) {
                if (SEGMENT_STATUS == true) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            segDisp.setBrightness(SEG_BRIGHTNESS);
                            segDisp.setDisplay((String) input.get("input"));
                        }
                    };
                    new Thread(runnable).start();
                }
                else
                {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            segDisp.setBrightness(0);
                        }
                    };
                    new Thread(runnable).start();
                }
            }
        }
    }

    private void updateSensorDisp()
    {
        send = new Bundle();
        msg = new Message();
        send.putString("type", "Segment");
        send.putString("operation", "display");
        send.putString("input", String.valueOf(weatherSensor.getTemperature()));
        msg.setData(send);
        this.sendMessage(msg);
    }

    private void updateTimeDisp()
    {
        send = new Bundle();
        msg = new Message();
        send.putString("type", "Segment");
        send.putString("operation", "display");
        String time = alarmClock.getTime();
        if (Integer.valueOf(time) > 2100 && Integer.valueOf(time) < 900)
        {
            SEG_BRIGHTNESS = 1;
        }
        else
        {
            SEG_BRIGHTNESS = 10;
        }
        send.putString("input", time);
        msg.setData(send);
        this.sendMessage(msg);
    }

    private void chooseDisplay()
    {
        if (cTime == cChange)
        {
            if (dispSensor ==true)
            {
                dispSensor = false;
                dispTime = true;
            }
            else
            {
                dispTime= false;
                dispSensor = true;
            }
            cTime = 0;
        }
        cTime++;
    }
}
