package com.android.fragmentargument;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Switch;

public class MyPreference extends Preference {

	private CharSequence mPackageName;
	private int mResId;
	
	public MyPreference(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public MyPreference(Context context, AttributeSet attrs){
		super(context, attrs, 0);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GesturesSettings);
        mPackageName = a.getString(R.styleable.GesturesSettings_packageName);
        mResId = a.getResourceId(R.styleable.GesturesSettings_path, 0);
        a.recycle();
        
        
        Activity activity = (Activity)context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Switch actionBarSwitch  = (Switch)inflater.inflate(R.layout.imageswitch_layout, null);
        if(activity instanceof PreferenceActivity){
        	activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, 
        			ActionBar.DISPLAY_SHOW_CUSTOM);
        	activity.getActionBar().setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
        			ActionBar.LayoutParams.WRAP_CONTENT, 
        			ActionBar.LayoutParams.WRAP_CONTENT, 
        			Gravity.CENTER_VERTICAL | Gravity.END));
        }
	}

}
