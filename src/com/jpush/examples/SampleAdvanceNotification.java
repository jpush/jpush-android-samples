package com.jpush.examples;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SampleAdvanceNotification extends InstrumentedActivity  {
	final private int MSG_REFRESH_MSGID = 0;
	final private int MSG_SHOW_INFO_WITHMSGID = 1;
	final private int MSG_SHOW_INFO_STRING = 2;
	
	final private int INVALID_MSGID = -1;
	
	private int latestID = INVALID_MSGID;
	private BroadcastReceiver br;
	private Handler mHandler;
	private boolean isAddedDedicate = false;
	private boolean isAddedCustom = false;
	
	private TimePicker timeStartPicker;
	private TimePicker timeEndPicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.advnotificationsetting);
		
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Bundle bundle = intent.getExtras();
				
				if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
					latestID = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID, INVALID_MSGID);
					sendMSG(MSG_REFRESH_MSGID);
		 	    }
			}
		};
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(JPushInterface.ACTION_NOTIFICATION_RECEIVED);
		filter.addCategory(getPackageName());
		
		registerReceiver(br, filter);
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_SHOW_INFO_WITHMSGID:
						Toast.makeText(getApplicationContext(), msg.arg1, Toast.LENGTH_LONG).show();
					case MSG_SHOW_INFO_STRING:
						Toast.makeText(getApplicationContext(), (CharSequence) msg.obj, Toast.LENGTH_LONG).show();
					case MSG_REFRESH_MSGID:
						refreshNewMessageID();
				}
				
				super.handleMessage(msg);
			}
		};
		
		initTimePicker();
	}

	public void initTimePicker() {
		timeStartPicker = (TimePicker) findViewById(R.id.advSettingTimeStart);
		timeStartPicker.setIs24HourView(true);
				
		timeEndPicker = (TimePicker) findViewById(R.id.advSettingTimeEnd);
		timeEndPicker.setIs24HourView(true);
	}
	
	public void sendMSG(int msgID) {
		Message msg = mHandler.obtainMessage(msgID, 0, 0);
		mHandler.sendMessage(msg);
	}
	
	public void sendStringMSG(String info) {
		Message msg = mHandler.obtainMessage(MSG_SHOW_INFO_STRING, info);
		mHandler.sendMessage(msg);
	}
	
	public void sendMSG(int msgID, int resID) {
		Message msg = mHandler.obtainMessage(msgID, resID, 0);
		mHandler.sendMessage(msg);
	}
	
	public void refreshNewMessageID() {
		TextView msgIDView = (TextView) findViewById(R.id.latestNotification);
		
		if (latestID == INVALID_MSGID) {
			msgIDView.setText(R.string.clearlatestnotification);
		} else {
			msgIDView.setText(Integer.toString(latestID));
		}
		
		msgIDView.postInvalidate();
	}
	
		
	public void clearAllNotification(View v) {
		if (latestID == INVALID_MSGID) /* there should be no notification */
			return;
		latestID = INVALID_MSGID;
		JPushInterface.clearAllNotifications(getApplication());
		refreshLatestNotificationID();
	}
	
	public void clearDedicateNotification(View v) {
		if (INVALID_MSGID == latestID)
			return;
		latestID = INVALID_MSGID;
		JPushInterface.clearNotificationById(getApplicationContext(), latestID);
		refreshLatestNotificationID();
	}
	
	public void refreshLatestNotificationID() {
		TextView tv = (TextView) findViewById(R.id.latestNotification);
		tv.setText(R.string.latestnotificationid);
		tv.postInvalidate();
	}
	
	public void setDefaultNotification(View v) {
		BasicPushNotificationBuilder basic = new BasicPushNotificationBuilder(getApplicationContext());
		basic.statusBarDrawable = R.drawable.notification;
		basic.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //set auto disappear
		basic.notificationDefaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;  // set audio and vibrate
		JPushInterface.setDefaultPushNotificationBuilder(basic);
		
		sendStringMSG("Default style of Notification has changed");
	}
	
	public void setDedicateNotification(View v) {
		if (isAddedDedicate) {
			sendMSG(MSG_SHOW_INFO_WITHMSGID, R.string.setdedicatenotificationwarn);
			return;
		}
		
		isAddedDedicate = true;
		BasicPushNotificationBuilder basic = new BasicPushNotificationBuilder(getApplicationContext());
		basic.statusBarDrawable = R.drawable.notification1;
		basic.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //set auto disappear
		basic.notificationDefaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;  // set audio and vibrate
		JPushInterface.setPushNotificationBuilder(1, basic);
		
		sendMSG(MSG_SHOW_INFO_WITHMSGID, R.string.setdedicatenotificationinfo);
	}
	
	public void setCustomNotification(View v) {
		if (isAddedCustom) {
			sendMSG(MSG_SHOW_INFO_WITHMSGID, R.string.setcustomnotificationwarn);
			return;
		}
		
		isAddedCustom = true;
		
		CustomPushNotificationBuilder custom = new CustomPushNotificationBuilder(getApplicationContext(),
				R.layout.custom_notification, R.id.imageNotify, R.id.titleNotify, R.id.textNotify);

		custom.notificationDefaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS;  // set audio and vibrate
		JPushInterface.setPushNotificationBuilder(2, custom);
		
		sendMSG(MSG_SHOW_INFO_WITHMSGID, R.string.setcustomnotificationinfo);
	}
	
	public void setSilentTime(View v) {
		String result = null;
		int startH = timeStartPicker.getCurrentHour();
		int startM = timeStartPicker.getCurrentMinute();
		
		int endH = timeEndPicker.getCurrentHour();
		int endM = timeEndPicker.getCurrentMinute();
		
		JPushInterface.setSilenceTime(getApplicationContext(), startH, startM, endH, endM);
		
		result = getString(R.string.setsilenttimeinfo);
		
		result = String.format(result, Integer.toString(startH) + ":" + Integer.toString(startM)
				, Integer.toString(endH) + ":" + Integer.toString(endM));

		sendStringMSG(result);
		
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isAddedDedicate = false;
		isAddedCustom = false;
		unregisterReceiver(br);
		super.onDestroy();
	}
}
