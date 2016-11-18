package com.lenovo.infocollector.thread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;


/**
 * Logc
 * @author shiyl
 * 2014-8-14
 */
public abstract class InfoCollectorThread extends Thread {
	protected boolean isRunning;
	private int interval = 5000;
	protected FileOutputStream outputStream= null;
	protected int batteryLevel=-1,temperature=-1;
	String dataName;
	String fileName="";
	protected String content;
	protected String readLine=null;
	protected InfoCollectorService service;
	private ActivityManager activityManager;
	protected String curPkgName;
	private File logFile;
	protected static String logDir="";
	protected Utils utils;
	public InfoCollectorThread(int interval,InfoCollectorService service)
	{
		isRunning=true;
		this.interval=interval;
		this.service=service;
		utils=Utils.getInstance();
		activityManager = (ActivityManager)service.getSystemService(Context.ACTIVITY_SERVICE);  
		//logDir=service.getFilesDir().getAbsolutePath()+File.separatorChar+"InfoMonitor"+File.separatorChar;
		logDir=//Environment.getExternalStorageDirectory().getAbsolutePath()
				"/sdcard"+File.separatorChar+"InfoMonitor"+File.separatorChar;
		utils.createDir(logDir);
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		logFile =new File(logDir+fileName);
		if(!logFile.exists())
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		writeHeader();
		while(isRunning)
		{
				try {
					logFile =new File(logDir+fileName);
					//if(Utils.DEBUG)
					//Log.d(Utils.DEBUG_TAG, service.getFilesDir().getAbsolutePath()+fileName+" "+file.length());
					if(logFile.exists()&&logFile.length()>2048576)
					{
						fileName=createNewExcel(dataName);
						logFile =new File(logDir+fileName);
						writeHeader();
					}
                    
					outputStream = new FileOutputStream(logFile, true);
					IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
					service.registerReceiver(batteryReceiver,
							intentFilter);
					ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;  
					curPkgName = cn.getPackageName(); 
					if(writedata())		
					outputStream.write((content+","+curPkgName+"\r\n").getBytes());
//					Log.d(TAG, "current packagename "+packageName);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					try {
						//hw.write(outputStream);
						outputStream.flush();
						outputStream.close();
						sleep(interval*1000);
						//readExcel();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				
		}
	}
	public void stopThread()
	{
		isRunning=false;
		
	}
	protected void initExcel()
	{
//		Log.d(TAG, "init LogCollectorThread");
//		hw=new HSSFWorkbook();
//		hw.createSheet(dataName);
//		sheet=hw.getSheet(dataName);
//		if(sheet.getPhysicalNumberOfRows()==0)
//		initHeader();
		
	}
//	protected void readExcel()
//	{
//		try {
//			inputStream= service.openFileInput(fileName);
//			hw=new HSSFWorkbook(inputStream);
//			sheet=hw.getSheetAt(0);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	private void writeHeader()
	{
		try {
			
			outputStream =  new FileOutputStream(logFile, true);
		    initHeader();  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				outputStream.flush();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		}
		
	}
	protected abstract void initHeader();
	//protected abstract HSSFRow writeExcel(HSSFRow row);
	protected abstract boolean writedata();
	 protected String createNewExcel(String fileName)
	    {
		        String fileTime=utils.getCurTime();
	    		File file =new File(logDir+fileName+"_"+fileTime+".csv");
	    		if(!file.exists())
	    		{
						Log.d(Utils.DEBUG_TAG,"create new file "+fileName+fileTime+".csv");
						try {
							file.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return 	fileName+"_"+fileTime+".csv";
	    		}
			return null;
//		        for (int i = 0; i < 1000; i++) {
//					File file =new File(logDir+fileName+i+".csv");
//					if(!file.exists())
//					{
//						try {
//							file.createNewFile();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						return fileName+i+".csv";
//					}
//				}
//				return null;
	    }
	 BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				if (Intent.ACTION_BATTERY_CHANGED.equals(arg1.getAction())) {
					// 获取当前电量
					batteryLevel = arg1.getIntExtra("level", 0);
                    temperature = arg1.getIntExtra("temperature", 0);
					service.unregisterReceiver(batteryReceiver);
				}
			}
		};
		protected String getMemDataStr(String readLine)
		{
			String [] strs=readLine.split(" ");
			Log.d(Utils.DEBUG_TAG, "strs = "+strs[strs.length-2]+"  "+strs[0]);
			return strs[strs.length-2];
		}
}
