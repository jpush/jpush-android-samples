package com.jpush.examples;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SampleSetTAGAndAlias extends InstrumentedActivity {
	final private String TAG = "Set alias and TAG";
	final private int MAX_RESULT = 4;
	final private String Delimeter = "--";
	
	final private int MSG_SHOWINFO_RESID = 0;
	final private int MSG_SHOWINFO_STRING = 1;
	
	private Handler mHandler;
	private int excuteIndex = 0;
	private String excuteResult = "";
	private int mOperator = 0;/*nothing will set*/
	
	private int SIGN_SET_ALIAS = 0x0001;
	private int SIGN_SET_TAG = 0x0010;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settagandalias);
		
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case MSG_SHOWINFO_RESID:
						Toast.makeText(getApplicationContext(), msg.arg1, Toast.LENGTH_LONG).show();
					case MSG_SHOWINFO_STRING:
						Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
				}
				
				super.handleMessage(msg);
			}
		};
		
		CheckBox toggleAlias = (CheckBox) findViewById(R.id.toggleAlias);
		setOperatorValue(SIGN_SET_ALIAS, toggleAlias.isChecked());
		CheckBox toggleTAG = (CheckBox) findViewById(R.id.toggleTAG);
		setOperatorValue(SIGN_SET_TAG, toggleTAG.isChecked());
		
		EditText editAlias = (EditText) findViewById(R.id.setAlias);
		if (checkOperator(SIGN_SET_ALIAS))
			editAlias.setEnabled(true);
		else
			editAlias.setEnabled(false);
		
		EditText editTAG = (EditText) findViewById(R.id.setTAG);
		if (checkOperator(SIGN_SET_TAG))
			editTAG.setEnabled(true);
		else
			editTAG.setEnabled(false);
		
		refreshResult();
	}
	
	void setOperator(int opt) {
		mOperator |= opt;
	}
	
	void clearOperator(int opt) {
		mOperator &= ~opt;
	}
	
	boolean checkOperator(int opt) {
		return (mOperator & opt) != 0;
	}
	
	private void refreshResult() {
		TextView resultOfSet = (TextView) findViewById(R.id.resultOfSet);
		resultOfSet.setText(excuteResult);
		resultOfSet.postInvalidate();
	}
	
	public void toggleAlias(View v) {
		CheckBox toggle = (CheckBox)v;
		
		setOperatorValue(SIGN_SET_ALIAS, toggle.isChecked());
	}
	
	public void toggleTAG(View v) {
		CheckBox toggle = (CheckBox)v;
		
		setOperatorValue(SIGN_SET_TAG, toggle.isChecked());
	}
	
	private void setOperatorValue(int SIGN, boolean value) {
		if (value)
			setOperator(SIGN);
		else
			clearOperator(SIGN);
	}
	
	public void excuteSet(View v) {
		if (mOperator == 0) {
			sentToShowMSG(R.string.infotoset);
			return;
		}
			
	}
	
	public void sentToShowMSG(int resStringID) {
		Message msg = mHandler.obtainMessage(MSG_SHOWINFO_RESID, resStringID, 0);
		mHandler.sendMessage(msg);
	}
	
	public void sentToShowMSG(String msgContent) {
		Message msg = mHandler.obtainMessage(MSG_SHOWINFO_STRING, msgContent);
		mHandler.sendMessage(msg);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
