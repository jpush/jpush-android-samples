package com.jpush.examples;

import org.json.JSONObject;

import com.jpush.examples.utilities.Lg;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SampleReceNotification extends InstrumentedActivity {
	private String TAG = "Receive Notification";
	private ListenBroadcast listener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.recenotify);
		
		listener = new ListenBroadcast();
		
		IntentFilter filter = new IntentFilter();
		
		filter.addAction(JPushInterface.ACTION_NOTIFICATION_RECEIVED);
		filter.addAction(JPushInterface.ACTION_NOTIFICATION_OPENED);
		filter.addCategory(getPackageName());
		
		registerReceiver(listener, filter);
	}
	
	class ListenBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
			
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
	             /* 这里不处理 */
	        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
	        	/* 这里不处理 */
	        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
    
	            receivingNotification(context, bundle);
	 
	        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
      
	           openNotification(context, bundle);
	 
	        } else {
	            Lg.d(TAG, "Unhandled intent - " + intent.getAction());
	        }
		}
	}
	
	private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Lg.d(TAG, " title : " + title);
        String notification = bundle.getString(JPushInterface.EXTRA_ALERT);
        Lg.d(TAG, "message : " + notification);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Lg.d(TAG, "extras : " + extras);
        
        displayResult(title, notification, extras);
    } 
 
   private void openNotification(Context context, Bundle bundle){
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String myValue = ""; 
        try {
            JSONObject extrasJson = new JSONObject(extras);
            /* defined by developer on client and send this extra, will open this activity */
            myValue = extrasJson.optString(Config.PUSH_NOTIFICATION_EXTRA);
        } catch (Exception e) {
        	Lg.e(TAG, "Unexpected: extras is not a valid json", e);
            return;
        }
        
        if (myValue.equals(Config.CMD_OPEN_RECEIVE_NOTIFICATION)) {
            Intent mIntent = new Intent(context, SampleReceNotification.class);
            mIntent.putExtras(bundle);
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
    }
   
   private void displayResult(String title, String notification, String extra) {
	   EditText titleText = (EditText) findViewById(R.id.receive_notification_title);
		
		EditText titleContent = (EditText) findViewById(R.id.receive_notification_content);
		
		titleText.setText(title);
		String content = notification + "\n" + extra;
		titleContent.setText(content);
		
		titleText.postInvalidate();
		titleContent.postInvalidate();
   }
	
	public void clickToClear(View v) {
		EditText titleText = (EditText) findViewById(R.id.receive_notification_title);
		
		EditText titleContent = (EditText) findViewById(R.id.receive_notification_content);
		
		titleText.setText("");
		titleContent.setText("");
		
		titleText.postInvalidate();
		titleContent.postInvalidate();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(listener);
		super.onDestroy();
	}
}
