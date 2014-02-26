package com.jpush.examples;

import cn.jpush.android.api.InstrumentedActivity;
import android.os.Bundle;

public class SampleRichPush extends InstrumentedActivity {
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
