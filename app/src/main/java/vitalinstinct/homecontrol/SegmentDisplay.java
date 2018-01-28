package vitalinstinct.homecontrol;

import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;

import java.io.IOException;

/**
 * Created by Reece on 21/11/2017, vitalinstinct.homecontrol, HomeControl2
 */

public class SegmentDisplay {

    AlphanumericDisplay segment;
    int BRIGHTNESS = 5;

    public SegmentDisplay()
    {
        try {
            segment = new AlphanumericDisplay("I2C1");
            segment.setBrightness(BRIGHTNESS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDisplay(String input)
    {
        try {
            segment.display(input);
            segment.setEnabled(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBrightness(int value)
    {
        try {
            segment.setBrightness(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
