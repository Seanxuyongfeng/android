package com.android.prospect.disneywidget.provider;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;
import android.content.ComponentName;
import android.content.SharedPreferences;

import com.android.prospect.WebViewActivity;
import com.android.prospect.R;
import com.android.prospect.disneywidget.service.PictureService;
import com.android.prospect.util.WidgetHelper;
import android.os.Bundle;

public class PictureWidget extends AppWidgetProvider {
	private final static String TAG = "hellowidget";

	public JSONArray jsonArray;
	private LayoutInflater mInflater;
	public String imgPath[];
        public SharedPreferences picturePreferences;
        public SharedPreferences.Editor  pictureEditor;

	public static RemoteViews views = null;
	public static AppWidgetManager myappWidgetManager = null;
	public static int[] myappWidgetIds = null;

	public static String REFRESH_ACTION = "com.mospec.widgetjson.hellowidget.REFRESH";
	public static String AUTO_REFRESH_ACTION = "com.mospec.widgetjson.hellowidget.AUTO_REFRESH";
	public static String ReFRESHBY24 = "android.net.conn.CONNECTIVITY_CHANGE";
	public static String APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
        public static String USER_PRESENT="android.intent.action.USER_PRESENT";
        public static String DATE_CHANGED="android.intent.action.DATE_CHANGED";
        public static String TIME_CHANGED="android.intent.action.TIME_CHANGED";
	 public static String LOCAL_CHANGED="android.intent.action.LOCALE_CHANGED";
        public static String DISNEY_WIDGET="com.mospec.txz.DISNEY_WIDGET";
	public static long lastRefreshTime = System.currentTimeMillis();

       

	@Override
	public void onUpdate(final Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		if (appWidgetIds == null) {
			ComponentName compName = new ComponentName(context,
					PictureWidget.class);
			appWidgetIds = appWidgetManager.getAppWidgetIds(compName);
		}
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.picture_main);
		myappWidgetManager = appWidgetManager;
		myappWidgetIds = appWidgetIds;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// for listview set adapter
		Intent intent = new Intent(context, PictureService.class);
		views.setRemoteAdapter(android.R.id.list, intent);

		// redriector
		Intent intent1 = new Intent();
		intent1.setAction("android.intent.action.VIEW");


		PendingIntent pendingIntentTemplate = PendingIntent.getActivity(
				context, 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setPendingIntentTemplate(android.R.id.list, pendingIntentTemplate);

		// refresh by myself
		Intent refreshIntent = new Intent(context, PictureWidget.class);
		refreshIntent.setAction(PictureWidget.REFRESH_ACTION);
		PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(
				context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.btn_re, refreshPendingIntent);

		// refresh auto
		AlarmManager alarmManager = (AlarmManager) context
		 .getSystemService(Context.ALARM_SERVICE); Intent auto_intent = new
		 Intent(context,PictureWidget.class);
		 auto_intent.setAction(PictureWidget.AUTO_REFRESH_ACTION);
		 PendingIntent refreshpend = PendingIntent.getBroadcast(context, 0,
		 auto_intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 alarmManager.setRepeating(alarmManager.RTC, 0, 1000*60*60*24*3,
		 refreshpend);
		 

		appWidgetManager.updateAppWidget(appWidgetIds, views);

		Log.i(TAG, " HelloWidget onupdate method... end");

	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);

	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		System.gc();
		
                picturePreferences=context.getSharedPreferences("picturewidget",0);
                pictureEditor=picturePreferences.edit();
	    
		WidgetHelper.setWifiEnable(context);
		views = new RemoteViews(context.getPackageName(),
				R.layout.picture_main);
		NetworkInfo network = ((ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		if (intent.getAction() == REFRESH_ACTION) {
                         pictureEditor.putBoolean("pictureBoolean",true).commit();

			if (network == null || !network.isAvailable()||!network.isConnected()) {
				Toast.makeText(context, R.string.network_fail,
						Toast.LENGTH_SHORT).show();
				notifyAppwidget(context);
			} else {
				notifyAppwidget(context);
				pictureEditor.putLong("pictureTime",System.currentTimeMillis()).commit();
			}
		} else if (intent.getAction() == AUTO_REFRESH_ACTION) {
                        long result=picturePreferences.getLong("pictureTime",System.currentTimeMillis());
			if (network != null && network.isAvailable()&&network.isConnected()) {
				if ((System.currentTimeMillis()-result) > 259200000) {
                                pictureEditor.putBoolean("pictureBoolean",true).commit();
				pictureEditor.putLong("pictureTime",System.currentTimeMillis()).commit();
                                }
			}
			notifyAppwidget(context);
                        
		} else if (intent.getAction() == ReFRESHBY24) {
                       
			if (network != null && network.isAvailable()&&network.isConnected()) {
				long result=picturePreferences.getLong("pictureTime",System.currentTimeMillis());
				if ((System.currentTimeMillis()-result) > 259200000) {
                                        pictureEditor.putBoolean("pictureBoolean",true).commit();
					pictureEditor.putLong("pictureTime",System.currentTimeMillis()).commit();
				}
			}else{
                                pictureEditor.putBoolean("pictureBoolean",false).commit();                 
                        }
		} else if (intent.getAction() == APPWIDGET_UPDATE) {
                        long result=picturePreferences.getLong("pictureTime",System.currentTimeMillis());
                        Log.i("tanxizhuo","System.currentTimeMillis() - result:"+(System.currentTimeMillis() - result));
                        if ((System.currentTimeMillis() - result) < 259200000) {
                               pictureEditor.putBoolean("pictureBoolean",false).commit();
                         }else{
                               pictureEditor.putBoolean("pictureBoolean",true).commit();
                               pictureEditor.putLong("pictureTime",System.currentTimeMillis()).commit();
                         }
                        bootRefresh(context);
		}else if(intent.getAction()==USER_PRESENT){
                               pictureEditor.putBoolean("pictureBoolean",false).commit();
                               bootRefresh(context);
                }else if(intent.getAction()==DATE_CHANGED||intent.getAction()==TIME_CHANGED){
                                pictureEditor.putBoolean("pictureBoolean",false).commit();
                                pictureEditor.putLong("pictureTime",System.currentTimeMillis()).commit();
                }else if(intent.getAction()==LOCAL_CHANGED){
                               pictureEditor.putBoolean("pictureBoolean",false).commit();
                               try{
                                 Thread.sleep(4000);
                                }catch(Exception e){
                                }
                                
                                bootRefresh(context);
                }else if(intent.getAction()==DISNEY_WIDGET){
                               pictureEditor.putBoolean("pictureBoolean",false).commit();
                                bootRefresh(context);
                }
		
		
	}

	private void bootRefresh(Context context) {
		// TODO Auto-generated method stub
		views.setViewVisibility(R.id.text_start, View.VISIBLE);
		views.setTextViewText(R.id.text_start, context.getResources()
				.getString(R.string.toast));
		views.setViewVisibility(R.id.progress_bar, View.VISIBLE);
		ComponentName compName = new ComponentName(context,
				PictureWidget.class);
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		int[] appWidgetIds = mgr.getAppWidgetIds(compName);
		onUpdate(context, mgr, appWidgetIds);
               
	}

	/**
	 * refresh data
	 * 
	 * @param context
	 */
	private void notifyAppwidget(Context context) {
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, PictureWidget.class);
		mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
				android.R.id.list);
	}
  
}
