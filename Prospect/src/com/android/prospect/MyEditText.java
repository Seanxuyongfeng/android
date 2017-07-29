package com.android.prospect;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.view.KeyEvent;
import android.view.KeyCharacterMap;

public class MyEditText extends EditText {
    private static final String TAG = "MyEditText";

    public MyEditText(Context context) {
        super(context, null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	return super.onKeyDown(keyCode, event);
    }
}
