package com.android.prospect;

import android.animation.Animator;
import android.app.AlertDialog;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.ClipData;
import android.content.Context;
import android.content.res.Resources;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewDebug;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.widget.LinearLayout;
import com.android.prospect.R;


public class PreviewLayout extends ViewGroup implements View.OnLongClickListener/*, OnDragListener*/{
	private static final String TAG = "PreviewLayout";
	
	private final int mCountX;
	
	private final int mCountY;
	
    private int mCellWidth;
    
    private int mCellHeight;
    
    private int mWidthGap;
    
    private int mHeightGap;
    
    private int[] mEmptyCell = new int[2];
    
    private int[] mTargetCell = new int[2];
    
    private int[] mPreviousTargetCell = new int[2];

    private static final int ANIMATION_DURATION = 230;
    
    private final Context mContext;
    
    private final Resources mResources;
    
    private View mCurrenDragView;
    
    private View mPlustView;
    
    private WorkspacePreview mWorkspacePreview;
    
    private Drawable mDeleteEnter; 
    
    private Drawable mDeleteNormal;
    
    private static boolean mAnimating = false;
    
    private final OnDragListener mDragListener;
    
    private final OnDragListener mDeleteZoneListener;
    
    private int mHoverColor = 0;

    private AlertDialog mAlertDialog;

    private Alarm mReorderAlarm = new Alarm();

    public PreviewLayout(Context context) {
        this(context, null);
    }

    public PreviewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
	//initial the cell width and height
        mCellWidth = 214;
        mCellHeight = 322;
	//setup the gap
        mWidthGap = 17;
        mHeightGap = 25;
	//setup the capacity
        mCountX = 3;
        mCountY = 3;
	//get resources for later use
        mResources = context.getResources();
        mDragListener = new DragOverListener();
        mDeleteZoneListener = new DeleteListener();
	mHoverColor = context.getResources().getColor(R.color.delete_target_hover_tint);
    	mDeleteNormal = mResources.getDrawable(R.drawable.delete_zone_normal); 
    	mDeleteEnter = mResources.getDrawable(R.drawable.delete_zone_enter);
    }
    

    private void showDialog(){
	if(mAlertDialog == null){
		mAlertDialog = new AlertDialog.Builder(mContext, AlertDialog.THEME_HOLO_LIGHT)
		.setTitle(R.string.delete_preview_title)
		.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(final DialogInterface dialog, final int arg1){
			
				int x = mEmptyCell[0];
        			int y = mEmptyCell[1];
         			int index = y * mCountX + x;
				if(LauncherLog.DEBUG_PREVIEW){
    					LauncherLog.d(TAG, "showDilaog remove view from layout,index " + " : " + index);
    				}
				removeView(getChildAt(x, y));
     				//relayout children
         			relayoutIfNeeded();
         			mWorkspacePreview.removeScreen(index);
			}	
		})
		.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(final DialogInterface dialog, final int arg1){
				dialog.cancel();
			}		
		})
		.setMessage(R.string.delete_preview_body).create();
	}
	mAlertDialog.show();
    }

    public void setWrokspacePreview(WorkspacePreview workspacePreview){
    	mWorkspacePreview = workspacePreview;
    }
    
    OnAlarmListener mRecorderAlarmListener = new OnAlarmListener(){
        public void onAlarm(Alarm alarm){
            animateChildren(mTargetCell);
        }
    };

    public void measureChild(View child) {
        final int cellWidth = mCellWidth;
        final int cellHeight = mCellHeight;
        LayoutParams lp = (LayoutParams) child.getLayoutParams();

        lp.setup(cellWidth, cellHeight, mWidthGap, mHeightGap);
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
        int childheightMeasureSpec = MeasureSpec.makeMeasureSpec(lp.height,
                MeasureSpec.EXACTLY);
        child.measure(childWidthMeasureSpec, childheightMeasureSpec);
    }
    
    public void setupLp(LayoutParams lp) {
        lp.setup(mCellWidth, mCellHeight, mWidthGap, mHeightGap);
    }
    
    @Override
    public boolean onLongClick(View v) {
	if(getChildCount() <= 2){
		if(LauncherLog.DEBUG_PREVIEW){
			LauncherLog.w(TAG, "there is only one child and one plus button...");
		}
		return true;
	}
  	
     	LayoutParams lp = (LayoutParams) v.getLayoutParams();
     	mTargetCell[0] = mEmptyCell[0] = lp.cellX;
     	mTargetCell[1] = mEmptyCell[1] = lp.cellY;
        
	ClipData data = ClipData.newPlainText("","");
	mCurrenDragView = v;
	DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
	v.startDrag(data,shadowBuilder,v,0);
	removeView(v);
	//v.setVisibility(View.INVISIBLE);
     	return true;
    }
    
    public void addViewToPreview(View child, int index, int childId, PreviewLayout.LayoutParams params){
    	final PreviewLayout.LayoutParams lp = params;
    	if(lp.cellX >= 0 && lp.cellX <= (mCountX - 1) &&
    			lp.cellY >= 0 && (lp.cellY <= mCountY - 1)){
    		child.setId(childId);
    		addView(child, index, lp);
    	}
    }

    /**
     * M: add a new imageview which is empty to the preview layout.
     * 
     * @param child
     */
    public void addEmptyView(View child){
    	final int count = getChildCount();
	int x = count % mCountX;
	int y = count / mCountY;
    	LayoutParams lp = new PreviewLayout.LayoutParams(x, y, 1, 1);
	child.setId(count);
	if(LauncherLog.DEBUG_PREVIEW){
		LauncherLog.d(TAG, "addEmptyView, cellX: " + x + ", cellY: " + y + ", id : " + count);
	}
	addView(child, -1, lp);    	
	if(getChildCount() == mCountX * mCountY){
		//if the cell count is upto nine after add the this imageview, set mPlustView null.
		mPlustView = null;
		if(LauncherLog.DEBUG_PREVIEW){
			LauncherLog.d(TAG, "addEmptyView, the preview layout is full, the count is : " + getChildCount());
		}
	}
    }
    
    /**
     * M: add the addButton view to the preview layout.
     * 
     * @param child
     */
    public void addAddingButton(View child){
    	final int count = getChildCount();
	int x = count % mCountX;
	int y = count / mCountY;
    	LayoutParams lp = new PreviewLayout.LayoutParams(x, y, 1, 1);
	//set mPlustView
    	mPlustView = child;
	child.setId(count);
	addView(child, -1, lp);
	if(LauncherLog.DEBUG_PREVIEW){
		LauncherLog.d(TAG, "addPlusView, add the add button position is, cellX : " + x + ",  cellY : " + y);
	} 
    }

    /**
     * M: return the sequence index of the view that passed to this 
     * method in the preview layout.
     * 
     * @param child
     */
    public int getIndex(View child){
    	final LayoutParams lp = (LayoutParams)(child.getLayoutParams());
    	int cellX = lp.cellX;
    	int cellY = lp.cellY;
    	final int index = cellY * mCountX + cellX;
    	return index;
    }

    /**
     * M: return the certain child based on the given position, cellX and cellY. 
     * 
     * @param cellX
     * @param cellY
     */    
    public View getChildAt(int cellX, int cellY) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if ((lp.cellX <= cellX) && (cellX < lp.cellX + lp.cellHSpan) &&
                    (lp.cellY <= cellY) && (cellY < lp.cellY + lp.cellVSpan)) {
                return child;
            }
        }
        return null;
    }
    
    
    //this method can be called while deleting child
    private void relayoutIfNeeded(){
    	final int currentCount = getChildCount();
    	if(currentCount < mCountX * mCountY){
    		final int[] result = new int[2];
    		if(mPlustView != null){
    			//the mPlustView must be the last one child
    			final LayoutParams lp = (LayoutParams)(mPlustView.getLayoutParams());
    			result[0] = lp.cellX;
    			result[1] = lp.cellY;
    		}else{
    			//if mPlustView be null, there should be eight children after delete one
    			result[0] = mCountX - 1;
    			result[1] = mCountY - 1;
    		}
			animateChildren(result, false);
			invalidate();
    	}
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	 int count = getChildCount();
         for (int i = 0; i < count; i++) {
             View child = getChildAt(i);
             measureChild(child);
         }
         int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
         int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
         setMeasuredDimension(widthSpecSize, heightSpecSize);
    }
    
    
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                int childLeft = lp.x;
                int childTop = lp.y;
                child.layout(childLeft, childTop, childLeft + lp.width, childTop + lp.height);
            }
        }
	}
	
	public int getCountX(){
		return mCountX;
	}
	
	public int getCountY(){
		return mCountY;
	}
	
	
	public void animateChildren(int[] target){
		animateChildren(mEmptyCell, target, true);
	}
	
	public void animateChildren(int[] target, boolean swap){
		animateChildren(mEmptyCell, target, swap);
	}
	
	//until all the amimations are stoped
	public void animateChildren(int[] empty, int[] target, boolean swap){
		/*
		if(mAnimating == true){
			return;
		}
		mAnimating = true;*/
		int startX;
		int endX;
		int startY;
		int delay = 0;
		boolean wrap;
		float delayAmount = 30;
		
		if(readingOrderGreaterThan(target, empty)){
			wrap = empty[0] >= getCountX() - 1;
			startY = wrap ? empty[1] + 1 : empty[1];
			for(int y = startY; y <= target[1]; y++){
				startX = y == empty[1] ? empty[0] + 1 : 0;
				endX = y < target[1] ? getCountX() - 1 : target[0];
				for(int x = startX; x <= endX; x++){
					View v = getChildAt(x, y);
					if(animateChildToPosition(v, empty[0], empty[1], delay, swap)){
						empty[0] = x;
						empty[1] = y;
						delay += delayAmount;
						delayAmount *= 0.9;
					}
				}
			}
		}else{
			wrap = empty[0] == 0;
			startY = wrap ? empty[1] - 1: empty[1];
			for(int y = startY; y >= target[1]; y--){
				//if in the same row, then x - 1
				startX = y == empty[1] ? empty[0] - 1 : getCountX() - 1;
				endX = y > target[1] ? 0 : target[0];
				for(int x = startX; x >= endX; x--){
					View v = getChildAt(x, y);
					if(animateChildToPosition(v, empty[0], empty[1], delay, swap)){
						empty[0] = x;
						empty[1] = y;
						delay += delayAmount;
						delayAmount *= 0.9;
					}
				}
 			}
		}
	}
	
	public boolean animateChildToPosition(final View child, int cellX,int cellY, int delay, boolean swap){
		   if(indexOfChild(child) != -1){
			   final LayoutParams lp = (LayoutParams) child.getLayoutParams();
			   final int first = lp.cellY * mCountX + lp.cellX;
	           final int oldX = lp.x;
	           final int oldY = lp.y;
	           
	           lp.isLockedToGrid = true;
	           lp.cellX = cellX;
	           lp.cellY = cellY;
	           
	           setupLp(lp);
	           lp.isLockedToGrid = false;
	           final int newX = lp.x;
	           final int newY = lp.y;
	           lp.x = oldX;
	           lp.y = oldY;
	           
	           ValueAnimator va = LauncherAnimUtils.ofFloat(0f, 1f);
	           va.setDuration(ANIMATION_DURATION);
	           va.addUpdateListener(new AnimatorUpdateListener() {
	               @Override
	               public void onAnimationUpdate(ValueAnimator animation) {
	                   float r = ((Float) animation.getAnimatedValue()).floatValue();
	                   lp.x = (int) ((1 - r) * oldX + r * newX);
	                   lp.y = (int) ((1 - r) * oldY + r * newY);
	                   child.requestLayout();
			   child.setEnabled(false);
	               }
	           });
	           
	           va.addListener(new AnimatorListenerAdapter() {
	                 public void onAnimationEnd(Animator animation) {
	                	 //mPreviewsContainer.removeView(child);
	                	 //mPreviewsContainer.addView(child, targetIndex);
	                	 //child.setTag(targetIndex);
	                	 //mAnimating = false;
				 child.setEnabled(true);
	               }

	           });
	           va.setStartDelay(delay);
	           va.start();
	           if(swap){
	        	   final int second = cellY * mCountX + cellX;
	        	   mWorkspacePreview.swapScreen(first, second);
	           }
	           return true;
		   }
		   return false;
	}
	
    private boolean readingOrderGreaterThan(int[] v1, int[] v2) {
        if (v1[1] > v2[1] || (v1[1] == v2[1] && v1[0] > v2[0])) {
            return true;
        } else {
            return false;
        }
    }
	
    public OnDragListener getDeleteListener(){
    	return mDeleteZoneListener;
    }
    
    public OnDragListener getPreviewDragListener(){
    	return mDragListener/*new DragOverListener()*/;
    }

    class DragOverListener implements OnDragListener {
    	boolean isInLayout = false;
    	boolean isInOnDrop = false;
    	public boolean onDrag(View v , DragEvent event){
		/*
    		if(mAnimating == true){
    			return true;
    		}*/
    		int action = event.getAction();
    		switch(action){
    			case DragEvent.ACTION_DRAG_STARTED:
    				if(LauncherLog.DEBUG_PREVIEW){
    					LauncherLog.d(TAG, "DragOver ACTION_DRAG_STARTED"+ ",id : " + v.getId());
    				}
				mPreviousTargetCell[0] = -1;
				mPreviousTargetCell[1] = -1;
    			   break;
    			case DragEvent.ACTION_DRAG_ENTERED:
  				if(LauncherLog.DEBUG_PREVIEW){
    					LauncherLog.d(TAG, "DragOver ACTION_DRAG_ENTERED"+ ",id : " + v.getId()+ ",isInLayout : " + isInLayout);
    				}

	    			isInLayout = true;
	    			//play animation
	  	    		final LayoutParams lp = (LayoutParams)v.getLayoutParams();
			    	final int[] target = new int[2];
			    	mTargetCell[0] = target[0] = lp.cellX;
			    	mTargetCell[1] = target[1] = lp.cellY;
			    	//animateChildren(target);
				if(mTargetCell[0] != mPreviousTargetCell[0] || mTargetCell[1] != mPreviousTargetCell[1]){
					mReorderAlarm.cancelAlarm();
					mReorderAlarm.setOnAlarmListener(mRecorderAlarmListener);
					mReorderAlarm.setAlarm(150);
					mPreviousTargetCell[0] = mTargetCell[0];
					mPreviousTargetCell[1] = mTargetCell[1];
				}

    			   break;
    			case DragEvent.ACTION_DRAG_EXITED:
				mReorderAlarm.cancelAlarm();
    				if(LauncherLog.DEBUG_PREVIEW){
    					LauncherLog.d(TAG, "DragOver ACTION_DRAG_EXITED"+ ", id : " + v.getId());
    				}
    				isInLayout = false;
    				isInOnDrop = false;
    			   break;
    			case DragEvent.ACTION_DROP:
    				if(LauncherLog.DEBUG_PREVIEW){
    					LauncherLog.d(TAG, "DragOver ACTION_DROP"+ ",id : " + v.getId());
    				}
    				isInLayout = true;
    				isInOnDrop = true;
 
       			   break;
    			case DragEvent.ACTION_DRAG_ENDED:
				mReorderAlarm.cancelAlarm();
    				if(LauncherLog.DEBUG_PREVIEW){
    					final boolean dropped = event.getResult();
    					LauncherLog.d(TAG, "DragOver ACTION_DRAG_ENDED " + (dropped ? "Dropped!" : "No drop") + ",id : " + v.getId());
	  				LauncherLog.d(TAG, "DragOver ACTION_DRAG_ENDED " + ", mCurrenDragView : " + mCurrenDragView + 
						",x : " + mEmptyCell[0] + ", y : " + mEmptyCell[1]);				
    				}
    				if(mCurrenDragView != null){
    	       			int x = mEmptyCell[0];
            			int y = mEmptyCell[1];
            			int index = y * mCountX + x;
        				addViewToPreview(mCurrenDragView, -1, index, new LayoutParams(x, y, 1, 1));
    					mCurrenDragView = null;
    				}
    				if(!isInLayout && !isInOnDrop && (mCurrenDragView != null)){
    					mCurrenDragView.setVisibility(View.VISIBLE);
    				}
				isInLayout = false;
    				isInOnDrop = false;
    				
    			   break;
    			default:
    			   break;	
    		}
    		return true;
    	}
        }
    

   class DeleteListener implements OnDragListener {
   	public boolean onDrag(View v , DragEvent event){
   		int action = event.getAction();
		ViewGroup parent = (ViewGroup)event.getLocalState();
		CellPreview view = null;
		if(parent != null){
			view = (CellPreview)(parent.getChildAt(0));
		}
   		switch(action){
   			case DragEvent.ACTION_DRAG_STARTED:
   			   break;
   			case DragEvent.ACTION_DRAG_ENTERED:{
   				v.setBackground(mDeleteEnter);
				if(view != null && view instanceof CellPreview){
					((CellPreview)view).setColor(mHoverColor);
				}
   			   break;
			}    
   			case DragEvent.ACTION_DRAG_EXITED:{
   				v.setBackground(mDeleteNormal);
				if(view != null && view instanceof CellPreview){
					((CellPreview)view).setColor(0);
					}
   			   break;
			}
   			case DragEvent.ACTION_DROP:
     			if(getChildCount() >= 2){
				if(view != null && view instanceof CellPreview && view.isEmpty()){
		   			int x = mEmptyCell[0];
					int y = mEmptyCell[1];
		 			int index = y * mCountX + x;
	     				//relayout children
		 			relayoutIfNeeded();
		 			mWorkspacePreview.removeScreen(index);
     			mCurrenDragView = null;
				}else{
					showDialog();
				}
     			}/*else if(getChildCount() == 1){
	       			int x1 = mEmptyCell[0];
        			int y1 = mEmptyCell[1];
        			int index1 = y1 * mCountX + x1;
    				addViewToPreview(mCurrenDragView, -1, index1, new LayoutParams(x1, y1, 1, 1));
     			}*/
   			   break;
   			case DragEvent.ACTION_DRAG_ENDED:{
   				v.setBackground(mDeleteNormal);
				if(view != null && view instanceof CellPreview){
					((CellPreview)view).setColor(0);
				}
   			   break;
			}
   			default:
   				break;	
   		}
   		return true;
   	}
       }
    
    
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PreviewLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof PreviewLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new PreviewLayout.LayoutParams(p);
    }    
    
	 public static class LayoutParams extends ViewGroup.MarginLayoutParams {
	       /**
	         * Horizontal location of the item in the grid.
	         */
	        @ViewDebug.ExportedProperty
	        public int cellX;


	        /**
	         * Vertical location of the item in the grid.
	         */
	        @ViewDebug.ExportedProperty
	        public int cellY;

	        /**
	         * Number of cells spanned horizontally by the item.
	         */
	        @ViewDebug.ExportedProperty
	        public int cellHSpan;

	        /**
	         * Number of cells spanned vertically by the item.
	         */
	        @ViewDebug.ExportedProperty
	        public int cellVSpan;

	        // X coordinate of the view in the layout.
	        @ViewDebug.ExportedProperty
	        int x;
	        // Y coordinate of the view in the layout.
	        @ViewDebug.ExportedProperty
	        int y;

	        public boolean isLockedToGrid = true;
	        
	        boolean dropped;

	        public LayoutParams(Context c, AttributeSet attrs) {
	            super(c, attrs);
	            cellHSpan = 1;
	            cellVSpan = 1;
	        }

	        public LayoutParams(ViewGroup.LayoutParams source) {
	            super(source);
	            cellHSpan = 1;
	            cellVSpan = 1;
	        }

	        public LayoutParams(LayoutParams source) {
	            super(source);
	            this.cellX = source.cellX;
	            this.cellY = source.cellY;
	            this.cellHSpan = source.cellHSpan;
	            this.cellVSpan = source.cellVSpan;
	        }

	        public LayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
	            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	            this.cellX = cellX;
	            this.cellY = cellY;
	            this.cellHSpan = cellHSpan;
	            this.cellVSpan = cellVSpan;
	        }

	        public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap) {
	        	if(isLockedToGrid){
	                final int myCellHSpan = cellHSpan;
	                final int myCellVSpan = cellVSpan;
	                final int myCellX = cellX;
	                final int myCellY = cellY;

	                width = myCellHSpan * cellWidth + ((myCellHSpan - 1) * widthGap) -
	                        leftMargin - rightMargin;
	                height = myCellVSpan * cellHeight + ((myCellVSpan - 1) * heightGap) -
	                        topMargin - bottomMargin;
	                x = (int) (myCellX * (cellWidth + widthGap) + leftMargin);
	                y = (int) (myCellY * (cellHeight + heightGap) + topMargin);
	        	}
	        }

	        public String toString() {
	            return "(" + this.cellX + ", " + this.cellY + ")";
	        }

	        public void setWidth(int width) {
	            this.width = width;
	        }

	        public int getWidth() {
	            return width;
	        }

	        public void setHeight(int height) {
	            this.height = height;
	        }

	        public int getHeight() {
	            return height;
	        }

	        public void setX(int x) {
	            this.x = x;
	        }

	        public int getX() {
	            return x;
	        }

	        public void setY(int y) {
	            this.y = y;
	        }

	        public int getY() {
	            return y;
	        }
	 }


}
