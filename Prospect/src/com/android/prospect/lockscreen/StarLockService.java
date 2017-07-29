package com.android.prospect.lockscreen;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class StarLockService extends Service {

	private Intent mLaunchIntent;
	private KeyguardManager mKeyguardManager = null ;
	private KeyguardManager.KeyguardLock mKeyguardLock = null ;
	
	@Override
	public void onCreate(){
		super.onCreate();
		mLaunchIntent = new Intent(this, LockActivity.class);
		mLaunchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		registerComponent();
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}
	
	private BroadcastReceiver mLockScreenReceiver  = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals("android.intent.action.SCREEN_ON")
					/*||intent.getAction().equals("android.intent.action.SCREEN_OFF")*/){
				mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
				mKeyguardLock = mKeyguardManager.newKeyguardLock("FxLock");
				mKeyguardLock.disableKeyguard();
				startActivity(mLaunchIntent);
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
		startService(new Intent(StarLockService.this, StarLockService.class));
	}
}
