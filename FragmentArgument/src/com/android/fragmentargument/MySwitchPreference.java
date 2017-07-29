package com.android.fragmentargument;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.android.fragmentargument.R;


public class MySwitchPreference extends Preference {
	private String mPackageName;
	private String mClassName;
	private String mResId;
	
	private boolean mChecked = true;
	private CompoundButton.OnCheckedChangeListener mSwitchChangeListener = new Listener();
	
	private class Listener implements CompoundButton.OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			//setProvider(getk(), isChecked);
			saveState(isChecked);
			setChecked(isChecked);
			
		}
		
	};
	
	public MySwitchPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setLayoutResource(R.xml.my_switch_preference);
		init(context, attrs);
	}	
	
	public MySwitchPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setLayoutResource(R.xml.my_switch_preference);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GesturesSettings);
        mPackageName = a.getString(R.styleable.GesturesSettings_packageName);
        mClassName = a.getString(R.styleable.GesturesSettings_className);
        mResId = a.getString(R.styleable.GesturesSettings_path);
        a.recycle();
        
        Bundle bundle = getExtras();
        bundle.putString("packageName", mPackageName);
        bundle.putString("className", mClassName);
        bundle.putString("path",  mResId);
        bundle.putString("key", getKey());
       
	}
	
	@Override
	protected void onBindView(View view){
		super.onBindView(view);
		Switch switchbutton = (Switch)view.findViewById(R.id.switch_);
		if(mSwitchChangeListener != null && switchbutton != null){
			switchbutton.setClickable(true);
			switchbutton.setChecked(mChecked);
			switchbutton.setOnCheckedChangeListener(mSwitchChangeListener);
		}
	}
	
	public void setChecked(boolean checked){
		mChecked = checked;
		notifyChanged();
	}
	
	public boolean isChecked(){
		return mChecked;
	}
	
	public void saveState(boolean enabled){
		//save data
	}
	/*
	@Override
	public void performClick(PreferenceScreen preferenceScreen){
		super.performClick(preferenceScreen);
	}*/
}
