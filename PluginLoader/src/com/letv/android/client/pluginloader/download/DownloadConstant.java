package com.letv.android.client.pluginloader.download;

public class DownloadConstant {
	/**
	 * 下载Service Action
	 */
	public static final String SERVICE_ACTION = "com.grl.grlgkk.dloader.IDownloadService";
	
	/**
	 * 广播 Action
	 */
	public static final String BROAD_ACTION = "com.grl.grlgkk.dloader.OfflineFragment";
	
	public static final String ENTITY = "entity";
	public static final String TYPE = "type";
	public static final String PROCESS_SPEED = "process_speed";
	public static final String PROCESS_PROGRESS = "process_progress";
	public static final String PROCESS_RATE = "process_rate";
	public static final String URL = "url";
	public static final String ERROR_CODE = "error_code";
	public static final String ERROR_INFO = "error_info";
	public static final String IS_PAUSED = "is_paused";
	
	public class Types{
		public static final int PROCESS = 0;
		public static final int COMPLETE = 1;
		
		public static final int START = 2;
		public static final int PAUSE = 3;
		public static final int DELETE = 4;
		public static final int CONTINUE = 5;
		public static final int ADD = 6;
		public static final int STOP = 7; 
		public static final int ERROR = 9;
	}
}
