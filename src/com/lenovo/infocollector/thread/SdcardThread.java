package com.lenovo.infocollector.thread;

import java.io.File;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import android.annotation.SuppressLint;
import android.os.Environment;

import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorService;

@SuppressLint("NewApi")
public class SdcardThread extends InfoCollectorThread{

	public SdcardThread(int interval,InfoCollectorService service) {
		super(interval, service);
		dataName="sdcardinfo";
		fileName=createNewExcel(dataName);
		// TODO Auto-generated constructor stub
	}
	String [] sdcardHeader={"Date","Sdcard Used Size(MB)","Sdcard Total Size(MB)","Current Package"};
	public String getSdcardSize()
	{
		    if(Environment.getExternalStorageState().equals(  
		            Environment.MEDIA_MOUNTED))
		    {
		    // 取得sdcard文件路径   
		    File pathFile = Environment.getExternalStorageDirectory();   
		     
		    android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());   
		      
		    long nTotalBlocks = statfs.getBlockCount();   
		      
		    long nBlocSize = statfs.getBlockSize();   
		     
		    long nAvailaBlock = statfs.getAvailableBlocks();   
		     
		    long nFreeBlock = statfs.getFreeBlocks();   
		     
		    // 计算SDCard 总容量大小MB   
		    long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;   
		     
		    // 计算 SDCard 剩余大小MB   
		    long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024; 
		    
		    String str=String.valueOf(nSDFreeSize)+" "+String.valueOf(nSDTotalSize);
		    return str;
		    }
		    else
		    {
		    	System.out.println("sdcard unmounted");
		    	return null;
		    }
		   
		   
	}
	@Override
	protected void initHeader() {
		// TODO Auto-generated method stub
		try {
			for (int i = 0; i < sdcardHeader.length; i++) {
				outputStream.write((sdcardHeader[i]+",").getBytes());
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
		String sdcardInfo[]=getSdcardSize().split(" ");
		if(sdcardInfo==null)
			return false;
		String time=utils.getTime();
		content=time+","+sdcardInfo[0].trim()+","+sdcardInfo[1];
		return true;
	}
}
