package com.lenovo.infocollector.thread;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import android.annotation.SuppressLint;

import com.lenovo.infocollector.InfoCollectorService;

@SuppressLint("NewApi")
public class FpsThread extends InfoCollectorThread{

	public FpsThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="Fpsinfo";
		fileName=createNewExcel(dataName);
		// TODO Auto-generated constructor stub
	}
	String [] fpsHeader={"Date","FPS"};

	@Override
	protected void initHeader() {
		// TODO Auto-generated method stub
		for (int i = 0; i < fpsHeader.length; i++) {
			
		}
	}
	
	@Override
	protected boolean writedata() {
		// TODO Auto-generated method stub
		return false;
	}
}
