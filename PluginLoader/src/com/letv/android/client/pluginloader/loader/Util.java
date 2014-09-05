package com.letv.android.client.pluginloader.loader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;

public class Util {
	/**
	 * @author chenlifeng1
	 * @param context
	 * @param jarname
	 * @return jar存放路径
	 * 将jar插件存放到应用目录下,不放到扩展存储设备中，防止代码注入
	 */
	public static String copyJar(Context context, String jarname){
		File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE), jarname);
		BufferedInputStream bis = null;
		OutputStream dexWriter = null;
		
		final int BUF_SIZE = 8 * 1024;
		try {
			bis = new BufferedInputStream(context.getAssets().open(jarname));
		    dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
		    byte[] buf = new byte[BUF_SIZE];
		    int len;
		    while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
		        dexWriter.write(buf, 0, len);
		    }
		    dexWriter.close();
		    bis.close();
		} catch (Exception e) {
		}
		return dexInternalStoragePath.getAbsolutePath();
	}
	
	public static boolean hasNetwork(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		} else {
			if (info.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 文件操作
	 */
	public static boolean hasSDCard() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	public static long getAvailableStorage() {
		String storageDirectory = null;
		storageDirectory = Environment.getExternalStorageDirectory().toString();
		
		try {
		    StatFs stat = new StatFs(storageDirectory);
		    long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat.getBlockSize());
		    return avaliableSize;
		} catch (RuntimeException ex) {
		    return 0;
	        }
	  }
}
