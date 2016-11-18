package com.lenovo.inforeader;

import com.lenovo.info.util.Utils;



public class GetCpuStateThread extends Thread {
	  private String cpuName,line,cpuState;
	  private Utils utils;
      public GetCpuStateThread(String cpuName)
      {
    	  this.cpuName=cpuName;   
    	  utils=Utils.getInstance();
      }
      @Override
    public void run() {
    	// TODO Auto-generated method stub
    	super.run();
    	if(cpuName.equals("cpu"))
    		cpuState="使用率："+utils.getCpuUsagePer(cpuName)+" 最大频率："+utils.getCpuFreqStr(utils.getCpuMaxFreq())
    		+" 最小频率："+utils.getCpuFreqStr(utils.getCpuMinFreq())+"\r\n";
    	else
    	    cpuState="使用率："+utils.getCpuUsagePer(cpuName)+" 频率："+utils.getCpuFreqStr(utils.getCpuFreq(cpuName))+"\r\n";
    }
      public String getCpuState()
      {
    	  return this.cpuState;
      }
    
}
