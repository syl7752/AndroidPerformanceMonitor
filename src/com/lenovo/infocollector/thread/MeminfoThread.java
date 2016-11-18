package com.lenovo.infocollector.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import android.os.Handler;
import android.util.Log;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;

public class MeminfoThread extends InfoCollectorThread {
	public MeminfoThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="meminfo";
		fileName=createNewExcel(dataName);
		// TODO Auto-generated constructor stub
	}
	Process process = null;
	String [] meminfoHeader={"Time","Buffers","SwapCached","SwapTotal","SwapFree"};

	@Override
	protected void initHeader() {
		// TODO Auto-generated method stub
		try {
			for (int i = 0; i < meminfoHeader.length; i++) {
				outputStream.write((meminfoHeader[i]+",").getBytes());
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
			outputStream.write((time+",").getBytes());
			process = Runtime.getRuntime().exec("cat /proc/meminfo");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = "";
			String [] data =new String [4];
			while ((line = br.readLine()) != null) {
					if (line.startsWith("Buffers")) {
                       data[0]=getMemDataStr(line);
					}
					if (line.startsWith("SwapCached")) {
						  data[1]=getMemDataStr(line);
						}
					if (line.startsWith("SwapTotal")) {
						data[2]=getMemDataStr(line);
						}
					if (line.startsWith("SwapFree")) {
						data[3]=getMemDataStr(line);
						}
					
				}
			for (int i = 0; i < data.length; i++) {
				outputStream.write((data[i]+",").getBytes());
			}
			outputStream.write("\r\n".getBytes());
            getProcessMaps();
			getSwaps();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	private void getProcessMaps()
	{
		  ArrayList<MyProcess> processList=new ArrayList<MyProcess>();
		  try {
			Process process1=Runtime.getRuntime().exec("ps");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process1.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				if(line.contains("PID"))
					continue;
				MyProcess myProcess=new MyProcess();
				String [] strs=line.split(" ");
				for (int i = 1; i < strs.length; i++) {
					//Log.d(Utils.DEBUG_TAG, "strs "+i+" "+strs[i]+"in");
					if(!strs[i].equals(""))
					{
						myProcess.pid=Integer.parseInt(strs[i]);
						break;
					}
				}
				myProcess.processName=strs[strs.length-1];
				processList.add(myProcess);
			}
			
//			File mapsFile=utils.createFile(logDir+File.separatorChar+"maps.txt");
//			Writer output = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream(mapsFile, true))); // GBK
			
			for (int i = 0; i < processList.size(); i++) {
				MyProcess myPro=processList.get(i);
				Process process2=Runtime.getRuntime().exec("cat /proc/"+myPro.pid+"/maps");
				BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(
						process2.getInputStream()));
				String readLine=bufferedReader.readLine();
				if(readLine==null||readLine.equals(""))
					continue;
				String processFileDir=logDir+"Maps"+File.separatorChar+myPro.pid;
				utils.createDir(processFileDir);
				File mapFile=utils.createFile(processFileDir+File.separatorChar+"maps.txt");
				//File mapFile=utils.createFile(logDir+File.separatorChar+myPro.pid+".txt");
				Writer out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(mapFile, true))); // GBK
				out.write("ProcessName : "+myPro.processName+" Time : "+utils.getTime()+"\r\n");
				//output.write("ProcessName : "+myPro.processName+" Pid : "+myPro.pid+"\r\n");
				while((readLine=bufferedReader.readLine())!=null)
				{
					out.write(readLine+"\r\n");
				}
				out.close();
			}
			//output.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getSwaps()
	{
		Process pro;
		try {
			pro = Runtime.getRuntime().exec("cat /proc/swaps");
		
		BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(
				pro.getInputStream()));
		String readLine=bufferedReader.readLine();
		if(readLine==null||readLine.equals(""))
			return;
		String processFileDir=logDir+"Swaps";
		utils.createDir(processFileDir);
		File swapFile=utils.createFile(processFileDir+File.separatorChar+"swaps.txt");
		//File swapFile=utils.createFile(logDir+File.separatorChar+"swaps.txt");
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(swapFile, true))); // GBK
		out.write(" Time : "+utils.getTime()+"\r\n");
		while((readLine=bufferedReader.readLine())!=null)
		{
			out.write(readLine+"\r\n");
		}
		out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class MyProcess
	{
		String processName;
		int pid;
	}
}
