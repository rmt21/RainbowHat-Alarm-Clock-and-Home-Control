package vitalinstinct.homecontrol;

import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Reece on 24/09/2017, vitalinstinct.housecontrolepd, HouseControlepd
 */

public class JSONCreate {

    public JSONObject createData(String[] params) throws JSONException {
        JSONObject object = new JSONObject();

        String brightness = params[3];
        object.put("brightness", brightness);
        String type = params[0];
        String delay = params[2];

        Colours colour = new Colours();
        Color lastColour;

        if (params[4].equals("LED"))
        {
            if (type.equals("on"))
            {
                object.put("type", "all");
            }
            if (type.equals("off"))
            {
                object.put("type", "off");
            }
            if (type.equals("red"))
            {
                lastColour = colour.getColour("red");
                object.put("type", colourString(String.valueOf((int)lastColour.red()), String.valueOf((int)lastColour.green()), String.valueOf((int)lastColour.blue())));
            }
            if (type.equals("green"))
            {
                lastColour = colour.getColour("green");
                object.put("type", colourString(String.valueOf((int)lastColour.red()), String.valueOf((int)lastColour.green()), String.valueOf((int)lastColour.blue())));
            }
            if (type.equals("blue"))
            {
                lastColour = colour.getColour("blue");
                object.put("type", colourString(String.valueOf((int)lastColour.red()), String.valueOf((int)lastColour.green()), String.valueOf((int)lastColour.blue())));
            }
            if (type.equals("white"))
            {
               // object.put("type", (":" + colour.white.red() + colour.white.green() + colour.white.blue()));
            }
            if (type.equals("fade"))
            {
                object.put("type", "fade");
            }
            if (type.equals("pulse"))
            {
                object.put("type", "pulse");
            }
        }

        if (params[4].equals("Billboard"))
        {
            if (type.equals("AllOn"))
            {
                String parameter = "ON";
                object.put("type", "billboardScreenStatus");
                object.put("parameter", parameter);
            }
            if (type.equals("AllOff"))
            {
                String parameter = "OFF";
                object.put("type", "billboardScreenStatus");
                object.put("parameter", parameter);
            }
            if (type.equals("singleColourAll"))
            {
                object.put("type", type);
            }
            if (type.equals("colourChase"))
            {
                object.put("type", type);
            }
            if (type.equals("clear"))
            {
                object.put("type", type);
            }
            if (type.equals("flash"))
            {

                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.equals("fade"))
            {
                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.startsWith(":"))
            {
                object.put("type", type);
            }
        }

        if (params[4].equals("RGB"))
        {
            if (type.equals("singleColourAll"))
            {
                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.equals("on"))
            {
                object.put("type", "singleColourAll");
                object.put("delay", delay);
            }
            if (type.equals("off"))
            {
                object.put("type", "clear");
                object.put("delay", delay);
            }
            if (type.equals("colourChase"))
            {
                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.equals("clear"))
            {
                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.equals("flash"))
            {
                delay = params[2];
                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.equals("fade"))
            {
                delay = params[2];
                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.startsWith(":"))
            {
                object.put("type", type);
                object.put("delay", delay);
            }
            if (type.equals("red"))
            {
                lastColour = colour.getColour("red");
                object.put("type", colourString(String.valueOf((int)lastColour.red()), String.valueOf((int)lastColour.green()), String.valueOf((int)lastColour.blue())));
                object.put("delay", delay);
            }
            if (type.equals("green"))
            {
                lastColour = colour.getColour("green");
                object.put("type", colourString(String.valueOf((int)lastColour.red()), String.valueOf((int)lastColour.green()), String.valueOf((int)lastColour.blue())));
                object.put("delay", delay);
            }
            if (type.equals("blue"))
            {
                lastColour = colour.getColour("blue");
                object.put("type", colourString(String.valueOf((int)lastColour.red()), String.valueOf((int)lastColour.green()), String.valueOf((int)lastColour.blue())));
                object.put("delay", delay);
            }
        }

        return object;
    }

    public String colourString(String r, String g, String b)
    {
        if (r.equals("0"))
        {
            r = "000";
        }
        if (g.equals("0"))
        {
            g = "000";
        }
        if (b.equals("0"))
        {
            b = "000";
        }

        if (r.length() < 3)
        {
            r = "0" + r;
        }
        if (g.length() < 3)
        {
            g = "0" + g;
        }
        if (b.length() < 3)
        {
            b = "0" + b;
        }
        return (":" + r + g + b);
    }
}


