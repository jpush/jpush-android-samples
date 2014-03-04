package com.jpush.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SampleSetTAGAndAlias extends InstrumentedActivity {
	final private int MAX_RESULT = 4;
	final private String Delimeter = "--";
	
	final private int MSG_SHOWINFO_RESID = 0;
	final private int MSG_SHOWINFO_STRING = 1;
	final private int MSG_REFRESH = 2;
	
	private Handler mHandler;
	private int excuteIndex = 0;
	private String excuteResult = "";
	private int mOperator = 0;/*nothing will set*/
	
	private Set<String> invalidTAG = null;
	
	final private int SIGN_SET_ALIAS = 0x0001;
	final private int SIGN_SET_TAG = 0x0010;
	
	private ArrayList<setThread> workerList = new ArrayList<setThread>(MAX_RESULT);
	
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
						break;
					case MSG_SHOWINFO_STRING:
						Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
						break;
					case MSG_REFRESH:
						refreshResult();
						refreshView();
						break;
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
		
		refreshView();
		
		for (int i = 0; i < MAX_RESULT; i++) {
			workerList.add(new setThread());
		}
	}
	
	public String excuteToString(int excute) {
		String content = null;
		
		switch (excute) {
			case SIGN_SET_ALIAS:
				content = getString(R.string.infoalias);
				break;
			case SIGN_SET_TAG:
				content = getString(R.string.infotag);
				break;
			case SIGN_SET_ALIAS | SIGN_SET_TAG:
				content = getString(R.string.infoall);
				break;
			default:
				break;
		}
		
		return content;
	}
	
	final class setThread extends Thread {
		private Handler wHandler;
		private Runnable runnable;
		int mIndex;
		int mGlobleIndex;
		boolean isEnd = true;
		int excuteSet = 0;
		String mResult = "";
		TagAliasCallback cb;
		
		Set<String> mTags;
		String mAlias;
		
		public String getResult() {
			return mResult;
		}
		
		public setThread() {
			wHandler = new Handler();
			
			cb = new TagAliasCallback() {
				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					// TODO Auto-generated method stub
					switch (arg0) {
						case 6002:/*please refer to http://docs.jpush.cn/pages/viewpage.action?pageId=557241*/
							repeatSet();
							break;
						default:
							completeSet(arg0);
							sendMSG(MSG_REFRESH);
							break;
					}
				}
			};
		}
		
		public void setExcute(int exc) {
			excuteSet = exc;
		}
		
		public int getExcuteSet() {
			return excuteSet;
		}
		
		public void setGlobeIndex(int gIndex) {
			mGlobleIndex = gIndex;
		}
		
		public int getGlobeIndex() {
			return mGlobleIndex;
		}
		
		public void setAliasAndTag(String alias, Set<String> tags) {
			mAlias = alias;
			mTags = tags;
		}
		
		public void beginNewSet() {
			mResult = Integer.toString(mGlobleIndex) + Delimeter + excuteToString(excuteSet)
					+ Delimeter + getString(R.string.infosetting);
			isEnd = false;
			
			runnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					switch(getExcuteSet()) {
						case SIGN_SET_ALIAS:
							JPushInterface.setAlias(getApplicationContext(), mAlias, cb);
							break;
						case SIGN_SET_TAG:
							JPushInterface.setTags(getApplicationContext(), mTags, cb);
							break;
						case SIGN_SET_ALIAS | SIGN_SET_TAG:
							JPushInterface.setAliasAndTags(getApplicationContext(), mAlias, mTags, cb);
							break;
					
					}
				}
			};
			wHandler.post(runnable);
		}
		
		public void repeatSet() {
			wHandler.post(runnable);
		}
		
		/**
		 * 
		 * @param result 0:set success, other:set failure
		 */
		public void completeSet(int result) {
			isEnd = true;
			runnable = null;
			String mRes = result == 0 ? getString(R.string.infosucc) : getString(R.string.infofail);
			String format = String.format(getString(R.string.excuteresult), mRes, result);
			mResult = mResult.substring(0, mResult.lastIndexOf(getString(R.string.infosetting))) + format;
		}
		
		public boolean getWorkIsEnd() {
			return isEnd;
		}
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
		String temp = "";
		for (int i = 0; i < MAX_RESULT; i++) {
			temp += workerList.get(i).getResult() + "\n";
		}
		
		excuteResult = temp;
	}
	
	private void refreshView() {
		TextView resultOfSet = (TextView) findViewById(R.id.resultOfSet);
		resultOfSet.setText(excuteResult);
		resultOfSet.postInvalidate();
	}
	
	public void toggleAlias(View v) {
		CheckBox toggle = (CheckBox)v;
		
		setOperatorValue(SIGN_SET_ALIAS, toggle.isChecked());
		
		EditText editAlias = (EditText) findViewById(R.id.setAlias);
		if (checkOperator(SIGN_SET_ALIAS))
			editAlias.setEnabled(true);
		else
			editAlias.setEnabled(false);
	}
	
	public void toggleTAG(View v) {
		CheckBox toggle = (CheckBox)v;
		
		setOperatorValue(SIGN_SET_TAG, toggle.isChecked());
		
		EditText editTAG = (EditText) findViewById(R.id.setTAG);
		if (checkOperator(SIGN_SET_TAG))
			editTAG.setEnabled(true);
		else
			editTAG.setEnabled(false);
	}
	
	private void setOperatorValue(int SIGN, boolean value) {
		if (value)
			setOperator(SIGN);
		else
			clearOperator(SIGN);
	}
	
	private setThread findFreeWork(int index) {
		setThread res = null;
		setThread temp = null;
		
		for (int i = 0; i < MAX_RESULT; i++) {
			temp = workerList.get(i);
			if (temp.getResult() == null) {
				res = temp;
				break;
			}
		}
		
		if (res != null) {
			return res;
		}
		
		int nextIndex = ++index;
		int realIndex = 0;
		
		for (int i = 0; i < MAX_RESULT; i++) {
			realIndex = (nextIndex + i) % MAX_RESULT;
			temp = workerList.get(realIndex);
			if (temp.getWorkIsEnd()) {
				res = temp;
				break;
			}
		}
		return res;
	}
	
	public void excuteSet(View v) {
		if (mOperator == 0) {
			sentToShowMSG(R.string.infotoset);
			return;
		}
		
		final setThread work = findFreeWork(excuteIndex);
		
		if (work == null) {
			sentToShowMSG(R.string.maxexcute);
			return;
		}
				
		String lAlias = null;
		Set<String> lTAG = null;
		
		EditText editAlias;

		switch(mOperator) {
			case SIGN_SET_ALIAS:
				editAlias = (EditText) findViewById(R.id.setAlias);
				lAlias = editAlias.getText().toString();
				break;
			case SIGN_SET_TAG:
				lTAG = getTAGS();
				break;
			case SIGN_SET_ALIAS | SIGN_SET_TAG:
				editAlias = (EditText) findViewById(R.id.setAlias);
				lAlias = editAlias.getText().toString();
				lTAG = getTAGS();
				break;
			default:
				return;
		}
		
		if (!checkValidTAGS()) {
			sentToShowMSG(R.string.settagerror);
			return;
		}
		
		excuteIndex++;
		
		work.setGlobeIndex(excuteIndex);
		work.setExcute(mOperator);
		work.setAliasAndTag(lAlias, lTAG);
		work.beginNewSet();
		
		sendMSG(MSG_REFRESH);
	}
	
	public boolean checkValidTAGS() {
		boolean res = false;
		
		if (invalidTAG == null)
			return true;
		
		TextView tv = (TextView) findViewById(R.id.filterTAG);
		tv.setText(invalidTAG.toString());
		tv.postInvalidate();
		
		return res;
	}
	
	private Set<String> getTAGS() {
		String tags;
		String arrTags[];
		
		EditText editTAG = (EditText) findViewById(R.id.setTAG);
		tags = editTAG.getText().toString();
		arrTags = tags.split(",");
		
		Set<String> allTag = new HashSet<String>();
		
		Collections.addAll(allTag, arrTags);
		
		int orignalSize = allTag.size();
		int checkedSize = 0;
		
		invalidTAG = JPushInterface.filterValidTags(allTag);
		
		if (invalidTAG != null)
			checkedSize = invalidTAG.size();
		
		if (checkedSize == orignalSize) /* have invalid tag */
			invalidTAG = null;
		else {
			 if (allTag.removeAll(invalidTAG))
				 invalidTAG = allTag;
			 else
				 sentToShowMSG(" set tag has encounted an internal error!!!!!!!!! ");
		}
		
		return allTag;
	}
	
	public void sendMSG(int msgID) {
		Message msg = mHandler.obtainMessage(msgID, 0, 0);
		mHandler.sendMessage(msg);
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
		excuteIndex = 0;
		super.onDestroy();
	}
}
