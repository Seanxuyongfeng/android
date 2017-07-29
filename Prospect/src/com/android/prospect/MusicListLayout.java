package com.android.prospect;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.prospect.R;
import com.android.prospect.lyrics.LyricView;
import com.android.sean.musicwidgets.AlbulmActivity;
import com.android.sean.musicwidgets.BookActivity;
import com.android.sean.musicwidgets.DownLoadActivity;

public class MusicListLayout extends RelativeLayout implements 
View.OnClickListener, OnItemClickListener{
	private ArrayList<MusicListItem> mMusicItems = new ArrayList<MusicListItem>();
	private static int[] mImgArray = new int[]{R.drawable.muisc_item1,
		R.drawable.muisc_item2,
		R.drawable.muisc_item3,
		R.drawable.muisc_item4,
		R.drawable.muisc_item5,
		R.drawable.muisc_item6};
	private static int[] mTitleArray = new int[]{
		R.string.music_item1_title,
		R.string.music_item2_title,
		R.string.music_item3_title,
		R.string.music_item1_title,
		R.string.music_item2_title,
		R.string.music_item3_title		
	};
	
	private static int[] mContentArray = new int[]{
		R.string.music_item1_content,
		R.string.music_item2_content,
		R.string.music_item3_content,
		R.string.music_item1_content,
		R.string.music_item2_content,
		R.string.music_item3_content		
	};	
	
	private ListView mListView;
	private ListViewAdatper mAdapter;
	private LayoutInflater mInflater;
	private ImageView mAnimation;
	private AnimationDrawable mAnimGlowworm;
	private Button mShareButton;
	private Button mPreviousButton;
	private Button mPlayButton;
	private Button mNextButton;
	private Button mRepeatButton;
	private ImageView mMusicMoreButton;
	
	private View mMusicListLayout;
	private View mMusicDetailLayout;
	private View mBackgroundLayout;
	private LyricView mLyricView;
	private View mBackToLyrics;
	
	public MusicListLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}	
	public MusicListLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		mAdapter = new ListViewAdatper(context, this);
	}
	
    @Override
    protected void onFinishInflate() {
    	super.onFinishInflate();
    	mBackgroundLayout = findViewById(R.id.backgroundLayout);
    	mMusicListLayout = findViewById(R.id.music_list_layout);
    	mBackToLyrics = findViewById(R.id.back_to_lyrics);

    	if(mBackToLyrics != null){
    		mBackToLyrics.setClickable(true);
    		mBackToLyrics.setOnClickListener(this);
    	}
    	if(mMusicListLayout != null){
    		if(mBackgroundLayout != null){
    			mBackgroundLayout.setBackgroundResource(R.drawable.music_list_background);
    		}
    	}
    	mMusicDetailLayout = findViewById(R.id.music_detail_layout);
    	if(mMusicDetailLayout != null){
    		mMusicDetailLayout.setVisibility(View.INVISIBLE);
    	}
   	

    	mLyricView = (LyricView)findViewById(R.id.lyrics);
    	if(mLyricView != null){
    		mLyricView.setLayout(mMusicListLayout, mMusicDetailLayout, mBackgroundLayout);
    		mLyricView.setOnClickListener(this);
    	}
    	TextView currentTime = (TextView)findViewById(R.id.current_time);
    	TextView totalTime = (TextView)findViewById(R.id.total_time);
    	mLyricView.setTimeView(currentTime, totalTime);
    	mMusicMoreButton = (ImageView)findViewById(R.id.music_more_button);
    	if(mMusicMoreButton != null){
    		mMusicMoreButton.setOnClickListener(this);
    	}
    	mShareButton = (Button)findViewById(R.id.shared_button);
    	if(mShareButton != null){
    		mShareButton.setOnClickListener(this);
    	}
    	mPreviousButton = (Button)findViewById(R.id.previous_button);
    	if(mPreviousButton != null){
    		mPreviousButton.setOnClickListener(this);
    	}
    	mPlayButton = (Button)findViewById(R.id.play_pause_button);
    	if(mPlayButton != null){
    		mPlayButton.setOnClickListener(this);
    	}
    	mNextButton = (Button)findViewById(R.id.next_button);
    	if(mNextButton != null){
    		mNextButton.setOnClickListener(this);
    	}
    	mRepeatButton = (Button)findViewById(R.id.repeat_button);
    	if(mRepeatButton != null){
    		mRepeatButton.setOnClickListener(this);
    	}
    	mListView = (ListView)findViewById(android.R.id.list);
    	if(mListView != null){
    		mListView.setAdapter(mAdapter);
    		mListView.setOnItemClickListener(this);
    	}
    	mAnimation = (ImageView)findViewById(R.id.animation);
    	mAnimGlowworm = (AnimationDrawable)mAnimation.getBackground();
    	if(mLyricView != null){
    		mLyricView.setAnimation(mAnimation);
    		mAnimGlowworm.start();
    	}
    	
    	buildList();
    }

	private void buildList(){
		mMusicItems.clear();
		for(int i = 0; i < mImgArray.length; i++){
			mMusicItems.add(new MusicListItem(
					getContext().getString(mTitleArray[i]), 
					getContext().getString(mContentArray[i]), 
					mImgArray[i]));
		}
		
	}    
    
	class ListViewAdatper extends BaseAdapter {
		private Context mContext;
		private MusicListLayout mlayout;
		
		public ListViewAdatper(Context context, MusicListLayout layout){
			mContext = context;
			mlayout = layout;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE;
			//return mMusicItems.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			position = position % 6;
			MusicListItem itemInfo = mMusicItems.get(position);
			if(itemInfo != null){
				convertView = mInflater.inflate(R.layout.music_list_item, null);
				TextView title = (TextView)convertView.findViewById(R.id.title);
				title.setText( itemInfo.mTitle);
				TextView content = (TextView)convertView.findViewById(R.id.content);
				content.setText( itemInfo.mContent);
				ImageView img = (ImageView)convertView.findViewById(R.id.img);
				img.setImageResource(itemInfo.mImg);
				//View view = convertView.findViewById(R.id.list_item_play);
				//if(view != null){
					//view.setOnClickListener(mlayout);
				//}
			}else{
				convertView = null;
			}			
			return convertView;
		}
		
	}
	
	class MusicListItem{
		public MusicListItem(String title, String content, int img){
			mTitle = title;
			mContent = content;
			mImg = img;
		}
		
		public void initButtons(){
			
		}
		
		public String mTitle;
		public String mContent;
		
		public int mImg;
		
		public int mRingRes;
		public int mFavoriteRes;
		public int mPlayRes;
		
	}

	private boolean paly_button_status = false;
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view == null)return;
		
		int id = view.getId();
		if(id == R.id.shared_button){
			Intent intent = new Intent(getContext(), DownLoadActivity.class);
			
			getContext().startActivity(intent);
			//showInfo("click share button");
			/*
			view.setBackgroundResource(R.drawable.btn_download_pressed);
			postDelayed(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mShareButton.setBackgroundResource(R.drawable.btn_download_nor);
				}
				
			}, 1000*10);
			*/
		}else if(id == R.id.previous_button){
			//showInfo("click previous button");
		}else if(id == R.id.play_pause_button){
			if(view != null && view.getId() == R.id.play_pause_button){
				if(paly_button_status){
					view.setBackgroundResource(R.drawable.button_play_selector);
					paly_button_status = false;
				}else{
					view.setBackgroundResource(R.drawable.button_pause_selector);
					paly_button_status = true;
				}
			}
		}else if(id == R.id.next_button){
			//showInfo("click next button");
		}else if(id == R.id.repeat_button){
			//showInfo("click repeat button");
			/*
			Intent intent = new Intent();
			ComponentName component = new ComponentName("com.unison.miguring",
					"com.unison.miguring.activity.MiguLauncherActivity");
			intent.setComponent(component);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);		
			*/
			Intent intent = new Intent(getContext(), BookActivity.class);
			
			getContext().startActivity(intent);
		}else if(id == R.id.lyrics){
			Intent intent = new Intent(getContext(), AlbulmActivity.class);
			getContext().startActivity(intent);
		}else if(id == R.id.music_more_button){
			if(mMusicListLayout != null){
	    		if(mBackgroundLayout != null){
	    			mBackgroundLayout.setBackgroundResource(R.drawable.music_list_background);
	    		}
				mMusicListLayout.setVisibility(View.VISIBLE);
			}
			if(mMusicDetailLayout != null){
				mMusicDetailLayout.setVisibility(View.INVISIBLE);
			}
			
			/*
			Intent intent = new Intent("android.intent.sean.mainui");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);
			*/
		}else if(id == R.id.list_item_play){
			if(mMusicListLayout != null){
				mMusicListLayout.setVisibility(View.INVISIBLE);
			}
			if(mMusicDetailLayout != null){
				mMusicDetailLayout.setVisibility(View.VISIBLE);
			}
			if(mLyricView != null && mPlayButton != null){
				mLyricView.resetMusic(mPlayButton);
			}
		}else if(id == R.id.back_to_lyrics){
			if(mMusicListLayout != null){
				mMusicListLayout.setVisibility(View.INVISIBLE);
			}
			if(mMusicDetailLayout != null){
				mMusicDetailLayout.setVisibility(View.VISIBLE);
				if(mBackgroundLayout != null){
					mBackgroundLayout.setBackgroundResource(R.drawable.music_detail_background);
				}			
			}
		}
		
	}    
	
	private void showInfo(String msg){
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if(mListView != null){
			Intent intent = new Intent();
			ComponentName component = new ComponentName("cmccwm.mobilemusic",
					"cmccwm.mobilemusic.ui.activity.PreSplashActivityMigu");
			intent.setComponent(component);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getContext().startActivity(intent);
		}
	}
	
	///////////////
	public void setHandler(Handler handler){
		mHandler = new UIHandler(handler);
		Message mymsg = mHandler.obtainMessage();
		mymsg.what = UPDATE_LISTVIEW;
		Bundle data = mymsg.getData();
		data.putInt("index", 0);
		mymsg.setData(data);
		
		mHandler.sendMessageDelayed(mymsg, 1000*1);
	}
	private UIHandler mHandler;
    private static final int UPDATE_LISTVIEW = 1;
    
    class UIHandler extends Handler{
    	public UIHandler(Handler handler){
    		super(handler.getLooper());
    	}
    	
    	@Override
    	public void dispatchMessage(Message msg) {
    		// TODO Auto-generated method stub
			switch(msg.what){
			case UPDATE_LISTVIEW:
				if(mListView == null){
					break;
				}
				Bundle data1 = msg.getData();
				int position = data1.getInt("index");
				int startIndex = mListView.getFirstVisiblePosition();
				Log.i("xuyongfeng", "position = " + position + ", startIndex = " + startIndex);
				int endIndex = mListView.getLastVisiblePosition();
				if(position >= mListView.getCount()){
					position = 0;
				}else if(startIndex == 0 && position <= endIndex){
					position = endIndex;
				}
				
				mListView.smoothScrollToPosition(position);
				
				//update next item
				Message mymsg = mHandler.obtainMessage();
				mymsg.what = UPDATE_LISTVIEW;
				Bundle data = mymsg.getData();
				data.putInt("index", position+1);
				mymsg.setData(data);
				
				mHandler.sendMessageDelayed(mymsg, 1000*10);
				break;				
			}
    	}
    }
}
