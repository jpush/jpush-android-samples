package com.jpush.examples;

import java.io.File;

import com.jpush.examples.utilities.Lg;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class SampleRichPush extends InstrumentedActivity {
	final private int MSG_SHOW_INFO = 1;
	final private int MSG_OPEN_FILE = 2;
	
	private View richView;
	private Handler mHandler;
	private BroadcastReceiver br;
	private String newTitle = null;
	private String multiMediaPath = null;/* store multimedia file */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.richpush);
		
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Bundle bundle = intent.getExtras();
				
				if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) { 
					receivingNotification(context, bundle, Config.ReceiveType.TYPE_MESSAGE);
				} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
					receivingNotification(context, bundle, Config.ReceiveType.TYPE_NOTIFICATION);
		 	    } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
					richPushCallback(bundle);
		 	    } else {

		        }
			}
		};
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(JPushInterface.ACTION_MESSAGE_RECEIVED);
		filter.addAction(JPushInterface.ACTION_NOTIFICATION_RECEIVED);
		filter.addAction(JPushInterface.ACTION_NOTIFICATION_OPENED);
		filter.addAction(JPushInterface.ACTION_RICHPUSH_CALLBACK);
		filter.addCategory(getPackageName());
		
		registerReceiver(br, filter);
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_SHOW_INFO:
						Toast.makeText(getApplicationContext(), msg.arg1, Toast.LENGTH_LONG).show();
					case MSG_OPEN_FILE:
						openFile();
				}
				
				super.handleMessage(msg);
			}
		};
	}
	
	private void richPushCallback(Bundle bundle) {
		Lg.d("test", "richPushCallback 11111111111111111111111111");
	}
	
	private void receivingNotification(Context context, Bundle bundle, Config.ReceiveType type){
		String filePath = null;
        String fileRes = null;
//        String content = null;
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE); 
        String fileType = bundle.getString(JPushInterface.EXTRA_CONTENT_TYPE);
        
        if (title == null || title.equals("")) {
        	title = getString(R.string.notitle);
        }
        
        if (type == Config.ReceiveType.TYPE_NOTIFICATION) {
//        	content = bundle.getString(JPushInterface.EXTRA_ALERT);
        	filePath = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_PATH);
        	fileRes = bundle.getString(JPushInterface.EXTRA_RICHPUSH_HTML_RES);
        	
        	constructRichPushHtml(title, fileType, filePath, fileRes);
        }
        else if (type == Config.ReceiveType.TYPE_MESSAGE) {
//        	content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        	filePath = bundle.getString(JPushInterface.EXTRA_RICHPUSH_FILE_PATH);
        	constructRichPushVideo(title, fileType, filePath);
        }
        else
        	;/* do nothing */
    }
	
	private void openFile() {
		MimeTypeMap myMime = MimeTypeMap.getSingleton();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String mimiType = myMime.getMimeTypeFromExtension(getExtention(multiMediaPath));
		File file = new File(multiMediaPath);
		
		intent.setDataAndType(Uri.fromFile(file), mimiType);
		
		if (intent.resolveActivity(getPackageManager()) != null)
			startActivity(intent);
		else
			sendMSG(MSG_SHOW_INFO, R.string.fileunsupport);

		multiMediaPath = null;
	}
	
	public void clickButton(View v) {
		if (multiMediaPath != null) {
			sendMSG(MSG_OPEN_FILE);
		} else 
		if (richView != null) {
			richView.setVisibility(View.VISIBLE);
			richView.postInvalidate();
			richView = null;
		}
		v.setEnabled(false);
		((Button)v).setText(newTitle);
		v.postInvalidate();
	}
	
	private String getExtention(String path) {
		String ext = null;
		int index = -1;
		index = path.lastIndexOf('.');
		
		if (index == -1)
			return ext;
		
		index++;/* skip '.' */
		
		ext = path.substring(index);
		
		return ext;
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void constructRichPushHtml(String title, String type, String path, String resPath) {
		if (path == null || path.equals(""))
			return;
		
		newTitle = title;/* store the new title */
		
		setViewState();

		WebView wbV = (WebView) findViewById(R.id.webView);
		
		String lPath = "file:///" + path;
		wbV.getSettings().setJavaScriptEnabled(true);
		wbV.loadUrl(lPath);
		richView = wbV;
	}
	
	private void constructRichPushVideo(String title, String type, String path) {
		if (path == null || path.equals("")) {
			sendMSG(MSG_SHOW_INFO, R.string.fileerror);
			return;
		}
		
		multiMediaPath = path;
		
		newTitle = title;/* store the new title */
		
		setViewState();
	}
	
	public void sendMSG(int msgID) {
		Message msg = mHandler.obtainMessage(msgID, 0, 0);
		mHandler.sendMessage(msg);
	}
	
	public void sendMSG(int msgID, int resID) {
		Message msg = mHandler.obtainMessage(msgID, resID, 0);
		mHandler.sendMessage(msg);
	}
	
	private void setViewState() {
		WebView wbV = (WebView) findViewById(R.id.webView);
		wbV.setVisibility(View.GONE);
		
		Button btn = (Button) findViewById(R.id.richbutton);
		btn.setEnabled(true);
		btn.setText(R.string.newrichpush);
		btn.postInvalidate();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(br);
		super.onDestroy();
	}
}
