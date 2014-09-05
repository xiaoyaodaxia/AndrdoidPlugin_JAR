package com.letv.test;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.letv.android.client.pluginloader.loader.AppLog;
import com.letv.android.client.pluginloader.loader.JarLoader;
import com.letv.android.client.pluginloader.loader.Util;
import com.letv.plugin.plugin.PluginUtil;

public class MainActivity extends Activity {
	public static final String TAG = "MainActivity";
	private TextView textview;
	private Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textview = (TextView) findViewById(R.id.textview);
		button = (Button) findViewById(R.id.button);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("aa","...1");
				Util.copyJar(getApplicationContext(), "dexed.jar");
				Class clazz = JarLoader.loadClass(getApplicationContext(), "dexed.jar", "cn.com.iresearch.mapptracker", "IRMonitor");
				try {
					AppLog.log("aa", "...clazz is "+clazz);
//					Object obj = clazz.newInstance();
					Method action = clazz.getMethod("getInstance", new Class[]{Context.class});
					Object iRmonitor = action.invoke(clazz, new Object[]{MainActivity.this});
					AppLog.log("aa", "...iRmonitor is "+iRmonitor);
					String makey = "440e9707b1c3669a";
					String deviceId = "12345678";
					boolean isDebug = true;
					Method iRmethod = clazz.getMethod("Init", new Class[]{String.class, String.class, boolean.class});	//boolean.class小写
					AppLog.log("aa", "...iRmethod is "+iRmethod);
					iRmethod.invoke(iRmonitor, new Object[]{makey, deviceId, isDebug});
					
					textview.setText("class is "+iRmonitor.getClass().getSimpleName());
				} catch (Exception e) {
					textview.setText("error is "+e.getMessage());
				}
				Log.i("aa","...2");
			}
		});
	}
}
