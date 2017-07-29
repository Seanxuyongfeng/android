package com.android.sean.musicwidgets;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.android.prospect.R;

public class DownLoadActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.download_main_layout);
	}
}
