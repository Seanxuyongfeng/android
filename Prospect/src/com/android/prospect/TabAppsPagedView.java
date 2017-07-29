package com.android.prospect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import com.android.prospect.R;
import com.android.prospect.DropTarget.DragObject;

public class TabAppsPagedView extends PagedViewWithDraggableItems implements  View.OnClickListener, 
		View.OnKeyListener, PagedViewIcon.PressedCallback,
		DragSource{
	   private int mNumAppsPages;
	    
	   private final LayoutInflater mLayoutInflater;
	    
	   private final Context mContext;

	   private ArrayList<ApplicationInfo> mApps;
	    
		private static final int WIDTH = 720;
		
		private static final int HEIGHT = 1280;
		
	    private static final int ROWS = 3;
	
		private static final int COLUMNS = 5;

		private Launcher mLauncher;
		
	    public TabAppsPagedView(Context context) {
	        this(context, null);
	    }

	    public TabAppsPagedView(Context context, AttributeSet attrs) {
	        this(context, attrs, 0);
	    }

	    public TabAppsPagedView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
			mContext = context;
	        mApps = new ArrayList<ApplicationInfo>();
	        mLayoutInflater = LayoutInflater.from(context);
	    	mCellCountX = COLUMNS;
	    	mCellCountY = ROWS;    	
	    }
	    
		@Override
		public void syncPages() {
			// TODO Auto-generated method stub
			removeAllViews();
			Context context = mContext;
	        for (int i = 0; i < mNumAppsPages; ++i) {
			
		    PagedViewCellLayout layout = (PagedViewCellLayout) mLayoutInflater.inflate(R.layout.tab_apps_screen, this, false);
	            //PagedViewCellLayout layout = new PagedViewCellLayout(context);
	            setupPage(layout);
	            addView(layout);
	        }
		}
		
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        int width = MeasureSpec.getSize(widthMeasureSpec);
	        int height = MeasureSpec.getSize(heightMeasureSpec);
	        if (!isDataReady()) {
	            if (!mApps.isEmpty()) {
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
			 int numCells = mCellCountX * mCellCountY;
			 int startIndex = page * numCells;
			 int endIndex = Math.min(startIndex + numCells, mApps.size());
			 PagedViewCellLayout layout = (PagedViewCellLayout) getPageAt(page);
			 layout.removeAllViewsOnPage();
		     //ArrayList<Object> items = new ArrayList<Object>();
		     //ArrayList<Bitmap> images = new ArrayList<Bitmap>();
		     for (int i = startIndex; i < endIndex; ++i) {
		            ApplicationInfo info = mApps.get(i);
		            MTKAppIcon icon = (MTKAppIcon) mLayoutInflater.inflate(
		                    R.layout.item_tab_applications, layout, false);
		            icon.applyFromApplicationInfo(info, true, this);
		            icon.mAppIcon.setOnClickListener(this);
		            icon.mAppIcon.setOnLongClickListener(this);
		            icon.setOnTouchListener(this);
		            icon.mAppIcon.setOnKeyListener(this);

		            int index = i - startIndex;
		            int x = index % mCellCountX;
		            int y = index / mCellCountX;
		            layout.addViewToCellLayout(icon, -1, i, new PagedViewCellLayout.LayoutParams(x,y, 1,1));

		            //items.add(info);
		            //images.add(info.iconBitmap);
		     }

		     layout.createHardwareLayers();		     
		}
		
	    @Override
	    public boolean onLongClick(View v) {
	    	
	    	 return beginDragging(v);
	    }
		
	    @Override
	    protected boolean beginDragging(final View v) {
	    	
	    	if (!super.beginDragging(v)) return false;
	    	 
	    	if (v instanceof PagedViewIcon){
	    		beginDraggingApplication(v);
	    	}
	    	return true;
	    }
	    
	    private void beginDraggingApplication(View v) {
	        mLauncher.getWorkspace().onDragStartedWithItem(v);
	        mLauncher.getWorkspace().beginDragShared(v, this);
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
	        layout.setMinimumWidth(WIDTH);
	        layout.measure(widthSpec, heightSpec);
	        setVisibilityOnChildren(layout, View.VISIBLE);
	    }
	    
	    private void setVisibilityOnChildren(ViewGroup layout, int visibility) {
	        int childCount = layout.getChildCount();
	        for (int i = 0; i < childCount; ++i) {
	            layout.getChildAt(i).setVisibility(visibility);
	        }
	    }

    /*
     *  implementation
     */
    @Override
    protected void determineDraggingStart(android.view.MotionEvent ev) {
        // Disable dragging by pulling an app down for now.
    }
		@Override
		public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
	        if (v instanceof MTKAppIcon) {
	        	v = ((MTKAppIcon)v).mAppIcon;
	        }
	        
	        if (v instanceof PagedViewIcon) {
	        	final ApplicationInfo appInfo = (ApplicationInfo) v.getTag();
	        	CellLayout cellLayout = mLauncher.getCellLayout(LauncherSettings.Favorites.CONTAINER_DESKTOP, 
	        			mLauncher.getCurrentWorkspaceScreen());
	        	mLauncher.addExternalItemToScreen(appInfo, cellLayout);
	        }
		}

		@Override
		public void iconPressed(PagedViewIcon icon) {
			// TODO Auto-generated method stub
			
		}

		public void setup(Launcher launcher, DragController dragController) {
			// TODO Auto-generated method stub
			mLauncher = launcher;
		}

		public void setApps(ArrayList<ApplicationInfo> list) {
			// TODO Auto-generated method stub
			mApps = list;
			Collections.sort(mApps, LauncherModel.getAppNameComparator());
			reorderApps();
			updatePageCounts();
			invalidateOnDataChange();
		}

		private void invalidateOnDataChange() {
			  if (!isDataReady()) {
				  requestLayout();
			  }else{
				  invalidatePageData();
			  }
		}
		
		private void updatePageCounts() {
			mNumAppsPages = (int) Math.ceil((float) mApps.size() / (mCellCountX * mCellCountY));
		}
		
	    /**
	     * M: Reorder apps in applist.
	     */
	    public void reorderApps() {
	    	if(mApps.isEmpty()){
	    		return;
	    	}
	        ArrayList<ApplicationInfo> dataReorder = new ArrayList<ApplicationInfo>(
	                AllAppsList.DEFAULT_APPLICATIONS_NUMBER);
	        for (AllAppsList.TopPackage tp : AllAppsList.sTopPackages) {
	            for (ApplicationInfo ai : mApps) {
	                if (ai.componentName.getPackageName().equals(tp.packageName)
	                        && ai.componentName.getClassName().equals(tp.className)) {
	                    mApps.remove(ai);
	                    dataReorder.add(ai);
	                    break;
	                }
	            }
	        }
	        
	        for (AllAppsList.TopPackage tp : AllAppsList.sTopPackages) {
	            int newIndex = 0;
	            for (ApplicationInfo ai : dataReorder) {
	                if (ai.componentName.getPackageName().equals(tp.packageName)
	                        && ai.componentName.getClassName().equals(tp.className)) {
	                    newIndex = Math.min(Math.max(tp.order, 0), mApps.size());
	                    mApps.add(newIndex, ai);
	                    break;
	                }
	            }
	        }	        
	    }
	    
		public void addApps(ArrayList<ApplicationInfo> list) {
			// TODO Auto-generated method stub
	        addAppsWithoutInvalidate(list);
	        reorderApps();
	        updatePageCounts();
	        invalidateOnDataChange();
		}

	    private void addAppsWithoutInvalidate(ArrayList<ApplicationInfo> list) {
	        // We add it in place, in alphabetical order
	        int count = list.size();
	        for (int i = 0; i < count; ++i) {
	            ApplicationInfo info = list.get(i);
	            int index = Collections.binarySearch(mApps, info, LauncherModel.getAppNameComparator());
	            if (index < 0) {
	                mApps.add(-(index + 1), info);
	            }
	        }
	    }
	    
	    private void removeAppsWithoutInvalidate(ArrayList<ApplicationInfo> list) {
	        // loop through all the apps and remove apps that have the same component
	        int length = list.size();
	        for (int i = 0; i < length; ++i) {
	            ApplicationInfo info = list.get(i);
	            int removeIndex = findAppByComponent(mApps, info);
	            if (removeIndex > -1) {
	                mApps.remove(removeIndex);
	            }
	        }
	    }	
	    
	    private int findAppByComponent(List<ApplicationInfo> list, ApplicationInfo item) {
	        ComponentName removeComponent = item.intent.getComponent();
	        int length = list.size();
	        for (int i = 0; i < length; ++i) {
	            ApplicationInfo info = list.get(i);
	            if (info.intent.getComponent().equals(removeComponent)) {
	                return i;
	            }
	        }
	        return -1;
	    }
	    
		public void removeApps(ArrayList<String> list) {
			// TODO Auto-generated method stub
			removeAppsWithPackageNameWithoutInvalidate(list);
	        reorderApps();
	        updatePageCounts();
	        invalidateOnDataChange();
		}
		
	    private void removeAppsWithPackageNameWithoutInvalidate(ArrayList<String> packageNames) {
	        // loop through all the package names and remove apps that have the same package name
	        for (String pn : packageNames) {
	            int removeIndex = findAppByPackage(mApps, pn);
	            while (removeIndex > -1) {
	                mApps.remove(removeIndex);
	                removeIndex = findAppByPackage(mApps, pn);
	            }
	        }
	    }
	    
	    private int findAppByPackage(List<ApplicationInfo> list, String packageName) {
	        int length = list.size();
	        for (int i = 0; i < length; ++i) {
	            ApplicationInfo info = list.get(i);
	            if (ItemInfo.getPackageName(info.intent).equals(packageName)) {
	                /// M: we only remove items whose component is in disable state,
	                /// this is add to deal the case that there are more than one
	                /// activities with LAUNCHER category, and one of them is
	                /// disabled may cause all activities removed from app list.
	                final boolean isComponentEnabled = Utilities.isComponentEnabled(getContext(),
	                        info.intent.getComponent());
	                if (!isComponentEnabled) {
	                    return i;
	                }
	            }
	        }
	        return -1;
	    }	    
		public void updateApps(ArrayList<ApplicationInfo> list) {
			// TODO Auto-generated method stub
	        removeAppsWithoutInvalidate(list);
	        addAppsWithoutInvalidate(list);
	        updatePageCounts();
	        reorderApps();
	        invalidateOnDataChange();
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
		}

		@Override
		public void onDropCompleted(View target, DragObject d,
				boolean isFlingToDelete, boolean success) {
			// TODO Auto-generated method stub
	        if (isFlingToDelete) return;

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
		}
		
		public void updateAppsUnreadChanged(ComponentName component, int unreadNum) {
			updateUnreadNumInAppInfo(component, unreadNum);
			for (int i = 0; i < mNumAppsPages; i++) {
				PagedViewCellLayout cl = (PagedViewCellLayout) getPageAt(i);
				if (cl == null) {
					return;
				}
				final int count = cl.getPageChildCount();
				MTKAppIcon appIcon = null;
				ApplicationInfo appInfo = null;
				for (int j = 0; j < count; j++) {
					appIcon = (MTKAppIcon) cl.getChildOnPageAt(j);
					appInfo = (ApplicationInfo) appIcon.getTag();
					if (appInfo != null && appInfo.componentName.equals(component)) {
						appIcon.updateUnreadNum(unreadNum);
					}
				}
			}
		}


		public void updateAppsUnread() {
			updateUnreadNumInAppInfo(mApps);
			// Update apps which already shown in the customized pane.
			for (int i = 0; i < mNumAppsPages; i++) {
				PagedViewCellLayout cl = (PagedViewCellLayout) getPageAt(i);
				if (cl == null) {
					return;
				}
				final int count = cl.getPageChildCount();
				MTKAppIcon appIcon = null;
				ApplicationInfo appInfo = null;
				int unreadNum = 0;
				for (int j = 0; j < count; j++) {
					appIcon = (MTKAppIcon) cl.getChildOnPageAt(j);
					appInfo = (ApplicationInfo) appIcon.getTag();
					unreadNum = MTKUnreadLoader.getUnreadNumberOfComponent(appInfo.componentName);
					appIcon.updateUnreadNum(unreadNum);
				}
			}
		}
    
		private void updateUnreadNumInAppInfo(ComponentName component, int unreadNum) {
			final int size = mApps.size();
			ApplicationInfo appInfo = null;
			for (int i = 0; i < size; i++) {
				appInfo = mApps.get(i);
				if (appInfo.intent.getComponent().equals(component)) {
					appInfo.unreadNum = unreadNum;
				}
			}
		}


		public static void updateUnreadNumInAppInfo(final ArrayList<ApplicationInfo> apps) {
			final int size = apps.size();
			ApplicationInfo appInfo = null;
			for (int i = 0; i < size; i++) {
				appInfo = apps.get(i);
				appInfo.unreadNum = MTKUnreadLoader.getUnreadNumberOfComponent(appInfo.componentName);
			}
		}



	    private void endDragging(View target, boolean isFlingToDelete, boolean success) {
			//do nothing
	    }

}
