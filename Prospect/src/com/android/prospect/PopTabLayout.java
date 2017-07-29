package com.android.prospect;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.ComponentName;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import com.android.prospect.R;

public class PopTabLayout extends RelativeLayout implements OnClickListener{
	
	private static final int sTransitionInDuration = 200;
	
	private static final int mTabHeight = 152;
	
        private Launcher mLauncher;
    
	private TabShortcuts layout_shortcuts;
	
	private TabWallpapers layout_wallpaper;
	
	private View item_applications;
	
	private View item_shortcuts;
	
	private View item_wallpaper;
	
	private View item_widgets;
	
	private View current_tab;
	
	private View mTab;
	
	private View mContent;
	
	private ObjectAnimator mTabAnimation;
	
	private ObjectAnimator mContentAnimation;
	
    private TabWidgetsPagedView mTabWidgetsContent;
    
    private TabAppsPagedView mTabAppsContent;
	
	private boolean mIsTabBarHidden = true;
	 
    private static final AccelerateInterpolator sAccelerateInterpolator =
            new AccelerateInterpolator();
    
    public PopTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTab = findViewById(R.id.tab);
        mContent = findViewById(R.id.content);
        mTab.setTranslationY(-mTabHeight);
        //create animation for the view
        if(mTabAnimation != null){
        	mTabAnimation.cancel();
        }
        if(mContentAnimation != null){
        	mContentAnimation.cancel();
        }
        mTabAnimation = ObjectAnimator.ofFloat(mTab, "translationY", 0, mTabHeight);
        mContentAnimation = ObjectAnimator.ofFloat(mContent, "alpha", 1f, 0f);
        setupAnimation(mTabAnimation, mTab);
        setupAnimation(mContentAnimation, mContent);
        //find the items of the groups
		layout_shortcuts = (TabShortcuts)findViewById(R.id.layout_shortcuts);
		layout_wallpaper = (TabWallpapers)findViewById(R.id.layout_wallpaper);
		mTabWidgetsContent = (TabWidgetsPagedView)findViewById(R.id.layout_widgets);
		mTabAppsContent = (TabAppsPagedView)findViewById(R.id.layout_applications);
		//
		item_applications = (View)findViewById(R.id.item_application);
		item_shortcuts = (View)findViewById(R.id.item_shortcuts);
		item_wallpaper = (View)findViewById(R.id.item_wallpaper);
		item_widgets = (View)findViewById(R.id.item_widgets);
		//set onlistener
		item_applications.setOnClickListener(this);
		item_shortcuts.setOnClickListener(this);
		item_wallpaper.setOnClickListener(this);
		item_widgets.setOnClickListener(this);
    }
  		
    protected void onRestart() {
        layout_wallpaper.onRestart();
    }
		
    protected void onStop() {
        layout_wallpaper.onStop();	
    }  

    protected void onResume() {
	layout_wallpaper.onResume();
    }

    public void handlePackagesChanged(){
        layout_wallpaper.handlePackagesChanged();
    }   

    public void showTabBar(){
    	if(!mIsTabBarHidden){ 
    		 return;
    	}
    	resetVisible();
    	prepareStartAnimation(mTab);
    	mTabAnimation.reverse();
    	mIsTabBarHidden = false;
    }
    
    public void hideTabBar(){
    	if(mIsTabBarHidden){
    		 return;
    	}
    	if(current_tab != null){
    		current_tab.setVisibility(View.GONE);
    		current_tab = null;
    	}
        prepareStartAnimation(mTab);
        mTabAnimation.start();
        mIsTabBarHidden = true;
    }
    private void prepareStartAnimation(View v) {
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        v.buildLayer();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	 if (keyCode == KeyEvent.KEYCODE_BACK) {
    		 hideTabBar();
    		 return true;
         }
    	return false;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.item_application:
			setTabVisible(mTabAppsContent);
			break;
		case R.id.item_shortcuts:
			setTabVisible(layout_shortcuts);
			break;			
		case R.id.item_wallpaper:
			setTabVisible(layout_wallpaper);
			break;			
		case R.id.item_widgets:
			setTabVisible(mTabWidgetsContent);
			break;
		default:
			//do nothing
		}
	}
	//hide the view whatever the item is 
	private void resetVisible(){
		mTabAppsContent.setVisibility(View.GONE);
		layout_shortcuts.setVisibility(View.GONE);
		layout_wallpaper.setVisibility(View.GONE);
		mTabWidgetsContent.setVisibility(View.GONE);
	}
	//visibale the view when the respond the item is clicked
	private void setTabVisible(View view){
		if(view == null){
			return;
		}else if(view.getVisibility() == View.VISIBLE){
			view.setVisibility(View.GONE);
			current_tab = null;
			return;
		}
		
		if(current_tab != null){
			current_tab.setVisibility(View.GONE);
		}
		
		view.setVisibility(View.VISIBLE);
		current_tab = view;
	}
	
    private void setupAnimation(ObjectAnimator anim, final View v) {
        anim.setInterpolator(sAccelerateInterpolator);
        anim.setDuration(sTransitionInDuration);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
    }
    
    public void onPackagesUpdated() {
        if(mTabWidgetsContent != null){
        	mTabWidgetsContent.onPackagesUpdated();
        }
    }

	public void setup(Launcher launcher, DragController dragController) {
		// TODO Auto-generated method stub
		mLauncher = launcher;
		mTabWidgetsContent.setup(launcher, dragController);
		mTabAppsContent.setup(launcher, dragController);
		layout_shortcuts.setup(launcher);
		layout_wallpaper.setup(launcher);
	}

	//for efficiency, there is only one loadTask in LauncherModel,
	//we just used the load result for convenient.
	public void setApps(ArrayList<ApplicationInfo> list) {
		// TODO Auto-generated method stub
		if(mTabAppsContent != null){
			mTabAppsContent.setApps(list);
		}
	}

	public void addApps(ArrayList<ApplicationInfo> list) {
		// TODO Auto-generated method stub
		if(mTabAppsContent != null){
			mTabAppsContent.addApps(list);
		}
	}

	public void removeApps(ArrayList<String> list) {
		// TODO Auto-generated method stub
		if(mTabAppsContent != null){
			mTabAppsContent.removeApps(list);
		}
	}

	public void updateApps(ArrayList<ApplicationInfo> list) {
		// TODO Auto-generated method stub
		if(mTabAppsContent != null){
			mTabAppsContent.updateApps(list);
		}
	}

   	public void updateAppsUnreadChanged(ComponentName component, int unreadNum){
		if(mTabAppsContent != null){
			mTabAppsContent.updateAppsUnreadChanged(component, unreadNum);
		}
   	}

	public void updateAppsUnread() {
		if(mTabAppsContent != null){
			mTabAppsContent.updateAppsUnread();
		}
	}

}
