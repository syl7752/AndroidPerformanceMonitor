package com.lenovo.infocollector.thread;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import android.annotation.SuppressLint;
import android.util.Log;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;

@SuppressLint("NewApi")
public class TemperatureThread extends InfoCollectorThread{

	public TemperatureThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="teminfo";
		fileName=createNewExcel(dataName);
	}
	String [] temHeader={"Date","Temperature","Current Package"};
	@Override
	protected void initHeader() {
		try {
			for (int i = 0; i < temHeader.length; i++) {
				outputStream.write((temHeader[i]+",").getBytes());
			}
			outputStream.write("\r\n".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	@Override
	protected boolean writedata() {
		for (int i = 0; i < 5; i++) {
			if(temperature==-1)
			{
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
		if(temperature==-1)
		{
			Log.d(Utils.DEBUG_TAG, "get temperature timeout");
			return false;
		}
		String time=utils.getTime();
		content=time+","+(temperature/10+"."+temperature%10);
		return true;
	}
}
