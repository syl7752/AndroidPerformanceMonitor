package com.lenovo.infocollector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lenovo.info.util.Parameters;
import com.lenovo.info.util.ServiceObserver;
import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.MainActivity;
import com.lenovo.infomonitor.MainActivity.MoniteListener;
import com.lenovo.infomonitor.MyApp;
import com.lenovo.infomonitor.R;

/**
 *
 * @author shiyl
 * 2014-7-4
 */
public class InfoCollectorActivity extends Activity implements MoniteListener,ServiceObserver{
	private int intervel = 10000;
	static String processName = "";
	ListView processListView;
	String areas[];
	String processList = "All Processes";
	Button chooseProcessButton;
	private TextView statusView,appNameView;
    public Context context;
    public static int isServiceRunning = 0;
    CheckBox pssinfo,raminfo,cpuinfo,sdcard,battery,teminfo,meminfo, monitorwindows;
    Menu menu;
    private MyApp myApp;
    private ImageView appIcon;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logcollector);
		initUI();
     	context=this;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initListener();
		super.onResume();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void initUI() {
		statusView = (TextView) (this.findViewById(R.id.textView5));
		appNameView=(TextView) findViewById(R.id.appname);
		appIcon=(ImageView) findViewById(R.id.appIcon);
		IntentFilter intentFilter = new IntentFilter(
				);
		intentFilter.addAction("com.lenovo.logcollector");
		pssinfo = (CheckBox) this.findViewById(R.id.pssinfoCheckBox);
		raminfo = (CheckBox) this.findViewById(R.id.raminfoCheckBox);
		cpuinfo = (CheckBox) this.findViewById(R.id.cpuinfoCheckBox);
		sdcard = (CheckBox) this.findViewById(R.id.sdcardCheckBox);
		battery = (CheckBox) this.findViewById(R.id.batteryCheckBox);
		teminfo = (CheckBox) this.findViewById(R.id.teminfoCheckBox);
		meminfo = (CheckBox) this.findViewById(R.id.meminfoCheckBox);
		monitorwindows = (CheckBox) this.findViewById(R.id.monitorwindow);
		//CheckBox fpsinfo = (CheckBox) this.findViewById(R.id.fpsinfoCheckBox);
	}


	private void StartDump() {
		// 娓呴櫎鏃og
				File files =new File(context.getFilesDir().getAbsolutePath()+File.separatorChar+"InfoCollector");
				for (File file:files.listFiles()) {
					//Log.d(Utils.DEBUG_TAG, "Delete file "+file.getName());
					file.delete();
				}
		
		intervel = Integer.parseInt(((EditText) this
				.findViewById(R.id.editText1)).getText().toString());
		try {
			BufferedWriter output=new BufferedWriter(new OutputStreamWriter(context.openFileOutput("logcollector.cfg", Context.MODE_WORLD_WRITEABLE)));
			output.write("START-TIME " + new Date().getTime() + ",");
			output.write("INTERVAL " + intervel + ",");
			// added free command
			output.write("PSSINFO-ENABLE " + pssinfo.isChecked()
					+ ",");
			output.write("CPUINFO-ENABLE " + cpuinfo.isChecked() + ",");
			output.write("SDCARD-ENABLE " + sdcard.isChecked() + ",");
			output.write("BATTERY-ENABLE " + battery.isChecked() + ",");
			output.write("RAMINFO-ENABLE " + raminfo.isChecked() + ",");
			output.write("TEMPERATURE-ENABLE " + teminfo.isChecked() + ",");
			output.write("MEMINFO-ENABLE " + meminfo.isChecked() + ",");
			//output.write("FPS-ENABLE " + fpsinfo.isChecked() + ",");
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Intent i = new Intent(InfoCollectorActivity.this,
				InfoCollectorService.class);
		i.putExtra("isMonite", true);
		Log.d("LogCollector",
				"activity.StartDump : "
						+ "cpuinfo>" + cpuinfo.isChecked()
						+ sdcard.isChecked() 
						+ "battery>" + battery.isChecked() + "raminfo>" + raminfo.isChecked());
		startService(i);
	}

	private void StopDump() {
		Intent i = new Intent(InfoCollectorActivity.this,
				InfoCollectorService.class);
		stopService(i);
	}
	public void initListener() {
		chooseProcessButton = ((Button) (this.findViewById(R.id.choose_app)));
		chooseProcessButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
                Intent intent =new Intent(InfoCollectorActivity.this,AppListActivity.class);
                startActivityForResult(intent, 0);
			}
		});
		myApp=(MyApp) getApplication();
		myApp.setMoniteListener(this);
		Utils utils=Utils.getInstance();
		if(utils!=null)
		utils.registerObserver(this);
	}

	public String[] getProcessList() {
		ArrayList<String> list = new ArrayList<String>();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("procrank");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = "";
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if ((line.contains("Rss") && line.contains("Uss"))
						|| line.startsWith("procrank") || line.contains("/")) {
				} else {
					if (line.contains("--")) {
						break;
					}
					list.add(line.substring(42).trim());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String temp[] = new String[list.size() + 1];
		temp[0] = "All Processes";
		for (int i = 0; i < list.size(); i++) {
			temp[i + 1] = list.get(i);
		}
		return temp;
	}

	/**
	 * 鍒ゆ柇info绫诲瀷鏄惁琚�変腑
	 * @author shiyl
	 * 2014-9-4
	 */
	private boolean isItemChecked()
	{
		if(pssinfo.isChecked()||cpuinfo.isChecked()||sdcard.isChecked()||battery.isChecked()||raminfo.isChecked()||teminfo.isChecked()
	  ||meminfo.isChecked())
		{
			return true;
		}
		return false;
	}
	/**
	 * 鐩戝惉鑿滃崟閫夐」浜嬩欢
	 * @author shiyl
	 * 2014-9-4
	 */
	@Override
	public boolean onClickItem(int id) {
		// TODO Auto-generated method stub
		if(id==Parameters.MONITE_START)
		{
			if(!isItemChecked())
			{
				Toast.makeText(this, "Please choose info type", Toast.LENGTH_LONG).show();
				isServiceRunning=0;
                return false;
			}
			Toast.makeText(this, "Start Monitor.....", Toast.LENGTH_LONG).show();
			StartDump();
			statusView.setText("Start Monitor.....");	
			isServiceRunning=1;
			if (monitorwindows.isChecked()&&teminfo.isChecked()) {
				// TODO Auto-generated method stub 
				Log.d("yangrh2", "Windows start");
                Intent intent = new Intent(InfoCollectorActivity.this, TemperatureService.class);  
                startService(intent); 
			}
		}
		else if(id==Parameters.MONITE_STOP)
		{
			if (teminfo.isChecked()) {
				// TODO Auto-generated method stub 
				Log.d("yangrh2", "Windows stop");
                Intent intent = new Intent(InfoCollectorActivity.this, TemperatureService.class);  
                stopService(intent); 
			}
			Toast.makeText(this, "Stop Monitor", Toast.LENGTH_LONG).show();
			StopDump();
			statusView.setText("Stop Monitor(Click right top to export data)\r\nDefault data location:/sdcard/infomonitor");
			isServiceRunning=0;
		}
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==0)
		{
			String pkgName=data.getStringExtra("pkgName");
			String appName=data.getStringExtra("appName");
			PackageManager packageManager=getPackageManager();
			try {
				PackageInfo info=packageManager.getPackageInfo(pkgName, 0);
				appIcon.setImageDrawable(info.applicationInfo.loadIcon(packageManager));
				appNameView.setText(appName);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	public void onUpdate(int time) {
		// TODO Auto-generated method stub
		statusView.setText("service is running" + time + "mins");
		isServiceRunning = 1;
	}
	
}