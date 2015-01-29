package com.techblogon.alarmmanagerexample;

import java.io.IOException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmService_r extends Service 
{
	int hour, minute, number, duration;
	//For the number of Alarms
	int rohan;
	PowerManager.WakeLock wakeLock;
	//To check when Stop button pressed & perform action accordingly
	long track;
	//Gives status of Alarm
	public boolean isItRunning;
	//Used for register Alarm Manager
	PendingIntent pendingIntent;
	//Used to store running AlarmManager instance
	AlarmManager alarmManager;
	//Callback function for AlarmManager event
	BroadcastReceiver mReceiver;
	//For StopAlarm Activity
	Intent intentone;
	
	IBinder mBinder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}
	
	 public class LocalBinder extends Binder 
	 {
		  public AlarmService_r getServerInstance() 
		  {
		   return AlarmService_r.this;
		  }
	 }
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		RegisterAlarmBroadcast();
}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		rohan=0;
		
		String s = intent.getStringExtra("keyHour");
		hour = Integer.parseInt(s);
		s = intent.getStringExtra("keyMinute");
		minute = Integer.parseInt(s);
		s = intent.getStringExtra("keyNumber");
		number = Integer.parseInt(s);
		s = intent.getStringExtra("keyDuration");
		duration = Integer.parseInt(s);
		setAlarm();
		return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		UnregisterAlarmBroadcast();
		isItRunning=false;
		//It may cause an error
//		unregisterReceiver(mReceiver);
//		Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
	}
	
	//Stops the functions on pressing Stop Button
	public void stop()
	{
		if (track <= System.currentTimeMillis())
		{
			
		}
		else
		{
			UnregisterAlarmBroadcast();
			isItRunning= false;
//			Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
		}	
	}
	
	//Registers Broadcast Request and actions to be performed during alarm period
	private void RegisterAlarmBroadcast()
	{
		  Log.i("Alarm Example:RegisterAlarmBroadcast()", "Going to register Intent.RegisterAlramBroadcast");
		
		//This is the call back function(BroadcastReceiver) which will be call when your alarm time will reached.
		mReceiver = new BroadcastReceiver()
		{
		    @Override
		    public void onReceive(Context context, Intent intent)
		    {	
		    	rohan+=1;
				if (rohan>=number)
				{
					UnregisterAlarmBroadcast();
		    		Toast.makeText(context, "Last Alarm.\nYou better get up this time buddy.", Toast.LENGTH_SHORT).show();
				}
				//start activity
				intentone = new Intent(context.getApplicationContext(), StopAlarm.class);
				intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intentone);
		    }
		};
			
		// register the alarm broadcast here
		registerReceiver(mReceiver, new IntentFilter("com.techblogon.alarmexample") );
		pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("com.techblogon.alarmexample"),0 );
		alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
    }
		
	//Unregisters the Registered AlarmService Request
	public void UnregisterAlarmBroadcast()
	{
		alarmManager.cancel(pendingIntent);
//		getBaseContext().unregisterReceiver(mReceiver);
    }
	
	//Actually sets the Alarm
		private void setAlarm()
		{
			//Tells the service True or Fake
			boolean crook;
			//Set the alarm at particular time, Hours & Minutes
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(System.currentTimeMillis());
	        if (hour==59 && minute==59)
	        	crook=true;
	        else
	        	crook=false;
	        //in minutes here
	        int present = (calendar.get(Calendar.HOUR_OF_DAY)*60) + calendar.get(Calendar.MINUTE);
	        int alarmTime = (hour*60) + minute;
	        //Set the alarm
	        track = alarmTime*60*1000;
	        alarmTime -= present;
	        //For next day
	        if(alarmTime<0)
	        	alarmTime+=(24*60);
	        
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (alarmTime*60*1000) , (duration*60*1000)  , pendingIntent);
			isItRunning=true;
			minute=alarmTime%60;
			hour=alarmTime/60;
			if (!crook)
				if (minute!=0)
					Toast.makeText(this, "The alarm is set for " + (hour) + " hours and " + minute + " minutes from now", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "The alarm is set for " + hour + " hours from now", Toast.LENGTH_SHORT).show();
		}
	
}
