package com.letv.plugin.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class PluginUtil {
	public static void loadJar(Context context, String jarname) {
		try {
			Log.i("aa","...obj is 1");
			//File file = new File("/sdcard/des.jar");
			//File file = new File("file:///android_asset/des.jar");
			File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE),
					jarname);
			BufferedInputStream bis = null;
			OutputStream dexWriter = null;
			
			final int BUF_SIZE = 8 * 1024;
			try {
				bis = new BufferedInputStream(context.getAssets().open(jarname));
			      dexWriter = new BufferedOutputStream(
			          new FileOutputStream(dexInternalStoragePath));
			      byte[] buf = new byte[BUF_SIZE];
			      int len;
			      while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
			          dexWriter.write(buf, 0, len);
			      }
			      dexWriter.close();
			      bis.close();
			} catch (Exception e) {
			}
			Log.i("aa","...obj is 2");
			// File file = new File("/sdcard/test.apk");
			final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);
			if (dexInternalStoragePath.exists())
			{
				Log.i("aa","...context.getFilesDir().getAbsolutePath() is "+context.getFilesDir().getAbsolutePath());
				DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
						optimizedDexOutputPath.getAbsolutePath(), null, ClassLoader
								.getSystemClassLoader().getParent());
				
				Log.i("aa","...obj is 4");
				Class myClass = cl.loadClass("com.lance.framecore.download.IDownload");
				Log.i("aa","...obj is 5");
				Object obj = myClass.newInstance();

				Log.i("aa","...obj is "+obj);
				Log.i("aa","...obj is "+obj.getClass().getName());
				Class[] params = new Class[2];

				params[0] = Integer.TYPE;

				params[1] = Integer.TYPE;

				Method action = myClass.getMethod("Add", params);

				int ret = (Integer) action.invoke(obj, 15, 20);
				Log.i("aa","...ret is "+ret);
			}
		}catch (Exception ex){
			Log.i("aa","...ex is "+ex.getMessage());
		}
	}
}
