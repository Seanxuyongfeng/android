package com.android.fragmentargument;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.android.fragmentargument.GifHelper.GifFrame;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class InfoFragment extends PreferenceFragment implements CompoundButton.OnCheckedChangeListener{

	private Switch mWitch;
	private TextView mLabel;
	private ImageView mIcon;
	private ImageView mGifView;
	private WebView mWebView;

	private String mPackageName;
	private String mClassName;
	private String mPath;
	
	private ComponentName mComponentName;
	private PackageManager mPackageManager;
	
	private Switch mEnabledSwitch;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle a){
		super.onActivityCreated(a);
		mPackageManager = getActivity().getPackageManager();
		final Activity activity = getActivity();
		mEnabledSwitch = (Switch)activity.getLayoutInflater().inflate(R.layout.imageswitch_layout, null);
		mEnabledSwitch.setOnCheckedChangeListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		    
		final View view = inflater.inflate(R.layout.info_fragment, container, false);
		mIcon = (ImageView) view.findViewById(R.id.application_icon);
		mLabel = (TextView) view.findViewById(R.id.application_label);
		mGifView = (ImageView) view.findViewById(R.id.gif_view);
		mWebView = (WebView) view.findViewById(R.id.webView);
		return view;
	}
	
	@Override
	public void onStart(){
		super.onStart();
		final Activity activity = getActivity();
		activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
				ActionBar.DISPLAY_SHOW_CUSTOM);
		activity.getActionBar().setCustomView(mEnabledSwitch, new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT, 
				ActionBar.LayoutParams.WRAP_CONTENT, 
				Gravity.CENTER_VERTICAL|Gravity.END));
	}
	
	@Override
	public void onResume(){
		super.onResume();
		View root = getView();
		if(root != null){
			ViewGroup parent = (ViewGroup)root.getParent();
		    if(parent != null){
		    	parent.setPadding(0, 0, 0, 0);
		    }
		}
		Bundle bundle = getArguments();
		mPackageName = bundle.getString("packageName");
		mClassName = bundle.getString("className");
		if(mPackageName != null && mClassName != null){
			
			mComponentName = new ComponentName(mPackageName, mClassName);
			Intent intent = new Intent();
			intent.setComponent(mComponentName);
			List<ResolveInfo> resolveInfos = mPackageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			if(resolveInfos != null && resolveInfos.size() > 0){
				ResolveInfo info = resolveInfos.get(0);
				String appLabel = (String) info.loadLabel(mPackageManager);
				Drawable icon = info.loadIcon(mPackageManager);
				if(mIcon != null){
					mIcon.setImageDrawable(icon);
				}
				if(mLabel != null){
					mLabel.setText(appLabel);
				}
			}
			
		}
		mPath = bundle.getString("path");
		if(mPath != null && (!mPath.equals("")) && mWebView != null){
		    mWebView.loadUrl("file:///android_asset/"+mPath);
		}
		
		boolean checked = getState(bundle.getString("key"));
		mEnabledSwitch.setChecked(checked);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		final Activity activity = getActivity();
		activity.getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
		activity.getActionBar().setCustomView(null);
	}	
	
	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(buttonView == mEnabledSwitch){
			if(isChecked){
				saveState(isChecked);
			}else{
				//
				saveState(isChecked);
			}
		}
	}
	
	public void saveState(boolean enabled){
		//save data
	}
	
	public boolean getState(String key){
		//read data
		return true;
	}
	
	
}
