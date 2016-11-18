package com.lenovo.infomonitor.infodrawing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.MeasureSpec;

import com.lenovo.info.util.Utils;



public class GetCpuUsageThread extends Thread {
	  private String cpuUsage;
	  private boolean isRun=false;
	  private Handler handler;
	  private Utils utils;
      public GetCpuUsageThread(Handler handler)
      {
    	  this.handler=handler;
    	  isRun=true;
    	  utils=Utils.getInstance();
      }
      @Override
    public void run() {
    	// TODO Auto-generated method stub
    	super.run();
    	while(isRun)
    	{
    		cpuUsage=utils.getCpuUsagePer("cpu");
    		int cpuUse=Integer.parseInt(cpuUsage.substring(0, cpuUsage.lastIndexOf(".")));
    		Message msg=new Message();
    		Bundle bundle=new Bundle();
    		bundle.putInt("cpu", cpuUse);
    		msg.setData(bundle);
    		handler.sendMessage(msg);
    	}
    }
    public void stopThread()
    {
    	isRun=false;
    }
    
}
