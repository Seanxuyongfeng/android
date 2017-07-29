package com.android.prospect.lockscreen;

import java.util.Calendar;
import java.util.Date;

import com.android.prospect.Launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;

public class TimeAndDataManager {

    private final static String M12 = "h:mm";
    private final static String M24 = "kk:mm";
    
	private Context mContext;
	private Handler mHandler;
	private Calendar mCalendar;
	private String mTimeFormat;
	private String mDateFormat;
	public ContentObserver mTimeFormatChangeObserver;
	public BroadcastReceiver mIntentReceiver;
	
	public TimeAndDataManager(Context context, Handler handler){
		mContext = context;
		mHandler = handler;
		mDateFormat = new String("yyyy-MM-dd EEE");
		mCalendar = Calendar.getInstance();
		mTimeFormat = android.text.format.DateFormat.is24HourFormat(context)
	            ? M24 : M12;
		registerComponent();
		updateDate();
		updateTime();
	}
	
	private class TimeChangedReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(Intent.ACTION_SCREEN_ON)){
				mHandler.sendEmptyMessage(LockActivity.CHANGE_BACKGROUND);
			}else if(action.equals(Intent.ACTION_TIME_TICK) ||
					action.equals(Intent.ACTION_TIME_CHANGED) ||
					action.equals(Intent.ACTION_TIMEZONE_CHANGED)){
	            final boolean timezoneChanged =
	                    intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED);
	
	            if(timezoneChanged){
	            	mCalendar = Calendar.getInstance();
	            }
	            updateTime();
			}
            
		}
		
	}

	private class FormatChangeObserver extends ContentObserver{

		public FormatChangeObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		
		  @Override
	      public void onChange(boolean selfChange) {
			  mTimeFormat = android.text.format.DateFormat.is24HourFormat(mContext)
			            ? M24 : M12;
			  updateTime();
		  }
		
	}
	
	private void updateTime(){
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        CharSequence newTime = DateFormat.format(mTimeFormat, mCalendar);
        int ampm = mCalendar.get(Calendar.AM_PM);
        String week = String.valueOf(mCalendar.get(Calendar.DAY_OF_WEEK));
        Message msg = mHandler.obtainMessage();
        msg.what = LockActivity.UPDATE_TIME;
        Bundle data = new Bundle();
        data.putString("time", newTime.toString());
        data.putInt("ampm", ampm);
        data.putString("week", week);
        msg.setData(data);
        mHandler.sendMessage(msg);
        
	}
	
	private void updateDate(){
        Message msg = mHandler.obtainMessage();
        msg.what = LockActivity.UPDATE_DATE;
        Bundle data = new Bundle();
        String date = DateFormat.format(mDateFormat, new Date()).toString();
        data.putString("date", date);
        msg.setData(data);
        mHandler.sendMessage(msg);
	}
	
	private void registerComponent(){
		if(mIntentReceiver == null){
			mIntentReceiver = new TimeChangedReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
	        filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mIntentReceiver, filter);
			
		}
		
		if(mTimeFormatChangeObserver == null){
			mTimeFormatChangeObserver = new FormatChangeObserver(mHandler);
			mContext.getContentResolver().registerContentObserver(
					Settings.System.CONTENT_URI, true, 
					mTimeFormatChangeObserver);
		}
	}
	
	public void unregisterComponent(){
        if (mIntentReceiver != null) {
        	mContext.unregisterReceiver(mIntentReceiver);
        }
		
        if (mTimeFormatChangeObserver != null) {
        	mContext.getContentResolver().unregisterContentObserver(
        			mTimeFormatChangeObserver);
        }
        mTimeFormatChangeObserver = null;
        mIntentReceiver = null;
	}
	
}

