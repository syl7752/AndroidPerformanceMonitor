package com.lenovo.inforeader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.R;

public class MeminfoFragment extends FragmentBase{
	String line,free,cached,memTotal,ramSize,ramFreeSize,ramUsedSize;
	FileInputStream inputStream;
	HSSFWorkbook hw;
	HSSFSheet sheet;
	TextView systemInfoTv;
	String []csvHeader={"鏃堕棿","椤圭洰鍚嶇О","绯荤粺骞冲彴","RAM绌洪棿澶у皬(MB)","android鐗堟湰","鍒嗚鲸鐜�","鍙敤鍐呭瓨(MB)","宸茬敤鍐呭瓨(MB)","鍙敤鍐呭瓨鍗犳瘮"};
    List<String> dataList;
	public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	View view =inflater.inflate(R.layout.meminfo, container, false);
    	getInfoBtn=(Button) view.findViewById(R.id.get_meminfo);
    	textView=(TextView) view.findViewById(R.id.meminfoTv);
    	systemInfoTv=(TextView) view.findViewById(R.id.systeminfo);
    	getInfoBtn.setOnClickListener(this);
    	Log.d(TAG, "mem onCreateView");
    	StringBuffer stringBuffer=new StringBuffer();
    	stringBuffer.append("Platform : "+utils.getPlatform()+"\r\n");
    	stringBuffer.append("CPU ："+utils.getBoard()+"\r\n");
    	stringBuffer.append("android version : "+utils.getVersionRelease()+"\r\n");
    	stringBuffer.append("Resolution : "+utils.getResolution(getActivity())+"\r\n");
	    systemInfoTv.setText(stringBuffer.toString());
	    systemInfoTv.setTextColor(Color.RED);
    	return view;
    }
	@Override
	protected void getInfo() {
		// TODO Auto-generated method stub
		Process p;
    	createLogFile("meminfo.txt");
		StringBuffer sb=new StringBuffer();
		
		dataList=new ArrayList<String>();
		try {
			
			File memFile=new File(getActivity().getFilesDir(),"memdata.csv");
			if(!memFile.exists())
				memFile.createNewFile();
			    if(memFile.length()==0)
		           initExcel(null);
				else
				{
				   System.out.println(memFile.length());
				   inputStream=new FileInputStream(memFile);
			       initExcel(inputStream);
				}
			
			dataList.add(utils.getTime());
	    	dataList.add(utils.getPlatform());

	    	dataList.add(utils.getBoard());
	   
			p = Runtime.getRuntime().exec("cat /proc/meminfo");
			BufferedReader br=new BufferedReader(new InputStreamReader (p.getInputStream ()));
	    	while((line=br.readLine())!=null)
	    	{
	    		if(line.trim().length()<1){
	    			continue;
	    		}else if(line.contains("MemFree"))
	    		{
	    			System.out.println(line);
	    			free=line.split(":")[1].split("kB")[0].trim();
	    			System.out.println(free);
	    		}
	    		else if (line.contains("Cached"))
	    		{
	    			System.out.println(line);
	    			cached=line.split(":")[1].split("kB")[0].trim();
	    			System.out.println(cached);
	    			break;
	    		}else if (line.contains("MemTotal"))
	    		{
	    			System.out.println(line);
	    			memTotal=line.split(":")[1].split("kB")[0].trim();
	    			System.out.println(memTotal);
	    		}
            }
	    	double memFree=Double.parseDouble(free)+Double.parseDouble(cached);    
	    	memFree=memFree/1024;
	    	double total=Double.parseDouble(memTotal)/1024;
	    	Log.d(TAG, "free "+memFree+" total "+total);
	    	ramFreeSize=utils.parseDouble(memFree);
	    	ramSize=utils.parseDouble(total);
	    	double memUsed=total-memFree;
	    	ramUsedSize=utils.parseDouble(memUsed);
	    	
	    	 dataList.add(ramSize);

	    	 dataList.add(utils.getVersionRelease());

	    	 dataList.add(utils.getResolution(getActivity()));
	    	 if(total>1024)
	    	 {
			    	sb.append(utils.getTime()+"----"+"RAM Size : "+utils.parseDouble(total/1024.0)+"GB\r\n");
	    	 }
			    else
			    	sb.append(utils.getTime()+"----"+"RAM Size : "+ramSize+"MB\r\n");
	    	 if(memFree>1024)
	    		 sb.append(utils.getTime()+"----"+"Free RAM : "+utils.parseDouble(memFree/1024.0)+"GB\r\n");
			    else
			    	sb.append(utils.getTime()+"----"+"Free RAM : "+ramFreeSize+"MB\r\n");
	    	 dataList.add(ramFreeSize);
	    	 if(memUsed>1024)
	    		 sb.append(utils.getTime()+"----"+"Used RAM : "+utils.parseDouble(memUsed/1024.0)+"GB\r\n");
			    else
			    	sb.append(utils.getTime()+"----"+"Used RAM : "+ramUsedSize+"MB\r\n");
	    	 dataList.add(ramUsedSize);
	    	 String memFreePer= utils.parseDoubletoPer(memFree/total);    
	    	 sb.append(utils.getTime()+"----"+"Average Free : "+memFreePer+"\r\n\r\n");
	    	 dataList.add(memFreePer);
	    	info=sb.toString();
	    	FileOutputStream out=new FileOutputStream(memFile);
	    	writeExcel();
	    	if(inputStream!=null)
		    	inputStream.close();
	    	hw.write(out);
	    	out.flush();
	    	out.close();
	    	
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	}
	
	private void initExcel(FileInputStream in)
	{
		try {
			if(in==null)
			{
				hw=new HSSFWorkbook();
				sheet=hw.createSheet("memdata");
				HSSFRow headerRow=(HSSFRow) sheet.createRow(0);
				for (int i = 0; i < csvHeader.length; i++) {
					HSSFCell cell=headerRow.createCell(i);
					cell.setCellValue(csvHeader[i]);
					sheet.setColumnWidth(i, 20*256);
				}
			}
			else 
			{
				hw = new HSSFWorkbook(in);
			    sheet=hw.getSheet("memdata");
			    System.out.println("row "+sheet.getRow(sheet.getLastRowNum()).getCell(0).getStringCellValue());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void writeExcel()
	{
		HSSFRow row =sheet.createRow(sheet.getLastRowNum()+1);
		for (int i = 0; i < dataList.size(); i++) {
			HSSFCell cell=row.createCell(i);
			cell.setCellValue(dataList.get(i));
		}
		
	}
}