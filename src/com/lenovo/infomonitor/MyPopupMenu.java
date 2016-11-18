package com.lenovo.infomonitor;

import java.util.ArrayList;



import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Custom popmenu
 * @author shiyl
 * 2014-9-4
 */
public class MyPopupMenu extends Button implements OnClickListener {
	private PopupWindow mPopupWindow;
	private Context context;
	private ArrayList<PopupItem> listItems;
	private ListAdapter listAdapter;
	private View settingView;
	private PopupMenuListener mPopupMenuListener;

	public MyPopupMenu(Context context) {
		super(context);
		this.context = context;
		listItems = new ArrayList<MyPopupMenu.PopupItem>();
		setBackgroundResource(R.drawable.menupic);
		setTextAppearance(getContext(), android.R.style.Widget_Spinner);
		setOnClickListener(this);
	}
	public void setPopupMenuListener(PopupMenuListener mPopupMenuListener)
	{
		this.mPopupMenuListener=mPopupMenuListener;
	}
	public void show() {
		if (mPopupWindow == null) {
			View view = View.inflate(context, R.layout.popupmenu, null);
			ListView listView = (ListView) view.findViewById(R.id.listview);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					// TODO Auto-generated method stub
					dismiss();
					mPopupMenuListener.onClickMenuItem((int)id);
				}

			});
			view.setFocusableInTouchMode(true);
			view.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == KeyEvent.ACTION_UP
							&& keyCode == KeyEvent.KEYCODE_MENU)
					{
					if(isShowing())
						dismiss();
					else
						show();
					return true;
					}
					return false;
				}
			});
			listAdapter = new ListAdapter();
			listView.setAdapter(listAdapter);
			mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			mPopupWindow.setOutsideTouchable(true);
			mPopupWindow.setFocusable(true);
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(
					Color.TRANSPARENT));
		}
		mPopupWindow.setWidth(getPopupWindowWidth());
		mPopupWindow.showAsDropDown(this,
				Math.abs(this.getWidth() - mPopupWindow.getWidth()) / 2,
				0);
	}

	public void dismiss() {
		if (mPopupWindow != null)
			mPopupWindow.dismiss();
	}
    public boolean isShowing()
    {
    	if(mPopupWindow!=null)
    		return mPopupWindow.isShowing();
    	return false;
    	
    }
    protected void setItems(ArrayList<PopupItem> items) {
		listItems.clear();
		if(items != null && items.size() > 0) {
			listItems.addAll(items);
		}
		if(listAdapter != null) {
			listAdapter.notifyDataSetChanged();
		}
	}
    public void updateItems(int id,String name)
    {
    	if(!listItems.isEmpty())
    	{
    		for (PopupItem item:listItems) {
				if(item.id==id)
				{
					item.name=name;
					break;
				}
				
			}
    	}
    	if(listAdapter != null) {
			listAdapter.notifyDataSetChanged();
		}
    }
	private int getPopupWindowWidth() {
		View view = View.inflate(context, R.layout.popupitem, null);
		TextView textView = (TextView)view.findViewById(R.id.text);
		Paint paint = textView.getPaint();
		int width = 0;
		for(PopupItem item : listItems) {
			int w = (int)paint.measureText(item.name);
			width = width >= w ? width : w;
		}
		return width + (view.getPaddingLeft() + view.getPaddingRight()) * 2;
	}
	public static class PopupItem {
		public String name;
		public int id;
		public PopupItem(String name,int id)
		{
			this.name=name;
			this.id=id;
		}
	}

	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listItems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return ((PopupItem) listItems.get(position)).id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			
			PopupItem item = listItems.get(position);
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.popupitem, null);
				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView
						.findViewById(R.id.text);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.textView.setText(item.name);
			return convertView;
		}

		private class ViewHolder {
			TextView textView;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		show();
	}
    public interface PopupMenuListener
    {
    	public void onClickMenuItem(int id);
    }
}
