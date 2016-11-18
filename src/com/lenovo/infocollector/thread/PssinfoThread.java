package com.lenovo.infocollector.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import android.os.Handler;
import android.util.Log;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;

public class PssinfoThread extends InfoCollectorThread {
	public PssinfoThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="pssinfo";
		fileName=createNewExcel(dataName);
		// TODO Auto-generated constructor stub
	}
	private String processList[];
	Process process = null;
	String [] pssinfoHeader={"Time","PSS","Process","Pid"};
	Handler handler;
	private boolean isPss=false;

	public void setProcessList(String processList[]) {
		this.processList = processList;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
   
	@Override
	protected void initHeader() {
		// TODO Auto-generated method stub
		try {
			for (int i = 0; i < pssinfoHeader.length; i++) {
				outputStream.write((pssinfoHeader[i]+",").getBytes());
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
		String time=utils.getTime();
		try {
			process = Runtime.getRuntime().exec("dumpsys meminfo | grep ''");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				Log.e("yangrh2", line);
				if (isPss) {
					if (line.contains("kB:")) {
						//row=sheet.createRow(sheet.getLastRowNum()+1);
						String[] strs = line.split("kB:");
						String pssSize=strs[0].trim();
						String [] processInfo=strs[1].split(" \\(");
						String processName=processInfo[0].trim();
						String pid;
						if(processInfo[1].contains("/"))
							//pid=processInfo[1].substring(4, processInfo[1].lastIndexOf("/"));	
						    pid=processInfo[1].substring(4, processInfo[1].lastIndexOf("/"));	//android6.0
						else
							pid=processInfo[1].substring(4, processInfo[1].lastIndexOf(")"));
						content=time+","+Integer.parseInt(pssSize)+","+processName+","+Integer.parseInt(pid.trim())+"\r\n";
                        outputStream.write(content.getBytes());
						
					}
				}
				if (line.contains("Total PSS by process")) {
					isPss = true;
				}
				if (line.equals("")) {
					isPss = false;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
