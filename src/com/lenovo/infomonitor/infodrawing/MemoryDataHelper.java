package com.lenovo.infomonitor.infodrawing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.lenovo.info.util.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 建立数据库，用于存放记录的内存数据
 * @author shiyl
 * 2014-9-15
 */
public class MemoryDataHelper extends SQLiteOpenHelper{
    private static final String DB_NAME="mymemdata.db";
    private static final int version = 1;
    private SQLiteDatabase mydb;
    private static MemoryDataHelper memoryDataHelper;
	public MemoryDataHelper(Context context) {
		super(context, DB_NAME, null, version);
		// TODO Auto-generated constructor stub
	}
    public static MemoryDataHelper init(Context context)
    {
    	if(memoryDataHelper==null)
    		memoryDataHelper=new MemoryDataHelper(context);
    	return memoryDataHelper;
    }
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table memdata(memtime INTEGER,memvalue INTEGER);";          
        db.execSQL(sql);
        mydb=db;
        Log.d(Utils.DEBUG_TAG, "sqlite oncreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 插入数据
	 * @author shiyl
	 * 2014-9-15
	 */
	public void insert(Date date,int value)
	{
		
		ContentValues cv=new ContentValues();
		if(date==null)
		cv.put("memtime", new Date().getTime());
		else
			cv.put("memtime", date.getTime());
		cv.put("memvalue", value);
		getWritableDatabase().insert("memdata", null, cv);
	}
	/**
	 * 删除表中所有数据
	 * @author shiyl
	 * 2014-9-15
	 */
	public void removeAll()
	{
		getWritableDatabase().delete("memdata", null, null);
	}
	/**
	 * 查询表中数据 返回时间和内存的list
	 * @author shiyl
	 * 2014-9-15
	 */
    public ArrayList<ArrayList> query()
    {
    	Cursor c=getReadableDatabase().query("memdata", null, null, null, null, null, null);
    	ArrayList<Date> dateList=new ArrayList<Date>();
    	ArrayList<Integer> memList=new ArrayList<Integer>();
    	while(c.moveToNext())
    	{
    		long time=c.getLong(c.getColumnIndex("memtime"));
    		int free=c.getInt(c.getColumnIndex("memvalue"));
    		Date date=new Date(time);
    		dateList.add(date);
    		memList.add(free);
    	}
    	ArrayList<ArrayList> list=new ArrayList<ArrayList>();
    	list.add(dateList);
    	list.add(memList);
    	return list;
    }
    /**
     * 获取表的长度
     * @author shiyl
     * 2014-9-15
     */
    public int getSize()
    {
    	Cursor c=getReadableDatabase().query("memdata", null, null, null, null, null, null);
    	return c.getCount();
    }
}
