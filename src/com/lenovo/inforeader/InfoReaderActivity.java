package com.lenovo.inforeader;

import java.io.File;
import java.util.ArrayList;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.TabletTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomOutTranformer;
import com.lenovo.info.util.Utils;
import com.lenovo.infomonitor.R;

public class InfoReaderActivity extends FragmentActivity {

	ImageView imageView; // 指示器
	int bitWidth; // 图片宽度
	int offset = 0; // 偏移量
	int tabIndex = 0; // 全局index,用以标示当前的index
	boolean isScroll = false;
	TextView memLabel, cpuLabel, batteryLabel,temLabel,sdLabel,brightnessLabel;

	FragmentBase meminfoFragment;
	FragmentBase batteryinfoFragment;
	FragmentBase cpuinfoFragment;
	FragmentBase temperatureFragment;
	FragmentBase sdinfoFragment;
	FragmentBase brightnessFragment;
	
	FragmentManager fragmentManager;
	FragmentTransaction fragmentTransaction;
	
	ViewPager viewPager;
	private ArrayList<Fragment> fragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inforeader);
		InitImageView();
		InitTextView();
		meminfoFragment = new MeminfoFragment();
		cpuinfoFragment = new CpuinfoFragment();
		batteryinfoFragment = new BatteryinfoFragment();
		temperatureFragment=new TemperatureFragment();
		sdinfoFragment=new SdinfoFragment();
		brightnessFragment=new BrightnessinfoFragment();
		
		// 开启事物，添加第一个fragment
		fragmentManager = getSupportFragmentManager();
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(meminfoFragment);
		fragmentList.add(cpuinfoFragment);
		fragmentList.add(batteryinfoFragment);
		fragmentList.add(temperatureFragment);
		fragmentList.add(sdinfoFragment);
		fragmentList.add(brightnessFragment);
		
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		new MyFragmentPagerAdapter(getSupportFragmentManager(), viewPager,
				fragmentList);
		viewPager.setCurrentItem(0);
		viewPager.setPageTransformer(true, new RotateUpTransformer());
		fragmentManager
				.removeOnBackStackChangedListener(new OnBackStackChangedListener() {

					@Override
					public void onBackStackChanged() {
						// TODO Auto-generated method stub

					}
				});
		createLogPath();
	}

	private void createLogPath() {
		File file = new File(Utils.LOGPATH);
		if (!file.exists())
			file.mkdirs();
	}

	// 初始化指示器，获取平均偏移量
	private void InitImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bitWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.cursor).getWidth();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenW = displayMetrics.widthPixels;

		// 平均偏移量
		offset = (int) ((screenW / 6 - bitWidth) * 3.1);
		int loc = (screenW / 6 - bitWidth) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(loc, 0);
		imageView.setImageMatrix(matrix);
	}

	// 初始化
	private void InitTextView() {
		memLabel = (TextView) findViewById(R.id.memlabel);
		cpuLabel = (TextView) findViewById(R.id.cpulabel);
		batteryLabel = (TextView) findViewById(R.id.batterylabel);
		temLabel=(TextView) findViewById(R.id.temlabel);
		sdLabel=(TextView) findViewById(R.id.sdlabel);
		brightnessLabel=(TextView) findViewById(R.id.brightnesslabel);
		
		memLabel.setOnClickListener(new MyOnClickListener(0));
		cpuLabel.setOnClickListener(new MyOnClickListener(1));
		batteryLabel.setOnClickListener(new MyOnClickListener(2));
		temLabel.setOnClickListener(new MyOnClickListener(3));
		sdLabel.setOnClickListener(new MyOnClickListener(4));
		brightnessLabel.setOnClickListener(new MyOnClickListener(5));
	}

	class MyOnClickListener implements OnClickListener {

		int index; // 保存点击时传入的index

		public MyOnClickListener(int index) {
			this.index = index;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			isScroll = false;
			viewPager.setCurrentItem(index);
			scrollCursor(index);

		}
	}

	public void scrollCursor(int index) {
		Animation animation = new TranslateAnimation(offset * tabIndex, offset
				* index, 0, 0);
		tabIndex = index; // 保存当前index
		animation.setFillAfter(true);
		animation.setDuration(300);
		imageView.startAnimation(animation);
	}

	public class MyFragmentPagerAdapter extends PagerAdapter implements
			ViewPager.OnPageChangeListener {
		ArrayList<Fragment> list;
		private int currentPageIndex = 0;
		private FragmentManager fragmentManager;
		private OnExtraPageChangeListener onExtraPageChangeListener;
		private ViewPager viewPager;

		public MyFragmentPagerAdapter(FragmentManager fm, ViewPager viewPager,
				ArrayList<Fragment> list) {
			super();
			this.list = list;
			this.fragmentManager = fm;
			this.viewPager = viewPager;
			this.viewPager.setAdapter(this);
			this.viewPager.setOnPageChangeListener(this);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Fragment fragment = list.get(position);
			if (!fragment.isAdded()) { // 如果fragment还没有added
				FragmentTransaction ft = fragmentManager.beginTransaction();
				ft.add(fragment, fragment.getClass().getSimpleName());
				ft.commit();
				/**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
				 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
				 */
				fragmentManager.executePendingTransactions();
			}

			if (fragment.getView().getParent() == null) {
				container.addView(fragment.getView()); // 为viewpager增加布局
			}

			return fragment.getView();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			 //container.removeView(list.get(position).getView()); //
			// 移出viewpager两边之外的page布局
			 Log.d(FragmentBase.TAG, "destroy item");
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		/**
		 * 当前page索引（切换之前）
		 * 
		 * @return
		 */
		public int getCurrentPageIndex() {
			return currentPageIndex;
		}

		public OnExtraPageChangeListener getOnExtraPageChangeListener() {
			return onExtraPageChangeListener;
		}

		/**
		 * 设置页面切换额外功能监听器
		 * 
		 * @param onExtraPageChangeListener
		 */
		public void setOnExtraPageChangeListener(
				OnExtraPageChangeListener onExtraPageChangeListener) {
			this.onExtraPageChangeListener = onExtraPageChangeListener;
		}


		public void onPageScrolled(int i, float v, int i2) {
			isScroll = true;
			//Log.d(FragmentBase.TAG, "page scrolled");
			if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
				onExtraPageChangeListener.onExtraPageScrolled(i, v, i2);
			}
		}

		public void onPageSelected(int i) {
			list.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
			// fragments.get(currentPageIndex).onStop(); //
			// 调用切换前Fargment的onStop()
			Log.d(FragmentBase.TAG, "page selected");
			if (list.get(i).isAdded()) {
				// fragments.get(i).onStart(); // 调用切换后Fargment的onStart()
				list.get(i).onResume(); // 调用切换后Fargment的onResume()
			}
			currentPageIndex = i;
			if (isScroll)
				scrollCursor(currentPageIndex);
			if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
				onExtraPageChangeListener.onExtraPageSelected(i);
			}

		}

		public void onPageScrollStateChanged(int i) {
			if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
				onExtraPageChangeListener.onExtraPageScrollStateChanged(i);
			}
		}

		/**
		 * page切换额外功能接口
		 */
	}

	static class OnExtraPageChangeListener {
		public void onExtraPageScrolled(int i, float v, int i2) {
		}

		public void onExtraPageSelected(int i) {
		}

		public void onExtraPageScrollStateChanged(int i) {
		}
	}
}
