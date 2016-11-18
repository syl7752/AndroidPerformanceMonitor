package com.lenovo.infocollector.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;


public class CpuinfoThread extends InfoCollectorThread{

	Process process;
	int cpuPercent;
	private String time;
	BufferedReader br;
	int cpuNum;
	public CpuinfoThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="cpuinfo";
		fileName=createNewExcel(dataName);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void initHeader() {
		// TODO Auto-generated method stub
		try {

		outputStream.write("Date,CpuTotalUsed(%),".getBytes());
		cpuNum=utils.getNumCores();
		for (int i = 0; i < cpuNum; i++) {
			outputStream.write(("cpu"+i+" freq,").getBytes());
		}
		outputStream.write("Current Package".getBytes());
		outputStream.write("\r\n".getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected boolean writedata() {
		// TODO Auto-generated method stub
//		try {
//			process = Runtime.getRuntime().exec("top -n 1 -m 1");
//			br=new BufferedReader(new InputStreamReader (process.getInputStream ()));
//	    	while((readLine=br.readLine())!=null)
//	    	{
//	    		if(readLine.trim().length()<1){
//	    			continue;
//	    		}else if(readLine.contains("User")&&readLine.contains("System")){
//	    			CPUusr = readLine.split("%");
//			    	CPUusage = CPUusr[0].split("User ");
//			    	SYSusage = CPUusr[1].split("System ");
//			    	cpuPercent = Integer.valueOf(CPUusage[1]) + Integer.valueOf(SYSusage[1]);
//			    	time=utils.getTime();
//			    	content=time+","+CPUusage[1]+","+SYSusage[1]+","+cpuPercent;
//			    	break;
//	    		}
//	    	}
//	    	br.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Integer cpuUsage=(int)(utils.getCpuUsagePerNum("cpu")*100);
		time=utils.getTime();
		String str=time+","+cpuUsage+",";
		for (int i = 0; i < cpuNum; i++) {
			str+=utils.getCpuFreq("cpu"+i)+",";
		}
    	content=str;
		return true;
	}
    
}
