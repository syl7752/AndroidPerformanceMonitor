package com.lenovo.infomonitor.infoplugins;



import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import com.lenovo.info.util.LinpackLoop;
import com.lenovo.infomonitor.R;

public class CpuFillActivity extends Activity implements OnClickListener{
    Button start_OneThread,start_MultiThread;
    private static int mNow;
	 private boolean mNextRound = true;
	 private boolean isMultiThread=false;
	 private static boolean isRunning=false;
	 private TesterThread testerThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cpufill);
		start_OneThread=(Button) findViewById(R.id.cpufill_start1);
		start_MultiThread=(Button) findViewById(R.id.cpufill_start2);
		start_OneThread.setOnClickListener(this);
		start_MultiThread.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.cpufill_start1:
			isMultiThread=false;
			if(!isRunning)
			{	
				if(testerThread==null)
		    testerThread=new TesterThread(0, 200);
		    testerThread.start();
		    start_OneThread.setText("stop_simple");
		    isRunning=true;
			}
			else
			{
				interruptTester();
				if(testerThread!=null)
					testerThread=null;
				 start_OneThread.setText("start_simple");
				 isRunning=false;
			}
			break;
        case R.id.cpufill_start2:
        	isMultiThread=true;
        	if(!isRunning)
			{	
        	if(testerThread==null)
        	   testerThread=new TesterThread(0, 200);
		    testerThread.start();
		    start_MultiThread.setText("stop_multi");
		    isRunning=true;
			}
			else
			{
				interruptTester();
				start_MultiThread.setText("start_multi");
				  isRunning=false;
			}
			break;
		default:
			break;
		}
	}
	 class TesterThread extends Thread {
	      int mSleepingStart;
	      int mSleepingTime;
	      TesterThread(int sleepStart, int sleepPeriod) {
	          mSleepingStart = sleepStart;
	          mSleepingTime  = sleepPeriod;
	          mNow=1;
	      }

	      private void lazyLoop() throws Exception {
	          while (!isTesterFinished()) {
	              if (mNextRound) {
	                  mNextRound = false;
	                  oneRound();
	              } else {
	                  sleep(mSleepingTime);
	              }
	          }
	      }
	      public void run() {
	          try {
	              sleep(mSleepingStart);
	              System.out.println("loop");
	              lazyLoop();

	          } catch (Exception e) {
	                  e.printStackTrace();
	          }
	      }
	 }
	      public boolean isTesterFinished() {
	    	  	//Log.d(TAG, "mNow = " + mNow);
	    	      return (mNow <= 0);
	    	  }
	    	  public void interruptTester() {
	    	      mNow = 0;
	    	  }
	    	  public void oneRound() {
	    	      LinpackLoop.main();
	    	      if(isMultiThread)
	    	      {
	    	      new Thread(){
	    	    	  public void run() {
	    	    		  LinpackLoop.main();
	    	    		  new Thread(){
	    	    	    	  public void run() {
	    	    	    		  LinpackLoop.main();
	    	    	    	  };
	    	    	      }.start();
	    	    	  };
	    	      }.start();
	    	      }
	    	      mNextRound = true;
	    	  }
}
