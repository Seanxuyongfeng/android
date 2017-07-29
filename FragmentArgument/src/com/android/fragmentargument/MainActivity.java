package com.android.fragmentargument;


import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.android.fragmentargument.R;

public class MainActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//queryAppInfo();
		//addPreferencesFromResource(R.layout.fragment_layout);
	}
	public void queryAppInfo() {
        PackageManager pm = this.getPackageManager(); //获得PackageManager对象
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 通过查询，获得所有ResolveInfo对象.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        // 调用系统排序 ， 根据name排序
        // 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
                String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
                Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
                // 为应用程序的启动Activity 准备Intent
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName,
                        activityName));
                // 创建一个AppInfo对象，并赋值

            }
    }
	
	@Override
	public void onBuildHeaders(List<Header> headers){
		loadHeadersFromResource(R.xml.my_headers, headers);
	}
	
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
    	super.onPreferenceTreeClick(preferenceScreen, preference);
    	return true;
    }
    
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref){
    	String fragment = pref.getFragment();
    	String key = pref.getKey();
    	Bundle bundle = pref.getExtras();
    	if(key.equals("one_key")){
    		//bundle.put
    	}else if(key.equals("two_key")){
    		
    	}else if(key.equals("thr_key")){
    		
    	}
    	return super.onPreferenceStartFragment(caller, pref);
    }

}
