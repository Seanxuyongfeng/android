package com.android.prospect;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.android.prospect.R;

public class AppsSearchResult extends PagedView implements View.OnClickListener ,
	PagedViewIcon.PressedCallback, View.OnKeyListener{
	private static final String TAG = "AppsSearchResult";
	
	private int mContentWidth;
	private int mMaxAppCellCountX, mMaxAppCellCountY;
	private PagedViewCellLayout mWidgetSpacingLayout;
	
	//content
	private ArrayList<ApplicationInfo> mResultApps;
	private ArrayList<ApplicationInfo> mAppsList;
	private int mNumAppsPages;
	private AppsCustomizePagedView mAppsCustomizeContent;
	private MyGridLayout mKeyboard;
	private Launcher mLauncher;
	private ArrayList<String> mResults = new ArrayList<String>();
	private EditText mEditText;
	//const
	private final Context mContext;
	private final LayoutInflater mLayoutInflater;
	private final EditTextWacher mTextWatcher;
	
	public AppsSearchResult(Context context, AttributeSet attrs) {
		super(context, attrs);
		mWidgetSpacingLayout = new PagedViewCellLayout(getContext());
		mResultApps = new ArrayList<ApplicationInfo>();
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppsCustomizePagedView, 0, 0);
        mMaxAppCellCountX = a.getInt(R.styleable.AppsCustomizePagedView_maxAppCellCountX, -1);
        mMaxAppCellCountY = a.getInt(R.styleable.AppsCustomizePagedView_maxAppCellCountY, -1);
        a.recycle();
        mTextWatcher = new EditTextWacher();
	}
	
	@Override
	public void syncPages() {
		// TODO Auto-generated method stub
		removeAllViews();
		Context context = getContext();
		for(int i = 0; i < mNumAppsPages; i++){
			PagedViewCellLayout layout = new PagedViewCellLayout(context);
			setupPage(layout);
			addView(layout);
		}
	}

	@Override
	public void syncPageItems(int page, boolean immediate) {
		// TODO Auto-generated method stub
		syncAppsPageItems(page, immediate);
	}
	
	private void syncAppsPageItems(int page, boolean immediate){
        int numCells = mCellCountX * mCellCountY;
        int startIndex = page * numCells;
        int endIndex = Math.min(startIndex + numCells, mResultApps.size());
        PagedViewCellLayout layout = (PagedViewCellLayout) getPageAt(page);
        layout.removeAllViewsOnPage();
        ArrayList<Object> items = new ArrayList<Object>();
        ArrayList<Bitmap> images = new ArrayList<Bitmap>();
        for (int i = startIndex; i < endIndex; ++i) {
            ApplicationInfo info = mResultApps.get(i);
            MTKAppIcon icon = (MTKAppIcon) mLayoutInflater.inflate(
                    R.layout.mtk_apps_customize_application, layout, false);
            icon.applyFromApplicationInfo(info, true, this);
            icon.mAppIcon.setOnClickListener(this);
            icon.mAppIcon.setOnKeyListener(this);
            int index = i - startIndex;
            int x = index % mCellCountX;
            int y = index / mCellCountX;
            layout.addViewToCellLayout(icon, -1, i, new PagedViewCellLayout.LayoutParams(x,y, 1,1));
            items.add(info);
            images.add(info.iconBitmap);
        }

        layout.createHardwareLayers();
		
	}
	
    private void updatePageCounts() {
        int pages = mNumAppsPages = (int) Math.ceil((float) mResultApps.size() / (mCellCountX * mCellCountY));
        mNumAppsPages = Math.max(1, pages);
    }
	
	
	protected void onDataReady(int width, int height){
        int maxCellCountX = Integer.MAX_VALUE;
        int maxCellCountY = Integer.MAX_VALUE;
        if (mMaxAppCellCountX > -1) {
            maxCellCountX = Math.min(maxCellCountX, mMaxAppCellCountX);
        }
        // Temp hack for now: only use the max cell count Y for widget layout
        int maxWidgetCellCountY = maxCellCountY;
        if (mMaxAppCellCountY > -1) {
            maxWidgetCellCountY = Math.min(maxWidgetCellCountY, mMaxAppCellCountY);
        }
        
		mWidgetSpacingLayout.setGap(mPageLayoutWidthGap,mPageLayoutHeightGap);
		mWidgetSpacingLayout.setPadding(mPageLayoutPaddingLeft, mPageLayoutPaddingTop, 
				mPageLayoutPaddingRight, mPageLayoutPaddingBottom);
		mWidgetSpacingLayout.calculateCellCount(width, height, maxCellCountX, maxCellCountY);
        mCellCountX = mWidgetSpacingLayout.getCellCountX();
        mCellCountY = mWidgetSpacingLayout.getCellCountY();
		updatePageCounts();
        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
        mWidgetSpacingLayout.calculateCellCount(width, height, maxCellCountX, maxWidgetCellCountY);
        mWidgetSpacingLayout.measure(widthSpec, heightSpec);
        mContentWidth = mWidgetSpacingLayout.getContentWidth();
        invalidatePageData(0);
	}
	
	
	public void setAppsCustomizeContent(final AppsCustomizePagedView apps){
		mAppsCustomizeContent = apps;
	}
	
	public void setKeyboard(MyGridLayout keyboard){
		mKeyboard = keyboard;
	}
	
    public void syncResults(final List<String> result){
    	mAppsList = mAppsCustomizeContent.getApps();
    	final int count = mAppsList.size();
    	mResultApps.clear();
	    if(result != null){
	    	for(String name : result){
		    	for(int i = 0; i < count; i++){
		    		ApplicationInfo info = mAppsList.get(i);
		    		String title = info.title.toString();
		    		if(title.equalsIgnoreCase(name)){
		    			mResultApps.add(info);
		    		}
		    	}
	    	}
	    }
	    updatePageCounts();
	    invalidatePageData(0);
     }
    
    private void setupPage(PagedViewCellLayout layout) {
        layout.setCellCount(mCellCountX, mCellCountY);
        layout.setGap(mPageLayoutWidthGap, mPageLayoutHeightGap);
        layout.setPadding(mPageLayoutPaddingLeft, mPageLayoutPaddingTop,
                mPageLayoutPaddingRight, mPageLayoutPaddingBottom);

        // Note: We force a measure here to get around the fact that when we do layout calculations
        // immediately after syncing, we don't have a proper width.  That said, we already know the
        // expected page width, so we can actually optimize by hiding all the TextView-based
        // children that are expensive to measure, and let that happen naturally later.
        setVisibilityOnChildren(layout, View.GONE);
        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
        layout.setMinimumWidth(getPageContentWidth());
        layout.measure(widthSpec, heightSpec);
        setVisibilityOnChildren(layout, View.VISIBLE);
    }
    
    private void setVisibilityOnChildren(ViewGroup layout, int visibility) {
        int childCount = layout.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            layout.getChildAt(i).setVisibility(visibility);
        }
    }
    
    /**
     * Used by the parent to get the content width to set the tab bar to
     * @return
     */
    public int getPageContentWidth() {
        return mContentWidth;
    }
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		if(!isDataReady()){
			setDataIsReady();
			setMeasuredDimension(width, height);
			onDataReady(width, height);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setup(Launcher launcher){
		mLauncher = launcher;
	}
	
	public void onResume(){
		hideSearchView();
	}
	
	public void onPause(){
		hideSearchView();
	}
	
	public void hideSearchView(){
		if(mKeyboard != null){
			mEditText.setText("");
			mKeyboard.setVisibility(View.GONE);
		}
	}
	
	public void onStop(){
		hideSearchView();
	}
	
	public void onStart(){
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() ==  R.id.search_text){
			if(mKeyboard.getVisibility() == View.VISIBLE){
				mKeyboard.setVisibility(View.GONE);
			}else{
				mKeyboard.setVisibility(View.VISIBLE);
				initKeyboardState();
			}
			return;
		}		
	    if (v instanceof MTKAppIcon) {
	       	v = ((MTKAppIcon)v).mAppIcon;
	    }
	    if (v instanceof PagedViewIcon) {
	        // Animate some feedback to the click
	         final ApplicationInfo appInfo = (ApplicationInfo) v.getTag();
	         // NOTE: We want all transitions from launcher to act as if the wallpaper were enabled
	         // to be consistent.  So re-enable the flag here, and we will re-disable it as necessary
	         // when Launcher resumes and we are still in AllApps.
	         mLauncher.updateWallpaperVisibility(true);
	         mLauncher.startActivitySafely(v, appInfo.intent, appInfo);
	    }



	}

	@Override
	public void iconPressed(PagedViewIcon icon) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
    private class EditTextWacher implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			mResults.clear();
			String queryStr = s.toString().replace("#", "[0-9]");
			if(queryStr.equals("")){
				syncResults(null);
				String nextChar = LauncherModel.queryAplications(mContext, queryStr, mResults, s.toString().length());
				mKeyboard.updateButtonState(nextChar);
				setVisibility(View.GONE);
				if(mAppsCustomizeContent != null){
					mAppsCustomizeContent.setVisibility(View.VISIBLE);
				}
			}else{
				String nextChar = LauncherModel.queryAplications(mContext, queryStr, mResults, s.toString().length());
				mKeyboard.updateButtonState(nextChar);
				if(mAppsCustomizeContent != null){
					mAppsCustomizeContent.setVisibility(View.GONE);
				}
				setVisibility(View.VISIBLE);
				syncResults(mResults);
			}
		}
    	
    }
    
    public void initKeyboardState(){
		String nextChar = LauncherModel.queryAplications(mContext, "", null, 0);
		mKeyboard.updateButtonState(nextChar);
    }
    
	public void setEditText(EditText editText){
		mEditText = editText;
		mEditText.setOnKeyListener(this);
		mEditText.setOnClickListener(this);
		mEditText.setKeyListener(new NumberKeyListener(){
			@Override
			public int getInputType() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			protected char[] getAcceptedChars() {
				// TODO Auto-generated method stub
				 return new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','#'};
			}
		});
		mEditText.addTextChangedListener(mTextWatcher);
	}    
}
