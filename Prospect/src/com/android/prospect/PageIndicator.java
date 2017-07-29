package com.android.prospect;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.res.Resources;
import com.android.prospect.R;

public class PageIndicator extends LinearLayout implements PagedView.PageSwitchListener{
	
	private static final String TAG = "PageIndicator";
	
	private final Context mContext;
	
	//private static final boolean useAnimation = true;
	
	private static final boolean onlyOnImg = false;
	
	private static final boolean twoImg = true;
	
	private LinearLayout.LayoutParams lp;
	
	private static int WIDTH_S = 37;
	
	private static int WIDTH_D = 32;
	
	private int mCurrentPageCount = 0;
	
	private static final int page_max = 9;
	
	private IndicatorView mCurrentView;
	
	public PageIndicator(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
                Resources res = getResources();
                WIDTH_D = res.getInteger(R.integer.pageindicator_width);
                
	}
	
	public void initLayout(View newPage, final int count, int currentPage){
		removeAllViews();
		final int indicator_width = getMeasuredWidth();
		int spacing = indicator_width - WIDTH_S * count;
		for(int i = 0; i < count; i++){
			IndicatorView child = new IndicatorView(mContext);
			lp = (LinearLayout.LayoutParams)child.getLayoutParams();
			child.setId(i);
			if(i == currentPage){
				child.setText(""+(i+1));
				child.setCurrenPage(true);
				mCurrentView = child;
			}else{
				child.setScaleX(.5f);
				child.setScaleY(.5f);
			}
			
			if(i != count - 1){
				if(count > page_max){
					lp.setMargins(0, 0, (int)(spacing/(count+1)), 0);
				}else{
					lp.setMargins(0, 0, 45, 0);
				}
			}
			addView(child);
		}
	}
	
	private void initLayout2(View newPage, final int count, int currentPage){
		removeAllViews();
		final int indicator_width = getMeasuredWidth();
		int spacing = (indicator_width - WIDTH_D * count);
                if(spacing < 0) spacing = 0;
		for(int i = 0; i < count; i++){
			IndicatorView child = new IndicatorView(mContext);
			lp = (LinearLayout.LayoutParams)child.getLayoutParams();
			child.setId(i);
			if(i == currentPage){
				child.setCurrenPage(true);
				child.toggleState();
				mCurrentView = child;
			}
			if(i != count - 1){
				if(count > page_max){
					lp.setMargins(0, 0, (int)(spacing/(count+1)), 0);
				}else{
					lp.setMargins(0, 0, (int)(spacing/(count+1))/*45*/, 0);
				}
			}
			child.toggleState();
			addView(child);
		}
	}
	
	@Override
	public void onPageSwitch(View newPage, int newPageIndex, int pageCount) {
		// TODO Auto-generated method stub
		
		
		if(onlyOnImg){
			if(mCurrentPageCount != pageCount){
				mCurrentPageCount = pageCount;
				initLayout(newPage, pageCount, newPageIndex);
				return;
			}
			IndicatorView toView;
			toView = (IndicatorView)getChildAt(newPageIndex);
			//Log.i(TAG, "newPageIndex = " + newPageIndex+ ", mCurrentView.getId() = " + mCurrentView.getId());
			if(toView != null && newPageIndex != mCurrentView.getId()){
				toView.setCurrenPage(true);	
				if(mCurrentView == null || mCurrentView != null && mCurrentView.getId() >= pageCount){
					toView.setText(""+(newPageIndex +1));
					toView.getToCurAnim().start();
				}else{
					IndicatorView fromView = mCurrentView;
					fromView.setText("");
					toView.setText(""+(newPageIndex +1));
					AnimatorSet mAnimation = LauncherAnimUtils.createAnimatorSet();
					mAnimation.playTogether(toView.getToCurAnim(), fromView.getToNorAnim());
					mAnimation.start();
					fromView.setCurrenPage(false);
				}

				mCurrentView = toView;
			}
		}else if(twoImg){
			if(mCurrentPageCount != pageCount){
				mCurrentPageCount = pageCount;
				initLayout2(newPage, pageCount, newPageIndex);
				return;
			}
			IndicatorView toView;
			toView = (IndicatorView)getChildAt(newPageIndex);
			if(toView != null && newPageIndex != mCurrentView.getId()){
				toView.setCurrenPage(true);	
				if(mCurrentView == null || mCurrentView != null && mCurrentView.getId() >= pageCount){
					toView.toggleState();
				}else{
					IndicatorView fromView = mCurrentView;
					fromView.setCurrenPage(false);
					fromView.toggleState();
					toView.toggleState();
				}
				mCurrentView = toView;
				
			}
		}else{
			//default
		}
		/*
		removeAllViews();
		if(pageCount > 0){
			for(int i = 0; i < pageCount; i++){
				TextView textView = new TextView(mContext);
				textView.setTextSize(10);
				textView.setId(i);
				if(i == newPageIndex){
					lp = new LinearLayout.LayoutParams(37, 37);
					textView.setText(""+(i+1));
					textView.setTextColor(Color.BLACK);
					textView.setGravity(0x10 | 0x01);
					//textView.setTypeface(null, Typeface.BOLD);
					textView.setBackgroundResource(R.drawable.page_focus_large);
					textView.setLayoutParams(lp);
				}else{
					lp = new LinearLayout.LayoutParams(20, 20);
					textView.setBackgroundResource(R.drawable.page_focus_middle);
					textView.setLayoutParams(lp);
				}
				if(i != pageCount - 1){
					lp.setMargins(0, 0, 50, 0);
				}
				addView(textView);
			}
		}
		*/

	}
	
	class IndicatorView extends TextView{
		
		private boolean mCurrentPage = false;
		
		private static final int animTime = 100;
		
		private float normalScale = 0.5f;
		
		private float currentScale = 1f;
		
		private LinearLayout.LayoutParams params =  
				new LinearLayout.LayoutParams(WIDTH_S, WIDTH_S);
		
		LauncherViewPropertyAnimator toCurAnim;
		
		LauncherViewPropertyAnimator toNorAnim;
		
		private AccelerateInterpolator acc = new AccelerateInterpolator();
		
		public IndicatorView(Context context){
			super(context);
			if(onlyOnImg){
				//init animation
				toCurAnim = new LauncherViewPropertyAnimator(this);
				toCurAnim.scaleX(currentScale).scaleY(currentScale).
				setDuration(animTime).
				setInterpolator(acc);
				//init animation
				toNorAnim = new LauncherViewPropertyAnimator(this);
				toNorAnim.scaleX(normalScale).scaleY(normalScale).
				setDuration(animTime).
				setInterpolator(acc);
				setBackgroundResource(R.drawable.page_focus_large);
			}else if(twoImg){
				params =  new LinearLayout.LayoutParams(WIDTH_D, WIDTH_D);
			}
			//set default display style
			setTextColor(Color.BLACK);
			setGravity(0x10 | 0x01);
			setTextSize(10);
			setLayoutParams(params);
		}
		
		public void setCurrenPage(boolean current){
			mCurrentPage = current;
		}
		
		public boolean isCurrentPage(){
			return mCurrentPage;
		}
		
		public LauncherViewPropertyAnimator getToNorAnim(){
			return toNorAnim;
		}
		
		public LauncherViewPropertyAnimator getToCurAnim(){
			return toCurAnim;
		}
		
		public void toggleState(){
			if(mCurrentPage){
				setBackgroundResource(R.drawable.mickey_indicator_cur);
			}else{
				setBackgroundResource(R.drawable.mickey_indicator_nor);
			}
		}
	}

}
