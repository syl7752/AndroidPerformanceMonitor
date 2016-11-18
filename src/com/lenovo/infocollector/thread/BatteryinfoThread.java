package com.lenovo.infocollector.thread;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import android.util.Log;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;

public class BatteryinfoThread extends InfoCollectorThread {
	public BatteryinfoThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="batteryinfo";
		fileName=createNewExcel(dataName);
		// TODO Auto-generated constructor stub
	}

	String batteryHeader[] = { "Date", "BatteryCurrentLevel" ,"Current Package"};



	@Override
	protected void initHeader() {
		// TODO Auto-generated method stub
		try {
		for (int i = 0; i < batteryHeader.length; i++) {
		
				outputStream.write((batteryHeader[i]+",").getBytes());
			
		}
		outputStream.write("\r\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	@Override
	protected boolean writedata() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 5; i++) {
			if(batteryLevel==-1)
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		if(batteryLevel==-1)
		{
			Log.e(Utils.DEBUG_TAG, "get batterylevel failed");
			return false;
		}
		String time=utils.getTime();
		content=time+","+batteryLevel;
		return true;
	}
	

}
