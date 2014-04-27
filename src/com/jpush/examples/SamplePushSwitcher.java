package com.jpush.examples;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

//import com.jpush.examples.R;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SamplePushSwitcher extends InstrumentedActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.push_switcher);
		
		
		getPushStatusResult();
	}
	
	private void getPushStatusResult() {
		TextView resView = (TextView) findViewById(R.id.pushstatus);
		
		String status = null;
		if (JPushInterface.isPushStopped(this))
			status = getString(R.string.closing);
		else
			status = getString(R.string.running);
		
		String result = getString(R.string.pushstatus);
		result = String.format(result, status);
		
		resView.setText(result);
		resView.postInvalidate();
	}
	
	public void clickStartPush(View v) {
		JPushInterface.resumePush(getApplicationContext());
	}
	
	public void clickClosePush(View v) {
		JPushInterface.stopPush(getApplicationContext());
	}

	public void isPushStop(View v) {
		getPushStatusResult();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
