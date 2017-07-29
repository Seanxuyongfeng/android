package com.android.sean.musicwidgets;

import com.android.prospect.ItemInfo;
import com.android.prospect.LauncherSettings;

public class FakeWidgetInfo extends ItemInfo {

	FakeWidgetInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public static FakeWidgetInfo makeFakeWidgetInfo(int screen, int spanX, int spanY){
		FakeWidgetInfo w = new FakeWidgetInfo();
		w.itemType = LauncherSettings.Favorites.ITEM_TYPE_WIDGET_FAKE;
		w.spanX = spanX;
		w.spanY = spanY;
		w.screen = screen;
		return w;
	}
	

}
