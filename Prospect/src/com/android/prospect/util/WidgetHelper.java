package com.android.prospect.util;

import java.io.Serializable;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.widget.RemoteViews;

import com.android.prospect.R;
import com.android.prospect.disneywidget.provider.FoodWidget;
import com.android.prospect.disneywidget.provider.MoviceWidget;
import com.android.prospect.disneywidget.provider.PictureWidget;
import com.android.prospect.disneywidget.provider.ShoppingWidget;

public class WidgetHelper implements Serializable {
	/**
	 * data refresh
	 * 
	 * @param context
	 */
	public static void notifyAppwidget(Context context) {

		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, FoodWidget.class);
		
		mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
				android.R.id.list);

	}

	/**
	 * wifi nerver sleep
	 * 
	 * @param mContext
	 */
	public static void setWifiEnable(Context mContext) {
		ContentResolver resolver = mContext.getContentResolver();
		int value = Settings.System.getInt(resolver,
				Settings.System.WIFI_SLEEP_POLICY,
				Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
		if (Settings.System.WIFI_SLEEP_POLICY_NEVER != value) {
			Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY,
					Settings.System.WIFI_SLEEP_POLICY_NEVER);
		}

	}

	/**
	 * failed in loading toast
	 * 
	 * @param mContext
	 * @param name
	 */
	public static void toastException(Context mContext, String name) {

		if (name.equals("foodwidget")) {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.food_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.VISIBLE);
			remoteViews.setTextViewText(R.id.text_start, mContext.getResources().getString(R.string.toast));
			FoodWidget.myappWidgetManager.updateAppWidget(
					FoodWidget.myappWidgetIds, remoteViews);
		} else if (name.equals("movicewidget")) {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.movie_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.VISIBLE);
			remoteViews.setTextViewText(R.id.text_start, mContext.getResources().getString(R.string.toast));
			MoviceWidget.myappWidgetManager.updateAppWidget(
					MoviceWidget.myappWidgetIds, remoteViews);
		} else if (name.equals("picturewidget")) {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.picture_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.VISIBLE);
			remoteViews.setTextViewText(R.id.text_start, mContext.getResources().getString(R.string.toast));
			PictureWidget.myappWidgetManager.updateAppWidget(
					PictureWidget.myappWidgetIds, remoteViews);
		} else {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.shopping_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.VISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.VISIBLE);
			remoteViews.setTextViewText(R.id.text_start, mContext.getResources().getString(R.string.toast));
			ShoppingWidget.myappWidgetManager.updateAppWidget(
					ShoppingWidget.myappWidgetIds, remoteViews);
		}

	}

	/**
	 * hide the Toast
	 * 
	 * @param mContext
	 * @param name
	 */
	public static void hideToast(Context mContext, String name) {
		if (name.equals("foodwidget")) {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.food_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.INVISIBLE);
			FoodWidget.myappWidgetManager.updateAppWidget(
					FoodWidget.myappWidgetIds, remoteViews);
		} else if (name.equals("movicewidget")) {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.movie_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.INVISIBLE);
			MoviceWidget.myappWidgetManager.updateAppWidget(
					MoviceWidget.myappWidgetIds, remoteViews);
		} else if (name.equals("picturewidget")) {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.picture_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.INVISIBLE);
			PictureWidget.myappWidgetManager.updateAppWidget(
					PictureWidget.myappWidgetIds, remoteViews);
		} else {
			RemoteViews remoteViews = new RemoteViews(
					mContext.getPackageName(), R.layout.shopping_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.INVISIBLE);
			ShoppingWidget.myappWidgetManager.updateAppWidget(
					ShoppingWidget.myappWidgetIds, remoteViews);
		}
	}
}
