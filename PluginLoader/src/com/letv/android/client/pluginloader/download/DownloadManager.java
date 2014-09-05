package com.letv.android.client.pluginloader.download;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.letv.android.client.pluginloader.common.Constant;
import com.letv.android.client.pluginloader.entity.DLingEntity;
import com.letv.android.client.pluginloader.loader.Util;

public class DownloadManager extends Thread {

    private static final int MAX_TASK_COUNT = 100;
    private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;

    private Context mContext;

    private TaskQueue mTaskQueue;
    private List<DownloadTask> mDownloadingTasks;
    private List<DownloadTask> mPausingTasks;
//    private DownLoadDAO downLoadDAO;

    private Boolean isRunning = false;

    public DownloadManager(Context context) {

        mContext = context;
        mTaskQueue = new TaskQueue();
        mDownloadingTasks = new ArrayList<DownloadTask>();
        mPausingTasks = new ArrayList<DownloadTask>();
//        downLoadDAO = new DownLoadDAO(context);
    	}

    public void startManage() {
        isRunning = true;
        this.start();
        checkUncompleteTasks();
    	}

    public void close() {

        isRunning = false;
        pauseAllTask();
        this.stop();
    	}

    public boolean isRunning() {

        return isRunning;
    	}

    @Override
    public void run() {

        super.run();
        while (isRunning) {
            DownloadTask task = mTaskQueue.poll();
            mDownloadingTasks.add(task);
            task.execute();
        }
    }

    public void addTask(DLingEntity dlentity) {

        if (!Util.hasSDCard()) {
            Toast.makeText(mContext, "未发现SD卡", Toast.LENGTH_LONG).show();
            return;
        }


        if (getTotalTaskCount() >= MAX_TASK_COUNT) {
            Toast.makeText(mContext, "任务列表已满", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            addTask(newDownloadTask(dlentity));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void addTask(DownloadTask task) {

        broadcastAddTask(task.getDlentity().getUrl());


        DLingEntity dl = task.getDlentity();
        if(dl.getStatus() == 1){  //暂停
        	  // move to pausing list
        	  task.onCancelled();
        	  try {
        		  task = newDownloadTask(task.getDlentity());
        		  mPausingTasks.add(task);
        	  } catch (MalformedURLException e) {
        		  e.printStackTrace();
        	  	     }
	     }else{
		     mTaskQueue.offer(task);
        	      }
        
        if (!this.isAlive()) {
            this.startManage();
        }
    }

    private void broadcastAddTask(String url) {

        broadcastAddTask(url, false);
    }

    private void broadcastAddTask(String url, boolean isInterrupt) {

        Intent nofityIntent = new Intent(DownloadConstant.BROAD_ACTION);
        nofityIntent.putExtra(DownloadConstant.TYPE, DownloadConstant.Types.ADD);
        nofityIntent.putExtra(DownloadConstant.URL, url);
        nofityIntent.putExtra(DownloadConstant.IS_PAUSED, isInterrupt);
        mContext.sendBroadcast(nofityIntent);
    }

    public void reBroadcastAddAllTask() {
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            broadcastAddTask(task.getDlentity().getUrl(), task.isInterrupt());
        	      }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            broadcastAddTask(task.getDlentity().getUrl());
        	     }
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            broadcastAddTask(task.getDlentity().getUrl());
                      }
            }

    public boolean hasTask(String url) {

        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task.getDlentity().getUrl().equals(url)) {
                return true;
            }
        }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
        }
        return false;
    }

    public DownloadTask getTask(int position) {

        if (position >= mDownloadingTasks.size()) {
            return mTaskQueue.get(position - mDownloadingTasks.size());
        } else {
            return mDownloadingTasks.get(position);
        }
    }

    public int getQueueTaskCount() {

        return mTaskQueue.size();
    }

    public int getDownloadingTaskCount() {

        return mDownloadingTasks.size();
    }

    public int getPausingTaskCount() {

        return mPausingTasks.size();
    }

    public int getTotalTaskCount() {

        return getQueueTaskCount() + getDownloadingTaskCount() + getPausingTaskCount();
           }

    public void checkUncompleteTasks() {
//	    String where = downLoadDAO.KEY_DLOAD_STATUS+"!='"+2+"'"; 
//	    ArrayList<DLingEntity> entities = downLoadDAO.query(null, where, null, null);
//	    for(DLingEntity entity : entities){
//		    if(!TextUtils.isEmpty(entity.getUrl())){
//			   addTask(entity);
//		    	   }
//	    	   }
    }

    public synchronized void pauseTask(DLingEntity dlingEntity) {
//	     String where = downLoadDAO.KEY_DLOAD_URL+"='"+dlingEntity.getUrl()+"'";
//	     downLoadDAO.update(dlingEntity, where, null);
	     
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null && task.getDlentity().getUrl().equals(dlingEntity.getUrl())) {
                pauseTask(task);
            	     }
        	}
    	   }

    public synchronized void pauseAllTask() {

        DownloadTask task;

        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            mTaskQueue.remove(task);
            mPausingTasks.add(task);
        		}

        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null) {
                pauseTask(task);
            			}
        		}
    	}

    public synchronized void deleteTask(String url) {
	     	     //更新据表中内容
//	     String sql = "update "+downLoadDAO.TABLE_DOWN_LOAD+" set status='"+2+"' where "+downLoadDAO.KEY_DLOAD_URL+"='"+url+"'";
//	     downLoadDAO.raw(sql);
	     
        DownloadTask task;
        for (int i = 0; i < mDownloadingTasks.size(); i++) {
            task = mDownloadingTasks.get(i);
            if (task != null && task.getDlentity().getUrl().equals(url)) {
        	    //TODO  只有flv一种格式
                File file = new File(mContext.getDir("dex", Context.MODE_PRIVATE), task.getDlentity().getName());
                if (file.exists())
                    file.delete();

                task.onCancelled();
                completeTask(task, -1);
                return;
            			}
        	      }
        for (int i = 0; i < mTaskQueue.size(); i++) {
            task = mTaskQueue.get(i);
            if (task != null && task.getDlentity().getUrl().equals(url)) {
                mTaskQueue.remove(task);
            			}
        	      }
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getDlentity().getUrl().equals(url)) {
                mPausingTasks.remove(task);
            			}
        		}
    	 }

    public synchronized void continueTask(String url) {

        DownloadTask task;
        for (int i = 0; i < mPausingTasks.size(); i++) {
            task = mPausingTasks.get(i);
            if (task != null && task.getDlentity().getUrl().equals(url)) {
                continueTask(task);
            			}

        		}
    	}

    public synchronized void pauseTask(DownloadTask task) {

        if (task != null) {
            task.onCancelled();

            // move to pausing list
            try {
                mDownloadingTasks.remove(task);
                task = newDownloadTask(task.getDlentity());
                mPausingTasks.add(task);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            			}

        		}
    	}

    public synchronized void continueTask(DownloadTask task) {

        if (task != null) {
            mPausingTasks.remove(task);
            mTaskQueue.offer(task);
        }
    }

	    /**
	     * 
     * @param task
     * @param status -1删除  0正在下载，1暂停，2完成下载
	     */
    public synchronized void completeTask(DownloadTask task, int status) {

        if (mDownloadingTasks.contains(task)) {
//        		Util.updateDownLoadStatus(mContext, status, task.getDlentity().getUrl());
            
        		mDownloadingTasks.remove(task);

            // notify list changed
            Intent nofityIntent = new Intent(DownloadConstant.BROAD_ACTION);
            nofityIntent.putExtra(DownloadConstant.TYPE, DownloadConstant.Types.COMPLETE);
            nofityIntent.putExtra(DownloadConstant.URL, task.getDlentity().getUrl());
            mContext.sendBroadcast(nofityIntent);
        }
    }

    /**
     * Create a new download task with default config
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */
    private DownloadTask newDownloadTask(final DLingEntity dlentity) throws MalformedURLException {

        DownloadTaskListener taskListener = new DownloadTaskListener() {

            @Override
            public void updateProcess(DownloadTask task) {

                Intent updateIntent = new Intent(DownloadConstant.BROAD_ACTION);
                updateIntent.putExtra(DownloadConstant.TYPE, DownloadConstant.Types.PROCESS);
                updateIntent.putExtra(DownloadConstant.PROCESS_SPEED, String.valueOf(task.getDownloadSpeed() + "kbps"));
                updateIntent.putExtra(DownloadConstant.PROCESS_RATE, String.valueOf(task.getDownloadSize()/1024) + "kB/" + String.valueOf(task.getTotalSize()/1024)+"kB");
                updateIntent.putExtra(DownloadConstant.PROCESS_PROGRESS, task.getDownloadPercent());
                updateIntent.putExtra(DownloadConstant.URL, task.getDlentity().getUrl());
                mContext.sendBroadcast(updateIntent);
            			}

            @Override
            public void preDownload(DownloadTask task) {
//        	    	 downLoadDAO.insert(dlentity);
            			}

            @Override
            public void finishDownload(DownloadTask task) {

                completeTask(task, 2);
            			}

            @Override
            public void errorDownload(DownloadTask task, Throwable error) {

                if (error != null) {
                    Toast.makeText(mContext, "Error: " + error.getMessage(), Toast.LENGTH_LONG)
                            .show();
                		}

                // Intent errorIntent = new
                // Intent("com.yyxu.download.activities.DownloadListActivity");
                // errorIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ERROR);
                // errorIntent.putExtra(MyIntents.ERROR_CODE, error);
                // errorIntent.putExtra(MyIntents.ERROR_INFO,
                // DownloadTask.getErrorInfo(error));
                // errorIntent.putExtra(MyIntents.URL, task.getUrl());
                // mContext.sendBroadcast(errorIntent);
                //
                // if (error != DownloadTask.ERROR_UNKOWN_HOST
                // && error != DownloadTask.ERROR_BLOCK_INTERNET
                // && error != DownloadTask.ERROR_TIME_OUT) {
                // completeTask(task);
                // }
            		}
        		};
        return new DownloadTask(mContext, dlentity, mContext.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(), taskListener);
    		}

    /**
     * A obstructed task queue
     * 
     * @author Yingyi Xu
     */
    private class TaskQueue {
        private Queue<DownloadTask> taskQueue;

        public TaskQueue() {

            taskQueue = new LinkedList<DownloadTask>();
        }

        public void offer(DownloadTask task) {

            taskQueue.offer(task);
        }

        public DownloadTask poll() {

            DownloadTask task = null;
            while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
                    || (task = taskQueue.poll()) == null) {
                try {
                    Thread.sleep(1000); // sleep
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return task;
        }

        public DownloadTask get(int position) {

            if (position >= size()) {
                return null;
            }
            return ((LinkedList<DownloadTask>) taskQueue).get(position);
        }

        public int size() {

            return taskQueue.size();
        }

        @SuppressWarnings("unused")
        public boolean remove(int position) {

            return taskQueue.remove(get(position));
        }

        public boolean remove(DownloadTask task) {

            return taskQueue.remove(task);
        }
    }

}
