package com.jpush.examples;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

public class SampleSetDebug extends InstrumentedActivity {
	final private int MESSAGE_SHOW_INFO = 1;
	Handler myHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setdebug);
		
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MESSAGE_SHOW_INFO:
						Toast.makeText(getApplicationContext(), msg.arg1, Toast.LENGTH_LONG).show();
				}
			}
		};
	}
	
	public void clickDebugOpen(View v) {
		JPushInterface.setDebugMode(true);
		Message msg = myHandler.obtainMessage(MESSAGE_SHOW_INFO, R.string.debuginfoopen, 0);
		myHandler.sendMessage(msg);
	}
	
	public void clickDebugClose(View v) {
		JPushInterface.setDebugMode(false);
		Message msg = myHandler.obtainMessage(MESSAGE_SHOW_INFO, R.string.debuginfoclose, 0);
		myHandler.sendMessage(msg);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
