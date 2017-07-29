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
        PackageManager pm = this.getPackageManager(); //���PackageManager����
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // ͨ����ѯ���������ResolveInfo����.
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        // ����ϵͳ���� �� ����name����
        // ���������Ҫ������ֻ����ʾϵͳӦ�ã��������г�������Ӧ�ó���
        Collections.sort(resolveInfos,new ResolveInfo.DisplayNameComparator(pm));
            for (ResolveInfo reInfo : resolveInfos) {
                String activityName = reInfo.activityInfo.name; // ��ø�Ӧ�ó��������Activity��name
                String pkgName = reInfo.activityInfo.packageName; // ���Ӧ�ó���İ���
                String appLabel = (String) reInfo.loadLabel(pm); // ���Ӧ�ó����Label
                Drawable icon = reInfo.loadIcon(pm); // ���Ӧ�ó���ͼ��
                // ΪӦ�ó��������Activity ׼��Intent
                Intent launchIntent = new Intent();
                launchIntent.setComponent(new ComponentName(pkgName,
                        activityName));
                // ����һ��AppInfo���󣬲���ֵ

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
