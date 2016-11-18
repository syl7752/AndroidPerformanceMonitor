package com.lenovo.infocollector;

import java.util.Timer;
import java.util.TimerTask;

import com.lenovo.infocollector.thread.InfoCollectorThread;
import com.lenovo.infomonitor.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog.Builder;
public class TemperatureService extends Service{

	//定义浮动窗口布局  
    LinearLayout mFloatLayout;  
    WindowManager.LayoutParams wmParams;  
    //创建浮动窗口设置布局参数的对象  
    WindowManager mWindowManager;  
      
    TextView mFloatView; 
    
    private Handler handler = new Handler();
    int temperature = 0;
    private static final String TAG = "Temperature";  
    
    @Override  
    public void onCreate()   
    {  
        // TODO Auto-generated method stub  
        super.onCreate();  
        Log.i(TAG, "oncreat");  
        createFloatView(); 
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		this.registerReceiver(batteryReceiver,
				intentFilter);
    }  
      
    private Timer timer;  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
        // 开启定时器，每隔0.5秒刷新一次  
        if (timer == null) {  
            timer = new Timer();  
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);  
        }  
        return super.onStartCommand(intent, flags, startId);  
    }  
    

    
    private void createFloatView()  
    {  
        wmParams = new WindowManager.LayoutParams();  
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);  
        Log.i(TAG, "mWindowManager--->" + mWindowManager);  
        wmParams.type = LayoutParams.TYPE_PHONE;   
        wmParams.format = PixelFormat.RGBA_8888;   
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;        
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;         
        wmParams.x = 0;  
        wmParams.y = 0;  
  
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
  
        LayoutInflater inflater = LayoutInflater.from(getApplication());  
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);  
        mWindowManager.addView(mFloatLayout, wmParams);  
        mFloatView = (TextView)mFloatLayout.findViewById(R.id.float_id);  
          
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,  
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec  
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));  
        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth()/2);  
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight()/2);  
        mFloatView.setOnTouchListener(new OnTouchListener()   
        {  
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
                wmParams.x = (int) arg1.getRawX() - mFloatView.getMeasuredWidth()/2;  
                Log.i(TAG, "RawX" + arg1.getRawX());  
                Log.i(TAG, "X" + arg1.getX());  
                wmParams.y = (int) arg1.getRawY() - mFloatView.getMeasuredHeight()/2 - 25;  
                Log.i(TAG, "RawY" + arg1.getRawY());  
                Log.i(TAG, "Y" + arg1.getY());  
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);  
                return false; 
			}  
        });   
          
        mFloatView.setOnClickListener(new OnClickListener()   
        {  
              
            @Override  
            public void onClick(View v)   
            {  
                Toast.makeText(TemperatureService.this, "Close", Toast.LENGTH_SHORT).show();  
                Builder builder = new AlertDialog.Builder(getApplicationContext());  
                builder.setTitle("Close");  
                builder.setMessage("Are you sure close temperature window?");  
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					@Override 
					public void onClick(DialogInterface arg0, int arg1) {
						onDestroy();
					}
				});
                Dialog dialog=builder.create();
				dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.show();
            }  
        });  
    }  
      
    @Override  
    public void onDestroy()   
    {  
        super.onDestroy();  
        if(mFloatLayout != null)  
        {  
        	this.unregisterReceiver(batteryReceiver);
            mWindowManager.removeView(mFloatLayout); 
            
        }  
    }  
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
    class RefreshTask extends TimerTask {
    	
		@Override
		public void run() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					TextView temperatureView = (TextView) mFloatView.findViewById(R.id.float_id);  
					 temperatureView.setText(getTemperature(getApplicationContext())); 
				}
			});
		}
    	
		public String getTemperature(Context context) {
			String tem = String.valueOf(temperature/10+"."+temperature%10+"℃");
			return tem;
		}
    }
    
	 BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if (Intent.ACTION_BATTERY_CHANGED.equals(arg1.getAction())) {
                 temperature = arg1.getIntExtra("temperature", 0);
                 Log.e("yangrh2", "temperature:"+temperature);
				}
			}
		};
}
