package com.techblogon.alarmmanagerexample;

import java.io.IOException;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.techblogon.alarmmanagerexample.AlarmService_r.LocalBinder;

public class StopAlarm extends Activity implements OnClickListener
{
	Button stopAlarm;
	boolean mBounded;
	AlarmService_r instance;
	MediaPlayer stPlayer;
	WakeLock wakeLock;
	KeyguardLock keyguardLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_alarm);
		stopAlarm= (Button)findViewById(R.id.bStopAlarm);
		stopAlarm.setOnClickListener(this);
//		wakeDevice();
		createWakeLocks();
		wakeDevice();
		playSound(this, getAlarmUri());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Ouch !", Toast.LENGTH_SHORT).show();
		stPlayer.stop();
		wakeLock.release();
		keyguardLock.reenableKeyguard();
		finish();
	}
	
	protected void createWakeLocks(){
//	    PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//	    wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
//	    partialWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Loneworker - PARTIAL WAKE LOCK");
		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        wakeLock.acquire();
	}
	// Called whenever we need to wake up the device
	public void wakeDevice() {
	    wakeLock.acquire();

	    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
	    keyguardLock = keyguardManager.newKeyguardLock("TAG");
	    keyguardLock.disableKeyguard();
	}
	
	//Plays the Alarm tone on the Alarm Trigger
			public void playSound(Context context, Uri alert)
			{
				stPlayer = new MediaPlayer();
				try
				{
					stPlayer.setDataSource(context, alert);
					final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
					if(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
					{
						stPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
						stPlayer.prepare();
						stPlayer.start();
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
//							wakeLock.release();
							stPlayer.stop();
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
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent mIntent = new Intent(this, AlarmService_r.class);
	    bindService(mIntent, mConnection, BIND_AUTO_CREATE);
	}

	ServiceConnection mConnection = new ServiceConnection() {

		  public void onServiceDisconnected(ComponentName name) {
//		   Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
//		   mBounded = false;
//		   toStop = null;
		  }

		  public void onServiceConnected(ComponentName name, IBinder service) {
		   Toast.makeText(StopAlarm.this, "Stop Alarm Service is connected", Toast.LENGTH_SHORT).show();
		   mBounded = true;
		   LocalBinder mLocalBinder = (LocalBinder)service;
		   instance = mLocalBinder.getServerInstance();
		  }
		 };

		 @Override
		 protected void onStop() {
		  super.onStop();
//		  if(mBounded) {
//		   unbindService(mConnection);
//		   mBounded = false;
//		  }
		 };
	
}
