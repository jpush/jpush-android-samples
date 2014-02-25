package com.jpush.examples;

import cn.jpush.android.api.InstrumentedActivity;
import android.app.Activity;
import android.os.Bundle;

public class SampleSetPushTime extends InstrumentedActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.developing);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
