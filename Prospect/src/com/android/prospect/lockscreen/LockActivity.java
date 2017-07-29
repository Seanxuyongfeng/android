package com.android.prospect.lockscreen;

import java.util.Random;

import com.android.prospect.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class LockActivity extends Activity {
    private static final String TAG = "LockActivity";
    
	public static final int MSG_LAUNCH_SMS = 0;
	public static final int MSG_LAUNCH_PHONE = 1;
	public static final int MSG_LAUNCH_CAMERA = 2;
	public static final int MSG_LAUNCH_MUSIC = 3;
	public static final int MSG_UNLOCK = 4;
	public static final int UPDATE_TIME = 5;
	public static final int UPDATE_DATE = 6;
	public static final int CHANGE_BACKGROUND = 7;
	private LockStarView mLockView;
	private PanningView mPanningView;
	private TimeAndDataManager mTimeAndDataManager;
	
	private ImageView mHour_left;
	private ImageView mHour_right;
	private ImageView mMinute_left;
	private ImageView mMinute_right;
	private ImageView mAmPm;
	private ImageView mDayOfWeek;
	private TextView mDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				   WindowManager.LayoutParams.FLAG_FULLSCREEN);	
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);  
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); 
		setContentView(R.layout.lock_main);
		registerComponent();
		mHour_left = (ImageView)findViewById(R.id.hour1);
		mHour_right = (ImageView)findViewById(R.id.hour2);
		mMinute_left = (ImageView) findViewById(R.id.minute1);
		mMinute_right = (ImageView)findViewById(R.id.minute2);
		mAmPm = (ImageView)findViewById(R.id.am_pm);
		mDayOfWeek = (ImageView)findViewById(R.id.days);
		mDate = (TextView)findViewById(R.id.date);
		mPanningView = (PanningView)findViewById(R.id.panningView);
		mTimeAndDataManager = new TimeAndDataManager(this, mHandler);
		mLockView = (LockStarView)findViewById(R.id.starView);
		mLockView.setHandler(mHandler);
		changeBackground();
	}


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
        //changeBackground();
    }	
	
    @Override
    public void onResume(){
    	super.onResume();
    	//changeBackground();
    }
    
    @Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		//this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}
    
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_LAUNCH_SMS:
				finish();
				launchSms();
				break;
			case MSG_LAUNCH_PHONE:
				finish();
				launchDial();
				break;
			case MSG_LAUNCH_CAMERA:
				finish();
				launchCamera();
				break;
			case MSG_LAUNCH_MUSIC:
				finish();
				launcherMusic();
				break;
			case MSG_UNLOCK:
				finish();
			case UPDATE_TIME:
				updateTime(msg);
				break;
			case CHANGE_BACKGROUND:
				//changeBackground();
				break;
			case UPDATE_DATE:
				if(mDate != null){
					Bundle data = msg.getData();
					if(data != null){
						String time = data.getString("date");
						mDate.setText(time);
					}
				}
				break;
			}
		}
	};
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTimeAndDataManager.unregisterComponent();
	}
	
    private void launchSms() {
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.android.mms",
				"com.android.mms.ui.ConversationList");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);
	}	
    
    private void launchDial() {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);
	}    
    
    private void launchCamera() {/*
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.android.gallery3d",
				"com.android.gallery3d.CameraActivity");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		startActivity(intent);*/
    	Intent intent = new Intent("android.media.action.STILL_IMAGE_CAMERA");
		startActivity(intent);    	
	}
    
    private void launcherMusic(){
    	
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("cmccwm.mobilemusic",
				"cmccwm.mobilemusic.ui.activity.PreSplashActivityMigu");
		intent.setComponent(comp);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	//Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
		startActivity(intent);   	
    }
    
    private static Random mRandom = new Random();
    private static final int[] mRandomBackground = {
		R.drawable.lock_background1, 
		R.drawable.lock_background2,
		R.drawable.lock_background3
    };
    
    public static int getRandomBackground(){
    	return mRandomBackground[mRandom.nextInt(3)];
    }
    
    private synchronized void changeBackground(){
        if(mPanningView != null){
		mPanningView.stopPanning();
        	mPanningView.setImageResource(getRandomBackground());
        	mPanningView.startPanning();
        }
    }
    
    private void updateTime(Message msg){
    	Bundle data = msg.getData();
    	String time = data.getString("time");
    	if(time == null){
    		return ;
    	}
    	int ampm = data.getInt("ampm");
    	String week = data.getString("week");
        String[] s = time.split(":");
        int hour_sys = Integer.parseInt(s[0]);
        int hour_1 = hour_sys/10;
        int hour_2 = hour_sys%10;    	
        int min_sys = Integer.parseInt(s[1]);
        int min_1 = min_sys/10;
        int min_2 = min_sys%10;  
        mHour_left.setImageResource(getHourDrawable(hour_1));
        mHour_right.setImageResource(getHourDrawable(hour_2));        
        mMinute_left.setImageResource(getMinitesDrawable(min_1));
        mMinute_right.setImageResource(getMinitesDrawable(min_2));
        mDayOfWeek.setImageResource(getDayOfWeek(week));
        if(ampm == 0){
        	mAmPm.setImageResource(R.drawable.am);
        }else{
        	mAmPm.setImageResource(R.drawable.pm);
        }
        //changeBackground();
    }
    
	public int getDayOfWeek(String dayOfWeek){
		int img = 0;
		if("1".equals(dayOfWeek)){  
			img = R.drawable.mon;
        }else if("2".equals(dayOfWeek)){  
            img = R.drawable.tues;  
        }else if("3".equals(dayOfWeek)){  
            img = R.drawable.wed; 
        }else if("4".equals(dayOfWeek)){  
            img = R.drawable.thur;
        }else if("5".equals(dayOfWeek)){  
            img = R.drawable.fri;
        }else if("6".equals(dayOfWeek)){  
            img = R.drawable.sat;
        }else if("7".equals(dayOfWeek)){  
            img = R.drawable.sun;
        }  
		return img;
	}
	public int getMinitesDrawable(int number){
		int img = 0;
		switch(number){
		case 0:
			img = R.drawable.minute_0;
			break;
		case 1:
			img = R.drawable.minute_1;
			break;
		case 2:
			img = R.drawable.minute_2;
			break;
		case 3:
			img = R.drawable.minute_3;
			break;
		case 4:
			img = R.drawable.minute_4;
			break;
		case 5:
			img = R.drawable.minute_5;
			break;
		case 6:
			img = R.drawable.minute_6;
			break;
		case 7:
			img = R.drawable.minute_7;
			break;
		case 8:
			img = R.drawable.minute_8;
			break;
		case 9:
			img = R.drawable.minute_9;
			break;
		}
		return img;			
	}
	
	public int getHourDrawable(int number){
		int img = 0;
		switch(number){
		case 0:
			img = R.drawable.hour_0;
			break;
		case 1:
			img = R.drawable.hour_1;
			break;
		case 2:
			img = R.drawable.hour_2;
			break;
		case 3:
			img = R.drawable.hour_3;
			break;
		case 4:
			img = R.drawable.hour_4;
			break;
		case 5:
			img = R.drawable.hour_5;
			break;
		case 6:
			img = R.drawable.hour_6;
			break;
		case 7:
			img = R.drawable.hour_7;
			break;
		case 8:
			img = R.drawable.hour_8;
			break;
		case 9:
			img = R.drawable.hour_9;
			break;
		}
		return img;
	}    
	
	   @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return disableKeycode(keyCode, event);
		}
	    
	    private boolean disableKeycode(int keyCode, KeyEvent event){
	    	int key = event.getKeyCode();
	    	switch (key)
	    	{
			case KeyEvent.KEYCODE_BACK:		
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			case KeyEvent.KEYCODE_VOLUME_UP:
				return true;			
			}
	    	return super.onKeyDown(keyCode, event);
	    }
		
	private BroadcastReceiver mLockScreenReceiver  = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals("android.intent.action.SCREEN_ON")){
					if(mPanningView != null){
						mPanningView.startPanning();
					}
				}else if(intent.getAction().equals("android.intent.action.SCREEN_OFF")){
			        if(mPanningView != null){
			        	mPanningView.stopPanning();
			            mPanningView.setImageResource(getRandomBackground());
			        }			
			    }

			}
			
		};
		
		private void registerComponent(){
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.SCREEN_ON");
			filter.addAction("android.intent.action.SCREEN_OFF");
			registerReceiver(mLockScreenReceiver, filter);
		}	
		private void unregisterComponent(){
			if(mLockScreenReceiver != null){
				unregisterReceiver(mLockScreenReceiver);
			}
		}
		
		@Override
		public void onDestroy(){
			super.onDestroy();
			unregisterComponent();
		}
	}

