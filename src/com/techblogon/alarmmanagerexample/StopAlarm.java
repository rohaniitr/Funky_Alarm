package com.techblogon.alarmmanagerexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StopAlarm extends Activity implements OnClickListener
{
	Button stopAlarm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stop_alarm);
		stopAlarm= (Button)findViewById(R.id.bStopAlarm);
		stopAlarm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Ouch !", Toast.LENGTH_SHORT).show();
	}
	
}
