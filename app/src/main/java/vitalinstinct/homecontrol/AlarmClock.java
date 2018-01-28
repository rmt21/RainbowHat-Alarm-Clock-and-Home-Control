package vitalinstinct.homecontrol;

import android.os.Bundle;
import android.os.Message;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Reece on 21/11/2017, vitalinstinct.homecontrol, HomeControl2
 */

public class AlarmClock {

    private String alarm;
    private int hours, minutes;

    private boolean alarmSet = false;

    Bundle send;
    Message msg;

    ProcessHandler handler;

    public AlarmClock(ProcessHandler handler)
    {
        this.handler = handler;
    }

    public String getTime()
    {
        TimeZone london = TimeZone.getTimeZone("Europe/London");
        long now = System.currentTimeMillis();
        long result = now + london.getOffset(now);
        Date date = new Date();
        date.setTime(result);
        String actual = date.toString();
        String time = actual.substring(11, 16);
        String seconds = actual.substring(17, 19);
        time = time.replace(":", "");
        checkAlarm(time);
        return time;
    }

    public void checkAlarm(String time)
    {
        if (time.equals(alarm))
        {
            if (alarmSet == true) {
                send = new Bundle();
                msg = new Message();
                send.putString("type", "Alarm");
                send.putString("operation", "true");
                msg.setData(send);
                handler.sendMessage(msg);
                alarmSet = false;
            }
        }
    }

    public String createTime(int hours, int minutes)
    {
        String h = String.valueOf(hours);
        String m = String.valueOf(minutes);

        if (h.length()<2)
        {
            h = "0" + h;
        }
        if (m.length()<2)
        {
            m = "0" + m;
        }

        return h + m;
    }

    public void setAlarm(String command)
    {
        if (command.equals("start")) {
            hours = 0;
            minutes = 0;
            send = new Bundle();
            msg = new Message();
            send.putString("type", "Segment");
            send.putString("operation", "display");
            send.putString("input", createTime(hours, minutes));
            msg.setData(send);
            handler.sendMessage(msg);
        }
        if (command.equals("hours"))
        {
            hours++;
            send = new Bundle();
            msg = new Message();
            send.putString("type", "Segment");
            send.putString("operation", "display");
            send.putString("input", createTime(hours, minutes));
            msg.setData(send);
            handler.sendMessage(msg);
        }
        if (command.equals("minutes"))
        {
            minutes++;
            send = new Bundle();
            msg = new Message();
            send.putString("type", "Segment");
            send.putString("operation", "display");
            send.putString("input", createTime(hours, minutes));
            msg.setData(send);
            handler.sendMessage(msg);
        }
        if (command.equals("save"))
        {
            alarm = createTime(hours, minutes);
            send = new Bundle();
            msg = new Message();
            send.putString("type", "Segment");
            send.putString("operation", "display");
            send.putString("input", createTime(hours, minutes));
            msg.setData(send);
            handler.sendMessage(msg);
            alarmSet = true;
        }

    }
}
