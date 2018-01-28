package vitalinstinct.homecontrol;

import android.graphics.Color;

/**
 * Created by Reece on 28/11/2017, vitalinstinct.homecontrol, HomeControl2
 */

public class Colours {


    public Color red = Color.valueOf(255, 000, 000);
    public Color green = Color.valueOf(000, 255, 000);
    public Color blue = Color.valueOf(68, 161, 255);
    public Color yellow = Color.valueOf(255, 255, 000);
    public Color orange = Color.valueOf(255, 115, 0);
    public Color pink = Color.valueOf(255, 102, 178);
    public Color purple = Color.valueOf(127, 000, 255);

    public Color getColour(String colour)
    {
        if (colour.equals("red"))
        {
            return red;
        }
        if (colour.equals("green"))
        {
            return green;
        }
        if (colour.equals("blue"))
        {
            return blue;
        }
        if (colour.equals("yellow"))
        {
            return yellow;
        }
        if (colour.equals("orange"))
        {
            return orange;
        }
        if (colour.equals("pink"))
        {
            return pink;
        }
        if (colour.equals("purple"))
        {
            return purple;
        }
        return null;
    }

}
