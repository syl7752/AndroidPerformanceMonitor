package com.lenovo.infocollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.thread.BatteryinfoThread;
import com.lenovo.infocollector.thread.CpuinfoThread;
import com.lenovo.infocollector.thread.InfoCollectorThread;
import com.lenovo.infocollector.thread.MeminfoThread;
import com.lenovo.infocollector.thread.PssinfoThread;
import com.lenovo.infocollector.thread.RaminfoThread;
import com.lenovo.infocollector.thread.SdcardThread;
import com.lenovo.infocollector.thread.TemperatureThread;
import com.lenovo.infomonitor.MainActivity;
import com.lenovo.infomonitor.R;

/**
 * 
 * @author shiyl 2014-7-4
 */
public class InfoCollectorService extends Service{
	InfoCollectorThread pssinfoThread;
	InfoCollectorThread cpuinfoThread;
	InfoCollectorThread sdcardThread;
	InfoCollectorThread batteryinfoThread;
	InfoCollectorThread raminfoThread;
	InfoCollectorThread temThread;
	InfoCollectorThread meminfoThread;
	UpdateStatusThread updateStatusThread;
	HashMap<String, String> map = new HashMap<String, String>();
	InfoCollectorService service;
	private static String snrFilePath;
	private static String logPath;
	Utils utils;

	@SuppressLint("NewApi")
	private void init() {
		Log.d(Utils.DEBUG_TAG, "LogCollectorService.init()");
		Notification notification = new Notification();
		startForeground(1, notification);
		utils = Utils.getInstance();

		service = this;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					openFileInput("logcollector.cfg")));
			String line = "";
			String temp = "";
			while ((line = br.readLine()) != null) {
				temp = temp.concat(line);
			}
			String strtemp[] = temp.split(",");
			for (String cfg : strtemp) {
				String keyValue[] = cfg.split(" ");
				map.put(keyValue[0], keyValue[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void startDump() {
		updateStatusThread=new UpdateStatusThread(handler);
		updateStatusThread.start();
		// Dumpsys线程
		if (map.get("PSSINFO-ENABLE").equals("true")) {
			pssinfoThread = new PssinfoThread(Integer.parseInt(map
					.get("INTERVAL")), service);
			pssinfoThread.start();
		}
		// Cpuinfo线程
		if (map.get("CPUINFO-ENABLE").equals("true")) {
			cpuinfoThread = new CpuinfoThread(Integer.parseInt(map
					.get("INTERVAL")), service);
			cpuinfoThread.start();
		}
		// Sdcard容量线程
		if (map.get("SDCARD-ENABLE").equals("true")) {
			sdcardThread = new SdcardThread(Integer.parseInt(map
					.get("INTERVAL")), service);
			sdcardThread.start();
		}
		if (map.get("BATTERY-ENABLE").equals("true")) {
			batteryinfoThread = new BatteryinfoThread(Integer.parseInt(map
					.get("INTERVAL")), service);
			batteryinfoThread.start();
		}
		if (map.get("RAMINFO-ENABLE").equals("true")) {
			raminfoThread = new RaminfoThread(Integer.parseInt(map
					.get("INTERVAL")), service);
			raminfoThread.start();
		}
		if (map.get("TEMPERATURE-ENABLE").equals("true")) {
			temThread = new TemperatureThread(Integer.parseInt(map
					.get("INTERVAL")), service);
			temThread.start();
		}
		if (map.get("MEMINFO-ENABLE").equals("true")) {
			meminfoThread = new MeminfoThread(Integer.parseInt(map
					.get("INTERVAL")), service);
			meminfoThread.start();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	// @Override
	// public void onStart(Intent intent, int startId) {
	// super.onStart(intent, startId);
	// }

	@Override
	public void onDestroy() {
		Log.d(Utils.DEBUG_TAG, "LogCollectorService.onDestroy()");
		if (pssinfoThread != null) {
			pssinfoThread.stopThread();
		}
		if (cpuinfoThread != null) {
			cpuinfoThread.stopThread();
		}
		if (sdcardThread != null) {
			sdcardThread.stopThread();
		}
		if (batteryinfoThread != null) {
			batteryinfoThread.stopThread();
		}
		if (raminfoThread != null) {
			raminfoThread.stopThread();
		}
		if (temThread != null) {
			temThread.stopThread();
		}
		if (meminfoThread != null) {
			meminfoThread.stopThread();
		}
		if(updateStatusThread!=null)
			updateStatusThread.stopThread();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		init();
		startDump();
		return START_STICKY;
	}
	 class UpdateStatusThread extends Thread
	    {
	    	private boolean isRunning=false;
	    	private int count=1;

	    	private Handler mHandler;
	    	public UpdateStatusThread(Handler handler)
	    	{

	    		isRunning=true;
	    		this.mHandler=handler;
	    	}
	    	@Override
	    	public void run() {
	    		// TODO Auto-generated method stub
	    		super.run();
	    		while (isRunning) {
	    			notifyUpdate(count);
	    			count++;
	    			try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
	    	}
	    	public void notifyUpdate(int time)
	    	{
	    		Message msg=new Message();
	    		msg.what=0;
	    		msg.arg1=time;
	    		mHandler.sendMessage(msg);
	    	}
	    	public void stopThread()
	    	{
	    		isRunning=false;
	    		utils.unregisterObserver();
	    		Utils.log("stop status thread");
	    	}
	    }
	    Handler handler=new Handler()
	    {
	    	public void handleMessage(android.os.Message msg) {
	    		switch (msg.what) {
				case 0:
					utils.log("notify update");
					utils.notifyUpdate(msg.arg1);
					break;

				default:
					break;
				}
	    	};
	    };
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
//		@Override
//		public void onUpdate() {
//			// TODO Auto-generated method stub
//			if(errorController!=null)
//			{
//				NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//				Notification n = new Notification(R.drawable.appicon, "InfoMonitor",
//						System.currentTimeMillis());
//				n.flags = Notification.FLAG_AUTO_CANCEL;
//				Intent i = new Intent(this, MainActivity.class);
//				PendingIntent contentIntent = PendingIntent.getActivity(this,
//						R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);
//				n.setLatestEventInfo(this, "InfoMonitor", "已发现"+errorController.getErrorNum()+"个错误",
//						contentIntent);
//				nm.notify(R.string.app_name, n);
//			}
//		}
}
