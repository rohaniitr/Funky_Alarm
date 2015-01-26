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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class AlarmService_r extends Service 
{
	MediaPlayer player;
	Vibrator vibrator;
	int sHour, sMinute, sNumber;
	int rohan;
	PowerManager.WakeLock wakeLock;
	//To check when Stop button pressed & perform action accordingly
	long track;
	public boolean isItRunning;

	//Used for register Alarm Manager
	PendingIntent pendingIntent;
	//Used to store running AlarmManager instance
	AlarmManager alarmManager;
	//Callback function for AlarmManager event
	BroadcastReceiver mReceiver;
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
		player = MediaPlayer.create(this, R.raw.silent);
		
		RegisterAlarmBroadcast();
}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		rohan=0;
		if (player.isPlaying())
		{
//			Toast.makeText(this, "isPlaying", Toast.LENGTH_SHORT).show();
			player.stop();
		}
		
		String s = intent.getStringExtra("keyHour");
		sHour = Integer.parseInt(s);
		s = intent.getStringExtra("keyMinute");
		sMinute = Integer.parseInt(s);
		s = intent.getStringExtra("keyNumber");
		sNumber = Integer.parseInt(s);
		setAlarm();
//		isItRunning=true;
		return START_STICKY;
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		stop();
		UnregisterAlarmBroadcast();
		isItRunning=false;
		//It may cause an error
		//unregisterReceiver(mReceiver);
		Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
	}
	
	//Stops the functions on pressing Stop Button
	public void stop()
	{
		if (track <= System.currentTimeMillis())
		{
//			player.stop();
			vibrator.cancel();
		}
		else
		{
			UnregisterAlarmBroadcast();
			isItRunning= false;
			Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
		}	
	}
	
	//Registers Broadcast Request and actions to be performed during alarm period
	private void RegisterAlarmBroadcast()
	{
		  Log.i("Alarm Example:RegisterAlarmBroadcast()", "Going to register Intent.RegisterAlramBroadcast");
		
		//This is the call back function(BroadcastReceiver) which will be call when your 
		//alarm time will reached.
		mReceiver = new BroadcastReceiver()
		{
		    @Override
		    public void onReceive(Context context, Intent intent)
		    {	
	    		//Displays the Toast
//				Toast.makeText(context, "Time is up!!!!.",Toast.LENGTH_LONG).show();
				
				//Vibrate the mobile phone
				vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(2000);
				
				//Play the alarm tone
//				playSound(context, getAlarmUri());
				
				rohan+=1;
				if (rohan>=sNumber)
				{
					UnregisterAlarmBroadcast();
		    		Toast.makeText(context, "Repeat Alarm Cancelled", Toast.LENGTH_SHORT).show();
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
//		isItRunning=false;
//		getBaseContext().unregisterReceiver(mReceiver);
    }
	
	//Actually sets the Alarm
		private void setAlarm()
		{
			boolean crook;
			//Set the alarm at particular time, Hours & Minutes
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(System.currentTimeMillis());
	        //Toast.makeText(this, sHour + " , " + sMinute, Toast.LENGTH_SHORT).show();
	        if (sHour==59 && sMinute==59)
	        	crook=true;
	        else
	        	crook=false;
	        sHour-=calendar.get(Calendar.HOUR);
	        sMinute-=calendar.get(Calendar.MINUTE);
	        
	        if (sHour<0)
	        	sHour+=12;
	        if (sHour>=12)
	        	sHour-=12;
	        if(sMinute<0)
	        	sMinute+=60;
	        //Set the alarm
	        track = System.currentTimeMillis() + (sHour*60*60*1000) + (sMinute*60*1000);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (sHour*60*60*1000) + (sMinute*60*1000) , (10*1000)  , pendingIntent);
			isItRunning=true;
			if (!crook)
				Toast.makeText(this, "Alarm set after " + sHour + " hour  & " + sMinute + " minute", Toast.LENGTH_SHORT).show();
		}
		
//		public void play()
//		{
//			playSound(getApplicationContext(), getAlarmUri());
//		}
		
		//Plays the Alarm tone on the Alarm Trigger
		public void playSound(Context context, Uri alert)
		{
			player = new MediaPlayer();
			try
			{
				player.setDataSource(context, alert);
				final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
				if(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
				{
					player.setAudioStreamType(AudioManager.STREAM_ALARM);
					player.prepare();
					player.start();
				}
			}		
			catch(IOException e)
			{
				Log.i("Alarm Receiver", "No audio Bitch Found");
			}
			Thread timer = new Thread(){
				public void run(){
					try
					{
						sleep(10000);					
					} 
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					finally
					{
//						wakeLock.release();
						player.stop();
					}
				}
			};
			timer.start();
		}
		
		//this
		//Gets the Alarm tone
		private Uri getAlarmUri()
		{
			Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			if (alert==null)
			{
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				if (alert==null)
				{
					alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
				}
			}
			return alert;
		}
	
		//Wake up the device if the screen is lock	
		public void wakeDevice() 
		{
//		    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		    wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
//		    wakeLock.acquire();
	//
//		    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//		    KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
//		    keyguardLock.disableKeyguard();
//		    runOnUiThread(new Runnable(){
//		        public void run(){
//		            getWindow().addFlags(
//		                      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//		                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//		                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//		                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);                
//		        }
//		    });
//		}
	//	
//		private Boolean editTextCheck() 
//		{
//			if(fHour.getText().toString().trim().length() == 0 || fMinute.getText().toString().trim().length() == 0)
//			{
//				return false;
//			}
//			return true;
//		}
		}
//
}
