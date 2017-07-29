/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.prospect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import android.graphics.Region;
import android.graphics.Region.Op;
import com.android.prospect.R;
import android.content.res.Resources;
/**
 * An icon on a PagedView, specifically for items in the launcher's paged view (with compound
 * drawables on the top).
 */
public class PagedViewIcon extends TextView {

    static final float SHADOW_LARGE_RADIUS = 4.0f;
    static final float SHADOW_SMALL_RADIUS = 1.75f;
    static final float SHADOW_Y_OFFSET = 2.0f;
    static final int SHADOW_LARGE_COLOUR = 0xDD000000;
    static final int SHADOW_SMALL_COLOUR = 0xCC000000;

    /** A simple callback interface to allow a PagedViewIcon to notify when it has been pressed */
    public static interface PressedCallback {
        void iconPressed(PagedViewIcon icon);
    }

    @SuppressWarnings("unused")
    private static final String TAG = "PagedViewIcon";
    private static final float PRESS_ALPHA = 0.4f;
    private final Resources mResources;
    private PagedViewIcon.PressedCallback mPressedCallback;
    private boolean mLockDrawableState = false;
    private Context mContext;
    private Bitmap mIcon;
    //private Bitmap mAppIconBackground;
    public PagedViewIcon(Context context) {
        this(context, null);
    }

    public PagedViewIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
	mContext = context;
	mResources = context.getResources();
	setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
	/*
        Drawable drawable = mResources.getDrawable(R.drawable.p7);
        BitmapDrawable bg = (BitmapDrawable)(drawable);
        mAppIconBackground = bg.getBitmap();*/
    }

    public void applyFromApplicationInfo(ApplicationInfo info, boolean scaleUp,
            PagedViewIcon.PressedCallback cb) {
        mIcon = info.iconBitmap;
        mPressedCallback = cb;

	final int width = mIcon.getWidth();
        final int height = mIcon.getHeight();
	/*
        Bitmap bitmap;
        if(LauncherFeature.SHORTCUTS_BACKGROUND_SUPPORT && ((info.flags & ApplicationInfo.DOWNLOADED_FLAG) != 0)){
        	bitmap = Launcher.createCompoundBitmap(mAppIconBackground, mIcon);
        }else{
		int iconRes = AllAppsList.getSystemAppIcon(info.componentName.getClassName());
		if(iconRes != 0){
			Drawable drawable = mResources.getDrawable(iconRes);
        		BitmapDrawable bg = (BitmapDrawable)(drawable);
        		bitmap = bg.getBitmap();
		}else{
			bitmap = mIcon;
		}
	
        }*/

	int iconRes = AllAppsList.getSystemAppIcon(info.componentName.getClassName());
	if(iconRes != 0){
		Drawable drawable = mResources.getDrawable(iconRes);
        	BitmapDrawable bg = (BitmapDrawable)(drawable);
        	mIcon = bg.getBitmap();
        }else{
		if(mIcon != null){
			mIcon= IconCache.getRandomBitmap(mContext, mIcon);
		}
	}

        mIcon = BubbleTextView.scaleBitmap(mIcon, (float)width, (float)height);

        setCompoundDrawablesWithIntrinsicBounds(null, new FastBitmapDrawable(mIcon), null, null);
        setText(info.title);
        setTag(info);
    }

    public void lockDrawableState() {
        mLockDrawableState = true;
    }

    public void resetDrawableState() {
        mLockDrawableState = false;
        post(new Runnable() {
            @Override
            public void run() {
                refreshDrawableState();
            }
        });
    }

   @Override
    public void draw(Canvas canvas) {
	
        // If text is transparent, don't draw any shadow
        if (getCurrentTextColor() == getResources().getColor(android.R.color.transparent)) {
            getPaint().clearShadowLayer();
            super.draw(canvas);
            return;
        }

        // We enhance the shadow by drawing the shadow twice
        getPaint().setShadowLayer(SHADOW_LARGE_RADIUS, 0.0f, SHADOW_Y_OFFSET, SHADOW_LARGE_COLOUR);
        super.draw(canvas);
	
        canvas.save(Canvas.CLIP_SAVE_FLAG);
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(),
                getScrollX() + getWidth(),
                getScrollY() + getHeight(), Region.Op.INTERSECT);
        getPaint().setShadowLayer(SHADOW_SMALL_RADIUS, 0.0f, 0.0f, SHADOW_SMALL_COLOUR);
        super.draw(canvas);
        canvas.restore();

    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();

        // We keep in the pressed state until resetDrawableState() is called to reset the press
        // feedback
        if (isPressed()) {
            setAlpha(PRESS_ALPHA);
            if (mPressedCallback != null) {
                mPressedCallback.iconPressed(this);
            }
        } else if (!mLockDrawableState) {
            setAlpha(1f);
        }
    }
}
