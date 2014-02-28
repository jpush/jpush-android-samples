package com.jpush.examples;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

public class SampleSetPushTime extends InstrumentedActivity {
	private Handler myHandler;
	final private int MSG_RESID = 1;
	

	Set<Integer> days = new HashSet<Integer>();
	
	private int startHour = 0;
	private int endHour = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.pushtime);
		
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_RESID: {
						Toast.makeText(getApplicationContext(), msg.arg1, Toast.LENGTH_LONG).show();
					}
				}
				
				super.handleMessage(msg);
			}
		};
		
		TimePicker timeStartHourPicker = (TimePicker) findViewById(R.id.timePickStart);
		timeStartHourPicker.setIs24HourView(true);
		timeStartHourPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				startHour = hourOfDay;
			}
		});
		
		TimePicker timeEndHourPicker = (TimePicker) findViewById(R.id.TimePickerEnd);
		timeEndHourPicker.setIs24HourView(true);
		timeEndHourPicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				endHour = hourOfDay;
			}
		});
	}
	
	private void getDays() {
		days.clear();
		CheckBox box;
		
		box = (CheckBox) findViewById(R.id.checkBox1);
		if (box.isChecked())
			days.add(0);/* this is represent for Sunday */
		box = (CheckBox) findViewById(R.id.checkBox2);
		if (box.isChecked())
			days.add(1);
		box = (CheckBox) findViewById(R.id.checkBox3);
		if (box.isChecked())
			days.add(2);
		box = (CheckBox) findViewById(R.id.checkBox4);
		if (box.isChecked())
			days.add(3);
		box = (CheckBox) findViewById(R.id.checkBox5);
		if (box.isChecked())
			days.add(4);
		box = (CheckBox) findViewById(R.id.checkBox6);
		if (box.isChecked())
			days.add(5);
		box = (CheckBox) findViewById(R.id.checkBox7);
		if (box.isChecked())
			days.add(6);
	}
	
	void sendMSG(int resID) {
		Message msg = myHandler.obtainMessage(MSG_RESID, resID, 0);
		myHandler.sendMessage(msg);
	}
	
	public void pushTimeSet(View v) {
		getDays();
		
		if (days.size() == 0) {
			sendMSG(R.string.pushtimeerror);
			return;
		}
		
		if (startHour == endHour) {
			sendMSG(R.string.pushtimesameerror);
			return;
		}
		
		JPushInterface.setPushTime(getApplicationContext(), days, startHour, endHour);
		sendMSG(R.string.pushtimecomplete);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
