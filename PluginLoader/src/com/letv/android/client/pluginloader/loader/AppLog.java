package com.letv.android.client.pluginloader.loader;

import android.util.Log;

import com.letv.android.client.pluginloader.common.Constant;

public class AppLog {
	public static void log(String tag, String msg){
		if(Constant.DEBUG){
			 Log.i(tag, msg);
		 }
	}
}
