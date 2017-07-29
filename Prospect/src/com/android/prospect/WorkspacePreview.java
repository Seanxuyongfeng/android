package com.android.prospect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import com.android.prospect.R;

public class WorkspacePreview extends RelativeLayout implements OnClickListener {

	private static final String TAG = "WorkspacePreview";

	private PreviewLayout mPreviewsContainer;

	private View mDeleteZone;

	private Workspace mWorkspace;

	private int mCurrentScreenCount;

	private PopupWindow mPopupWindow;

	private Launcher mLauncher;

	private final Context mContext;
	/*
	static final Object mLock = new Object();

	private static final HandlerThread mPreviewThread = new HandlerThread(
			"workspace-preview");

	static {
		mPreviewThread.start();
	}

	private static final Handler mWorker = new Handler(
			mPreviewThread.getLooper());
	 */
	private LinearLayout.LayoutParams mLinearLayoutParams;// 动态加载LinearLayout时的Params

 	private static final int IMAGE_WIDTH = 231;

        private static final int IMAGE_HEIGHT = 333;

	private LinearLayout mAddScreenButton;

	public WorkspacePreview(Context context) {
		this(context, null);
	}

	public WorkspacePreview(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WorkspacePreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		mLinearLayoutParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
		mAddScreenButton = createAddScreenButton();
	}

	public void setup(Workspace workspace, PopupWindow popupWindow,
			Launcher launcher) {
		mWorkspace = workspace;
		mPopupWindow = popupWindow;
		mLauncher = launcher;
	}

	public void initPreviewLayout() {
		if (mWorkspace == null) {
			if (mPopupWindow != null) {
				mPopupWindow.dismiss();
			}
			return;
		}
		CellLayout cell = (CellLayout) mWorkspace.getChildAt(0);
		mCurrentScreenCount = mWorkspace.getChildCount();
		int countX = mPreviewsContainer.getCountX();
		int countY = mPreviewsContainer.getCountY();
		int numCells = countX * countY;
		int startIndex = 0;
		int endIndex = Math.min(numCells, mCurrentScreenCount);

		for (int i = startIndex; i < endIndex; i++) {
			LinearLayout cellContainer = new LinearLayout(mContext);
			setupContainer(cellContainer);
			cellContainer.setTag(i);
			CellPreview screenImage = new CellPreview(mContext);
			cell = (CellLayout) mWorkspace.getChildAt(i);
			drawScreenImage(screenImage, cell);
			screenImage.setTag(i);
			cellContainer.addView(screenImage);

			int x = i % countX;
			int y = i / countY;
			mPreviewsContainer.addViewToPreview(cellContainer, -1, i,
					new PreviewLayout.LayoutParams(x, y, 1, 1));
		}
		showAddButtonIfNeeded();
		mPreviewsContainer.invalidate();
	}

	private void drawScreenImage(CellPreview screen, CellLayout image){
		if(image != null){
			ViewGroup child = (ViewGroup)image.getChildAt(0);	
			if(child != null){
				screen.setScreenEmpty(child.getChildCount() == 0);				
			}	
		}
		final Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT,Bitmap.Config.ARGB_8888);	
		final Canvas c = new Canvas(bitmap);
		c.scale(0.32f, 0.33f);
		image.dispatchDraw(c);	
		screen.setImageBitmap(bitmap);	
		screen.setBackgroundResource(R.drawable.preview_background);	
	}

	//initial the linearlayout params
	private void setupContainer(LinearLayout containerLayout){
		containerLayout.setLayoutParams(mLinearLayoutParams);
		containerLayout.setOrientation(LinearLayout.VERTICAL);
		containerLayout.setGravity(Gravity.CENTER_VERTICAL);
		containerLayout.setClickable(true);
		containerLayout.setLongClickable(true);
		containerLayout.setOnDragListener(mPreviewsContainer.getPreviewDragListener());
		containerLayout.setOnClickListener(this);
		containerLayout.setOnLongClickListener(mPreviewsContainer);	
	}

	/**
	 * Create a screen that contain the "+" flag, add a empty screen
	 * into the container when clicked
	 */
	public LinearLayout createAddScreenButton() {
		LinearLayout addScreenContainer = new LinearLayout(mContext);
		addScreenContainer.setLayoutParams(mLinearLayoutParams);
		addScreenContainer.setOrientation(LinearLayout.VERTICAL);
		addScreenContainer.setGravity(Gravity.CENTER);

		ImageView addScreenImage = new ImageView(mContext);
		addScreenImage.setBackgroundResource(R.drawable.addpreviews);
		addScreenContainer.addView(addScreenImage);

		addScreenContainer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				addEmptyScreen();
			}
		});
		return addScreenContainer;
	}

	public void addEmptyScreen() {
		mCurrentScreenCount = mWorkspace.getChildCount();
		if (mCurrentScreenCount >= mLauncher.getMaxScreenCount()
		    || mPreviewsContainer == null) {
			return;
		}
		LinearLayout newScreen = createEmptyScreen();
		mWorkspace.addScreenToWorkspace(null);
		mCurrentScreenCount = mWorkspace.getChildCount();
		mLauncher.updateScreenCount(mCurrentScreenCount);
		mPreviewsContainer.removeView(mAddScreenButton);
		mPreviewsContainer.addEmptyView(newScreen);
		showAddButtonIfNeeded();
		mPreviewsContainer.invalidate();
	}

	/**
	 * 先删除view上的,再删除数据库中的,再更新tag
	 */
	public void removeScreen(int screenIndex) {
		//Log.i(TAG, "removeScreen index = " + screenIndex);
		mWorkspace.removeScreenFromWorkspace(screenIndex);
		mCurrentScreenCount = mWorkspace.getChildCount();
		mLauncher.updateScreenCount(mCurrentScreenCount);
		showAddButtonIfNeeded();
		mPreviewsContainer.invalidate();
	}

	/**
	 * 交换screen,其实就是交换Container内的ImageView
	 */
	public void swapScreen(int first, int second) {
		mWorkspace.swapScreen(first, second);
		mPreviewsContainer.invalidate();
	}

	public LinearLayout createEmptyScreen() {
		LinearLayout newScreenContainer = new LinearLayout(mContext);
		setupContainer(newScreenContainer);
		newScreenContainer.setTag(mCurrentScreenCount);
		//create a object
		CellPreview newScreenImage = new CellPreview(mContext);
		final Bitmap bitmap = Bitmap.createBitmap(IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
		newScreenImage.setImageBitmap(bitmap);
		newScreenImage.setTag(mCurrentScreenCount);
		newScreenImage.setBackgroundResource(R.drawable.preview_background);
		newScreenContainer.addView(newScreenImage);
		return newScreenContainer;
	}

	private void showAddButtonIfNeeded() {
		if (mAddScreenButton != null && mPreviewsContainer != null) {
			if (showAddButton()) {
				//if the add button is there, we should not add it again.
				if (!checkAddButtonExits()) {
					mPreviewsContainer.addAddingButton(mAddScreenButton);
				}
			} else {
				mPreviewsContainer.removeView(mAddScreenButton);
			}
		}
	}

	/**
	 * show the add button when the number is less than 9.
	 */
	private boolean showAddButton() {
		mCurrentScreenCount = mWorkspace.getChildCount();
		return mCurrentScreenCount < mLauncher.getMaxScreenCount() ? true : false;
	}

	/**
	 * Check if the add button is there, the button should be there 
	 * if the preview number is more over the number of the workspace children.
	 */
	private boolean checkAddButtonExits() {
		assert(mPreviewsContainer != null);
		mCurrentScreenCount = mWorkspace.getChildCount();
		return mPreviewsContainer.getChildCount() > mCurrentScreenCount ? true : false;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mPreviewsContainer = (PreviewLayout) findViewById(R.id.previews_container);
		mPreviewsContainer.setWrokspacePreview(this);
		mDeleteZone = findViewById(R.id.delete_preview_zone);
		mDeleteZone.setOnDragListener(mPreviewsContainer.getDeleteListener());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
			mLauncher.setViewShow(true);
		}
		mWorkspace.snapToPage(mPreviewsContainer.getIndex(v));
	}

}
