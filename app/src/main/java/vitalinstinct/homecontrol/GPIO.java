package vitalinstinct.homecontrol;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by Reece on 02/12/2017, vitalinstinct.homecontrol, HomeControl2
 */

public class GPIO {

    public static Gpio btnA, btnB, btnC;
    PeripheralManagerService service;

    public GPIO()
    {
        service = new PeripheralManagerService();

        try {
            btnA = service.openGpio("BCM21");
            btnA.setDirection(Gpio.DIRECTION_IN);
            btnA.setEdgeTriggerType(Gpio.EDGE_FALLING);
            btnB = service.openGpio("BCM20");
            btnB.setDirection(Gpio.DIRECTION_IN);
            btnB.setEdgeTriggerType(Gpio.EDGE_FALLING);
            btnC = service.openGpio("BCM16");
            btnC.setDirection(Gpio.DIRECTION_IN);
            btnC.setEdgeTriggerType(Gpio.EDGE_FALLING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
