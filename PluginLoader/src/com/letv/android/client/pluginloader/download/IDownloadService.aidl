package com.letv.android.client.pluginloader.download;

interface IDownloadService {
	
	void startManage();
	
	void addTask(String url);
	
	void pauseTask(String url);
	
	void deleteTask(String url);
	
	void continueTask(String url);
}
