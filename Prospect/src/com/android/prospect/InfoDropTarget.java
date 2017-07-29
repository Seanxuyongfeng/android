/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.android.prospect.R;

public class InfoDropTarget extends ButtonDropTarget {

    private ColorStateList mOriginalTextColor;
    private TransitionDrawable mDrawable;

    public InfoDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mOriginalTextColor = getTextColors();

        // Get the hover color
        Resources r = getResources();
        /// M: modified for theme feature, get the different hover color for different themes.
        mHoverColor = Launcher.getThemeColor(r, R.color.info_target_hover_tint);
        mDrawable = (TransitionDrawable) getCurrentDrawable();
        mDrawable.setCrossFadeEnabled(true);

        // Remove the text in the Phone UI in landscape
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!LauncherApplication.isScreenLarge()) {
                setText("");
            }
        }
    }

    private boolean isFromAllApps(DragSource source) {
        return (source instanceof AppsCustomizePagedView);
    }

    private boolean isFromWorkspaceTab(DragSource source){
	return ((source instanceof TabAppsPagedView) || (source instanceof TabWidgetsPagedView));    
    }

    @Override
    public boolean acceptDrop(DragObject d) {
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "acceptDrop: d = " + d + ", d.dragInfo = " + d.dragInfo);
        }

        // acceptDrop is called just before onDrop. We do the work here, rather than
        // in onDrop, because it allows us to reject the drop (by returning false)
        // so that the object being dragged isn't removed from the drag source.
        ComponentName componentName = null;
        if (d.dragInfo instanceof ApplicationInfo) {
            componentName = ((ApplicationInfo) d.dragInfo).componentName;
        } else if (d.dragInfo instanceof ShortcutInfo) {
            componentName = ((ShortcutInfo) d.dragInfo).intent.getComponent();
        } else if (d.dragInfo instanceof PendingAddItemInfo) {
            componentName = ((PendingAddItemInfo) d.dragInfo).componentName;
        }
        if (componentName != null) {
            mLauncher.startApplicationDetailsActivity(componentName);
        }

        // There is no post-drop animation, so clean up the DragView now
        d.deferDragViewCleanupPostAnimation = false;
        return false;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "onDratStart: source = " + source + ", info = " + info
                    + ", dragAction = " + dragAction);
        }

        boolean isVisible = true;

        // Hide this button unless we are dragging something from AllApps or workspace tab
        if (!(isFromAllApps(source) || isFromWorkspaceTab(source))) {
            isVisible = false;
        }

        mActive = isVisible;
        mDrawable.resetTransition();
        setTextColor(mOriginalTextColor);
        ((ViewGroup) getParent()).setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDragEnd() {
        super.onDragEnd();
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "onDragEnd.");
        }
        mActive = false;
    }

    public void onDragEnter(DragObject d) {
        super.onDragEnter(d);
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "onDragEnter: d = " + d);
        }

        mDrawable.startTransition(mTransitionDuration);
        setTextColor(mHoverColor);
    }

    public void onDragExit(DragObject d) {
        super.onDragExit(d);
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "onDragExit: d = " + d + ", d.dragComplete = " + d.dragComplete);
        }

        if (!d.dragComplete) {
            mDrawable.resetTransition();
            setTextColor(mOriginalTextColor);
        }
    }
}