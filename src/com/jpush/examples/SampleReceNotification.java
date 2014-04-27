package com.jpush.examples;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jpush.examples.utilities.BoardTextView;
import com.jpush.examples.utilities.Lg;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

public class SampleReceNotification extends InstrumentedActivity {
	final private String TAG = "Receive Notification";
	final private String Delimeter = "-------->";
	final private int MSG_NEW_RECEIVE = 1;
		
	private ListenBroadcast listener;
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.recenotify);
		
		listener = new ListenBroadcast();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(JPushInterface.ACTION_MESSAGE_RECEIVED);
		filter.addAction(JPushInterface.ACTION_NOTIFICATION_RECEIVED);
		filter.addAction(JPushInterface.ACTION_NOTIFICATION_OPENED);
		filter.addCategory(getPackageName());
		
		registerReceiver(listener, filter);
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_NEW_RECEIVE:
						Toast.makeText(getApplicationContext(), msg.arg1, Toast.LENGTH_LONG).show();
				}
				
				super.handleMessage(msg);
			}
		};
	}
	
	class ListenBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
			
			if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) { 
				receivingNotification(context, bundle, Config.ReceiveType.TYPE_MESSAGE);
			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				receivingNotification(context, bundle, Config.ReceiveType.TYPE_NOTIFICATION);
	 	    } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
	 	    	openNotification(context, bundle);
	        } else {
	            Lg.d(TAG, "Unhandled intent - " + intent.getAction());
	        }
		}
	}
	
	private void newReceiveInfo(Config.ReceiveType type) {
		int resID;
		
		switch (type) {
			case TYPE_NOTIFICATION:
				resID = R.string.newnotification;
				break;
			case TYPE_MESSAGE:
				resID = R.string.newmessage;
				break;
			default:
				return;
		}
		
		Message msg = mHandler.obtainMessage(MSG_NEW_RECEIVE, resID, 0);
		mHandler.sendMessage(msg);
	}
	
	private void receivingNotification(Context context, Bundle bundle, Config.ReceiveType type){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Lg.d(TAG, " title : " + title);
        String content;
        
        if (type == Config.ReceiveType.TYPE_NOTIFICATION)
        	content = bundle.getString(JPushInterface.EXTRA_ALERT);
        else if (type == Config.ReceiveType.TYPE_MESSAGE)
        	content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        else
        	content = getString(R.string.unknown);
        
        Lg.d(TAG, "message : " + content);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Lg.d(TAG, "extras : " + extras);
        
        if (extras == null || extras.isEmpty())
        	extras = getString(R.string.noextra);
        
        newReceiveInfo(type);
        displayResult(title, content, extras);
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
	    BoardTextView titleText = (BoardTextView) findViewById(R.id.receive_notification_title);
		
	    BoardTextView titleContent = (BoardTextView) findViewById(R.id.receive_notification_content);
		
		titleText.setText(title);
		String content = notification + "\nresult of parse Json content :\n" + parseJson(extra);
		titleContent.setText(content);
		
		titleText.postInvalidate();
		titleContent.postInvalidate();
   }
   
   private String parseJson(String json) {
	   String result = "";
	   if (json == null || json.isEmpty())
		   return null;
	   
	   try {
			JSONObject extrasJson = new JSONObject(json);
			JSONArray arr = extrasJson.names();
			if (arr == null)
				return json;
			int count = arr.length();
			String key = null;
			for (int i = 0; i < count; i++) {
				key = arr.getString(i);
				result += key + Delimeter + extrasJson.getString(key) + "\n";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return json;
		}

	   return result;
   }
	
	public void clickToClear(View v) {
		BoardTextView titleText = (BoardTextView) findViewById(R.id.receive_notification_title);
		
		BoardTextView titleContent = (BoardTextView) findViewById(R.id.receive_notification_content);
		
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
