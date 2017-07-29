package com.android.prospect;


import java.util.TimeZone;

import org.apache.http.client.protocol.ClientContext;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews.RemoteView;
import java.util.Date;

public class MickeyAnalogClock extends View {
    private static final String TAG="qxtdebug";
    private Context mContext = null;
    private boolean mAttached;
    private Time mCalendar;
    private String mTimeZoneId;
    private float mHour = 0.0f;
    private float mMinutes = 0.0f;
    private float mSeconds = 0.0f;
    private Drawable mDrawableHour = null;
    private Drawable mDrawableMinute = null;
    private Drawable mDrawableSecond1 = null;
    private Drawable mDrawableSecond2 = null;
    private Drawable mDrawableClockBg = null;
    private int mHandSecondNumber = 0;
    private int mHandCenterX = 0;
    private int mHandCenterY  = 0;
    private int mDateAndWeekX = 0;
    private int mDateAndWeekY = 0;
    private int mDigitalClockX = 0;
    private int mDigitalClockY = 0;
    private final Handler mHandler = new Handler();

    private boolean mChanged = false;    

    private void showLog(String s){
        Log.e(TAG,s);
    }

    private final Runnable mClockTick = new Runnable () {

        @Override
        public void run() {
            onTimeChanged();
            invalidate();
            MickeyAnalogClock.this.postDelayed(mClockTick, 1000);
        }
    };
  
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
            }
            onTimeChanged();
            invalidate();
        }
    };

	public MickeyAnalogClock(Context context){
		this(context,null);
	}

	public MickeyAnalogClock(Context context,AttributeSet attrs){
		this(context,attrs, 0);
	}
	
	public MickeyAnalogClock(Context context,AttributeSet attrs, int defStyle){
		super(context,attrs, defStyle);
		Log.e("qxtdebug","===init====start");
		mContext = context;
		Resources r = mContext.getResources();
		TypedArray a = mContext.obtainStyledAttributes(attrs,R.styleable.MickeyAnalogClock, defStyle, 0);;
		mDrawableHour = a.getDrawable(R.styleable.MickeyAnalogClock_mickeyHandHour);


		mDrawableMinute = a.getDrawable(R.styleable.MickeyAnalogClock_mickeyHandMinute);

		mDrawableSecond1 = a.getDrawable(R.styleable.MickeyAnalogClock_mickeyHandSecond1);

		mDrawableSecond2 = a.getDrawable(R.styleable.MickeyAnalogClock_mickeyHandSecond2);

		mDrawableClockBg = a.getDrawable(R.styleable.MickeyAnalogClock_mickeyClockBg);
	
		mHandCenterX = a.getInt(R.styleable.MickeyAnalogClock_mickeyAnalogClockX, 266);
		mHandCenterY = a.getInt(R.styleable.MickeyAnalogClock_mickeyAnalogClockY, 314);
        mDateAndWeekX = a.getInt(R.styleable.MickeyAnalogClock_mickeyDateWeekX, 110);
        mDateAndWeekY = a.getInt(R.styleable.MickeyAnalogClock_mickeyDateWeekY, 110);
        mDigitalClockX = a.getInt(R.styleable.MickeyAnalogClock_mickeyDigitalClockX, 410);
        mDigitalClockY = a.getInt(R.styleable.MickeyAnalogClock_mickeyDigitalClockY, 110);
        mHandSecondNumber = a.getInt(R.styleable.MickeyAnalogClock_mickeyHandSecondNumber, 2);
		a.recycle();
	}
    
    private void onTimeChanged() {
        mCalendar.setToNow();

        if (mTimeZoneId != null) {
            mCalendar.switchTimezone(mTimeZoneId);
        }

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        mSeconds = second;
        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;

        mChanged = true;
        updateContentDescription(mCalendar);
    }
  
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
        }

        // The time zone may have changed while the receiver wasn't registered, so update the Time
        mCalendar = new Time();
        // Make sure we update to the current time
        onTimeChanged();
        // tick the seconds
        post(mClockTick);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            removeCallbacks(mClockTick);
            mAttached = false;
        }
    }
    	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

                boolean scaled = false;
		int availableWidth = getRight() - getLeft();//mRight - mLeft;
		int availableHeight = getBottom() - getTop();//mBottom - mTop;
		int x = availableWidth / 2;
		int y = availableHeight / 2;

		boolean changed = mChanged;
		if (changed) {
		    mChanged = false;
		}

               /*draw background start*/
		final Drawable background = mDrawableClockBg;
		int w = background.getIntrinsicWidth();
		int h = background.getIntrinsicHeight();
                
		if (availableWidth < w || availableHeight < h) {
		    scaled = true;
		    float scale = Math.min((float) availableWidth / (float) w,
		                           (float) availableHeight / (float) h);
		    canvas.save();
		    canvas.scale(scale, scale, x, y);
		    showLog("onDraw==scaled==:"+scale);
		}

                background.setBounds(x-w/2, y-h/2, x+w/2, y+h/2);
                background.draw(canvas);
                /*draw background end*/

                /*draw hand hour start*/
		canvas.save();
		canvas.rotate(mHour / 12.0f * 360.0f, x-w/2+mHandCenterX, y-h/2+mHandCenterY);
		final Drawable hourHand = mDrawableHour;
		if(changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x-w/2, y-h/2, x+w/2, y+h/2);
		}
		hourHand.draw(canvas);
		canvas.restore();               
                /*draw hand hour end*/

                /*draw hand minute start*/
		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x-w/2+mHandCenterX, y-h/2+mHandCenterY);
		final Drawable minuteHand = mDrawableMinute;
		if(changed) {
		    w = minuteHand.getIntrinsicWidth();
		    h = minuteHand.getIntrinsicHeight();
		    minuteHand.setBounds(x-w/2, y-h/2, x+w/2, y+h/2);
		}
		minuteHand.draw(canvas);
		canvas.restore();
                /*draw hand minute end*/

                /*draw hand second start*/
                if(mHandSecondNumber > 0){
			canvas.save();
			canvas.rotate(mSeconds / 60.0f * 360.0f,  x-w/2+mHandCenterX, y-h/2+mHandCenterY);

		        final Drawable secondHand;
                        if(mHandSecondNumber > 1){
 			        int b = (int)mSeconds %2;
				if(b == 0){
					secondHand = mDrawableSecond1;
				}else{
					secondHand = mDrawableSecond2;
				}
		        }else{
                            secondHand = mDrawableSecond1;
                        }

			if(changed){
				w = secondHand.getIntrinsicWidth();
				h = secondHand.getIntrinsicHeight();
				secondHand.setBounds(x-w/2, y-h/2, x+w/2, y+h/2);
			}
			secondHand.draw(canvas);
			canvas.restore();
                }
                /*draw hand second end*/

               showDateAndWeek(canvas);
               if(scaled){
                    canvas.restore();
               }
		
	}

    private void showDateAndWeek(Canvas canvas){
        Rect rect = new Rect();
        Date date = new Date();
        Paint paint = new Paint();
        paint.setTextSize(30);//50
        paint.setColor(Color.WHITE);
        paint.setShadowLayer(5, 3, 3, 0xFF000000); 

	final Drawable background = mDrawableClockBg;
        int base_x = (/*mRight*/getRight() - getLeft()/*mLeft*/ - background.getIntrinsicWidth())/2;
        int base_y = (/*mBottom - mTop*/getBottom() - getTop() - background.getIntrinsicHeight())/2;


        CharSequence week = DateFormat.format("E", date);
        CharSequence amPm = DateFormat.format("A", date);
        String time = Integer.toString(mCalendar.month+1)+"."+Integer.toString(mCalendar.monthDay);
        
        /*draw date*/
        paint.getTextBounds(time, 0, time.length()-1, rect);
        canvas.drawText(time, base_x + mDateAndWeekX-rect.width()/2, base_y + mDateAndWeekY-10, paint);
        int week_x = base_x + mDateAndWeekX + rect.width() + 20;
        /*draw week*/
        String country = mContext.getResources().getConfiguration().locale.getCountry();
        int dateX = mDateAndWeekX;
        if(country.equals("CN") || country.equals("TW")){
          //dateX -= 16;
        }
        paint.getTextBounds(week.toString(), 0, week.length()-1, rect);
        canvas.drawText(week.toString(), 
        		week_x/*base_x + dateX -rect.width()/2*/, 
        		/*base_y + mDateAndWeekY+rect.height()+10*/
        		base_y + mDateAndWeekY-10, paint);

        /*draw digital clock*/
        CharSequence hour;
        CharSequence minute = DateFormat.format("mm", date);
        if(DateFormat.is24HourFormat(mContext)){
            hour = DateFormat.format("k", date);
        }else{
            hour = DateFormat.format("h", date);
        }
        String curDate = hour.toString()+":"+minute.toString();
        paint.setTextSize(60);
        paint.getTextBounds(curDate, 0, curDate.length()-1, rect); 
        //canvas.drawText(curDate, base_x + mDigitalClockX-rect.width()/2, base_y + mDigitalClockY+rect.height()/2 + 10, paint);
    }	

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);
        int mDialWidth = mDrawableClockBg.getIntrinsicWidth();
        int mDialHeight = mDrawableClockBg.getIntrinsicHeight();

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float )heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);
        
        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    } 
    	
    @SuppressWarnings("deprecation")
	private void updateContentDescription(Time time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
        String contentDescription = DateUtils.formatDateTime(mContext,
                time.toMillis(false), flags);
        setContentDescription(contentDescription);
    }

}
