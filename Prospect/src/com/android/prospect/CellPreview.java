package com.android.prospect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class CellPreview extends ImageView {
	private static final String TAG = "CellPreview";
	private Paint mPaint = new Paint();
	
	private Rect mRect = new Rect();

	private boolean mEmpty = true;

	public CellPreview(Context context){
		this(context, null);
	}
	
	public CellPreview(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public CellPreview(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(3);
		setScaleType(ImageView.ScaleType.FIT_CENTER);
	}

	 @Override
     protected void onDraw(Canvas canvas) {
		 super.onDraw(canvas);
		/*
		 canvas.getClipBounds(mRect);
		 mRect.bottom--;
		 mRect.right--;
		 canvas.drawRect(mRect, mPaint);*/
     }

	    public void setColor(int color) {
		/*
	        if (mPaint == null) {
	            mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	        }
	        if (color != 0) {
	            mPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
	        } else {
	            mPaint.setColorFilter(null);
	        }*/
		//mPaint.setColor(color);
	        //invalidate();
	    }

    public void setScreenEmpty(final boolean flag){
	mEmpty = flag;
    }

    public boolean isEmpty(){
	return mEmpty;
    }
}	
