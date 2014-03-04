package com.jpush.examples;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SampleAdvanceNotification extends InstrumentedActivity  {
	private int latestID = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.advsetting);
	}
	
	class ListenBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public void clearAllNotification(View v) {
		JPushInterface.clearAllNotifications(getApplication());
	}
	
	public void clearDedicateNotification(View v) {
		JPushInterface.clearNotificationById(getApplicationContext(), latestID);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
