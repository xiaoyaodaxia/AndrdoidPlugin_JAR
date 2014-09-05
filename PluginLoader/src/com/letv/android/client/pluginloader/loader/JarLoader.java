package com.letv.android.client.pluginloader.loader;

import java.io.File;

import dalvik.system.DexClassLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

@SuppressLint("NewApi")
public class JarLoader {
	/**
	 * @author chenlifeng1
	 * @param context
	 * @param jarname  插件文件名 *.jar
	 * @param packagename 插件包名
	 * @param classname	插件包名后面的路径 不需要.class后缀
	 * @return 返回class实例
	 */
	public static Class loadClass(Context context, String jarname, String packagename, String classname){
		File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE), jarname);
		File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);

		Class myClass = null;
		AppLog.log("aa","...dexInternalStoragePath.exists() + "+dexInternalStoragePath.exists());
		if(dexInternalStoragePath.exists()){
			try {
				DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
						optimizedDexOutputPath.getAbsolutePath(), null, ClassLoader
						.getSystemClassLoader().getParent());
				String clazzname = packagename+"."+classname;
				AppLog.log("aa","...clazzname is "+clazzname);
				myClass = cl.loadClass(clazzname);
				
//				Object obj = myClass.newInstance();
//
//				AppLog.log("aa","...obj is "+obj);
//				AppLog.log("aa","...obj is "+obj.getClass().getName());
			} catch (Exception e) {
				AppLog.log("aa","...ex is "+e.getMessage());
			}
		}
		return myClass;
	}
}
