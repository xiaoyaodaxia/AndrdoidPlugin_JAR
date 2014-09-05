package com.letv.android.client.pluginloader.activity;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.letv.android.client.pluginloader.R;
import com.letv.android.client.pluginloader.download.DownloadConstant;
import com.letv.android.client.pluginloader.loader.AppLog;
import com.letv.android.client.pluginloader.loader.JarLoader;
import com.letv.android.client.pluginloader.loader.Util;

public class MainActivity extends Activity {
	public static final String TAG = "";
	private TextView textview1, textview2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textview1 = (TextView) findViewById(R.id.textview1);
		textview2 = (TextView) findViewById(R.id.textview2);
		
		String url = "http://10.59.4.67/download/dexed.jar";
		Intent download_it = new Intent(DownloadConstant.SERVICE_ACTION);
		download_it.putExtra(DownloadConstant.TYPE, DownloadConstant.Types.ADD);
		download_it.putExtra(DownloadConstant.URL, url);
		startService(download_it);
		
		//Test1
		Util.copyJar(getApplicationContext(), "dexed.jar");
		Class clazz = JarLoader.loadClass(getApplicationContext(), "dexed.jar", "cn.com.iresearch.mapptracker", "IRMonitor");
		try {
//			Object obj = clazz.newInstance();
			Method action = clazz.getMethod("getInstance", new Class[]{Context.class});
			Object iRmonitor = action.invoke(clazz, new Object[]{MainActivity.this});
			AppLog.log("aa", "...iRmonitor is "+iRmonitor);
			String makey = "440e9707b1c3669a";
			String deviceId = "12345678";
			boolean isDebug = true;
			Method iRmethod = clazz.getMethod("Init", new Class[]{String.class, String.class, boolean.class});	//boolean.class小写
			AppLog.log("aa", "...iRmethod is "+iRmethod);
			iRmethod.invoke(iRmonitor, new Object[]{makey, deviceId, isDebug});
			
			textview1.setText("class is "+iRmonitor.getClass().getSimpleName());
		} catch (Exception e) {
			textview1.setText("error is "+e.getMessage());
		}
		
		//test2
		Util.copyJar(getApplicationContext(), "AddFunc.jar");
		Class clazz2 = JarLoader.loadClass(getApplicationContext(), "AddFunc.jar", "com.demo.jar", "AddFunc");
		try {
			Object obj2 = clazz2.newInstance();
			Method addMethod = clazz2.getMethod("Add", new Class[]{int.class, int.class});
			int rt = (Integer) addMethod.invoke(obj2, new Object[]{20, 21});
			textview2.setText("20+21="+rt);
		} catch (Exception e) {
			textview2.setText("error is "+e.getMessage());
		}
	}
}
