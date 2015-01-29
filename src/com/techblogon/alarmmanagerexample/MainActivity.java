package com.techblogon.alarmmanagerexample;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.techblogon.alarmmanagerexample.AlarmService_r.LocalBinder;

public class MainActivity extends Activity implements OnClickListener
{
	Button stop, set;
	EditText number;
	TimePicker timePicker;
	//To access the function of the service, e.i., AlarmService_r
	AlarmService_r toStop;
	//To check if Service is connected to activity or not
	boolean mBounded;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		stop=(Button)findViewById(R.id.bStop);
		set=(Button)findViewById(R.id.bSet);
		timePicker=(TimePicker)findViewById(R.id.tpAlarm);
		number=(EditText)findViewById(R.id.etNumber);
		stop.setOnClickListener(this);
		set.setOnClickListener(this);
	}
	
	
	
	@Override
	 protected void onStart() {
	  super.onStart();
	  
	  if (!isMyServiceRunning(AlarmService_r.class))
	  {
		//try catch will work after putting this only, e.i.,parseInt
		int i = 59;
		String s;
		s=Integer.toString(i);
		//Create Intent and send the info through Bundles
		Intent sService = new Intent(getBaseContext(), AlarmService_r.class);
		//Bind the data you want to send to the service
		sService.putExtra("keyHour", s);
		sService.putExtra("keyMinute", s);
		sService.putExtra("keyNumber", s);
		//Start the service
		startService(sService);
	  }
		  
	  
	  //These two lines connect this activity to the AlarmService_r service, and wherever this is placed, the connection gets started
	  //Can be placed anywhere
	  //Does not restart if already started, like Service
	  Intent mIntent = new Intent(this, AlarmService_r.class);
      bindService(mIntent, mConnection, BIND_AUTO_CREATE);
//      unbindService(mConnection);
	 };

	 ServiceConnection mConnection = new ServiceConnection() {

	  public void onServiceDisconnected(ComponentName name) {
//	   Toast.makeText(MainActivity.this, "Service is disconnected", Toast.LENGTH_SHORT).show();
//	   mBounded = false;
//	   toStop = null;
	  }

	  public void onServiceConnected(ComponentName name, IBinder service) 
	  {
	   mBounded = true;
	   LocalBinder mLocalBinder = (LocalBinder)service;
	   toStop = mLocalBinder.getServerInstance();
	  }
	 };

	 @Override
	 protected void onStop() {
	  super.onStop();
//	  if(mBounded) {
//	   unbindService(mConnection);
//	   mBounded = false;
//	  }
	 };
	
	

	//Action performed on switching
	@Override
	public void onClick(View v) 
	{
		
		switch(v.getId())
		{		
			case R.id.bSet:
				//Stop the service if it is already going on
//				if (isMyServiceRunning(AlarmService_r.class))
				if(toStop.isItRunning)
					stopService(new Intent(getBaseContext(),AlarmService_r.class));
					
				try
				{
					hideKeyboard();
					//try catch will work after putting this only, e.i.,parseInt
					int i = Integer.parseInt(number.getText().toString());
					//Create Intent and send the info through Bundles
					Intent sService = new Intent(getBaseContext(), AlarmService_r.class);
					//Bind the data you want to send to the service
					sService.putExtra("keyHour", timePicker.getCurrentHour().toString());
					sService.putExtra("keyMinute", timePicker.getCurrentMinute().toString());
					sService.putExtra("keyNumber", number.getText().toString());
					//Start the service
					startService(sService);
				}
				catch(NumberFormatException e)
				{
					Toast.makeText(this, "Fill- How many alarms you want to set", Toast.LENGTH_LONG).show();
				}
				
				break;
			case R.id.bStop:
				Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
				toStop.stop();
				stopService(new Intent(getBaseContext(),AlarmService_r.class));
				
				break;
		}
	}
	
	
	//Hides the keyboard
	private void hideKeyboard()
	{
		InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
		inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	//Check if the service is working or not
	private boolean isMyServiceRunning(Class<?> serviceClass) 
	{
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
	    {
	        if (serviceClass.getName().equals(service.service.getClassName())) 
	        {
	            return true;
	        }
	    }
	    return false;
	}
	
	
}
