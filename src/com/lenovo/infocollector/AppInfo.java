package com.lenovo.infocollector;

import android.graphics.drawable.Drawable;

/**
 * save app info
 * @author shiyl
 * 2014-8-12
 */
public class AppInfo {
       private String appName;
       private Drawable appIcon;
       private String packageName;
       public void setAppName(String appName)
       {
    	   this.appName=appName;
       }
       public void setAppIcon(Drawable appIcon)
       {
    	   this.appIcon=appIcon;
       }
       public void setPackageName(String packageName)
       {
    	   this.packageName=packageName;
       }
       public String getAppName()
       {
    	   return this.appName;
       }
       public Drawable getAppIcon()
       {
    	   return this.appIcon;
       }
       public String getPackageName()
       {
    	   return this.packageName;
       }
       
}
