package com.android.prospect.lyrics;

import java.io.BufferedReader;  

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStream;
import java.io.InputStreamReader;  
import java.util.Iterator;  
import java.util.TreeMap;  
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  
import com.android.prospect.Launcher;

import android.app.Activity;
import android.content.Context;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;  
import android.util.Log;  
import android.view.MotionEvent;  
import android.view.View;  
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;  
import android.widget.TextView;

import com.android.prospect.R;


public class LyricView extends View implements View.OnClickListener{
	 private static TreeMap<Integer, LyricObject> lrc_map;  
	    private float mX;       //屏幕X轴的中点，此值固定，保持歌词在X中间显示  
	    private float offsetY;      //歌词在Y轴上的偏移量，此值会根据歌词的滚动变小  
	    private static boolean blLrc = false;  
	    private float touchY;   //当触摸歌词View时，保存为当前触点的Y轴坐标  
	    private float touchX;  
	    private boolean blScrollView = false;  
	    private int lrcIndex = 0; //保存歌词TreeMap的下标  
	    private  int SIZEWORD = 40;//显示歌词文字的大小值  
	    private  int INTERVAL = 20;//歌词每行的间隔  
	    Paint paint = new Paint();//画笔，用于画不是高亮的歌词  
		Paint paintHL = new Paint();  //画笔，用于画高亮的歌词，即当前唱到这句歌词  
	    private SeekBar mSeekBar;
		private View mMusicListLayout;
		private View mMusicDetailLayout;
		private View mBackgroundLayout;
		
		private View mAnimation;
		private TextView mTotalTime;
		private TextView mCurrentTime;
		private Activity mContext;
		
	    public LyricView(Context context){  
	        super(context);
	        mContext = (Activity)context;
	        init();  
	    }  
	      
	    public LyricView(Context context, AttributeSet attrs) {  
	        super(context, attrs);
	        mContext = (Activity)context;
	        init();  
	    }  
	    
	    
    public void setLayout(View listLayout, View detailLayout, View background){
    	mMusicListLayout = listLayout;
    	mMusicDetailLayout = detailLayout;
    	mBackgroundLayout = background;
    }
    
    public void setAnimation(View animation){
    	mAnimation = animation;
    	if(mAnimation != null){
    		mAnimation.setVisibility(View.INVISIBLE);
    	}
    }
    
    public void setTimeView(TextView currentTime, TextView totalTime){
    	mTotalTime = totalTime;
    	mCurrentTime = currentTime;
    }
	    @Override  
	    protected void onDraw(Canvas canvas) {  
	        if(blLrc){  
	            paintHL.setTextSize(SIZEWORD);  
	            paint.setTextSize(SIZEWORD);  
	            LyricObject temp=lrc_map.get(lrcIndex);  
	            canvas.drawText(temp.lrc, mX, offsetY+(SIZEWORD+INTERVAL)*lrcIndex, paintHL);  
	            // 画当前歌词之前的歌词  
	            for(int i=lrcIndex-1;i>=0;i--){  
	                temp=lrc_map.get(i);  
	                if(offsetY+(SIZEWORD+INTERVAL)*i<0){  
	                    break;  
	                }  
	                canvas.drawText(temp.lrc, mX, offsetY+(SIZEWORD+INTERVAL)*i, paint);  
	            }  
	            // 画当前歌词之后的歌词  
	            for(int i=lrcIndex+1;i<lrc_map.size();i++){  
	                temp=lrc_map.get(i);  
	                if(offsetY+(SIZEWORD+INTERVAL)*i>600){  
	                    break;  
	                }  
	                canvas.drawText(temp.lrc, mX, offsetY+(SIZEWORD+INTERVAL)*i, paint);  
	            }  
	        } else{  
	            paint.setTextSize(25);  
	            canvas.drawText("找不到歌词", mX, 310, paint);  
	        }  
	        super.onDraw(canvas);  
	    }  
	  
	    @Override  
	    public boolean onTouchEvent(MotionEvent event) {  
	        // TODO Auto-generated method stub  
	    	
	    	if(true){
	    		return super.onTouchEvent(event);
	    	}
	        float tt=event.getY();  
	        if(!blLrc){  
	            return super.onTouchEvent(event);  
	        }  
	        switch(event.getAction()){  
	        case MotionEvent.ACTION_DOWN:  
	            touchX=event.getX();  
	            break;  
	        case MotionEvent.ACTION_MOVE:  
	            touchY=tt-touchY;             
	            offsetY=offsetY+touchY;  
	            break;  
	        case MotionEvent.ACTION_UP:  
	            blScrollView=false;  
	            break;        
	        }  
	        touchY=tt;  
	        return true;  
	    }  
	  
	    public void init(){  
	        lrc_map = new TreeMap<Integer, LyricObject>();  
	        offsetY = 320;      
	          
	        paint = new Paint();  
	        paint.setTextAlign(Paint.Align.CENTER);  
	        paint.setColor(Color.BLACK);  
	        paint.setAntiAlias(true);  
	        paint.setDither(true);  
	        paint.setAlpha(180);  
	          
	          
	        paintHL=new Paint();  
	        paintHL.setTextAlign(Paint.Align.CENTER);  
	          
	        paintHL.setColor(Color.RED);  
	        paintHL.setAntiAlias(true);  
	        paintHL.setAlpha(255);  
	        readAsset("LyricSync.lrc");
	        calcTextSize();
	    }  
	      
	    /** 
	     * 根据歌词里面最长的那句来确定歌词字体的大小 
	     */  
	      
	    public void calcTextSize(){
	        if(!blLrc){  
	            return;  
	        }  
	        int max = lrc_map.get(0).lrc.length();  
	        for(int i=1;i<lrc_map.size();i++){  
	            LyricObject lrcStrLength=lrc_map.get(i);  
	            if(max<lrcStrLength.lrc.length()){  
	                max=lrcStrLength.lrc.length();  
	            }  
	        }  
	        SIZEWORD = 320/max;  
	      
	    }  
	      
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
	        mX = w * 0.5f;  
	        super.onSizeChanged(w, h, oldw, oldh);  
	    }  
	      
	    /** 
	     *  歌词滚动的速度 
	     *  
	     * @return 返回歌词滚动的速度 
	     */  
	    public Float SpeedLrc(){  
	        float speed=0;  
	        if(offsetY+(SIZEWORD+INTERVAL)*lrcIndex>220){  
	            speed=((offsetY+(SIZEWORD+INTERVAL)*lrcIndex-220)/20);  
	  
	        } else if(offsetY+(SIZEWORD+INTERVAL)*lrcIndex < 120){  
	            //Log.i("speed", "speed is too fast!!!");  
	            speed = 0;  
	        }  
	        return speed;  
	    }  
	      
	    /** 
	     * 按当前的歌曲的播放时间，从歌词里面获得那一句 
	     * @param time 当前歌曲的播放时间 
	     * @return 返回当前歌词的索引值 
	     */  
	    public int selectIndex(int time){  
	        if(!blLrc){  
	            return 0;  
	        }  
	        int index = 0;  
	        for(int i = 0; i < lrc_map.size(); i++){  
	            LyricObject temp = lrc_map.get(i);  
	            if(temp.begintime < time){  
	                ++index;  
	            }  
	        }  
	        lrcIndex = index-1;  
	        if(lrcIndex < 0){  
	            lrcIndex=0;  
	        }  
	        return lrcIndex;  
	      
	    }  
	      
	    
    public void readAsset(String path){
	    TreeMap<Integer, LyricObject> lrc_read = new TreeMap<Integer, LyricObject>();  
	    String data = "";  
	    try {  

	          blLrc=true;  
	          InputStream is = ((Launcher)(getContext())).getAssets().open(path);
	          BufferedReader br = new BufferedReader(new InputStreamReader(is,"GB2312"));     
	          int i = 0;  
	          Pattern pattern = Pattern.compile("\\d{2}");  
	          while ((data = br.readLine()) != null) {     
	             // System.out.println("++++++++++++>>"+data);  
	                data = data.replace("[","");//将前面的替换成后面的  
	                data = data.replace("]","@");  
	                String splitdata[] =data.split("@");//分隔  
	                if(data.endsWith("@")){  
	                    for(int k=0;k<splitdata.length;k++){  
	                        String str=splitdata[k];  
	                          
	                        str = str.replace(":",".");  
	                        str = str.replace(".","@");  
	                        String timedata[] =str.split("@");  
	                        Matcher matcher = pattern.matcher(timedata[0]);  
	                        if(timedata.length==3 && matcher.matches()){  
	                            int m = Integer.parseInt(timedata[0]);  //分  
	                            int s = Integer.parseInt(timedata[1]);  //秒  
	                            int ms = Integer.parseInt(timedata[2]); //毫秒  
	                            int currTime = (m*60+s)*1000+ms*10;  
	                            LyricObject item1= new LyricObject();  
	                            item1.begintime = currTime;  
	                            item1.lrc       = "";  
	                            lrc_read.put(currTime,item1);  
	                        }  
	                    }  
	                      
	                }  
	                else{  
	                    String lrcContenet = splitdata[splitdata.length-1];   
	              
	                    for (int j=0;j<splitdata.length-1;j++)  
	                    {  
	                        String tmpstr = splitdata[j];  
	                          
	                        tmpstr = tmpstr.replace(":",".");  
	                        tmpstr = tmpstr.replace(".","@");  
	                        String timedata[] =tmpstr.split("@");  
	                        Matcher matcher = pattern.matcher(timedata[0]);  
	                        if(timedata.length==3 && matcher.matches()){  
	                            int m = Integer.parseInt(timedata[0]);  //分  
	                            int s = Integer.parseInt(timedata[1]);  //秒  
	                            int ms = Integer.parseInt(timedata[2]); //毫秒  
	                            int currTime = (m*60+s)*1000+ms*10;  
	                            LyricObject item1= new LyricObject();  
	                            item1.begintime = currTime;  
	                            item1.lrc       = lrcContenet;  
	                            lrc_read.put(currTime,item1);// 将currTime当标签  item1当数据 插入TreeMap里  
	                            i++;  
	                        }  
	                    }  
	                }  
	                  
	          }   
	         is.close();  
	        }  
	        catch (FileNotFoundException e) {  
	        }  
	        catch (IOException e) {  
	        }  
	          
	        /* 
	         * 遍历hashmap 计算每句歌词所需要的时间 
	        */  
	        lrc_map.clear();  
	        data ="";  
	        Iterator<Integer> iterator = lrc_read.keySet().iterator();  
	        LyricObject oldval  = null;  
	        int i =0;  
	        while(iterator.hasNext()) {  
	            Object ob =iterator.next();  
	            LyricObject val = (LyricObject)lrc_read.get(ob);  
	              
	            if (oldval==null){
	                oldval = val; 
	            }else{  
	                LyricObject item1= new LyricObject();  
	                item1  = oldval;  
	                item1.timeline = val.begintime-oldval.begintime;  
	                lrc_map.put(new Integer(i), item1);  
	                i++;  
	                oldval = val;  
	            }  
	            if (!iterator.hasNext()) {  
	                lrc_map.put(new Integer(i), val);  
	            }
	        }  
    }	    
	 /** 
	     * 读取歌词文件 
	     * @param file 歌词的路径 
	     *  
	*/  
	public static void read(String file) {  
	    TreeMap<Integer, LyricObject> lrc_read = new TreeMap<Integer, LyricObject>();  
	    String data = "";  
	    try {  
	          File saveFile=new File(file);  
	         // System.out.println("是否有歌词文件"+saveFile.isFile());  
	          if(!saveFile.isFile()){  
	              blLrc=false;  
	              return;  
	          }  
	          blLrc=true;  
	            
	          //System.out.println("bllrc==="+blLrc);  
	          FileInputStream stream = new FileInputStream(saveFile);//  context.openFileInput(file);  
	            
	            
	          BufferedReader br = new BufferedReader(new InputStreamReader(stream,"GB2312"));     
	          int i = 0;  
	          Pattern pattern = Pattern.compile("\\d{2}");  
	          while ((data = br.readLine()) != null) {     
	             // System.out.println("++++++++++++>>"+data);  
	                data = data.replace("[","");//将前面的替换成后面的  
	                data = data.replace("]","@");  
	                String splitdata[] =data.split("@");//分隔  
	                if(data.endsWith("@")){  
	                    for(int k=0;k<splitdata.length;k++){  
	                        String str=splitdata[k];  
	                          
	                        str = str.replace(":",".");  
	                        str = str.replace(".","@");  
	                        String timedata[] =str.split("@");  
	                        Matcher matcher = pattern.matcher(timedata[0]);  
	                        if(timedata.length==3 && matcher.matches()){  
	                            int m = Integer.parseInt(timedata[0]);  //分  
	                            int s = Integer.parseInt(timedata[1]);  //秒  
	                            int ms = Integer.parseInt(timedata[2]); //毫秒  
	                            int currTime = (m*60+s)*1000+ms*10;  
	                            LyricObject item1= new LyricObject();  
	                            item1.begintime = currTime;  
	                            item1.lrc       = "";  
	                            lrc_read.put(currTime,item1);  
	                        }  
	                    }  
	                      
	                }  
	                else{  
	                    String lrcContenet = splitdata[splitdata.length-1];   
	              
	                    for (int j=0;j<splitdata.length-1;j++)  
	                    {  
	                        String tmpstr = splitdata[j];  
	                          
	                        tmpstr = tmpstr.replace(":",".");  
	                        tmpstr = tmpstr.replace(".","@");  
	                        String timedata[] =tmpstr.split("@");  
	                        Matcher matcher = pattern.matcher(timedata[0]);  
	                        if(timedata.length==3 && matcher.matches()){  
	                            int m = Integer.parseInt(timedata[0]);  //分  
	                            int s = Integer.parseInt(timedata[1]);  //秒  
	                            int ms = Integer.parseInt(timedata[2]); //毫秒  
	                            int currTime = (m*60+s)*1000+ms*10;  
	                            LyricObject item1= new LyricObject();  
	                            item1.begintime = currTime;  
	                            item1.lrc       = lrcContenet;  
	                            lrc_read.put(currTime,item1);// 将currTime当标签  item1当数据 插入TreeMap里  
	                            i++;  
	                        }  
	                    }  
	                }  
	                  
	          }   
	         stream.close();  
	        }  
	        catch (FileNotFoundException e) {  
	        }  
	        catch (IOException e) {  
	        }  
	          
	        /* 
	         * 遍历hashmap 计算每句歌词所需要的时间 
	        */  
	        lrc_map.clear();  
	        data ="";  
	        Iterator<Integer> iterator = lrc_read.keySet().iterator();  
	        LyricObject oldval  = null;  
	        int i =0;  
	        while(iterator.hasNext()) {  
	            Object ob =iterator.next();  
	              
	            LyricObject val = (LyricObject)lrc_read.get(ob);  
	              
	            if (oldval==null)  
	                oldval = val;  
	            else  
	            {  
	                LyricObject item1= new LyricObject();  
	                item1  = oldval;  
	                item1.timeline = val.begintime-oldval.begintime;  
	                lrc_map.put(new Integer(i), item1);  
	                i++;  
	                oldval = val;  
	            }  
	            if (!iterator.hasNext()) {  
	                lrc_map.put(new Integer(i), val);  
	            }  
	              
	        }  
	  
	    }     
	      
	    /** 
	     * @return the blLrc 
	     */  
	    public static boolean isBlLrc() {  
	        return blLrc;  
	    }  
	  
	    /** 
	     * @return the offsetY 
	     */  
	    public float getOffsetY() {  
	        return offsetY;  
	    }  
	  
	    /** 
	     * @param offsetY the offsetY to set 
	     */  
	    public void setOffsetY(float offsetY) {  
	        this.offsetY = offsetY;  
	    }  
	  
	    /** 
	     * @return 返回歌词文字的大小 
	     */  
	    public int getLyricsTextSize() {  
	        return SIZEWORD;  
	    }  
	  
	/** 
	* 设置歌词文字的大小 
	* @param sIZEWORD the sIZEWORD to set 
	*/  
	public void setSyricsTextSize(int textSize) {  
		SIZEWORD = textSize;  
	}
	    
	private static final HandlerThread sWorkerThread = new HandlerThread("music-lyrics");
	static {
		sWorkerThread.start();
	}
	    
	private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
	private MediaPlayer mMediaPlayer;
	    
	public void playMusic(String lyricFile){
		if(mAnimation != null){
			mAnimation.setVisibility(View.VISIBLE);
		}		
		initLyricInfo(lyricFile);
		calcTextSize();
		initMediaPlayerAndSeekBar();
		sWorker.post(new runable());
	}
	    
    public void initLyricInfo(String lyricFile){
        setOffsetY(350);	    	
    }
	    
	public void initMediaPlayer() {  
		if(mMediaPlayer != null){
			mMediaPlayer.reset();
		}
		try {  
	        	mMediaPlayer = MediaPlayer.create(getContext(), R.raw.song);  
	        	mMediaPlayer.prepare();  
		}catch (IllegalArgumentException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
		}catch (IllegalStateException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
		} catch (IOException e) {  
	            // TODO Auto-generated catch block  
		            e.printStackTrace();  
		}  
	        
		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
	            @Override  
	            public void onCompletion(MediaPlayer mp) {  
	            	initMediaPlayerAndSeekBar();
	            	setOffsetY(200);
	                mMediaPlayer.start();  
	            }  
	        });
			mMediaPlayer.start();  
			setOffsetY(220 - selectIndex(
					mMediaPlayer.getCurrentPosition())  
	                * (getLyricsTextSize() + 45 - 1));			
	    }
		
    
	private Handler mHandler;
	public void setHandler(Handler handler){
		mHandler = handler;
	}
	    
		class runable implements Runnable {  
	        @Override  
	        public void run() {
	            // TODO Auto-generated method stub  
	            while (true) {
	                  try {  
	                    Thread.sleep(100);  
	                    if (mMediaPlayer.isPlaying()) {  
	                    	setOffsetY(getOffsetY() - SpeedLrc());  
	                    	selectIndex(mMediaPlayer.getCurrentPosition());
	                    	mContext.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
			                    	if(mCurrentTime != null){
			                    		//mCurrentTime.setText(""+mMediaPlayer.getCurrentPosition());
			                    	}
								}
							});
	
	                    	mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
	                        mHandler.sendEmptyMessage(Launcher.UPDATE_LYRICS); 
	                    }
	                } catch (InterruptedException e) {  
	                    // TODO Auto-generated catch block  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if(view != null && view.getId() == R.id.play_pause_button){
			/*
			if(mMusicListLayout != null){
				mMusicListLayout.setVisibility(View.INVISIBLE);
			}
			if(mMusicDetailLayout != null){
				if(mBackgroundLayout != null){
					mBackgroundLayout.setBackgroundResource(R.drawable.music_detail_background);
				}
				mMusicDetailLayout.setVisibility(View.VISIBLE);
			}*/
			
			if(mMediaPlayer != null){
				if(mMediaPlayer.isPlaying()){
					view.setBackgroundResource(R.drawable.button_play_selector);
					if(mAnimation != null){
						mAnimation.setVisibility(View.INVISIBLE);
					}
					mMediaPlayer.pause();
				}else{
					mMediaPlayer.start();
					if(mAnimation != null){
						mAnimation.setVisibility(View.VISIBLE);
						
					}
					view.setBackgroundResource(R.drawable.button_pause_selector);
				}
			}else{
				playMusic("LyricSync.lrc");
				view.setBackgroundResource(R.drawable.button_pause_selector);
			}
		}
	}	    

	public void resetMusic(View view){
		playMusic("LyricSync.lrc");
		view.setBackgroundResource(R.drawable.button_pause_selector);
		if(mMusicListLayout != null){
			mMusicListLayout.setVisibility(View.INVISIBLE);
		}
		if(mMusicDetailLayout != null){
			if(mBackgroundLayout != null){
				mBackgroundLayout.setBackgroundResource(R.drawable.music_detail_background);
			}
			mMusicDetailLayout.setVisibility(View.VISIBLE);
		}		
	}
	
	private void initMediaPlayerAndSeekBar(){
		initMediaPlayer();
    	if(mSeekBar != null){
    		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					if(fromUser){
						if(mMediaPlayer != null){
						   mMediaPlayer.seekTo(progress);
						}

	                    setOffsetY(220 - selectIndex(progress) * (getLyricsTextSize() + 45-1)); 
					}
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
    		});
    		if(mMediaPlayer != null){
    			mSeekBar.setMax(mMediaPlayer.getDuration());
    			if(mTotalTime != null){
    				//mTotalTime.setText(mMediaPlayer.getDuration() + "");
    			}
    		}
    	}		
	}
	
    public void setSeekBar(SeekBar seekBar){
    	mSeekBar = seekBar;
    }
}
