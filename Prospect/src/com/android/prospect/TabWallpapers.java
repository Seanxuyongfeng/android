package com.android.prospect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.net.Uri;
import android.os.Bundle;
import android.os.PatternMatcher;

import android.os.Parcelable;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;

import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View.MeasureSpec;
import com.android.prospect.R;
import com.android.prospect.DropTarget.DragObject;

public class TabWallpapers extends PagedViewWithDraggableItems implements  View.OnClickListener, 
		View.OnKeyListener, PagedViewIcon.PressedCallback,
		DragSource{
	private static final String TAG = "TabWallpapers";
	
	private int mPages;
	    
	private final LayoutInflater mLayoutInflater;
	    
	private final Context mContext;

	private static int WIDTH = 720;
		
	private static int HEIGHT = 180;
		
	private static final int ROWS = 1;

	private static final int COLUMNS = 4;

	private Launcher mLauncher;
		
    private PackageManager mPm;

	private boolean mShowExtended = false;

	private List<DisplayResolveInfo> mList;

	private List<ResolveInfo> mCurrentResolveList;
	
	private List<ResolveInfo> mBaseResolveList;
		
	private final boolean mAlwaysUseOption = false;

	private final int mLaunchedFromUid;

	private final Intent mIntent;

	private int mIconSize;
		
    private final Resources mResources;

	private int mIconDpi;

	Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
/*
	private final PackageMonitor mPackageMonitor = new PackageMonitor();
	
	static final IntentFilter mFilter = new IntentFilter();
	static {
		mFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		mFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
		mFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
		mFilter.addDataScheme("package");
	}
*/


 	private final class DisplayResolveInfo {
		ResolveInfo ri;
		CharSequence displayLabel;
		Drawable displayIcon;
		CharSequence extendedInfo;
		Intent origIntent;

		DisplayResolveInfo(ResolveInfo pri, CharSequence pLabel,
		        CharSequence pInfo, Intent pOrigIntent) {
		    ri = pri;
		    displayLabel = pLabel;
		    extendedInfo = pInfo;
		    origIntent = pOrigIntent;
		}
   	}

	    public TabWallpapers(Context context) {
	        this(context, null);
	    }

	    public TabWallpapers(Context context, AttributeSet attrs) {
	        this(context, attrs, 0);
	    }

	    public TabWallpapers(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
		mContext = context;
		mLaunchedFromUid = -1;
		final ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		mIconSize = am.getLauncherLargeIconSize();
		mIconDpi = am.getLauncherLargeIconDensity();
		mPm = mContext.getPackageManager();
            	mIntent = new Intent(pickWallpaper);
            	mIntent.setComponent(null);
	        mLayoutInflater = LayoutInflater.from(context);
		List<ResolveInfo> list = mPm.queryIntentActivities(pickWallpaper, 0);
		mBaseResolveList = list;
               mResources = context.getResources();
               WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
               WIDTH = wm.getDefaultDisplay().getWidth();
               HEIGHT = wm.getDefaultDisplay().getHeight();
	    	mCellCountX = COLUMNS;
	    	mCellCountY = ROWS;    	
	    }
	    
		@Override
		public void syncPages() {
			// TODO Auto-generated method stub
			removeAllViews();
			Context context = mContext;
	        for (int i = 0; i < mPages; ++i) {
	            PagedViewCellLayout layout = new PagedViewCellLayout(context);
	            setupPage(layout);
	            addView(layout);
	        }
		}
		
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        int width = MeasureSpec.getSize(widthMeasureSpec);
	        int height = MeasureSpec.getSize(heightMeasureSpec);
	        if (!isDataReady()) {
	            if (!mList.isEmpty()) {
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
			 int endIndex = Math.min(startIndex + numCells, mList.size());
			 PagedViewCellLayout layout = (PagedViewCellLayout) getPageAt(page);
			 layout.removeAllViewsOnPage();
		     for (int i = startIndex; i < endIndex; ++i) {
			            LinearLayout icon = (LinearLayout) mLayoutInflater.inflate(
		                    R.layout.wallpappers_item, layout, false);
			    icon.setTag(i);
			    bindView(icon, mList.get(i));
		            icon.setOnClickListener(this);
		            icon.setOnTouchListener(this);
		            icon.setOnKeyListener(this);

		            int index = i - startIndex;
		            int x = index % mCellCountX;
		            int y = index / mCellCountX;
		            layout.addViewToCellLayout(icon, -1, i, new PagedViewCellLayout.LayoutParams(x,y, 1,1));
		     }
 		}
		
    @Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
	rebuildList();
	updatePageCounts();
	invalidateOnDataChange();
    }	


	private final void bindView(View view, DisplayResolveInfo info) {
	    ImageView icon1 = (ImageView)view.findViewById(R.id.icon);
	    ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) icon1.getLayoutParams();
	    lp.width = lp.height = mIconSize;	  
            TextView text = (TextView)view.findViewById(R.id.text1);
            TextView text2 = (TextView)view.findViewById(R.id.text2);
            ImageView icon = (ImageView)view.findViewById(R.id.icon);
            text.setText(info.displayLabel);
            
            if (mShowExtended) {
                text2.setVisibility(View.VISIBLE);
                text2.setText(info.extendedInfo);
            } else {
                text2.setVisibility(View.GONE);
            }
            if (info.displayIcon == null) {
                info.displayIcon = loadIconForResolveInfo(info.ri);
            }
            icon.setImageDrawable(info.displayIcon);            
	}

	    @Override
	    public boolean onLongClick(View v) {
	    	
	    	 return true;
	    }
		
	    @Override
	    protected boolean beginDragging(final View v) {
	    	
	    	return true;
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
 			startSelected((Integer)(v.getTag()), false);
		}

		@Override
		public void iconPressed(PagedViewIcon icon) {
			// TODO Auto-generated method stub
			
		}

	public void setup(Launcher launcher){
		mLauncher = launcher;
	}

		private void invalidateOnDataChange() {
			  if (!isDataReady()) {
				  requestLayout();
			  }else{
				  invalidatePageData();
			  }
		}
		
		private void updatePageCounts() {
			mPages = (int) Math.ceil((float) mList.size() / (mCellCountX * mCellCountY));
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

		}
		
	    private void endDragging(View target, boolean isFlingToDelete, boolean success) {
			//do nothing
	    }

	void startSelected(int which, boolean always) {
		ResolveInfo ri = resolveInfoForPosition(which);
		Intent intent = intentForPosition(which);
		onIntentSelected(ri, intent, always);
	}
	 
		private void rebuildList() {
		 if (mBaseResolveList != null) {
	                mCurrentResolveList = mBaseResolveList;
	         } else{
	        	 mCurrentResolveList = mPm.queryIntentActivities(
	                        mIntent, PackageManager.MATCH_DEFAULT_ONLY
	                        | (mAlwaysUseOption ? PackageManager.GET_RESOLVED_FILTER : 0));
	        	 if (mCurrentResolveList != null){
	        		  for (int i = mCurrentResolveList.size()-1; i >= 0; i--) {
	        			  ActivityInfo ai = mCurrentResolveList.get(i).activityInfo;
	        	/*
	        	 * this is hide in api
	        			int granted = ActivityManager.checkComponentPermission(
	                                ai.permission, mLaunchedFromUid,
	                                ai.applicationInfo.uid, ai.exported);
	                      if (granted != PackageManager.PERMISSION_GRANTED) {
                            // Access not allowed!
                            mCurrentResolveList.remove(i);
                        }    
	                */
	        		  }
	        	 }
	         }
			 
			 int N;
			 if ((mCurrentResolveList != null) && ((N = mCurrentResolveList.size()) > 0)) {
	             // Only display the first matches that are either of equal
	             // priority or have asked to be default options.
				 ResolveInfo r0 = mCurrentResolveList.get(0);
				 for (int i=1; i<N; i++) {
					 ResolveInfo ri = mCurrentResolveList.get(i);
	                 if (r0.priority != ri.priority ||
	                       r0.isDefault != ri.isDefault) {
	                       while (i < N) {
	                           mCurrentResolveList.remove(i);
	                           N--;
	                       }
	                 }
	                 
	                 
				 }
	             if (N > 1) {
	                    ResolveInfo.DisplayNameComparator rComparator =
	                            new ResolveInfo.DisplayNameComparator(mPm);
	                    Collections.sort(mCurrentResolveList, rComparator);
	             }
	             
	             mList = new ArrayList<DisplayResolveInfo>();
	             /*
	             for(int i = 0; i < N; i++){
	            	 ResolveInfo oldri = mCurrentResolveList.get(i);
	            	 ActivityInfo ai = mCurrentResolveList.get(i).activityInfo;
	            	 if(oldri == null){
                         Log.w("ResolverActivity", "No activity found for "
                                 + oldri);
	            		 continue;
	            	 }
	            	 ResolveInfo ri = new ResolveInfo();
                     ri.activityInfo = ai;
                     ri.resolvePackageName = oldri.resolvePackageName;
                     ri.labelRes = oldri.labelRes;
                     ri.nonLocalizedLabel = oldri.nonLocalizedLabel;
                     ri.icon = oldri.icon;
                     mList.add(new DisplayResolveInfo(ri,
                             ri.loadLabel(getPackageManager()), null, null));
	            	 
	             }*/
	             
	             // Check for applications with same name and use application name or
	             // package name if necessary
				 r0 = mCurrentResolveList.get(0);
	             int start = 0;
	             CharSequence r0Label =  r0.loadLabel(mPm);
	             mShowExtended = false;
	             for (int i = 1; i < N; i++) {
	                 if (r0Label == null) {
	                    r0Label = r0.activityInfo.packageName;
	                 }
	                 ResolveInfo ri = mCurrentResolveList.get(i);
	                 CharSequence riLabel = ri.loadLabel(mPm);
	                 if (riLabel == null) {
	                     riLabel = ri.activityInfo.packageName;
	                 }
	                 if (riLabel.equals(r0Label)) {
	                    continue;
	                 }
	                  
	                 processGroup(mCurrentResolveList, start, (i-1), r0, r0Label);
	                 r0 = ri;
	                 r0Label = riLabel;
	                 start = i;
	             }
	             // Process last group
	             processGroup(mCurrentResolveList, start, (N-1), r0, r0Label);
			 }
		}
		
		private void processGroup(List<ResolveInfo> rList, int start, int end, ResolveInfo ro,
	                CharSequence roLabel) {
			// Process labels from start to i
			int num = end - start+1;
            if (num == 1) {
                // No duplicate labels. Use label for entry at start
                mList.add(new DisplayResolveInfo(ro, roLabel, null, null));
            } else {
            	//mShowExtended = true;
                boolean usePkg = false;
                CharSequence startApp = ro.activityInfo.applicationInfo.loadLabel(mPm);
                if (startApp == null) {
                    usePkg = true;
                }
                
                if (!usePkg) {
                    // Use HashSet to track duplicates
                    HashSet<CharSequence> duplicates =
                        new HashSet<CharSequence>();
                    duplicates.add(startApp);
                    for (int j = start+1; j <= end ; j++) {
                        ResolveInfo jRi = rList.get(j);
                        CharSequence jApp = jRi.activityInfo.applicationInfo.loadLabel(mPm);
                        if ( (jApp == null) || (duplicates.contains(jApp))) {
                            usePkg = true;
                            break;
                        } else {
                            duplicates.add(jApp);
                        }
                    }
                    // Clear HashSet for later use
                    duplicates.clear();
                }

                for (int k = start; k <= end; k++) {
                    ResolveInfo add = rList.get(k);
                    if (usePkg) {
                        // Use application name for all entries from start to end-1
                        mList.add(new DisplayResolveInfo(add, roLabel,
                                add.activityInfo.packageName, null));
                    } else {
                        // Use package name for all entries from start to end-1
                        mList.add(new DisplayResolveInfo(add, roLabel,
                                add.activityInfo.applicationInfo.loadLabel(mPm), null));
                    }
                }               
            }
		 }	

	public void handlePackagesChanged() {
	    List<ResolveInfo> list = mPm.queryIntentActivities(pickWallpaper, 0);
	    mBaseResolveList = list;
	    final int oldItemCount = mList.size();
	    rebuildList();
	    updatePageCounts();
	    invalidateOnDataChange();
            if (mList == null || mList.size() <= 0) {
                // We no longer have any items...  just return;
                return;
            }
	    /*
            final int newItemCount = mList.size();
            if (newItemCount != oldItemCount) {
                /// if the last selected item has been selected and uninstalled
                /// we should disable always and once buttons since it has beend removed @{
            	 if (mAlwaysUseOption) {
           		 
            	 }
            }
	*/
	 
	}

	Drawable loadIconForResolveInfo(ResolveInfo ri) {
	     Drawable dr;
	     try {
	            if (ri.resolvePackageName != null && ri.icon != 0) {
	                dr = getIcon(mPm.getResourcesForApplication(ri.resolvePackageName), ri.icon);
	                if (dr != null) {
	                    return dr;
	                }
	            }
	            final int iconRes = ri.getIconResource();
	            if (iconRes != 0) {
	                dr = getIcon(mPm.getResourcesForApplication(ri.activityInfo.packageName), iconRes);
	                if (dr != null) {
	                    return dr;
	                }
	            }
	        } catch (NameNotFoundException e) {
	            Log.e(TAG, "Couldn't find resources for package", e);
	        }
	        return ri.loadIcon(mPm);
	 }
	    
	 Drawable getIcon(Resources res, int resId) {
	        Drawable result;
	        try {
	            result = res.getDrawableForDensity(resId, mIconDpi);
	        } catch (Resources.NotFoundException e) {
	            result = null;
	        }

	        return result;
	}	 

	protected void onIntentSelected(ResolveInfo ri, Intent intent, boolean alwaysCheck) {
		      if (alwaysCheck) {
		            // Build a reasonable intent filter, based on what matched.
		            IntentFilter filter = new IntentFilter();

		            if (intent.getAction() != null) {
		                filter.addAction(intent.getAction());
		            }
		            Set<String> categories = intent.getCategories();
		            if (categories != null) {
		                for (String cat : categories) {
		                    filter.addCategory(cat);
		                }
		            }
		            filter.addCategory(Intent.CATEGORY_DEFAULT);

		            int cat = ri.match&IntentFilter.MATCH_CATEGORY_MASK;
		            Uri data = intent.getData();
		            if (cat == IntentFilter.MATCH_CATEGORY_TYPE) {
		                String mimeType = intent.resolveType(mLauncher);
		                if (mimeType != null) {
		                    try {
		                        filter.addDataType(mimeType);
		                    } catch (IntentFilter.MalformedMimeTypeException e) {
		                        Log.w("ResolverActivity", e);
		                        filter = null;
		                    }
		                }
		            }
		            if (data != null && data.getScheme() != null) {
		                // We need the data specification if there was no type,
		                // OR if the scheme is not one of our magical "file:"
		                // or "content:" schemes (see IntentFilter for the reason).
		                if (cat != IntentFilter.MATCH_CATEGORY_TYPE
		                        || (!"file".equals(data.getScheme())
		                                && !"content".equals(data.getScheme()))) {
		                    filter.addDataScheme(data.getScheme());
		    
		                    // Look through the resolved filter to determine which part
		                    // of it matched the original Intent.
		                    Iterator<IntentFilter.AuthorityEntry> aIt = ri.filter.authoritiesIterator();
		                    if (aIt != null) {
		                        while (aIt.hasNext()) {
		                            IntentFilter.AuthorityEntry a = aIt.next();
		                            if (a.match(data) >= 0) {
		                                int port = a.getPort();
		                                filter.addDataAuthority(a.getHost(),
		                                        port >= 0 ? Integer.toString(port) : null);
		                                break;
		                            }
		                        }
		                    }
		                    Iterator<PatternMatcher> pIt = ri.filter.pathsIterator();
		                    if (pIt != null) {
		                        String path = data.getPath();
		                        while (path != null && pIt.hasNext()) {
		                            PatternMatcher p = pIt.next();
		                            if (p.match(path)) {
		                                filter.addDataPath(p.getPath(), p.getType());
		                                break;
		                            }
		                        }
		                    }
		                }
		            }

		            if (filter != null) {
		                final int N = mList.size();
		                ComponentName[] set = new ComponentName[N];
		                int bestMatch = 0;
		                for (int i=0; i<N; i++) {
		                    ResolveInfo r = mList.get(i).ri;
		                    set[i] = new ComponentName(r.activityInfo.packageName,
		                            r.activityInfo.name);
		                    if (r.match > bestMatch) bestMatch = r.match;
		                }
		                mContext.getPackageManager().addPreferredActivity(filter, bestMatch, set,
		                        intent.getComponent());
		            }
		        }

		        if (intent != null) {
		            mContext.startActivity(intent);
		        }			
		}
		
        public ResolveInfo resolveInfoForPosition(int position) {
            if (mList == null) {
                return null;
            }

            return mList.get(position).ri;
        }
        
        public Intent intentForPosition(int position) {
            if (mList == null) {
                return null;
            }

            DisplayResolveInfo dri = mList.get(position);
            
            Intent intent = new Intent(dri.origIntent != null
                    ? dri.origIntent : mIntent);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    |Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            ActivityInfo ai = dri.ri.activityInfo;
            intent.setComponent(new ComponentName(
                    ai.applicationInfo.packageName, ai.name));
            return intent;
        }
		
	protected void onRestart() {
		
	}
		
	protected void onStop() {

	}

    	protected void onResume() {
	
    	}
		
}
