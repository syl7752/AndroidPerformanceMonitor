package com.lenovo.infomonitor.infodrawing;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.lenovo.info.util.Parameters;
import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.MainActivity.MoniteListener;
import com.lenovo.infomonitor.MyApp;
import com.lenovo.infomonitor.R;

public class DrawingActivity extends Activity implements MoniteListener{
	private GraphicalView chartView;
	private TimeSeries series1;
    private XYMultipleSeriesDataset dataset1;
    private MyApp myApp;
    private ArrayList<Integer> memDataList;
    private ArrayList<Date> memTimeList;
    private MemoryDataHelper memoryDataHelper;
    Utils utils;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.drawing);
    	memoryDataHelper=MemoryDataHelper.init(this);
    	utils=Utils.getInstance();
    }
     private XYMultipleSeriesRenderer getRenderer() {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setChartTitle("Cpu Usage View");
		    renderer.setChartTitleTextSize(30);
		    renderer.setYTitle("Cpu Usage(%)");    
		    renderer.setXTitle("System Time");   
		    renderer.setAxisTitleTextSize(16);
		    renderer.setAxesColor(Color.BLACK);
		    renderer.setLabelsTextSize(20);   
		    renderer.setLabelsColor(Color.BLACK);
		    renderer.setLegendTextSize(20);    
		    renderer.setXLabelsColor(Color.BLACK);
		    renderer.setYLabelsColor(0,Color.BLACK);
		    renderer.setShowLegend(false);
		    renderer.setMargins(new int[] {40, 30, 50, 0});
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.parseColor("#5CACEE"));
		    r.setLineWidth(3);
		    r.setChartValuesTextSize(30);
		    r.setChartValuesSpacing(3);
		    r.setPointStyle(PointStyle.CIRCLE);
		    //r.setFillBelowLine(true);
		    //r.setFillBelowLineColor(Color.parseColor("#E0FFFF"));
		    r.setFillPoints(true);
		    r.setPointStyle(PointStyle.POINT );
		    renderer.addSeriesRenderer(r);
		    renderer.setMarginsColor(Color.WHITE);
		    renderer.setPanEnabled(false,false);
		    renderer.setShowGrid(true);
		    renderer.setGridColor(Color.LTGRAY);
		    renderer.setBackgroundColor(Color.WHITE);
		    int total=utils.getTotalMem(Utils.TYPE_MB);
		    if(Utils.DEBUG)
		    	Log.d(Utils.DEBUG_TAG, "Total Memory = "+total);
		    renderer.setYAxisMax(total);
		    renderer.setYAxisMin(0);
		    renderer.setInScroll(true);  
		    return renderer;
		  }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	myApp=(MyApp) getApplication();
    	myApp.setMoniteListener(this);
    	LinearLayout linearLayout=(LinearLayout) findViewById(R.id.cpuView);
    	chartView=ChartFactory.getTimeChartView(this, getDateDataset(), getRenderer(), "hh:mm:ss");
    	linearLayout.addView(chartView);
    	setBackground(false);
    	IntentFilter intentFilter=new IntentFilter("updateDrawView");
    	registerReceiver(broadcastReceiver, intentFilter);
    }
    private void setBackground(boolean isBackground)
    {
    	SharedPreferences sp=getSharedPreferences("mymem", MODE_PRIVATE);
    	Editor editor=sp.edit();
    	editor.putBoolean("isBackgroud", isBackground);
    	editor.commit();
    }
	   private XYMultipleSeriesDataset getDateDataset() {
		    dataset1 = new XYMultipleSeriesDataset();
		    series1 = new TimeSeries("Free Series");
		    setChartViewData();
		    dataset1.addSeries(series1);
		    return dataset1;
		  }
	/**
	 * 更新绘图UI
	 * @author shiyl
	 * 2014-9-4
	 */
	public void updateChartView(int free) {
		// TODO Auto-generated method stub
		if(free==-1)
		{
			Log.e(Utils.DEBUG_TAG, "free size = -1");
		    return;
		}
		Log.d(Utils.DEBUG_TAG,"count = "+ series1.getItemCount());
		Log.d(Utils.DEBUG_TAG,"size = "+ memoryDataHelper.getSize());
		if(series1.getItemCount()>=40)
		{
			series1.clear();
		}
		series1.add(new Date(), free);
		//Log.d(Utils.DEBUG_TAG, "update ui free = "+free);
     	dataset1.removeSeries(series1);
		dataset1.addSeries(series1);
		chartView.invalidate();
	}
//	private void takeSnapshot()
//	{
//		View view= getWindow().getDecorView();
//
//		Bitmap bmp = Bitmap.createBitmap(480, 800, Bitmap.Config.ARGB_8888);
//
//		view.draw(new Canvas(bmp));
//		bmp.compress(CompressFormat.PNG, 100, new FileOutputStream(new File()));
//	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		setBackground(true);
		unregisterReceiver(broadcastReceiver);
	}
	private void setChartViewData()
	{
		series1.clear();
		int [] nums={0,0,0,0,0,0,0,0,0,0};
	    long value = new Date().getTime();
		ArrayList<ArrayList> list=memoryDataHelper.query();
		memTimeList=list.get(0);
		memDataList=list.get(1);
		int dataLength=memDataList.size();
		if(dataLength>0){
			Log.d(Utils.DEBUG_TAG, "length = "+dataLength);
		for (int k = 0; k < dataLength; k++) {
    		series1.add(memTimeList.get(k), memDataList.get(k));
    	}
		}else
		{
	      for (int k = 9; k >=0; k--) {
	        series1.add(new Date(value-k*1000), nums[k]);
	        memoryDataHelper.insert(new Date(value-k*1000), nums[k]);
	      }
		}
	}
	@Override
	public boolean onClickItem(int id) {
		// TODO Auto-generated method stub
		Intent intent =new Intent(DrawingActivity.this,DrawingService.class);
		switch (id) {
		case Parameters.DRAW_START:
			if(Utils.DEBUG)
				Log.d(Utils.DEBUG_TAG, "DRAW_START");
			memoryDataHelper.removeAll();
			setChartViewData();
			startService(intent);
			return true;
        case Parameters.DRAW_STOP:
        	series1.clear();
            stopService(intent);
			return true;
		default:
			break;
		}
		return false;
	}
	BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equalsIgnoreCase("updateDrawView"))
			{
				int free= intent.getIntExtra("free", -1);
	      		updateChartView(free);
			}
		}
	};
}
