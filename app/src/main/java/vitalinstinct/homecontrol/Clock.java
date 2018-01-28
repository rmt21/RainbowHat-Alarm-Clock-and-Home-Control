package vitalinstinct.homecontrol;

import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Clock {
	
	String pastTime;
	
	public Clock()
	{
		pastTime = "00:00";
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
        String sDate = actual.substring(0, 10) + " " + actual.substring(24, 28);
        //time = time.replace(":", "");
        //time = time + ":" + seconds;
        return time + "-" + sDate;
	}
	
	public boolean checkTimeValid()
	{
		if (pastTime.equals(getTime()))
		{
			return true;
		}
		else
		{
			pastTime = getTime();
			return false;
		}
	}

}
