package com.android.prospect.disneywidget.service;



import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViewsService;

import com.android.prospect.disneywidget.remoteviewfactory.ShoppingFactory;


public class ShoppingService  extends RemoteViewsService { 
	private static final String TAG="MyWidgetService";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) { 
        
        return (RemoteViewsFactory) new ShoppingFactory(this.getApplicationContext(), intent); 
    } 
  
    @Override
    public void onCreate() { 
        // TODO Auto-generated method stub 
        Log.e(TAG, "MyWidgetService ...  onCreate");
    } 
      
   
	@Override
    public void onDestroy() { 
        // TODO Auto-generated method stub 
        Log.e(TAG, "MyWidgetService ...  onDestroy");
        super.onDestroy(); 
    } 
  
    @Override
    public boolean onUnbind(Intent intent) { 
        // TODO Auto-generated method stub 
        Log.e(TAG, "MyWidgetService ...  onUnbind");
        return super.onUnbind(intent); 
    } 
  
    
	@Override
    public void onRebind(Intent intent) { 
        // TODO Auto-generated method stub 
        Log.e(TAG, "MyWidgetService ...  onRebind");
        super.onRebind(intent);
    } 
  
   
	@Override
    public void onStart(Intent intent, int startId) { 
        // TODO Auto-generated method stub 
        Log.e(TAG, "MyWidgetService ...  onStart");
        super.onStart(intent, startId); 
    } 
  
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { 
        // TODO Auto-generated method stub 
        Log.e(TAG, "MyWidgetService ...  onStartCommand");
        return START_STICKY; 
    } 
    
    
     
     
   


}
