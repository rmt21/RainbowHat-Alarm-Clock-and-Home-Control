package vitalinstinct.homecontrol;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.android.things.contrib.driver.apa102.Apa102;

import java.io.IOException;
import java.util.Random;

import vitalinstinct.homecontrol.ProcessHandler;

import static com.google.android.things.contrib.driver.apa102.Apa102.MAX_BRIGHTNESS;

/**
 * Created by Reece on 16/11/2017, PACKAGE_NAME, PiMus2AT
 */

public class APA102 {

    private int LED_COUNT = 28;

    int[] colours;

    Apa102 led;
    Message choiceLed;
    Bundle send;
    Random random;

    boolean clearOperation;

    public APA102(ProcessHandler handler)
    {
        choiceLed = new Message();
        send = new Bundle();
        colours = new int[LED_COUNT];
        setup();
        clearOperation = false;
        random = new Random();
    }

    public void setup()
    {
        while (led == null) {
            try {
                led = new Apa102("SPI0.0", Apa102.Mode.RGB);
                led.setBrightness(5);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("LED", "SETUP COMPLETE");
    }
    public void close()
    {
        try {
            led.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setColour(Color colour)
    {
        int R = (int)colour.red();
        int G = (int)colour.green();
        int B = (int)colour.blue();
        singleColour(Color.argb(255, B, G, R));
    }

    public void singleColour(int colour)
    {
        for (int i=0; i< LED_COUNT; i++)
        {
            colours[i] = colour;
        }
        try {
            led.write(colours);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void simpleToggle(boolean status)
    {
        if (status == true)
        {
            clear();
            int R = random.nextInt(255);
            int G = random.nextInt(255);
            int B = random.nextInt(255);
            singleColour(Color.argb(255, B, G, R));
        }
        if (status ==false)
        {
            clear();
        }
    }

    public void setOperation(String operation) {
        clearOperation = true;
        clear();
        if (operation.equals("fade")) {
            clearOperation = false;
            while (clearOperation == false) {
                int R = random.nextInt(255);
                int G = random.nextInt(255);
                int B = random.nextInt(255);
                for (int i = 0; i < LED_COUNT; i++) {
                    colours[i] = Color.argb(255, B, G, R);
                }

                for (int i = 0; i < MAX_BRIGHTNESS; i++) {
                    led.setBrightness(i);
                    try {
                        led.write(colours);
                        Thread.sleep(200);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = MAX_BRIGHTNESS; i > 0; i--) {
                    led.setBrightness(i);
                    try {
                        led.write(colours);
                        Thread.sleep(200);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (operation.equals("chase")) {
                clearOperation = false;
                while (clearOperation == false) {
                    for (int i = 0; i < LED_COUNT; i++) {
                        for (int ii = 0; ii < LED_COUNT; ii++) {
                            colours[ii] = Color.argb(255, 0, 0, 0);
                        }
                        int R = random.nextInt(255);
                        int G = random.nextInt(255);
                        int B = random.nextInt(255);
                        colours[i] = Color.argb(255, B, G, R);
                        try {
                            led.write(colours);
                            Thread.sleep(200);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

    public void clearOperationStatus(boolean status)
    {
        clearOperation = status;
    }

    public void clear()
    {
        for (int i=0; i< LED_COUNT; i++)
        {
            colours[i] = Color.argb(0, 0, 0, 0);
        }
        try {
            led.write(colours);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
