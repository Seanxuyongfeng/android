package com.android.fragmentargument;


import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.Gravity;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.CompoundButton;

public class GesturesSettings extends PreferenceFragment implements CompoundButton.OnCheckedChangeListener{

	private static final String KEY_GESTURE_C = "gesture_c";
	private static final String KEY_GESTURE_E = "gesture_e";
	private static final String KEY_GESTURE_W = "gesture_w";
	private static final String KEY_GESTURE_O = "gesture_o";
	private static final String KEY_GESTURE_M = "gesture_m";
	private static final String KEY_GESTURE_S = "gesture_s";
	private static final String KEY_GESTURE_Z = "gesture_z";
	private static final String KEY_GESTURE_V = "gesture_v";
	
	private MySwitchPreference mGestureC;
	private MySwitchPreference mGestureE;
	private MySwitchPreference mGestureW;
	private MySwitchPreference mGestureO;
	private MySwitchPreference mGestureM;
	private MySwitchPreference mGestureS;
	private MySwitchPreference mGestureZ;
	private MySwitchPreference mGestureV;
	
	private Switch mEnabledSwitch;
	private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_main);
		
		//Read Settings
		mGestureC = (MySwitchPreference)findPreference(KEY_GESTURE_C);
		mGestureC.setChecked(false);
		mAllPrefs.add(mGestureC);
		mGestureE = (MySwitchPreference)findPreference(KEY_GESTURE_E);
		mAllPrefs.add(mGestureE);
		mGestureW = (MySwitchPreference)findPreference(KEY_GESTURE_W);
		mAllPrefs.add(mGestureW);
		mGestureO = (MySwitchPreference)findPreference(KEY_GESTURE_O);
		mAllPrefs.add(mGestureO);
		mGestureM = (MySwitchPreference)findPreference(KEY_GESTURE_M);
		mAllPrefs.add(mGestureM);
		mGestureS = (MySwitchPreference)findPreference(KEY_GESTURE_S);
		mAllPrefs.add(mGestureS);
		mGestureZ = (MySwitchPreference)findPreference(KEY_GESTURE_Z);
		mAllPrefs.add(mGestureZ);
		mGestureV = (MySwitchPreference)findPreference(KEY_GESTURE_V);
		mAllPrefs.add(mGestureV);		
		initialAllState();
	}
	
	private void initialAllState(){
		initSteate(mGestureC);
		initSteate(mGestureE);
		initSteate(mGestureW);
		initSteate(mGestureO);
		initSteate(mGestureM);
		initSteate(mGestureS);
		initSteate(mGestureZ);
		initSteate(mGestureC);
	}
	
	private void initSteate(MySwitchPreference preference){
		boolean checked = getState(preference.getKey());
		preference.setChecked(checked);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		final Activity activity = getActivity();
		mEnabledSwitch = (Switch)activity.getLayoutInflater().inflate(R.layout.imageswitch_layout, null);
		boolean enabled = getState("gestures_enabled");
		mEnabledSwitch.setEnabled(enabled);
		mEnabledSwitch.setOnCheckedChangeListener(this);
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
			ListView lv = (ListView) getView().findViewById(android.R.id.list);
		    if(lv != null){
		    	lv.setPadding(0, 0, 0, 0);
		    }
		}
		initialAllState();
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		final Activity activity = getActivity();
		activity.getActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
		activity.getActionBar().setCustomView(null);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if(buttonView == mEnabledSwitch){

			if(isChecked){
				
			}else{
				setPrefsEnabledState(isChecked);
			}
		}
	}
	
	private void setPrefsEnabledState(boolean enabled){
		
		for(int i = 0; i < mAllPrefs.size(); i++){
			Preference pref = mAllPrefs.get(i);
			pref.setEnabled(enabled);
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
