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

public class TemperatureFragment extends FragmentBase{
	String tem;
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    		Bundle savedInstanceState) {
	    	// TODO Auto-generated method stub
	    	View view =inflater.inflate(R.layout.temperatureinfo, container, false);
	    	getInfoBtn=(Button) view.findViewById(R.id.get_temperature);
	    	textView=(TextView) view.findViewById(R.id.temperatureTv);
	    	getInfoBtn.setOnClickListener(this);
	    	Log.d(TAG, "temperature onCreateView");
	    	sb.append("Œ¬∂»: \r\n");

	    	
	    	return view;
	    }
	    @Override
	    public void onStart() {
	    	// TODO Auto-generated method stub
	    	super.onStart();
	    	Log.d(TAG, "onstart TemperatureFragment");
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			 getActivity().registerReceiver(batteryReceiver,
					intentFilter);
	    }
	    @Override
	    public void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	Log.d(TAG, "ondestroy TemperatureFragment");
	    	getActivity().unregisterReceiver(batteryReceiver);
	    }
		protected void getInfo() {
	    	createLogFile("temperatureinfo.txt");
			tem=temperature/10+"."+temperature%10+"°Ê";
			info=utils.getTime()+"-----Œ¬∂» : "+tem+"\r\n";
		};
		BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				if (Intent.ACTION_BATTERY_CHANGED.equals(arg1.getAction())) {
					// Ëé∑ÂèñÂΩìÂâçÁîµÈáè
					temperature = arg1.getIntExtra("temperature", 0);
					Log.d(TAG, "battery temperature = "+temperature);	
				}
			}
		};
	    

}
