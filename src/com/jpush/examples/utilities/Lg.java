package com.jpush.examples.utilities;

import com.jpush.examples.Config;

import android.util.Log;

public class Lg {
	private static boolean getLogAble() {
		return Config.log_stwitcher;
	}
	
	public static void i(String TAG, String info) {
		if (getLogAble())
			Log.i(TAG, info);
	}
	
	public static void d(String TAG, String info) {
		if (getLogAble())
			Log.d(TAG, info);
	}
	
	public static void e(String TAG, String info) {
		if (getLogAble())
			Log.e(TAG, info);
	}
	
	public static void e(String TAG, String info,  Throwable tr) {
		if (getLogAble())
			Log.e(TAG, info, tr);
	}
}
