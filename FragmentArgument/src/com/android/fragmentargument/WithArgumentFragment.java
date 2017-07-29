package com.android.fragmentargument;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class WithArgumentFragment extends PreferenceFragment {
	
	private CharSequence mPackageName;
	private int mResId;
	
	@Override
	public void onCreate(Bundle d){
		super.onCreate(d);
		addPreferencesFromResource(R.layout.activity_main);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
            Bundle savedInstanceState){
		super.onInflate(activity, attrs, savedInstanceState);

        TypedArray a = activity.obtainStyledAttributes(attrs,
                R.styleable.WithArgumentFragment);
        mPackageName = a.getString(R.styleable.WithArgumentFragment_packageName);
        mResId = a.getResourceId(R.styleable.WithArgumentFragment_path, 0);
        a.recycle();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		View root = getView();

		
		if(root != null){
			ListView lv = (ListView) getView().findViewById(android.R.id.list);
			ViewGroup parent = (ViewGroup)root.getParent();
		    if(lv != null){
		    	lv.setPadding(0, 0, 0, 0);
		    }
		}
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}	

}
