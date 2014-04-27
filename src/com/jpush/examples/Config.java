package com.jpush.examples;

public class Config {
	public static boolean log_stwitcher = true;
	
	public static String PUSH_NOTIFICATION_EXTRA = "CMD_NOTIFICATION";
	public static String CMD_OPEN_RECEIVE_NOTIFICATION = "receiveNotification";/*  */
	
	public enum ReceiveType {
		TYPE_NOTIFICATION,
		TYPE_MESSAGE
	}
}
