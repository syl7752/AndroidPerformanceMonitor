package com.lenovo.inforeader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lenovo.info.util.Utils;

public abstract class FragmentBase extends Fragment implements OnClickListener {
	protected File logFile;
	protected TextView textView;
	protected Button getInfoBtn;
	protected StringBuffer sb;
	protected String info;
	protected int batteryLevel = -1, temperature = -1;
	public static final String TAG = "InfoReader";
	protected ProgressDialog progressDialog;
	protected FileOutputStream outputStream;
	protected Utils utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sb = new StringBuffer();
		utils=Utils.getInstance();
	}

	Handler handler = new Handler() {
		Bundle bundle = null;
		String content;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				bundle = msg.getData();
				content = bundle.getString("content");
				sb.append(content);
				textView.setText(sb.toString());
				textView.setTextColor(Color.BLACK);
				break;
			case 1:
				progressDialog = ProgressDialog.show(getActivity(), "提醒",
						"正在获取CPU信息...");
				break;
			case 2:
				progressDialog.dismiss();
				break;
			default:
				break;
			}

		};
	};

	protected void createLogFile(String fileName) {
		logFile = new File(getActivity().getFilesDir().getAbsolutePath()
				+ File.separatorChar + "InfoReader" + File.separatorChar
				+ fileName);
		try {
			if (!logFile.exists())
				logFile.createNewFile();
			outputStream = new FileOutputStream(logFile.getAbsolutePath(), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void writeFile(String content) {
		try {
			PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(
					outputStream, "UTF-8"));
			fileWriter.write(content);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onclick base");
		new GetInfoThread().start();
	}

	class GetInfoThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			getInfo();
			sendMessage(0, info);
			writeFile(info);
		}
	}

	protected abstract void getInfo();

	/*
	 * send Message to update UI
	 */
	protected void sendMessage(int what, String content) {
		Message msg = Message.obtain();
		msg.what = what;
		Bundle bundle = new Bundle();
		bundle.putString("content", content);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

}
