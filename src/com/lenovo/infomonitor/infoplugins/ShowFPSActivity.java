package com.lenovo.infomonitor.infoplugins;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lenovo.infomonitor.R;

public class ShowFPSActivity extends Activity implements OnClickListener{
	    Button startBtn,stopBtn;
	    public static Process process;
         @Override
        protected void onCreate(Bundle savedInstanceState) {
        	// TODO Auto-generated method stub
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.showfps);
        	startBtn=(Button) findViewById(R.id.showfps_start);
        	stopBtn=(Button) findViewById(R.id.showfps_stop);
        	startBtn.setOnClickListener(this);
        	stopBtn.setOnClickListener(this);
        }
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (arg0.getId()) {
			case R.id.showfps_start:
				start_showfps();
				break;
case R.id.showfps_stop:
				stop_showfps();
				break;
			default:
				break;
			}
		}
		private void start_showfps()
		{
			try {
				if(process==null)
				process=Runtime.getRuntime().exec("showfps");
				//process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		private void stop_showfps()
		{
			if(process!=null)
			{
			process.destroy();
			process=null;
			}
		}

         
}
