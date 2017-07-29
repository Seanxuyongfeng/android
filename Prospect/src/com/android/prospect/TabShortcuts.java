package com.android.prospect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.android.prospect.R;

public class TabShortcuts extends LinearLayout implements View.OnClickListener{
	
	private View contacts_shortcuts;
	
	private View settings_shortcuts;
	
	private View bookmark_shortcuts;
	
	private View music_shortcuts;
	
	private View call_shortcuts;
	
	private View sms_shortcuts;
	
	private Launcher mLauncher;
	
    private int[] mTargetCell = new int[2];
    
    public TabShortcuts(Context context) {
        this(context, null);
    }

    public TabShortcuts(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabShortcuts(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
    	contacts_shortcuts = findViewById(R.id.contacts_shortcuts);
    	settings_shortcuts = findViewById(R.id.settings_shortcuts);
    	bookmark_shortcuts = findViewById(R.id.bookmark_shortcuts);
    	music_shortcuts = findViewById(R.id.music_shortcuts);
    	call_shortcuts = findViewById(R.id.call_shortcuts);
    	sms_shortcuts = findViewById(R.id.sms_shortcuts);
    	
    	contacts_shortcuts.setOnClickListener(this);
    	settings_shortcuts.setOnClickListener(this);
    	bookmark_shortcuts.setOnClickListener(this);
    	music_shortcuts.setOnClickListener(this);
    	call_shortcuts.setOnClickListener(this);
    	sms_shortcuts.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.contacts_shortcuts:
			processShortcut("com.android.contacts", "com.android.contacts.ContactShortcut");
			break;
		case R.id.settings_shortcuts:
			processShortcut("com.android.settings", "com.android.settings.CreateShortcut");
			break;
		case R.id.bookmark_shortcuts:
			processShortcut("com.android.browser", "com.android.browser.ShortcutActivity");
			break;
		case R.id.music_shortcuts:
			processShortcut("com.android.music", "com.android.music.PlaylistShortcutActivity");
			break;
		case R.id.call_shortcuts:
			processShortcut("com.android.contacts", "alias.DialShortcut");
			break;
		case R.id.sms_shortcuts:
			processShortcut("com.android.contacts", "alias.MessageShortcut");
			break;
		}
	}
	
	public void setup(Launcher launcher){
		mLauncher = launcher;
	}

	private void processShortcut(String pkg, String cls){
		if(pkg == null || cls == null){
			return;
		}
		//
		ComponentName componentName = new ComponentName(pkg, cls);
        if(mLauncher != null){
        	mLauncher.processShortcutFromDrop(componentName, 
        			LauncherSettings.Favorites.CONTAINER_DESKTOP, 
        			mLauncher.getCurrentWorkspaceScreen(), null, null);
        }
	}
}
