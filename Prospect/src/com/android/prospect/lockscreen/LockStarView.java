package com.android.prospect.lockscreen;

import com.android.prospect.Launcher;
import com.android.prospect.R;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class LockStarView extends ViewGroup {

	private static final String TAG = "LockStarView";
	private ImageView mCameraView;
	private ImageView mMusicView;
	private ImageView mPhoneView;
	private ImageView mSmsView;
	private ImageView mCenterView;
	
	private int mWidth;
	private int mHeight;
	
	private Rect mCameraRect;
	private Rect mMusicRect;
	private Rect mPhoneRect;
	private Rect mSmsRect;
	private Rect mCenterRect;
	private static final int CIRCLE_WIDTH = 140;
	private static final int RANGE_WIDTH = 100;
	
	private boolean mTracking = false;
	private float mLimited_x;
	private float mLimited_y;
	private Handler mHandler;
	
	public LockStarView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public LockStarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mCameraView = makeImageView(context, R.drawable.camera_nor);
		mMusicView = makeImageView(context, R.drawable.music_nor);
		mPhoneView = makeImageView(context, R.drawable.phone_nor);
		mSmsView = makeImageView(context, R.drawable.sms_nor);
		mCenterView = makeImageView(context, R.drawable.center_view_nor);
		addView(mCameraView);
		addView(mMusicView);
		addView(mPhoneView);
		addView(mSmsView);
		addView(mCenterView);
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		mWidth = r;
		mHeight = b;
		initialRects(mWidth, mHeight);
		layoutChildren(mWidth, mHeight);
	}
	
	private void layoutChildren(int parentWidth, int parentHeight){
		mPhoneView.layout(mPhoneRect.left, mPhoneRect.top, mPhoneRect.right, mPhoneRect.bottom);
		mSmsView.layout(mSmsRect.left, mSmsRect.top, mSmsRect.right, mSmsRect.bottom);
		mCameraView.layout(mCameraRect.left, mCameraRect.top, mCameraRect.right, mCameraRect.bottom);
		mMusicView.layout(mMusicRect.left, mMusicRect.top, mMusicRect.right, mMusicRect.bottom);
		mCenterView.layout(mCenterRect.left, mCenterRect.top, mCenterRect.right, mCenterRect.bottom);
	}
	
	private ImageView makeImageView(Context context, int resId){
		ImageView imageView = new ImageView(context);
		imageView.setImageResource(resId);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT));
		//imageView.setVisibility(View.INVISIBLE);
		return imageView;
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(mCameraView, RANGE_WIDTH, RANGE_WIDTH);
        measureChildren(mPhoneView, RANGE_WIDTH, RANGE_WIDTH);
        measureChildren(mMusicView, RANGE_WIDTH, RANGE_WIDTH);
        measureChildren(mSmsView, RANGE_WIDTH, RANGE_WIDTH);
        measureChildren(mCenterView, CIRCLE_WIDTH, CIRCLE_WIDTH);
        setMeasuredDimension(widthSpecSize, heightSpecSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureChildren(View child, int width, int height){
        int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
        child.measure(widthSpec, heightSpec);
    }
    
    private void initialRects(int parentWidth, int parentHeight){
     	int cameraWidth = mCameraView.getMeasuredWidth();
    	int cameraHeight = mCameraView.getMeasuredHeight();
    	int phoneWidth = mPhoneView.getMeasuredWidth();
    	int phoneHeight = mPhoneView.getMeasuredHeight();
    	int musicWidth = mMusicView.getMeasuredWidth();
    	int musicHeight = mMusicView.getMeasuredHeight();
    	int smsWidth = mSmsView.getMeasuredWidth();
    	int smsHeight = mSmsView.getMeasuredHeight();
    	int centerViewWidth = mCenterView.getMeasuredWidth();
    	int centerViewHeight = mCenterView.getMeasuredHeight();
    	int widthGap = centerViewWidth;
    	int heightGap = centerViewHeight;
		int centerViewTop = 4 * parentHeight / 7 - (centerViewHeight >> 1);
		int centerViewBottom = 4 * parentHeight / 7 + (centerViewHeight >> 1);
		
    	mPhoneRect = new Rect(parentWidth/2 - 3*centerViewWidth/2 - phoneWidth/2,
    			centerViewTop + centerViewHeight/2 - phoneHeight/2,
    			parentWidth/2 - 3*centerViewWidth/2 + phoneWidth/2,
    			centerViewBottom - centerViewHeight/2 + phoneHeight/2);
    	mCameraRect = new Rect(parentWidth/2 - cameraWidth/2,
    			centerViewTop + 2*centerViewHeight - cameraHeight/2,
    			parentWidth/2 + cameraWidth/2,
    			centerViewBottom + centerViewHeight + cameraHeight/2);
    	
    	mSmsRect = new Rect(parentWidth/2 + 3*centerViewWidth/2 - smsWidth,
    			centerViewTop + centerViewHeight/2 - smsHeight/2,
    			parentWidth/2 + 3*centerViewWidth/2 + smsWidth,
    			centerViewBottom - centerViewHeight/2 + smsHeight/2);
    	
    	mMusicRect = new Rect(parentWidth/2 - musicWidth/2,
    			centerViewTop - centerViewHeight - musicHeight/2,
    			parentWidth/2 + musicWidth/2,
    			centerViewBottom - 2*centerViewHeight + musicHeight/2);
    	
    	mCenterRect = new Rect(parentWidth/2 - centerViewWidth/2,
    			centerViewTop,
    			parentWidth/2 + centerViewWidth/2,
    			centerViewBottom);
    }
    
    public void setHandler(Handler handler){
    	mHandler = handler;
    }
    
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(mCenterRect.contains((int)x, (int)y)){
				mTracking = true;
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return false;
	}    
    
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mTracking){
			final int action = event.getAction();
			final float x = event.getX();
			final float y = event.getY();
			switch(action){
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				handleMove(x, y);
				break;
			case MotionEvent.ACTION_UP:
				mTracking = false;
				doTrigger((int)mLimited_x, (int)mLimited_y);
				resetCenterView();
				break;
			case MotionEvent.ACTION_CANCEL:
				mTracking = false;
				doTrigger((int)mLimited_x, (int)mLimited_y);
				resetCenterView();
				break;
			}
		}
		return mTracking || super.onTouchEvent(event);
	}
	
	private void handleMove(float x, float y){
		int centerViewWidth = mCenterView.getMeasuredWidth();
		int radius = centerViewWidth + centerViewWidth/2;
		if(distance(x, y) > radius){
			Log.w(TAG, "out of radius");
			x = (radius/distance(x, y))*(x - mWidth/2) + mWidth/2;
			y = (radius/distance(x, y))*
					(y - (mCenterView.getTop() + centerViewWidth/2))+
					mCenterView.getTop() + centerViewWidth/2;
		}
		
		mLimited_x = x;
		mLimited_y = y;
		
		mCenterView.setX((int)x - mCenterView.getMeasuredWidth() /2);
		mCenterView.setY((int)y - mCenterView.getMeasuredHeight()/2);
		changeBackground((int)x, (int)y);
		invalidate();
	}
	
	private float distance(float x, float y){
		float dx = x  - mWidth/2;
		float dy = y - (mCenterView.getTop() + mCenterView.getMeasuredWidth()/2);
		return (float) Math.sqrt(dx*dx + dy*dy);
	}
	
	private void doTrigger(int x, int y){
		if(mSmsRect.contains(x, y)){
			virbate();
			sendMessage(LockActivity.MSG_LAUNCH_SMS);
		}else if(mCameraRect.contains(x, y)){
			virbate();
			sendMessage(LockActivity.MSG_LAUNCH_CAMERA);
		}else if(mPhoneRect.contains(x, y)){
			virbate();
			sendMessage(LockActivity.MSG_LAUNCH_PHONE);
		}else if(mMusicRect.contains(x, y)){
			virbate();
			sendMessage(LockActivity.MSG_LAUNCH_MUSIC);
		}else if(mCenterRect.contains(x, y)){
			virbate();
			sendMessage(LockActivity.MSG_UNLOCK);
		}else{
			virbate();
			sendMessage(LockActivity.MSG_UNLOCK);
		}
	}
	
	private void sendMessage(final int msg){
		if(mHandler != null){
			mHandler.obtainMessage(msg).sendToTarget();
		}
	}
	
	private void resetCenterView(){
		mCenterView.setX(mWidth/2 - mCenterView.getMeasuredWidth()/2);
		mCenterView.setY(mCenterView.getTop() + mCenterView.getMeasuredHeight()/2 - 
				mCenterView.getMeasuredHeight()/2);
		invalidate();
	}
	
	private void changeBackground(int x, int y){
		if(mSmsRect.contains(x, y)){
			mCenterView.setVisibility(View.INVISIBLE);
			mSmsView.setImageResource(R.drawable.sms_nor_sel);
		}else if(mCameraRect.contains(x, y)){
			mCenterView.setVisibility(View.INVISIBLE);
			mCameraView.setImageResource(R.drawable.camera_nor_sel);
		}else if(mPhoneRect.contains(x, y)){
			mCenterView.setVisibility(View.INVISIBLE);
			mPhoneView.setImageResource(R.drawable.phone_nor_sel);
		}else if(mMusicRect.contains(x, y)){
			mCenterView.setVisibility(View.INVISIBLE);
			mMusicView.setImageResource(R.drawable.music_nor_sel);
		}else if(mCenterRect.contains(x, y)){
			//mCenterView.setVisibility(View.INVISIBLE);
			mCenterView.setImageResource(R.drawable.center_view_sel);
		}else{
			mCenterView.setVisibility(View.VISIBLE);
			mSmsView.setImageResource(R.drawable.sms_nor);
			mCameraView.setImageResource(R.drawable.camera_nor);
			mPhoneView.setImageResource(R.drawable.phone_nor);
			mMusicView.setImageResource(R.drawable.music_nor);
			mCenterView.setImageResource(R.drawable.center_view_nor);
		}
	}
	
	private void virbate(){
		Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
	}
	
	@Override
	protected void onAnimationStart() {
		super.onAnimationStart();
		
	}
	
	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
	}
	
}


















