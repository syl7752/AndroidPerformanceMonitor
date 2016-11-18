package com.lenovo.infomonitor.infodrawing;

import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.lenovo.info.util.Utils;

public class DrawingService extends Service {
    private static ArrayList<Integer> memDataList=new ArrayList<Integer>();
    private static ArrayList<Date> memTimeList=new ArrayList<Date>();
	private static GetMemoryThread getMemoryThread;
	private MemoryDataHelper memoryDataHelper;
	Utils utils;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Notification notification=new Notification();
		startForeground(1, notification);
		memoryDataHelper=MemoryDataHelper.init(this);
		utils=Utils.getInstance();
		startDraw();
	}
	  /**
     * 开启获得free线程
     * @author shiyl
     * 2014-9-4
     */
    public void startDraw()
    {
    	if(Utils.DEBUG)
    		Log.d(Utils.DEBUG_TAG, "start draw memory");
    	if(getMemoryThread==null)
    	getMemoryThread=new GetMemoryThread();
    	getMemoryThread.start();
    }
    /**
     * 每隔2分钟获取free 并发送消息更新UI
     * @author shiyl
     * 2014-9-4
     */
    public class GetMemoryThread extends Thread
    {
    	private boolean isRun=false;
    	private int free=0;
    	public GetMemoryThread()
    	{
    		isRun=true;

    	}
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		super.run();
    		if(Utils.DEBUG)
        		Log.d(Utils.DEBUG_TAG, "GetMemoryThread start");
    		while(isRun)
    		{
    			free=utils.getFreeMem();
    			memoryDataHelper.insert(null,free);
    			
    		    SharedPreferences sp=getSharedPreferences("mymem", MODE_PRIVATE);
    		    boolean isBackgroud=sp.getBoolean("isBackgroud", true);
    		    if(!isBackgroud)
    		    {
    			Intent intent=new Intent("updateDrawView");
    			intent.putExtra("free", free);
    			sendBroadcast(intent);
    		    }
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			int size = memoryDataHelper.getSize();
    		    if(size>=40) 
    		    {
    		    	memoryDataHelper.removeAll();
    		    }
    		}
    		
    	}
    	public void stopThread()
    	{
    		isRun=false;
    	}
    	public boolean getThreadState()
    	{
    		return this.isRun;
    	}
    }
    /**
     * 获取内存线程是否在运行
     * @author shiyl
     * 2014-9-16
     */
    public static boolean isDrawing()
    {
    	if(getMemoryThread!=null)
    		return getMemoryThread.getThreadState();
    	return false;
    }
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(getMemoryThread!=null)
    	{
    		getMemoryThread.stopThread();
    		getMemoryThread=null;
    	}
    	memoryDataHelper.removeAll();
    }
}
