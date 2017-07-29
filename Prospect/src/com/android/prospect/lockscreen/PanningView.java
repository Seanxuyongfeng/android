package com.android.prospect.lockscreen;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class PanningView extends ImageView {
	private PanningViewAttacher mAttacher;

	private int mPanningDurationInMs;

	public PanningView(Context context) {
		this(context, null);
	}

	public PanningView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public PanningView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		readStyleParameters(context, attr);
		super.setScaleType(ScaleType.MATRIX);
		
	}

	/**
	 * @param context
	 * @param attributeSet
	 */
	private void readStyleParameters(Context context, AttributeSet attributeSet) {
		//TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.PanningView);
		//try {
			mPanningDurationInMs = 5000;//a.getInt(R.styleable.PanningView_panningDurationInMs, PanningViewAttacher.DEFAULT_PANNING_DURATION_IN_MS);
		//} finally {
		//	a.recycle();
		//}
	}


	@Override
	// setImageBitmap calls through to this method
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		stopUpdateStartIfNecessary();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if(mAttacher == null){
		   mAttacher = new PanningViewAttacher(this, mPanningDurationInMs);
		}
		stopUpdateStartIfNecessary();
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		stopUpdateStartIfNecessary();
	}

	private void stopUpdateStartIfNecessary() {
		if (null != mAttacher) {
			boolean wasPanning = mAttacher.isPanning();
			mAttacher.stopPanning();
			mAttacher.update();
			if(wasPanning) {
				mAttacher.startPanning();
			}
		}
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		throw new UnsupportedOperationException("only matrix scaleType is supported");
	}

	@Override
	protected void onDetachedFromWindow() {
	       if(mAttacher != null){
		    mAttacher.cleanup();
	       }
		super.onDetachedFromWindow();
	}

	public void startPanning() {
		if(mAttacher != null){
		    mAttacher.startPanning();
		}
	}

	public void stopPanning() {
		if(mAttacher != null){
		    mAttacher.stopPanning();
		}
	}
}
