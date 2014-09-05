package com.letv.android.client.pluginloader.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.letv.android.client.pluginloader.entity.DLingEntity;

public class DownloadService extends Service {

    private DownloadManager mDownloadManager;

    @Override
    public IBinder onBind(Intent intent) {

        return new DownloadServiceImpl();
    	}

    @Override
    public void onCreate() {

        super.onCreate();
        mDownloadManager = new DownloadManager(this);
    	}

    @Override
    public void onStart(Intent intent, int startId) {

        super.onStart(intent, startId);

        if (intent.getAction().equals(DownloadConstant.SERVICE_ACTION)) {
            int type = intent.getIntExtra(DownloadConstant.TYPE, -1);
            DLingEntity dlentity;

            switch (type) {
                case DownloadConstant.Types.START:{
                    if (!mDownloadManager.isRunning()) {
                        mDownloadManager.startManage();
                    } else {
                        mDownloadManager.reBroadcastAddAllTask();
                    				      }
                    break;
                			  }
                case DownloadConstant.Types.ADD:{
	                 dlentity = (DLingEntity) intent.getSerializableExtra(DownloadConstant.ENTITY);
	                 String url = dlentity.getUrl();
	                 if (!TextUtils.isEmpty(url) && !mDownloadManager.hasTask(url)) {
	                	  mDownloadManager.addTask(dlentity);
			                	     }
	                 break;
                		          }
                case DownloadConstant.Types.CONTINUE:{
                    String url = intent.getStringExtra(DownloadConstant.URL);
                    if (!TextUtils.isEmpty(url)) {
                        mDownloadManager.continueTask(url);
                    				      }
                    break;
                			 }
                case DownloadConstant.Types.DELETE:{
                    String url = intent.getStringExtra(DownloadConstant.URL);
                    if (!TextUtils.isEmpty(url)) {
                        mDownloadManager.deleteTask(url);
                    				      }
                    break;
                			 }
                case DownloadConstant.Types.PAUSE:{
                	  dlentity = (DLingEntity) intent.getSerializableExtra(DownloadConstant.ENTITY);
                	  String url = dlentity.getUrl();
                    if (!TextUtils.isEmpty(url)) {
                        mDownloadManager.pauseTask(dlentity);
                    				      }
                    break;
                			 }
                case DownloadConstant.Types.STOP:{
                    mDownloadManager.close();
                    // mDownloadManager = null;
                    break;
                			 }
                default:
                    break;
            }
        }

    }

    private class DownloadServiceImpl extends IDownloadService.Stub {

        @Override
        public void startManage() throws RemoteException {

            mDownloadManager.startManage();
        	     }

        @Override
        public void addTask(String url) throws RemoteException {

//            mDownloadManager.addTask(url);
        	     }

        @Override
        public void pauseTask(String url) throws RemoteException {

        	      }

        @Override
        public void deleteTask(String url) throws RemoteException {

        		}

        @Override
        public void continueTask(String url) throws RemoteException {

        		}

    }

}
