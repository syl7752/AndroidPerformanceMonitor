package com.lenovo.infocollector.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;

import android.content.Context;
import android.util.Log;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;

public class RaminfoThread extends InfoCollectorThread {
	Sheet freeSheet;
	String cachedPss,usedPss,totalPss,cachedFree,free,totalFree;
	FileOutputStream out;
	private String detailsFileName,detailsName;
	private File detailFile;
	public RaminfoThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="raminfo";
		fileName=createNewExcel(dataName);
		detailsName="ramdetail";
		detailsFileName=createNewExcel(detailsName);
		initFreeHeader();
		// TODO Auto-generated constructor stub
	}

	String[] raminfoHeader = { "Time", "Total RAM", "Free RAM", "Used RAM",
			"Lost RAM", "ZRAM"};
	String[] freeHeader = { "Time", "Cached PSS(KB)", "Used PSS","APP Memory", "Cached Free",
			"Free", "Free Memory","Firmware Memory","Graphics","GL","Current Package"};


	@Override
	protected void initHeader() {
		// TODO Auto-generated method stub
		try {
			for (int i = 0; i < raminfoHeader.length; i++) {
				outputStream.write((raminfoHeader[i]+",").getBytes());
			}
			outputStream.write("\r\n".getBytes());
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
    private void initFreeHeader()
    {
    	detailFile =new File(logDir+detailsFileName);
    
    	try {
    		if(!detailFile.exists())
    			detailFile.createNewFile();
			out=new FileOutputStream(detailFile, true);
			for (int i = 0; i < freeHeader.length; i++) {
				out.write((freeHeader[i]+",").getBytes());
			}
			out.write("\r\n".getBytes());
			out.flush();
			out.close();
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	protected boolean writedata() {
		// TODO Auto-generated method stub
//		HSSFRow freeRow=null;
		try {
			Process process = Runtime.getRuntime().exec("dumpsys meminfo");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			
			Process meminfoPro = Runtime.getRuntime().exec("cat /proc/meminfo");
			BufferedReader meminfoBr = new BufferedReader(new InputStreamReader(
					meminfoPro.getInputStream()));
			String memStr=null;
			int memFree=0;
			int cached=0;
			while((memStr=meminfoBr.readLine())!=null)
			{
				if (memStr.startsWith("MemFree")) {
					memFree=Integer.parseInt(getMemDataStr(memStr));
			    }
				if(memStr.startsWith("Cached"))
				{
					cached=Integer.parseInt(getMemDataStr(memStr));
				}
			}
			meminfoBr.close();
			
			detailFile =new File(logDir+detailsFileName);
			if(detailFile.exists()&&detailFile.length()>2048576)
			{
				detailsFileName=createNewExcel(detailsName);
				initFreeHeader();
			}
			out=new FileOutputStream(detailFile, true);
			String time = utils.getTime();
			StringBuffer ramBuffer=new StringBuffer();
			StringBuffer detailsBuffer=new StringBuffer();
			String [] details =new String [10];
			details[0]=time;
			ramBuffer.append(time+",");
			while ((readLine = br.readLine()) != null) {
				if (readLine.contains("Total RAM:") || readLine.contains("Free RAM:")
						|| readLine.contains("Used RAM:")
						|| readLine.contains("Lost RAM:") || readLine.contains("ZRAM:")) {
					for (int i = 1; i < raminfoHeader.length; i++) {
						if (readLine.contains(raminfoHeader[i])) {
							String[] strs = readLine.split(":");
							ramBuffer.append(strs[1]+",");
							Log.d(Utils.DEBUG_TAG, "ram str[1] ="
									+ strs[1] + " length = " + strs[1].length());
						}
					}
					if(readLine.contains("Free RAM:"))
					{
					   String str[]=readLine.split("\\+");
					   cachedFree=String.valueOf(cached);
					   free=String.valueOf(memFree);
					   String str1[]=str[0].split("\\(");
					   cachedPss=str1[1].split(" ")[0];
					   details[1]=cachedPss;
					   details[4]=cachedFree;
					   details[5]=free;
					}
					if(readLine.contains("Used RAM:"))
					{
					   String str[]=readLine.split("\\+");
					   usedPss=str[0].split("\\(")[1].split(" ")[0];
					   int appMem=Integer.parseInt(usedPss)+Integer.parseInt(cachedPss);
					   int freeMem=Integer.parseInt(cachedFree)+Integer.parseInt(free);
					   int firmware=utils.getTotalMem(Utils.TYPE_KB)-(freeMem+appMem);
					   details[2]=usedPss;
					   details[3]=String.valueOf(appMem);
					   details[6]=String.valueOf(freeMem);
					   details[7]=String.valueOf(firmware);
					}
					

				}
				if(readLine.contains("Graphics"))
				{
					String graphics[]= readLine.split("kB:");
					details[8]=graphics[0].trim();
				}
				if(readLine.contains("GL"))
				{
					String gl[]= readLine.split("kB:");
					details[9]=gl[0].trim();
				}
			}
			for (int i = 0; i < details.length; i++) {
				detailsBuffer.append(details[i]+",");
			}
			detailsBuffer.append(curPkgName);
			content=ramBuffer.toString();
			out.write((detailsBuffer.toString()+"\r\n").getBytes());
			out.flush();
			out.close();
			details=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
}
