package com.lenovo.infomonitor.infoplugins;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;

import com.lenovo.infomonitor.R;
import com.lenovo.infomonitor.R.drawable;
import com.lenovo.infomonitor.R.id;
import com.lenovo.infomonitor.R.layout;

public class PluginActivity extends Activity implements OnTouchListener{
	LinearLayout cpufillLayout,memfillLayout,showfpsLayout;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.plugin);
        initUI();
    }
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.cpufill_layout:
			switch (arg1.getAction()) {
			  case MotionEvent.ACTION_DOWN:  
				  cpufillLayout.setBackgroundResource(R.drawable.clr_pressed);  
	                break;  
	            case MotionEvent.ACTION_UP:  
	            	cpufillLayout.setBackgroundResource(R.drawable.corners_btn); 
	            	Intent intent=new Intent(this,CpuFillActivity.class);
	            	startActivity(intent);
	            	
	                break;  
			}
			break;
		case R.id.showfps_layout:
			switch (arg1.getAction()) {
			  case MotionEvent.ACTION_DOWN:  
				  showfpsLayout.setBackgroundResource(R.drawable.clr_pressed);  
	                break;  
	            case MotionEvent.ACTION_UP:  
	            	showfpsLayout.setBackgroundResource(R.drawable.corners_btn); 
	            	Intent intent=new Intent(this,ShowFPSActivity.class);
	            	startActivity(intent);
	            	
	                break;  
			}
			break;

		default:
			break;
		}
		return true;
	}
    private void initUI()
    {
    	cpufillLayout=(LinearLayout) findViewById(R.id.cpufill_layout);
    	cpufillLayout.setOnTouchListener(this);
        memfillLayout=(LinearLayout) findViewById(R.id.memfill_layout);
        memfillLayout.setOnTouchListener(this);
    	showfpsLayout=(LinearLayout) findViewById(R.id.showfps_layout);
        showfpsLayout.setOnTouchListener(this);
    }
}
