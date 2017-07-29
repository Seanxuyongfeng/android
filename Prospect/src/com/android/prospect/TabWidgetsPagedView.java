package com.android.prospect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.graphics.Shader;
import android.graphics.BitmapShader;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.TableMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.os.AsyncTask;
import android.os.Process;
import android.os.Bundle;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.appwidget.AppWidgetManager;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import com.android.prospect.DropTarget.DragObject;
import java.util.Iterator;
import com.android.prospect.R;


public class TabWidgetsPagedView extends PagedViewWithDraggableItems implements View.OnClickListener,
	PagedViewWidget.ShortPressListener, View.OnKeyListener, DragSource{

	private static final String TAG = "TabWidgetsPagedView";
	
	private int mNumPages;
	
	private ArrayList<Object> mWidgets;
	
    private Launcher mLauncher;
	
	private final Context mContext;	
	
	private static final int mWidgetCountX = 3;
	
	private static final int mWidgetCountY = 1;
	
	private static int WIDTH = 720;
	
	private static int HEIGHT = 1280;
	
    private static int widget_width = 175;
    
    private static int widget_height = 223;
    private static final float width_radio = 0.243f;
    private static final float height_radio = 0.79f; 
	private static int mWidgetWidthGap = 48;

	private static int mWidgetHeightGap = 36;

    int mWidgetCleanupState = WIDGET_NO_CLEANUP_REQUIRED;
    
    static final int WIDGET_NO_CLEANUP_REQUIRED = -1;
	
    static final int WIDGET_PRELOAD_PENDING = 0;
	
    static final int WIDGET_BOUND = 1;
	
    static final int WIDGET_INFLATED = 2;
	
    int mWidgetLoadingId = -1;
	
    PendingAddWidgetInfo mCreateWidgetInfo = null;
	
    private Runnable mInflateWidgetRunnable = null;
	
    private Runnable mBindWidgetRunnable = null;
	
    private Canvas mCanvas;
    
    private final LayoutInflater mLayoutInflater;
    
    private final PackageManager mPackageManager;
	
    private final Resources mResources;
    
    private int mAppIconSize;
    
    private Drawable mDefaultWidgetBackground;
    
    private IconCache mIconCache;
    
    private DragController mDragController;
    
    private boolean mDraggingWidget = false;


    // Used for drawing widget previews
    CanvasCache mCachedAppWidgetPreviewCanvas = new CanvasCache();
	
    RectCache mCachedAppWidgetPreviewSrcRect = new RectCache();
	
    RectCache mCachedAppWidgetPreviewDestRect = new RectCache();
	
    PaintCache mCachedAppWidgetPreviewPaint = new PaintCache();
    
    // Deferral of loading widget previews during launcher transitions
    private boolean mInTransition;
    private ArrayList<AsyncTaskPageData> mDeferredSyncWidgetPageItems =
        new ArrayList<AsyncTaskPageData>();
    private ArrayList<Runnable> mDeferredPrepareLoadWidgetPreviewsTasks =
        new ArrayList<Runnable>();
    ArrayList<AppsCustomizeAsyncTask> mRunningTasks;
    private static final int sPageSleepDelay = 200;

    private Rect mTmpRect = new Rect();

    public TabWidgetsPagedView(Context context) {
        this(context, null);
    }

    public TabWidgetsPagedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabWidgetsPagedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
		mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mWidgets = new ArrayList<Object>();
        mPackageManager = context.getPackageManager();
        // Save the default widget preview background
        mResources = context.getResources();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        WIDTH = wm.getDefaultDisplay().getWidth();
        HEIGHT = wm.getDefaultDisplay().getHeight();
        int layoutHeight =  mResources.getDimensionPixelSize(R.dimen.tab_content_widgets_height);
        widget_width = (int)(width_radio*WIDTH);
        widget_height = (int)(layoutHeight*height_radio);
        mWidgetWidthGap = (WIDTH - widget_width * mWidgetCountX)/(mWidgetCountX+1);
        mWidgetHeightGap = (layoutHeight - widget_height)/2;
        mDefaultWidgetBackground = mResources.getDrawable(R.drawable.default_widget_preview_holo);
        mAppIconSize = mResources.getDimensionPixelSize(R.dimen.app_icon_size);
        mIconCache = ((LauncherApplication) context.getApplicationContext()).getIconCache();
        mCanvas = new Canvas();
	 mRunningTasks = new ArrayList<AppsCustomizeAsyncTask>();
    }
    
    /*
     * Widgets PagedView implementation
     */
    private void setupPage(PagedViewGridLayout layout) {
        layout.setPadding(mPageLayoutPaddingLeft, mPageLayoutPaddingTop,
                mPageLayoutPaddingRight, mPageLayoutPaddingBottom);

        // Note: We force a measure here to get around the fact that when we do layout calculations
        // immediately after syncing, we don't have a proper width.
        int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST);
        layout.setMinimumWidth(WIDTH);
        layout.measure(widthSpec, heightSpec);
    }
    
	@Override
	public void syncPages() {
		// TODO Auto-generated method stub
		removeAllViews();
		Context context = mContext;
		for (int j = 0; j < mNumPages; ++j) {
			PagedViewGridLayout layout = new PagedViewGridLayout(context, mWidgetCountX, mWidgetCountY);
			setupPage(layout);
			addView(layout, new PagedView.LayoutParams(LayoutParams.MATCH_PARENT,
	                    LayoutParams.MATCH_PARENT));
		}
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (!isDataReady()) {
            if (!mWidgets.isEmpty()) {
                setDataIsReady();
                setMeasuredDimension(width, height);
                onDataReady(width, height);
            }
        }
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
    protected void onDataReady(int width, int height) {
        invalidatePageData(0, false);
    }
    
	@Override
	public void syncPageItems(int page, boolean immediate) {
		// TODO Auto-generated method stub
		syncWidgetPageItems(page, immediate);
	}
	
	public void syncWidgetPageItems(final int page, final boolean immediate) {
		int numItemsPerPage = mWidgetCountX * mWidgetCountY;
		final ArrayList<Object> items = new ArrayList<Object>();

		int offset = page * numItemsPerPage;
		for(int i = offset; i < Math.min(offset + numItemsPerPage, mWidgets.size()); i++){
			items.add(mWidgets.get(i));
		}

		final PagedViewGridLayout layout = (PagedViewGridLayout) getPageAt(page);	
		layout.setColumnCount(layout.getCellCountX());	

		for (int i = 0; i < items.size(); ++i) {
			Object rawInfo = items.get(i);
			PendingAddItemInfo createItemInfo = null;
		    PagedViewWidget widget = (PagedViewWidget) mLayoutInflater.inflate(
	                    R.layout.item_tab_widget, layout, false);
		    if (rawInfo instanceof AppWidgetProviderInfo) {
		    	AppWidgetProviderInfo info = (AppWidgetProviderInfo) rawInfo;
		    	createItemInfo = new PendingAddWidgetInfo(info, null, null);
		    	int[] spanXY = Launcher.getSpanForWidget(mLauncher, info);
                createItemInfo.spanX = spanXY[0];
                createItemInfo.spanY = spanXY[1];
                int[] minSpanXY = Launcher.getMinSpanForWidget(mLauncher, info);
                createItemInfo.minSpanX = minSpanXY[0];
                createItemInfo.minSpanY = minSpanXY[1];
                /*
                Bitmap b = getWidgetPreview(info.provider, info.previewImage, info.icon,
                        0, 0, widget_width, widget_height);
                widget.applyPreview(new FastBitmapDrawable(b), i);  
                */
                widget.applyFromAppWidgetProviderInfo(info, -1, spanXY);
                widget.setTag(createItemInfo);
                widget.setShortPressListener(this);                
		    }else if (rawInfo instanceof ResolveInfo) {
                // Fill in the shortcuts information
                ResolveInfo info = (ResolveInfo) rawInfo;
                createItemInfo = new PendingAddShortcutInfo(info.activityInfo);
                createItemInfo.itemType = LauncherSettings.Favorites.ITEM_TYPE_SHORTCUT;
                createItemInfo.componentName = new ComponentName(info.activityInfo.packageName,
                        info.activityInfo.name);
                widget.applyFromResolveInfo(mPackageManager, info);
                widget.setTag(createItemInfo);
            }
		    
            widget.setOnClickListener(this);
            widget.setOnLongClickListener(this);
            widget.setOnTouchListener(this);
            widget.setOnKeyListener(this);
            // Layout each widget
            int ix = i % mWidgetCountX;
            int iy = i / mWidgetCountX;
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams(
                    GridLayout.spec(iy, GridLayout.LEFT),
                    GridLayout.spec(ix, GridLayout.TOP));
            lp.width = widget_width;
            lp.height = widget_height;
            //lp.setGravity(Gravity.CENTER);
            lp.leftMargin = mWidgetWidthGap;
            lp.topMargin = mWidgetHeightGap;
            layout.addView(widget, lp);
		}		
		layout.setOnLayoutListener(new Runnable(){

			@Override
			public void run() {
				//load the widget previews
				int maxPreviewWidth = widget_width;
				int maxPreviewHeight = widget_height;
				if(layout.getChildCount() > 0){
					PagedViewWidget w = (PagedViewWidget)layout.getChildAt(0);
					int[] maxSize = w.getPreviewSize();
					maxPreviewWidth = maxSize[0];
					maxPreviewHeight = maxSize[1];
									
				}
				

				int count = items.size();
				if(immediate){
					AsyncTaskPageData data = new AsyncTaskPageData(page, items,
							maxPreviewWidth, maxPreviewHeight, null, null);
					loadWidgetPreviewsInBackground(null, data);
					onSyncWidgetPageItems(data);
					/*
					for(int i = 0; i < count; ++i){
						Object rawInfo = items.get(i);
						if (rawInfo instanceof AppWidgetProviderInfo) {
							AppWidgetProviderInfo info = (AppWidgetProviderInfo) rawInfo;
							PagedViewWidget widget = (PagedViewWidget)layout.getChildAt(i);
							Bitmap b = getWidgetPreview(info.provider, info.previewImage, info.icon,
		                        0, 0, widget_width, widget_height);
							widget.applyPreview(new FastBitmapDrawable(b), i); 
						}
					}*/
				}else{
				       if (mInTransition) {
                       			 	mDeferredPrepareLoadWidgetPreviewsTasks.add(this);
                    			} else {
                        			prepareLoadWidgetPreviewsTask(page, items,
                                		maxPreviewWidth, maxPreviewHeight, mWidgetCountX);
                    			}	
				}
			}
			
		});
		
	}

   /**
     * Creates and executes a new AsyncTask to load a page of widget previews.
     */
    private void prepareLoadWidgetPreviewsTask(int page, ArrayList<Object> widgets,
            int cellWidth, int cellHeight, int cellCountX) {
        // Prune all tasks that are no longer needed
        Iterator<AppsCustomizeAsyncTask> iter = mRunningTasks.iterator();
        while (iter.hasNext()) {
            AppsCustomizeAsyncTask task = (AppsCustomizeAsyncTask) iter.next();
            int taskPage = task.page;
            if (taskPage < getAssociatedLowerPageBound(mCurrentPage) ||
                    taskPage > getAssociatedUpperPageBound(mCurrentPage)) {
                task.cancel(false);
                iter.remove();
            } else {
                task.setThreadPriority(getThreadPriorityForPage(taskPage));
            }
        }

        // We introduce a slight delay to order the loading of side pages so that we don't thrash
        final int sleepMs = getSleepForPage(page);
        AsyncTaskPageData pageData = new AsyncTaskPageData(page, widgets, cellWidth, cellHeight,
            new AsyncTaskCallback() {
                @Override
                public void run(AppsCustomizeAsyncTask task, AsyncTaskPageData data) {
                    try {
                        try {
                            Thread.sleep(sleepMs);
                        } catch (Exception e) {}
                        loadWidgetPreviewsInBackground(task, data);
                    } finally {
                        if (task.isCancelled()) {
                            data.cleanup(true);
                        }
                    }
                }
            },
            new AsyncTaskCallback() {
                @Override
                public void run(AppsCustomizeAsyncTask task, AsyncTaskPageData data) {
                    mRunningTasks.remove(task);
                    if (task.isCancelled()) {
                        return;
                    }
                    // do cleanup inside onSyncWidgetPageItems
                    onSyncWidgetPageItems(data);
                }
            });

        // Ensure that the task is appropriately prioritized and runs in parallel
        AppsCustomizeAsyncTask t = new AppsCustomizeAsyncTask(page,
                AsyncTaskPageData.Type.LoadWidgetPreviewData);
        t.setThreadPriority(getThreadPriorityForPage(page));
        t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pageData);
        mRunningTasks.add(t);
    }

    /**
     * Return the appropriate thread priority for loading for a given page (we give the current
     * page much higher priority)
     */
    private int getThreadPriorityForPage(int page) {
        // TODO-APPS_CUSTOMIZE: detect number of cores and set thread priorities accordingly below
        int pageDiff = getWidgetPageLoadPriority(page);
        if (pageDiff <= 0) {
            return Process.THREAD_PRIORITY_LESS_FAVORABLE;
        } else if (pageDiff <= 1) {
            return Process.THREAD_PRIORITY_LOWEST;
        } else {
            return Process.THREAD_PRIORITY_LOWEST;
        }
    }


    private int getSleepForPage(int page) {
        int pageDiff = getWidgetPageLoadPriority(page);
        return Math.max(0, pageDiff * sPageSleepDelay);
    }


    /**
     * A helper to return the priority for loading of the specified widget page.
     */
    private int getWidgetPageLoadPriority(int page) {
        // If we are snapping to another page, use that index as the target page index
        int toPage = mCurrentPage;
        if (mNextPage > -1) {
            toPage = mNextPage;
        }

        // We use the distance from the target page as an initial guess of priority, but if there
        // are no pages of higher priority than the page specified, then bump up the priority of
        // the specified page.
        Iterator<AppsCustomizeAsyncTask> iter = mRunningTasks.iterator();
        int minPageDiff = Integer.MAX_VALUE;
        while (iter.hasNext()) {
            AppsCustomizeAsyncTask task = (AppsCustomizeAsyncTask) iter.next();
            minPageDiff = Math.abs(task.page - toPage);
        }

        int rawPageDiff = Math.abs(page - toPage);
        return rawPageDiff - Math.min(rawPageDiff, minPageDiff);
    }

  private void loadWidgetPreviewsInBackground(AppsCustomizeAsyncTask task,
            AsyncTaskPageData data) {
        // loadWidgetPreviewsInBackground can be called without a task to load a set of widget
        // previews synchronously
        if (task != null) {
            // Ensure that this task starts running at the correct priority
            task.syncThreadPriority();
        }

        // Load each of the widget/shortcut previews
        ArrayList<Object> items = data.items;
        ArrayList<Bitmap> images = data.generatedImages;
        int count = items.size();
        for (int i = 0; i < count; ++i) {
            if (task != null) {
                // Ensure we haven't been cancelled yet
                if (task.isCancelled()) break;
                // Before work on each item, ensure that this task is running at the correct
                // priority
                task.syncThreadPriority();
            }

            Object rawInfo = items.get(i);
            if (rawInfo instanceof AppWidgetProviderInfo) {
                AppWidgetProviderInfo info = (AppWidgetProviderInfo) rawInfo;
                int[] cellSpans = Launcher.getSpanForWidget(mLauncher, info);

                int maxWidth = Math.min(data.maxImageWidth, cellSpans[0] * widget_width);
                int maxHeight = Math.min(data.maxImageHeight, cellSpans[1] * widget_height);
                Bitmap b = getWidgetPreview(info.provider, info.previewImage, info.icon,
                        cellSpans[0], cellSpans[1], maxWidth, maxHeight);
                images.add(b);
            } else if (rawInfo instanceof ResolveInfo) {
                // Fill in the shortcuts information
                //ResolveInfo info = (ResolveInfo) rawInfo;
                //images.add(getShortcutPreview(info, data.maxImageWidth, data.maxImageHeight));
            }
        }
    }

    private void onSyncWidgetPageItems(AsyncTaskPageData data) {
        if (mInTransition) {
            mDeferredSyncWidgetPageItems.add(data);
            return;
        }
        try {
            int page = data.page;
            PagedViewGridLayout layout = (PagedViewGridLayout) getPageAt(page);

            ArrayList<Object> items = data.items;
            int count = items.size();
            for (int i = 0; i < count; ++i) {
                PagedViewWidget widget = (PagedViewWidget) layout.getChildAt(i);
                if (widget != null) {
                    Bitmap preview = data.generatedImages.get(i);
                    widget.applyPreview(new FastBitmapDrawable(preview), i);
                }
            }

            if (LauncherLog.DEBUG) {
                LauncherLog.d(TAG, "onSyncWidgetPageItems: page = " + page + ", layout = " + layout
                    + ", count = " + count + ", this = " + this);
            }

            layout.createHardwareLayer();
            invalidate();

            // Update all thread priorities
            Iterator<AppsCustomizeAsyncTask> iter = mRunningTasks.iterator();
            while (iter.hasNext()) {
                AppsCustomizeAsyncTask task = (AppsCustomizeAsyncTask) iter.next();
                int pageIndex = task.page;
                task.setThreadPriority(getThreadPriorityForPage(pageIndex));
            }
        } finally {
            data.cleanup(false);
        }
    }

	 private Bitmap getWidgetPreview(ComponentName provider, int previewImage,
	            int iconId, int cellHSpan, int cellVSpan, int maxWidth,
	            int maxHeight) {
	        // Load the preview image if possible
	        String packageName = provider.getPackageName();
	        if (maxWidth < 0) maxWidth = Integer.MAX_VALUE;
	        if (maxHeight < 0) maxHeight = Integer.MAX_VALUE;

	        Drawable drawable = null;
	        if (previewImage != 0) {
	            drawable = mPackageManager.getDrawable(packageName, previewImage, null);
	            if (drawable == null) {
	                Log.w(TAG, "Can't load widget preview drawable 0x" +
	                        Integer.toHexString(previewImage) + " for provider: " + provider);
	            }
	        }

	        int bitmapWidth = 0;
	        int bitmapHeight = 0;
	        Bitmap defaultPreview = null;
	        boolean widgetPreviewExists = (drawable != null);

		//
		boolean useWidgetPreview = false;
	
	        if (widgetPreviewExists) {
	            bitmapWidth = drawable.getIntrinsicWidth();
	            bitmapHeight = drawable.getIntrinsicHeight();
		    if(bitmapWidth <= 0 || bitmapHeight <= 0){
		    	//
 		    }else{
		    	useWidgetPreview = true;
 		    }
	        } 

		if(useWidgetPreview == false) {
	            // Generate a preview image if we couldn't load one
	            if (cellHSpan < 1) cellHSpan = 1;
	            if (cellVSpan < 1) cellVSpan = 1;

	            BitmapDrawable previewDrawable = (BitmapDrawable) mResources
	                    .getDrawable(R.drawable.widget_preview_tile);
	            final int previewDrawableWidth = previewDrawable
	                    .getIntrinsicWidth();
	            final int previewDrawableHeight = previewDrawable
	                    .getIntrinsicHeight();
	            bitmapWidth = previewDrawableWidth * cellHSpan; // subtract 2 dips
	            bitmapHeight = previewDrawableHeight * cellVSpan;

	            defaultPreview = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
	                    Config.ARGB_8888);
	            final Canvas c = mCachedAppWidgetPreviewCanvas.get();
	            c.setBitmap(defaultPreview);
	            previewDrawable.setBounds(0, 0, bitmapWidth, bitmapHeight);
		    //
		    final Bitmap previewBitmap = previewDrawable.getBitmap();
		    final BitmapShader shader = new BitmapShader(previewBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		    final Paint shaderPaint = new Paint();
		    shaderPaint.setShader(shader);
		    c.drawPaint(shaderPaint);
		    //
	            //previewDrawable.setTileModeXY(Shader.TileMode.REPEAT,
	            //       Shader.TileMode.REPEAT);
	            // previewDrawable.draw(c);
		    //
	            c.setBitmap(null);

	            // Draw the icon in the top left corner
	            int minOffset = (int) (mAppIconSize * 0.25f);
	            int smallestSide = Math.min(bitmapWidth, bitmapHeight);
	            float iconScale = Math.min((float) smallestSide
	                    / (mAppIconSize + 2 * minOffset), 1f);

	            try {
	                Drawable icon = null;
	                int hoffset =
	                        (int) ((previewDrawableWidth - mAppIconSize * iconScale) / 2);
	                int yoffset =
	                        (int) ((previewDrawableHeight - mAppIconSize * iconScale) / 2);
	                if (iconId > 0) {
	                    icon = mIconCache.getFullResIcon(packageName, iconId);
	                }
	                Resources resources = mResources;
	                if (icon != null) {
	                    renderDrawableToBitmap(icon, defaultPreview, hoffset,
	                            yoffset, (int) (mAppIconSize * iconScale),
	                            (int) (mAppIconSize * iconScale));
	                }
	            } catch (Resources.NotFoundException e) {
	            }
	        }

	        // Scale to fit width only - let the widget preview be clipped in the
	        // vertical dimension
	        float scale = 1f;
	        if (bitmapWidth > maxWidth) {
	            scale = maxWidth / (float) bitmapWidth;
	        }
	        if (scale != 1f) {
	            bitmapWidth = (int) (scale * bitmapWidth);
	            bitmapHeight = (int) (scale * bitmapHeight);
	        }

	        Bitmap preview = Bitmap.createBitmap(bitmapWidth, bitmapHeight,
	                Config.ARGB_8888);

	        // Draw the scaled preview into the final bitmap
	        if (widgetPreviewExists) {
	            renderDrawableToBitmap(drawable, preview, 0, 0, bitmapWidth,
	                    bitmapHeight);
	        } else {
	            final Canvas c = mCachedAppWidgetPreviewCanvas.get();
	            final Rect src = mCachedAppWidgetPreviewSrcRect.get();
	            final Rect dest = mCachedAppWidgetPreviewDestRect.get();
	            c.setBitmap(preview);
	            src.set(0, 0, defaultPreview.getWidth(), defaultPreview.getHeight());
	            dest.set(0, 0, preview.getWidth(), preview.getHeight());

	            Paint p = mCachedAppWidgetPreviewPaint.get();
	            if (p == null) {
	                p = new Paint();
	                p.setFilterBitmap(true);
	                mCachedAppWidgetPreviewPaint.set(p);
	            }
	            c.drawBitmap(defaultPreview, src, dest, p);
	            c.setBitmap(null);
	        }
	        return preview;
	 }
	
	 private void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x, int y, int w, int h) {
	    renderDrawableToBitmap(d, bitmap, x, y, w, h, 1f);
	 }

	 private void renderDrawableToBitmap(Drawable d, Bitmap bitmap, int x, int y, int w, int h,
	            float scale) {
	        if (bitmap != null) {
	            Canvas c = new Canvas(bitmap);
	            c.scale(scale, scale);
	            Rect oldBounds = d.copyBounds();
	            d.setBounds(x, y, x + w, y + h);
	            d.draw(c);
	            d.setBounds(oldBounds); // Restore the bounds
	            c.setBitmap(null);
	        }
	 }	 
	 
	    @Override
	    public boolean onLongClick(View v) {
	    	
	    	 return beginDragging(v);
	    }
	 
    /*
     * implementation
     */
    @Override
    protected void determineDraggingStart(android.view.MotionEvent ev) {
        // Disable dragging by pulling an app down for now.
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 if (v instanceof PagedViewWidget){
			 final PendingAddItemInfo ItemInfo = (PendingAddItemInfo) v.getTag();
		     if (ItemInfo instanceof PendingAddItemInfo) {
		        final PendingAddItemInfo pendingInfo = (PendingAddItemInfo) ItemInfo;
                int span[] = new int[2];
                span[0] = ItemInfo.spanX;
                span[1] = ItemInfo.spanY;
		        mLauncher.addAppWidgetFromDrop((PendingAddWidgetInfo) pendingInfo,
		        		LauncherSettings.Favorites.CONTAINER_DESKTOP, mLauncher.getCurrentWorkspaceScreen(), null, span, null);
	    	 }
		 }
		
	}

	@Override
	public void onShortPress(View v) {
		// TODO Auto-generated method stub
        if (mCreateWidgetInfo != null) {
            // Just in case the cleanup process wasn't properly executed. This shouldn't happen.
            cleanupWidgetPreloading(false);
        }
        mCreateWidgetInfo = new PendingAddWidgetInfo((PendingAddWidgetInfo) v.getTag());
        preloadWidget(mCreateWidgetInfo);		
	}
	
	@Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return FocusHelper.handleAppsCustomizeKeyEvent(v,  keyCode, event);
    }
    
	@Override
	public void cleanUpShortPress(View v) {
		// TODO Auto-generated method stub
	 	if (!mDraggingWidget) {
            		cleanupWidgetPreloading(false);
        	}
	}

    public void onPackagesUpdated() {
        postDelayed(new Runnable() {
            public void run() {
                updatePackages();
            }
         }, 1500);
    }
    
    public void updatePackages() {
		// Get the list of widgets
		mWidgets.clear();
		List<AppWidgetProviderInfo> widgets =
	            AppWidgetManager.getInstance(mLauncher).getInstalledProviders();
		for (AppWidgetProviderInfo widget : widgets) {
			if (widget.minWidth > 0 && widget.minHeight > 0) {
				int[] spanXY = Launcher.getSpanForWidget(mLauncher, widget);
				int[] minSpanXY = Launcher.getMinSpanForWidget(mLauncher, widget);
                		int minSpanX = Math.min(spanXY[0], minSpanXY[0]);
                		int minSpanY = Math.min(spanXY[1], minSpanXY[1]);
				if (minSpanX <= LauncherModel.getCellCountX() &&
                        		minSpanY <= LauncherModel.getCellCountY()) {
					mWidgets.add(widget);
				}
			}
		}
		updatePageCounts();
    	invalidatePageData();
    }
    
    private void updatePageCounts() {
    	mNumPages = (int) Math.ceil(mWidgets.size() /
                (float) (mWidgetCountX * mWidgetCountY));
    }    
    
    
	public void setup(Launcher launcher, DragController dragController) {
		// TODO Auto-generated method stub
		mLauncher = launcher;
		mDragController = dragController;
	}
	
    @Override
    protected boolean beginDragging(final View v) {
	if (!super.beginDragging(v)) return false;
	
    	if (v instanceof PagedViewWidget) {
            if (!beginDraggingWidget(v)) {
                return false;
            }
    	}
    	return true;
    }
	
	
	private boolean beginDraggingWidget(final View v) {
		mDraggingWidget = true;
		 ImageView image = (ImageView) v.findViewById(R.id.widget_preview);
		 PendingAddItemInfo createItemInfo = (PendingAddItemInfo) v.getTag();
	     if (image.getDrawable() == null) {
		mDraggingWidget = false;
	         return false;
	     }
	     
	     Bitmap preview;
	     Bitmap outline;
	     float scale = 1f;
	     if (createItemInfo instanceof PendingAddWidgetInfo){
	            // This can happen in some weird cases involving multi-touch. We can't start dragging
	            // the widget if this is null, so we break out.
	            if (mCreateWidgetInfo == null) {
	                return false;
	            }

	            PendingAddWidgetInfo createWidgetInfo = mCreateWidgetInfo;
	            createItemInfo = createWidgetInfo;
	            int spanX = createItemInfo.spanX;
	            int spanY = createItemInfo.spanY;
	            int[] size = mLauncher.getWorkspace().estimateItemSize(spanX, spanY,
	                    createWidgetInfo, true);

	            FastBitmapDrawable previewDrawable = (FastBitmapDrawable) image.getDrawable();
	            float minScale = 1.25f;
	            int maxWidth = Math.min((int) (previewDrawable.getIntrinsicWidth() * minScale), size[0]);
	            int maxHeight = Math.min((int) (previewDrawable.getIntrinsicHeight() * minScale), size[1]);
	            preview = getWidgetPreview(createWidgetInfo.componentName, createWidgetInfo.previewImage,
	                    createWidgetInfo.icon, spanX, spanY, maxWidth, maxHeight);

	            // Determine the image view drawable scale relative to the preview
	            float[] mv = new float[9];
	            Matrix m = new Matrix();
	            m.setRectToRect(
	                    new RectF(0f, 0f, (float) preview.getWidth(), (float) preview.getHeight()),
	                    new RectF(0f, 0f, (float) previewDrawable.getIntrinsicWidth(),
	                            (float) previewDrawable.getIntrinsicHeight()),
	                    Matrix.ScaleToFit.START);
	            m.getValues(mv);
	            scale = (float) mv[0];
	        }else{
	            PendingAddShortcutInfo createShortcutInfo = (PendingAddShortcutInfo) v.getTag();
	            Drawable icon = mIconCache.getFullResIcon(createShortcutInfo.shortcutActivityInfo);
	            preview = Bitmap.createBitmap(icon.getIntrinsicWidth(),
	                        icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

	            mCanvas.setBitmap(preview);
	            mCanvas.save();
	            renderDrawableToBitmap(icon, preview, 0, 0,
	                        icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
	            mCanvas.restore();
	            mCanvas.setBitmap(null);
	            createItemInfo.spanX = createItemInfo.spanY = 1;
	    }
	     
	    Paint alphaClipPaint = null;
	    if (createItemInfo instanceof PendingAddWidgetInfo) {
	            if (((PendingAddWidgetInfo) createItemInfo).previewImage != 0) {
	                MaskFilter alphaClipTable = TableMaskFilter.CreateClipTable(0, 255);
	                alphaClipPaint = new Paint();
	                alphaClipPaint.setMaskFilter(alphaClipTable);
	            }
	    }

	        // Save the preview for the outline generation, then dim the preview
	        outline = Bitmap.createScaledBitmap(preview, preview.getWidth(), preview.getHeight(),
	                false);

	        // Start the drag
	    alphaClipPaint = null;
	    mLauncher.lockScreenOrientation();
	    mLauncher.getWorkspace().onDragStartedWithItem(createItemInfo, outline, false);
	    mDragController.startDrag(image, preview, this, createItemInfo,
	                DragController.DRAG_ACTION_COPY, null, scale);
	    outline.recycle();
	    preview.recycle();
   	    return true;
	}
	
    Bundle getDefaultOptionsForWidget(Launcher launcher, PendingAddWidgetInfo info) {
        Bundle options = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            AppWidgetResizeFrame.getWidgetSizeRanges(mLauncher, info.spanX, info.spanY, mTmpRect);
            Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(mLauncher,
                    info.componentName, null);

            float density = getResources().getDisplayMetrics().density;
            int xPaddingDips = (int) ((padding.left + padding.right) / density);
            int yPaddingDips = (int) ((padding.top + padding.bottom) / density);

            options = new Bundle();
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                    mTmpRect.left - xPaddingDips);
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                    mTmpRect.top - yPaddingDips);
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                    mTmpRect.right - xPaddingDips);
            options.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                    mTmpRect.bottom - yPaddingDips);
        }
        return options;
    }

    private void preloadWidget(final PendingAddWidgetInfo info) {
        final AppWidgetProviderInfo pInfo = info.info;
 	final Bundle options = getDefaultOptionsForWidget(mLauncher, info);
        if (LauncherLog.DEBUG) {
        	LauncherLog.d(TAG, "preloadWidget info = " + info + ", pInfo = " + pInfo + 
        			", pInfo.configure = " + pInfo.configure);
        }

        if (pInfo.configure != null) {
	    info.bindOptions = options;
            return;
        }

        mWidgetCleanupState = WIDGET_PRELOAD_PENDING;
        mBindWidgetRunnable = new Runnable() {
            @Override
            public void run() {
                mWidgetLoadingId = mLauncher.getAppWidgetHost().allocateAppWidgetId();
                // Options will be null for platforms with JB or lower, so this serves as an
                // SDK level check.
                if (options == null) {
                    if (AppWidgetManager.getInstance(mLauncher).bindAppWidgetIdIfAllowed(
                            mWidgetLoadingId, info.componentName)) {
                        mWidgetCleanupState = WIDGET_BOUND;
                    }
                } else {
                    if (AppWidgetManager.getInstance(mLauncher).bindAppWidgetIdIfAllowed(
                            mWidgetLoadingId, info.componentName, options)) {
                        mWidgetCleanupState = WIDGET_BOUND;
                    }
                }
            }
        };
        post(mBindWidgetRunnable);

        mInflateWidgetRunnable = new Runnable() {
            @Override
            public void run() {
		if(mWidgetCleanupState != WIDGET_BOUND){
			return;		
		}
                AppWidgetHostView hostView = mLauncher.
                        getAppWidgetHost().createView(getContext(), mWidgetLoadingId, pInfo);
                info.boundWidget = hostView;
                mWidgetCleanupState = WIDGET_INFLATED;
                hostView.setVisibility(INVISIBLE);
                int[] unScaledSize = mLauncher.getWorkspace().estimateItemSize(info.spanX,
                        info.spanY, info, false);

                // We want the first widget layout to be the correct size. This will be important
                // for width size reporting to the AppWidgetManager.
                DragLayer.LayoutParams lp = new DragLayer.LayoutParams(unScaledSize[0],
                        unScaledSize[1]);
                lp.x = lp.y = 0;
                lp.customPosition = true;
                hostView.setLayoutParams(lp);
                mLauncher.getDragLayer().addView(hostView);
            }
        };
        post(mInflateWidgetRunnable);
    }	
    
    private void cleanupWidgetPreloading(boolean widgetWasAdded) {

        if (!widgetWasAdded) {
            // If the widget was not added, we may need to do further cleanup.
            PendingAddWidgetInfo info = mCreateWidgetInfo;
            mCreateWidgetInfo = null;

            if (mWidgetCleanupState == WIDGET_PRELOAD_PENDING) {
                // We never did any preloading, so just remove pending callbacks to do so
                removeCallbacks(mBindWidgetRunnable);
                removeCallbacks(mInflateWidgetRunnable);
            } else if (mWidgetCleanupState == WIDGET_BOUND) {
                 // Delete the widget id which was allocated
                if (mWidgetLoadingId != -1) {
                    mLauncher.getAppWidgetHost().deleteAppWidgetId(mWidgetLoadingId);
                }

                // We never got around to inflating the widget, so remove the callback to do so.
                removeCallbacks(mInflateWidgetRunnable);
            } else if (mWidgetCleanupState == WIDGET_INFLATED) {
                // Delete the widget id which was allocated
                if (mWidgetLoadingId != -1) {
                    mLauncher.getAppWidgetHost().deleteAppWidgetId(mWidgetLoadingId);
                }

                // The widget was inflated and added to the DragLayer -- remove it.
                AppWidgetHostView widget = info.boundWidget;
                mLauncher.getDragLayer().removeView(widget);
            }
        }
        mWidgetCleanupState = WIDGET_NO_CLEANUP_REQUIRED;
        mWidgetLoadingId = -1;
        mCreateWidgetInfo = null;
        PagedViewWidget.resetShortPressTarget();
    }

	@Override
	public boolean supportsFlingToDelete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onFlingToDeleteCompleted() {
		// TODO Auto-generated method stub
        endDragging(null, true, true);
        cleanupWidgetPreloading(false);
        mDraggingWidget = false;
	}

	@Override
	public void onDropCompleted(View target, DragObject d,
			boolean isFlingToDelete, boolean success) {
		// TODO Auto-generated method stub
		 if (isFlingToDelete) {
			return;
		 }
		 endDragging(target, false, success);
		 if (!success) {
	            boolean showOutOfSpaceMessage = false;
	            if (target instanceof Workspace) {
	                int currentScreen = mLauncher.getCurrentWorkspaceScreen();
	                Workspace workspace = (Workspace) target;
	                CellLayout layout = (CellLayout) workspace.getChildAt(currentScreen);
	                ItemInfo itemInfo = (ItemInfo) d.dragInfo;
	                if (layout != null) {
	                    layout.calculateSpans(itemInfo);
	                    showOutOfSpaceMessage =
	                            !layout.findCellForSpan(null, itemInfo.spanX, itemInfo.spanY);
	                }
	                /// M: Display an error message if the drag failed due to exist one IMTKWidget 
	                /// which providerName equals the providerName of the dragInfo.
	                if (d.dragInfo instanceof PendingAddWidgetInfo) {
	                    PendingAddWidgetInfo info = (PendingAddWidgetInfo) d.dragInfo;
	                    if (workspace.searchIMTKWidget(workspace, info.componentName.getClassName()) != null) {
	                        mLauncher.showOnlyOneWidgetMessage(info);
	                    }
	                }
	            }
	            if (showOutOfSpaceMessage) {
	                mLauncher.showOutOfSpaceMessage(false);
	            }

	            d.deferDragViewCleanupPostAnimation = false;
	        }
	     cleanupWidgetPreloading(success);
	     mDraggingWidget = false;		 
	}    
	
    private void endDragging(View target, boolean isFlingToDelete, boolean success) {
		//do nothing
    }
}
