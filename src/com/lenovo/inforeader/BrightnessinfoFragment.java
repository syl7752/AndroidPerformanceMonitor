package com.lenovo.inforeader;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.R;

public class BrightnessinfoFragment extends FragmentBase{
	String brightnessinfo;
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	View view =inflater.inflate(R.layout.brightnessinfo, container, false);
    	getInfoBtn=(Button) view.findViewById(R.id.get_brightnessinfo);
    	textView=(TextView) view.findViewById(R.id.brightnessinfoTv);
    	getInfoBtn.setOnClickListener(this);
    	Log.d(TAG, "Brightness onCreateView");
    	sb.append("屏幕亮度: \r\n");

    	return view;
    }
	@Override
	protected void getInfo() {
		// TODO Auto-generated method stub
    	createLogFile("brightnessinfo.txt");
    	brightnessinfo="亮度："+getScreenBrightness();
	    	info=utils.getTime()+"----"+brightnessinfo+"\r\n";
	}
	public int getScreenBrightness() {
	    int value = 0;
	    ContentResolver cr = getActivity().getContentResolver();
	    try {
	        value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
	    } catch (SettingNotFoundException e) {
	        
	    }
	    return value;
	}
}
