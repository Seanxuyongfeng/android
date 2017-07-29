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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.LinearLayout;
import com.android.prospect.R;
import android.content.res.Resources;

public class WallpaperItem extends LinearLayout {

    private static final String TAG = "PagedViewIcon";
    private static final float PRESS_ALPHA = 0.4f;

    public WallpaperItem(Context context) {
        this(context, null);
    }

    public WallpaperItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WallpaperItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (isPressed()) {
            setAlpha(PRESS_ALPHA);
        } else {
            setAlpha(1f);
        }
    }
}
