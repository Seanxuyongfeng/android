package com.android.prospect.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StandardBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		 Intent service = new Intent();
		 service.setClass(context, StarLockService.class);
		 context.startService(service);
	}

}
