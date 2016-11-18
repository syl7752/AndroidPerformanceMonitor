package com.lenovo.infomonitor;

import com.lenovo.infomonitor.MainActivity.MoniteListener;

import android.app.Application;

public class MyApp extends Application{
     private static MoniteListener mListener;
	 public void setMoniteListener(MoniteListener mListener)
	 {
		 this.mListener=mListener;
	 }
	 public MoniteListener getMoniteListener()
	 {
		 return this.mListener;
	 }
}
