package com.lenovo.infocollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;

import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.R;

/**
 * choose process activity
 * @author shiyl
 * 2014-8-12
 */
public class AppListActivity extends Activity{
    private HashMap<String, Object> appInfoMap;
    private ArrayList<HashMap<String, Object>> appList;
    ProgressDialog progressDialog;
    MyTask myTask;
    ListView listview;
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist);
		progressDialog=new ProgressDialog(this);
		listview=(ListView) findViewById(R.id.listview);
		initView();
	}
	 @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	private void initView()
	{
		progressDialog.setTitle("");
		progressDialog.setMessage("Please wait");
		progressDialog.show();
		PackageManager packageManager=this.getPackageManager();
		myTask=new MyTask();
		myTask.execute(packageManager);
		appList=new ArrayList<HashMap<String,Object>>();

	}
	/**
	 * 获取app信息列表后更新UI
	 * @author shiyl
	 * 2014-9-18
	 */
	class MyTask extends AsyncTask<PackageManager, Integer, ArrayList<HashMap<String,Object>>>
	{

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(
				PackageManager... pkg) {
			// TODO Auto-generated method stub
			ArrayList<HashMap<String,Object>> list=new ArrayList<HashMap<String,Object>>();
			
			List<PackageInfo> packages = pkg[0].getInstalledPackages(0);
			for (int i = 0; i < packages.size(); i++) {
				PackageInfo info=packages.get(i);
				HashMap<String, Object> appInfoMap=new HashMap<String, Object>();
				if((info.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0)
				{
				appInfoMap.put("appname", 	info.applicationInfo.loadLabel(pkg[0]).toString());
				appInfoMap.put("appicon", info.applicationInfo.loadIcon(pkg[0]));
				appInfoMap.put("packagename", info.packageName);
				list.add(appInfoMap);
				}
			}
			publishProgress(100);
			return list;
		}
		@Override
		protected void onPostExecute(final ArrayList<HashMap<String, Object>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			 SimpleAdapter adapter=new SimpleAdapter(AppListActivity.this, result, R.layout.applistitem, new String[]{"appname","appicon","packagename"},
			 new int[]{R.id.app_name,R.id.appIcon,R.id.package_name});
	         adapter.setViewBinder(new ViewBinder(){
             public boolean setViewValue(View view,Object data,String textRepresentation){
                 if(view instanceof ImageView && data instanceof Drawable){
                 ImageView iv=(ImageView)view;
                      iv.setImageDrawable((Drawable)data);
                                return true;
                           }
                              else return false;
                           }
                      });
	         listview.setAdapter(adapter);
	         listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					HashMap<String, Object> appInfo=result.get(position);
					String pkgName=(String) appInfo.get("packagename");
					String appName=(String) appInfo.get("appname");
					Intent intent=new Intent();
					intent.putExtra("pkgName", pkgName);
					intent.putExtra("appName", appName);
					setResult(0, intent);
					finish();
				}
			});
	         progressDialog.dismiss();
		}
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			Utils.log("AppListActivity-----call back");
			setResult(1);
			finish();
		}
		return false;
	}
	public void getData()
	{
		
	}
	
}
