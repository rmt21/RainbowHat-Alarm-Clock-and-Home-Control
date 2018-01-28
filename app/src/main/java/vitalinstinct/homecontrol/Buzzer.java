package vitalinstinct.homecontrol;

import com.google.android.things.contrib.driver.pwmspeaker.Speaker;
import com.google.android.things.contrib.driver.rainbowhat.RainbowHat;

import java.io.IOException;

/**
 * Created by Reece on 28/01/2018, vitalinstinct.homecontrol, HomeControl2
 */

public class Buzzer {

    Speaker speaker;

    public Buzzer()
    {
        try {
            speaker = RainbowHat.openPiezo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBuzzer()
    {
        try {
            speaker.play(440);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopBuzzer()
    {
        try {
            speaker.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
