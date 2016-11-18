package com.lenovo.info.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utils {
	  public static final String SDPATH=Environment.getExternalStorageDirectory().getAbsolutePath();
	  public static final String LOGPATH=SDPATH+"/InfoReader";
	  public static final String DEBUG_TAG="InfoMonitor";
	  public static final boolean DEBUG=true;
	  public static final int TYPE_KB=0;
	  public static final int TYPE_MB=1;
	  public static final int TYPE_GB=2;
	  private static String time="";
	  private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
	  private static Utils utils;
	  private ServiceObserver observer;
	  public static Utils getInstance()
	  {
		  if(utils==null)
			  utils=new Utils();
		  return utils;
	  }
      public String getTime()
      {
    	 time =simpleDateFormat.format(new Date());
  		return time;
      }
      public String getCurTime()
      {
    	 SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
    	 String curTime =simpleDateFormat.format(new Date());
  		return curTime;
      }
      public void execSuperCmd(Context context,String cmd)
      {
    	    File  filePath = new File(context.getCacheDir().getParent(), "bash.sh");
    		BufferedWriter bw;
    		try {
    			bw = new BufferedWriter(new FileWriter(filePath));
    			bw.write(cmd);
    			bw.flush();
    			bw.close();
    			Log.i(DEBUG_TAG, "bash.sh path: " + filePath.getAbsolutePath());
    			LocalSocketAddress localHostAddress = new LocalSocketAddress("nac_server");
    			LocalSocket localSocket = new LocalSocket();
    			localSocket.connect(localHostAddress);
    			//localSocket.getOutputStream();
    			PrintWriter socketWriter = new PrintWriter(localSocket.getOutputStream(), true);
    			//din = new DataInputStream(localSocket.getInputStream());
    		
    			socketWriter.write(filePath.getAbsolutePath());
    			socketWriter.flush();
    			socketWriter.close();
    			localSocket.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
      }
      public String getProp(String prop)
      {
    	  String value = "";
    	  try {
			Process p = Runtime.getRuntime().exec("getprop "+prop);
			BufferedReader br=new BufferedReader(new InputStreamReader (p.getInputStream ()));
			value=br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value.trim();
      }
      public String parseDoubletoPer(double num)
      {
    	  DecimalFormat df = new DecimalFormat("0.00%");     
   	      String numStr= df.format(num);
   	      return numStr;
      }
      public String parseDouble(double num)
      {
    	  DecimalFormat df = new DecimalFormat("#.00");     
   	      String numStr= df.format(num);
   	      return numStr;
      }
      public String getCpuFreq(String cpuName)
    	{
    		String cpuFreq="";
    		BufferedReader reader;
    		File file =new File("/sys/devices/system/cpu/"+cpuName+"/cpufreq/scaling_cur_freq");
    		if(!file.exists())
    			return "-";
    		try {
    			reader = new BufferedReader(new FileReader(file));
    			cpuFreq=reader.readLine();
    			System.out.println("cpuFreq "+cpuFreq);
    	    	reader.close();
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (NumberFormatException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return cpuFreq;
        	
    	}
    	public String getCpuFreqStr(String cpuFreq)
    	{
    		if(cpuFreq.equals("-"))
    			return cpuFreq;
    		long freq=Integer.parseInt(cpuFreq);
    		if(freq>1000)
    		{
    			
    			if(freq>1000000)
    			{
    				double f=(double)freq;
    				return parseDouble(f/1000000)+"GHz";
    			}
    			else
    				return String.valueOf(freq/1000)+"MHz";
    		}
    		return freq+"KHz";
    		
    	}
    	public int[] getCpuUsage(String cpuName)
  	{
  		int total=0,idle=0;
  		String line="";
  		BufferedReader reader;
  		try {
  			reader = new BufferedReader(new FileReader(new File("/proc/stat")));
  			while((line=reader.readLine())!=null)
  	    	{	   
  	    			String []cpus=line.split(" ");
  	    			if(cpus[0].trim().equals(cpuName))
  	    			{
  	                for (int i = 2; i < cpus.length; i++) {
  	                	//if(DEBUG)
//  	                	Log.d(DEBUG_TAG,"cpus "+i+"   "+cpus[i].trim());
  						total+=Integer.parseInt(cpus[i].trim());
  					}
  	                if(cpuName.equals("cpu"))
  	                  idle=Integer.parseInt(cpus[5].trim());
  	                else
  	                  idle=Integer.parseInt(cpus[4].trim());
//  	                if(DEBUG)
//  	                Log.d(DEBUG_TAG,"total "+total+" idle "+idle);
  	                break;
  	    			}
  	    	}
  	    	reader.close();
  		} catch (FileNotFoundException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (NumberFormatException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		int times[] = {0,0};
  		times[0]=total;
  		times[1]=idle;
  		return times;
      	
  	}
  	public String getCpuUsagePer(String cpuName)
  	{
  		double per=getCpuUsagePerNum(cpuName);
  		if(per==0)
  			return "offline";
  		return parseDoubletoPer(per);
   	}
	public double getCpuUsagePerNum(String cpuName)
  	{
  		int [] time1=getCpuUsage(cpuName);
  		try {
  			Thread.sleep(1000);
  		} catch (InterruptedException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		int [] time2=getCpuUsage(cpuName);
  		int totaltime=time2[0]-time1[0];
  		int idle=time2[1]-time1[1];
  		if(totaltime==0)
  			return 0;
  		Log.d(DEBUG_TAG,"totaltime "+totaltime+" idle "+idle);
  		double per=1-(double)idle/(totaltime*1.0);
  		return per;
   	}
  	public String getCpuMaxFreq()
  	{
  		String cpuFreq="";
		BufferedReader reader;
		File file =new File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
		if(!file.exists())
			return "-";
		try {
			reader = new BufferedReader(new FileReader(file));
			cpuFreq=reader.readLine();
	    	reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cpuFreq;
  	}
  	public int getNumCores() {
  	    //Private Class to display only CPU devices in the directory listing
  	    class CpuFilter implements FileFilter {
  	        @Override
  	        public boolean accept(File pathname) {
  	            //Check if filename is "cpu", followed by a single digit number
  	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
  	                return true;
  	            }
  	            return false;
  	        }      
  	    }

  	    try {
  	        //Get directory containing CPU info
  	        File dir = new File("/sys/devices/system/cpu/");
  	        //Filter to only list the devices we care about
  	        File[] files = dir.listFiles(new CpuFilter());
  	        Log.d(DEBUG_TAG, "CPU Count: "+files.length);
  	        //Return the number of cores (virtual CPU devices)
  	        return files.length;
  	    } catch(Exception e) {
  	        //Print exception
  	        Log.d(DEBUG_TAG, "CPU Count: Failed.");
  	        e.printStackTrace();
  	        //Default to return 1 core
  	        return 1;
  	    }
  	}
  	public String getCpuMinFreq()
  	{
  		String cpuFreq="";
		BufferedReader reader;
		File file =new File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
		if(!file.exists())
			return "-";
		try {
			reader = new BufferedReader(new FileReader(file));
			cpuFreq=reader.readLine();
	    	reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cpuFreq;
  	}
  	public String getResolution(Activity activity)
	{
		    DisplayMetrics dm = new DisplayMetrics();  
		    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	        int width=dm.widthPixels;   
	        int height=dm.heightPixels;
	        String resolution=height+"*"+width;
	        return resolution;
	}
	public int[] getScreenSize(Activity activity)
	{
		    DisplayMetrics dm = new DisplayMetrics();  
		    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	        int width=dm.widthPixels;   
	        int height=dm.heightPixels;
	        int [] screenSize =new int[2];
	        screenSize[0]=width;
	        screenSize[1]=height;
	        return screenSize;
	}
  	public String getBoard()
	{
		return getProp("ro.product.board");
	}
  	public String getPlatform()
	{
		return getProp("ro.lenovo.platform");
	}
  	public String getVersionRelease()
	{
		return getProp("ro.build.version.release");
	}
  	public int getTotalMem(int type)
  	{
  		String meminfo[]=getMeminfo();
  		if(meminfo.length!=0)
  		{
  			int totalMem=Integer.parseInt(meminfo[0]);
  			if(type==TYPE_KB)
  			{	
  				return totalMem;
  			}
  			else if(type==TYPE_MB)
  			{
  	  			totalMem=totalMem/1024;
  	  			return totalMem;
  			}
  		}
  			
  		return 0;
  	}

  	public int getFreeMem()
  	{
  		String meminfo[]=getMeminfo();
  		if(meminfo.length!=0)
  		{
  			int free=Integer.parseInt(meminfo[1]);
  			int cached=Integer.parseInt(meminfo[2]);
  			int freeMem=(free+cached)/1024;
  			return freeMem;
  		}
  		return 0;
  	}
  	public String [] getMeminfo()
  	{
  		String [] meminfo=new String [3];
		BufferedReader reader;
		File file =new File("/proc/meminfo");
		if(!file.exists())
			return null;
		try {
			String mem;
			reader = new BufferedReader(new FileReader(file));
			while((mem=reader.readLine())!=null)
			{
				if(mem.contains("MemTotal"))
				{
					String free=mem.split(":")[1];
					meminfo[0]=free.trim().split(" ")[0];

				}
				if(mem.contains("MemFree"))
				{
					String free=mem.split(":")[1];
					meminfo[1]=free.trim().split(" ")[0];

				}
				if(mem.contains("Cached")&&!mem.contains("Swap"))
				{
					String free=mem.split(":")[1];
					meminfo[2]=free.trim().split(" ")[0];
				
				}
			}
	    	reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return meminfo;
  		
  	}
  	public File createDir(String path)
  	{
  		File file =new File(path);
  		if(!file.exists())
  			file.mkdirs();
  		return file;
  	}
	public File createFile(String path)
  	{
  		File file =new File(path);
  		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  		return file;
  	}
	public static void log(String content)
	{
		if(DEBUG)
		Log.d(DEBUG_TAG, content);
	}
	public static void logFile(String content)
	{
		File file =new File(SDPATH+"/InfoMonitor/Drop.txt");
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(file, true));
			bw.write(content+"\r\n");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 public void registerObserver(ServiceObserver observer)
	    {
	    	this.observer=observer;
	    	log("register observer");
	    }
	    public void unregisterObserver()
	    {
	    	this.observer=null;
	    	log("unregister observer");
	    }
	    public void notifyUpdate(int time)
	    {
	    	if (observer!=null) {
	        	observer.onUpdate(time);
			}
	    }
	
}
