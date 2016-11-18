package com.lenovo.inforeader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.R;

public class BatteryinfoFragment extends FragmentBase{
	int batteryLevel=-1;
public void onActivityCreated(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onActivityCreated(savedInstanceState);
	System.out.println("activity create");
}
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	View view =inflater.inflate(R.layout.batteryinfo, container, false);
    	getInfoBtn=(Button) view.findViewById(R.id.get_batteryinfo);
    	textView=(TextView) view.findViewById(R.id.batteryinfoTv);
    	getInfoBtn.setOnClickListener(this);
    	Log.d(TAG, "Battery onCreateView");
    	sb.append("电量: \r\n");

    	return view;
    }
    @Override
    public void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	Log.d(TAG, "onstart bat");
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		 getActivity().registerReceiver(batteryReceiver,
				intentFilter);
    }
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	Log.d(TAG, "ondestroy bat");
    	getActivity().unregisterReceiver(batteryReceiver);
    }
	protected void getInfo() {
    	createLogFile("batteryinfo.txt");
		info=utils.getTime()+"-----电量 : "+batteryLevel+"\r\n";
	};
	BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			System.out.println("get");
			if (Intent.ACTION_BATTERY_CHANGED.equals(arg1.getAction())) {
				// 获取当前电量
				batteryLevel = arg1.getIntExtra("level", 0);
				Log.d(TAG, "battery changed level = "+batteryLevel);	
			}
		}
	};
    
}
