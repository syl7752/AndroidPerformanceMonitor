package com.lenovo.infomonitor;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.Toast;

import com.lenovo.info.util.Parameters;
import com.lenovo.info.util.Utils;
import com.lenovo.infocollector.InfoCollectorActivity;
import com.lenovo.infomonitor.MyPopupMenu.PopupItem;
import com.lenovo.infomonitor.MyPopupMenu.PopupMenuListener;
import com.lenovo.infomonitor.infodrawing.DrawingActivity;
import com.lenovo.infomonitor.infodrawing.DrawingService;
import com.lenovo.infomonitor.infoplugins.PluginActivity;
import com.lenovo.inforeader.InfoReaderActivity;

public class MainActivity extends TabActivity implements PopupMenuListener {
	TabHost tabHost;
	MyPopupMenu myPopupMenu;
	private MyApp myApp;
	private MoniteListener mListener;
	private ArrayList<PopupItem> menuItems;
	private PopupItem popupItem;
	private static boolean MONITE_SHOW=false;
	private static boolean DRAW_SHOW=false;
	Utils utils;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initUI();
		utils=Utils.getInstance();
	}

	private void initUI()
    {
    	tabHost = this.getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, InfoCollectorActivity.class);
		spec = tabHost.newTabSpec("InfoCollector").setIndicator("InfoCollector")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, InfoReaderActivity.class);
		spec = tabHost.newTabSpec("InfoReader").setIndicator("InfoReader")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, PluginActivity.class);
		spec = tabHost.newTabSpec("Plugin").setIndicator("Plugin")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, DrawingActivity.class);
		spec = tabHost.newTabSpec("Drawing").setIndicator("Drawing")
				.setContent(intent);
		tabHost.addTab(spec);
		tabHost.setCurrentTab(0);
		myApp=(MyApp) getApplication();
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new Notification(R.drawable.appicon, "InfoMonitor",
				System.currentTimeMillis());
		n.flags = Notification.FLAG_AUTO_CANCEL;
		Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this,
				R.string.app_name, i, PendingIntent.FLAG_UPDATE_CURRENT);

		n.setLatestEventInfo(this, "InfoMonitor", "InfoMonitor is running!",
				contentIntent);
		nm.notify(R.string.app_name, n);
		
		TypedArray actionbarSizeTypedArray = this.obtainStyledAttributes(new int[] {  
		        android.R.attr.actionBarSize  
		});  
		  
		float h = actionbarSizeTypedArray.getDimension(0, 0);  
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
				(int)(h/1.4), (int)(h/1.4));
		lp.gravity = lp.gravity & ~Gravity.HORIZONTAL_GRAVITY_MASK
				| Gravity.RIGHT;
		myPopupMenu = new MyPopupMenu(this);
		myPopupMenu.setPopupMenuListener(this);
		getActionBar().setCustomView(myPopupMenu,lp);
		int change = getActionBar().getDisplayOptions()
				^ ActionBar.DISPLAY_SHOW_CUSTOM;
		getActionBar().setDisplayOptions(change, ActionBar.DISPLAY_SHOW_CUSTOM);
		RadioGroup radioGroup = (RadioGroup) this
				.findViewById(R.id.main_tab_group);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.main_tab_infocollector:
					tabHost.setCurrentTabByTag("InfoCollector");
					MONITE_SHOW=true;
					DRAW_SHOW=false;
				
					break;
				case R.id.main_tab_inforeader:
					tabHost.setCurrentTabByTag("InfoReader");	
					MONITE_SHOW=false;
					DRAW_SHOW=false;
					break;
				case R.id.main_tab_plugin:
					tabHost.setCurrentTabByTag("Plugin");
					MONITE_SHOW=false;
					DRAW_SHOW=false;
					break;
				case R.id.main_tab_drawing:
					tabHost.setCurrentTabByTag("Drawing");
					MONITE_SHOW=false;
					DRAW_SHOW=true;
					break;
				default:
					break;
				}
				myPopupMenu.setItems(getMenuItems());
			}
		});
		MONITE_SHOW=true;
		DRAW_SHOW=false;
		myPopupMenu.setItems(getMenuItems());
		createLogDir();
    }
    private void createLogDir()
    {
    	File file =new File(getFilesDir().getAbsolutePath()+File.separatorChar+"InfoCollector");
    	if(!file.exists())
    		file.mkdir();
    	File readerFile =new File(getFilesDir().getAbsolutePath()+File.separatorChar+"InfoReader");
    	if(!readerFile.exists())
    		readerFile.mkdir();
    }
   
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	private void sendBroadCaseRemountSDcard() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
		intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()
				.getAbsoluteFile()));
		sendBroadcast(intent);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
			if (myPopupMenu != null) {
				if (myPopupMenu.isShowing())
					myPopupMenu.dismiss();
				else
					myPopupMenu.show();
			}
		}
		return super.dispatchKeyEvent(event);
	}

	

	@Override
	public void onClickMenuItem(int id) {
		// TODO Auto-generated method stub
		mListener = myApp.getMoniteListener();
		switch (id) {
		case Parameters.ITEM_MONITE:
			if (InfoCollectorActivity.isServiceRunning == 1) {

				if (mListener.onClickItem(Parameters.MONITE_STOP))
					myPopupMenu.updateItems(Parameters.ITEM_MONITE, "Start Monitor");
			} else {

				if (mListener.onClickItem(Parameters.MONITE_START))
					myPopupMenu.updateItems(Parameters.ITEM_MONITE, "Stop Monitor");
			}
			break;
		case Parameters.ITEM_DRAW:
			if(DrawingService.isDrawing())
			{
				if (mListener.onClickItem(Parameters.DRAW_STOP))
					myPopupMenu.updateItems(Parameters.ITEM_DRAW, "Start Drawing");
			}
			else
			{
				if (mListener.onClickItem(Parameters.DRAW_START))
					myPopupMenu.updateItems(Parameters.ITEM_DRAW, "Stop Drawing");
			}
			break;
		case Parameters.ITEM_PULL:
			sendBroadCaseRemountSDcard();
			Toast.makeText(this, "Export Data Success", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	/**
	 * change menu items
	 * @author shiyl
	 * 2014-9-4
	 */
	private ArrayList<PopupItem> getMenuItems() {
         menuItems=new ArrayList<MyPopupMenu.PopupItem>();
         if(MONITE_SHOW)
         {
        	 if(InfoCollectorActivity.isServiceRunning == 1)
        	 popupItem=new PopupItem("Stop Monitor",Parameters.ITEM_MONITE);
        	 else
        		 popupItem=new PopupItem("Start Monitor",Parameters.ITEM_MONITE);
        	 menuItems.add(popupItem);
         }
         if(DRAW_SHOW)
         {
        	 if(DrawingService.isDrawing())
        	 popupItem=new PopupItem("Stop Drawing",Parameters.ITEM_DRAW);
        	 else
             popupItem=new PopupItem("Start Drawing",Parameters.ITEM_DRAW);
        	 menuItems.add(popupItem);
         }
         popupItem=new PopupItem("Export Data",Parameters.ITEM_PULL);
         menuItems.add(popupItem);
         return menuItems;
	}

	public interface MoniteListener {
		public boolean onClickItem(int id);
	}

}
