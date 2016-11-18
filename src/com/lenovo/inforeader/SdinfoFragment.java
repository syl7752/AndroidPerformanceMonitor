package com.lenovo.inforeader;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.R;

public class SdinfoFragment extends FragmentBase{
  public void onCreate(Bundle savedInstanceState) {
  	// TODO Auto-generated method stub
  	super.onCreate(savedInstanceState);
  }
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
  		Bundle savedInstanceState) {
  	// TODO Auto-generated method stub
  	View view =inflater.inflate(R.layout.sdinfo, container, false);
  	getInfoBtn=(Button) view.findViewById(R.id.get_sdinfo);
  	textView=(TextView) view.findViewById(R.id.sdinfoTv);
  	getInfoBtn.setOnClickListener(this);
  	Log.d(TAG, "SD onCreateView");
  	sb.append("SD容量: \r\n");

  	return view;
  }
	@Override
	protected void getInfo() {
		// TODO Auto-generated method stub
	  	createLogFile("sdinfo.txt");
		info=utils.getTime()+"----"+getSdcardSize()+"\r\n";
	}
	public String getSdcardSize()
	{
		    if(Environment.getExternalStorageState().equals(  
		            Environment.MEDIA_MOUNTED))
		    {
		    // 鍙栧緱sdcard鏂囦欢璺緞   
		    File pathFile = Environment.getExternalStorageDirectory();   
		     
		    android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());   
		      
		    long nTotalBlocks = statfs.getBlockCount();   
		      
		    long nBlocSize = statfs.getBlockSize();   
		     
		    long nAvailaBlock = statfs.getAvailableBlocks();   
		     
		    long nFreeBlock = statfs.getFreeBlocks();   
		     
		    // 璁＄畻SDCard 鎬诲閲忓ぇ灏廙B   
		    long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;   
		     
		    // 璁＄畻 SDCard 鍓╀綑澶у皬MB   
		    long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024; 
		    String str="";
		    if(nSDFreeSize>1024)
		    	str="剩余："+nSDFreeSize/1024+"."+nSDFreeSize%1024/10+"GB";
		    else
		    	str="剩余："+nSDFreeSize+"MB";
		    if(nSDTotalSize>1024)
		    	str+="总容量："+nSDTotalSize/1024+"."+nSDTotalSize%1024/10+"GB";
		    else
		    	str+="总容量："+nSDTotalSize+"MB";
		    return str;
		    }
		    else
		    {
		    	System.out.println("sdcard unmounted");
		    	return null;
		    }
		   
		   
	}
}
