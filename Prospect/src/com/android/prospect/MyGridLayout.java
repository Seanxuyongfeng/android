package com.android.prospect;

import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.GridLayout;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import com.android.prospect.R;

public class MyGridLayout extends GridLayout implements OnClickListener, View.OnKeyListener{
	private static final String TAG = "MyGridLayout";

	private final Context mContext;
	private EditText mEditText = null;
	
	private static final int[] mAllIds = {
		R.id.loc_A, R.id.loc_B,R.id.loc_C, R.id.loc_D,
		R.id.loc_E, R.id.loc_F,R.id.loc_G, R.id.loc_H,
		R.id.loc_I, R.id.loc_J,R.id.loc_K, R.id.loc_L,
		R.id.loc_M,	R.id.loc_N,R.id.loc_O, R.id.loc_P,
		R.id.loc_Q, R.id.loc_R,R.id.loc_S, R.id.loc_T,
		R.id.loc_U, R.id.loc_V,R.id.loc_W, R.id.loc_X,
		R.id.loc_Y,R.id.loc_Z,R.id.loc_num};
	
	private static final String mAllTags = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789#";
	
	InputFilter filter = new InputFilter() { 
        public CharSequence filter(CharSequence source, int start, int end, 
        		Spanned dest, int dstart, int dend) { 
                for (int i = start; i < end; i++) { 
                        if (!(Character.isUpperCase(source.charAt(i)) || source.charAt(i) == '#')) { 
                                return ""; 
                        } 
                } 
                return null; 
        } 
	}; 
	

	 public MyGridLayout(Context context) {
		 this(context, null);
	 }
	 
	 public MyGridLayout(Context context, AttributeSet attrs) {
		 this(context, attrs, 0);
	 }
	 
	 public MyGridLayout(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
		 mContext = context;
		 setOnClickListener(this);
	 }
	 
	 
	 @Override
	 protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		for(int i = 0; i < getChildCount(); i++){
			View v = getChildAt(i);
			if(v.getTag() != null){
				v.setOnClickListener(this);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.deleteButton:
				keyPressed(KeyEvent.KEYCODE_DEL);
				break;
			case R.id.loc_A:
				keyPressed(KeyEvent.KEYCODE_A);
				break;
			case R.id.loc_B:
				keyPressed(KeyEvent.KEYCODE_B);
				break;
			case R.id.loc_C:
				keyPressed(KeyEvent.KEYCODE_C);
				break;
			case R.id.loc_D:
				keyPressed(KeyEvent.KEYCODE_D);
				break;
			case R.id.loc_E:
				keyPressed(KeyEvent.KEYCODE_E);
				break;
			case R.id.loc_F:
				keyPressed(KeyEvent.KEYCODE_F);
				break;
			case R.id.loc_G:
				keyPressed(KeyEvent.KEYCODE_G);
				break;
			case R.id.loc_H:
				keyPressed(KeyEvent.KEYCODE_H);
				break;
			case R.id.loc_I:
				keyPressed(KeyEvent.KEYCODE_I);
				break;
			case R.id.loc_J:
				keyPressed(KeyEvent.KEYCODE_J);
				break;
			case R.id.loc_K:
				keyPressed(KeyEvent.KEYCODE_K);
				break;
			case R.id.loc_L:
				keyPressed(KeyEvent.KEYCODE_L);
				break;
			case R.id.loc_M:
				keyPressed(KeyEvent.KEYCODE_M);
				break;
			case R.id.loc_N:
				keyPressed(KeyEvent.KEYCODE_N);
				break;
			case R.id.loc_O:
				keyPressed(KeyEvent.KEYCODE_O);
				break;
			case R.id.loc_P:
				keyPressed(KeyEvent.KEYCODE_P);
				break;
			case R.id.loc_Q:
				keyPressed(KeyEvent.KEYCODE_Q);
				break;
			case R.id.loc_R:
				keyPressed(KeyEvent.KEYCODE_R);
				break;
			case R.id.loc_S:
				keyPressed(KeyEvent.KEYCODE_S);
				break;
			case R.id.loc_T:
				keyPressed(KeyEvent.KEYCODE_T);
				break;
			case R.id.loc_U:
				keyPressed(KeyEvent.KEYCODE_U);
				break;
			case R.id.loc_V:
				keyPressed(KeyEvent.KEYCODE_V);
				break;
			case R.id.loc_W:
				keyPressed(KeyEvent.KEYCODE_W);
				break;
			case R.id.loc_X:
				keyPressed(KeyEvent.KEYCODE_X);
				break;
			case R.id.loc_Y:
				keyPressed(KeyEvent.KEYCODE_Y);
				break;
			case R.id.loc_Z:
				keyPressed(KeyEvent.KEYCODE_Z);
				break;
			case R.id.loc_num:
				keyPressed(KeyEvent.KEYCODE_POUND);
				break;
			default:
				break;
		}
	}
	
	public void updateButtonState(final String list){
		String listStr = list;
		String match = ".*\\d+.*";
		Log.i(TAG, "s = "+ listStr);
		for(int i = 0; i < mAllIds.length; i++){
			View v = findViewById(mAllIds[i]);
			if(v == null){
				continue;
			}
			String tag = (String)(v.getTag());
			
			//char c = tag.charAt(0);
			if(listStr.contains(tag)){
				v.setEnabled(true);
				Log.i(TAG, "tag = "+ tag + ", true");
			}else if(listStr.matches(match) && "#".equals(tag)){
				v.setEnabled(true);
				Log.i(TAG, "tag = "+ tag + ", true");
			}else{
				Log.i(TAG, "tag = "+ tag + ", false");
				v.setEnabled(false);
			}
		}
	}
	
	public void updateAllVisible(boolean visible){
		final int length = mAllIds.length;
		for(int i = 0; i < length; i++){
			View v = findViewById(mAllIds[i]);
			if(v != null){
				v.setEnabled(visible);
			}
		}
	}
	     
	public void setEditText(EditText editText){
		mEditText = editText;
	}

        @Override
	public boolean onKey(View view, int keyCode, KeyEvent event){
		return false;	
	}
	

    
    //keydown event   
	private void keyPressed(int keyCode){
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		mEditText.onKeyDown(keyCode, event);	
	}
}
