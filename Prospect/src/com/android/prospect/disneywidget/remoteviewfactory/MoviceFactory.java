package com.android.prospect.disneywidget.remoteviewfactory;



import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;
import android.net.Uri;
import android.content.SharedPreferences;

import com.android.prospect.R;
import com.android.prospect.disneywidget.provider.MoviceWidget;
import com.android.prospect.entity.Movice;
import com.android.prospect.util.WidgetHelper;


public class MoviceFactory implements RemoteViewsService.RemoteViewsFactory {


        public SharedPreferences moviceFactoryPreferences;
	private List<Object> mWidgetItems;
	private Context mContext;
	private int mAppWidgetId;
	private String[] arr;
	private RemoteViews rv = null;
	private boolean flag = true;
	private boolean flag_count = true;
	private boolean flag_out = true;
	private boolean flag_conn=true;
        private boolean flag_first=true;
        private boolean flag_hide=true;
        private boolean flag_data=true;
        private boolean  flag_ln=false;
        static int j=0;
	private final static String TAG = "ListRemote";

	public MoviceFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public int getCount() {
		arr = new String[mWidgetItems.size()];
		if(mWidgetItems.size()>0&&!flag_out){
			WidgetHelper.hideToast(mContext,"movicewidget");
                        flag_hide=false;
			
		}
		return mWidgetItems.size()+1;

	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public RemoteViews getLoadingView() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public RemoteViews getViewAt(final int position) {
		// TODO Auto-generated method stub
		if(position==mWidgetItems.size()){
                    rv = new RemoteViews(mContext.getPackageName(),
							R.layout.item_more);
                    rv.setTextViewText(R.id.more,mContext.getResources().getString(R.string.more_mivie));
                    Intent intent = new Intent();
		    String url="http://m.movie.dol.cn/Mnews/index-m.shtml";
		    Uri content_url = Uri.parse(url);   
                    intent.setData(content_url);
	            rv.setOnClickFillInIntent(R.id.more, intent);
                    return rv;
                 
                }else{
		new Thread(new Runnable() {
			
			public void run() {
				synchronized (MoviceFactory.this) {
					// refresh UI
                                        rv = new RemoteViews(mContext.getPackageName(),
							R.layout.movie_listadapter);
					rv.setTextViewText(R.id.desc,
							((Movice) mWidgetItems.get(position)).getDesc());
					rv.setTextViewText(R.id.title,
							((Movice) mWidgetItems.get(position)).getTitle());
					arr[position] = ((Movice) mWidgetItems.get(position))
							.getImgPath();
					//redriector
					Intent intent = new Intent();
					//the url
					String url=((Movice)mWidgetItems.get(position)).getUrl();
					Uri content_url = Uri.parse(url);   
                                        intent.setData(content_url);
					rv.setOnClickFillInIntent(R.id.desc, intent);
					rv.setOnClickFillInIntent(R.id.img, intent);
					rv.setOnClickFillInIntent(R.id.title, intent);
					//set picture
					flag_out = true;
					flag = false;
				}

			}

		}).start();

		while (flag) {
		}
		
		flag=true;
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				try{
						String url="";
						InputStream in =null;
                                                InputStream in_show=null;
                                                NetworkInfo network = ((ConnectivityManager) mContext
						.getSystemService(mContext.CONNECTIVITY_SERVICE))
						.getActiveNetworkInfo();	


                                          if(flag_ln&&network!=null&&network.isAvailable()){
                                                        url=((Movice) mWidgetItems.get(position)).getImgPath();
							in=openConnection(url,position);
							String file_path=Environment.getExternalStorageDirectory()
									.toString()+"/widget";
							File  f=new File(file_path);
							if(!f.exists()){
								f.mkdirs();
							}
							String file_movie=file_path+"/movie";
							File  f_movie=new File(file_movie);
							if(!f_movie.exists()){
								f_movie.mkdirs();
							}
							File file=new File(file_movie + "/" + position + ".jpg");
							byte[] b = new byte[1024];
							int count = 0;
							try {
								FileOutputStream fos = new FileOutputStream(file,false);
								while ((count = in.read(b)) != -1) {
									fos.write(b,0,count);
								}
								fos.close();    
								in.close();
							} catch (Exception e) {
								showException(e);
							}
						}

					
				       
				              url=Environment.getExternalStorageDirectory().toString() +"/widget/movie"+ "/" + position + ".jpg";
			       	             try {
								in_show=new FileInputStream(new File(url));
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				       
						
						Bitmap bitmap = BitmapFactory.decodeStream(in_show);
						rv.setImageViewBitmap(R.id.img, bitmap);
						flag_first=false;
                                                in_show.close();
						



						flag_out = false;
                                                      if(flag_hide){
                                                                    WidgetHelper.hideToast(mContext,"movicewidget");
                                                                    flag_hide=false;
                                                                                 }
				}catch (Exception e) {
					// TODO: handle exception
					showException(e);
				}
			}

		}).start();
		
		while (flag_out) {
		}
                while(j<=0){
			if(!flag_first){
				WidgetHelper.hideToast(mContext,"movicewidget");
			}
			j++;
		}
		return rv;
                }
	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public void onCreate() {
        moviceFactoryPreferences=mContext.getSharedPreferences("movicewidget",0);
       Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			public void uncaughtException(Thread arg0, Throwable arg1) {
				// TODO Auto-generated method stub
			
				 
			}
		});
	}

	public void onDataSetChanged() {
		// TODO Auto-generated method stub
		String json="";
                flag_data=true;
		NetworkInfo network = ((ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

                flag_ln=moviceFactoryPreferences.getBoolean("moviceBoolean",false);
                String url=Environment.getExternalStorageDirectory().toString()+"/widget/widgetmovice.txt";
                File file=new File(url);
                if(!file.exists()){
                flag_ln=true;
                }

                 
                String url_img=null;
                File file_img=null;
                for(int i=0;i<=9;i++){
                url_img=Environment.getExternalStorageDirectory().toString()+"/widget/movie/"+i+".jpg";
                file_img=new File(url_img);
                if(!file_img.exists()){
                flag_ln=true;
                break;
                }  
                }

                
        if(!flag_ln||network==null||!network.isAvailable()){
                 Log.i("0723","movice local");
        	getLocalData();
                while(flag_data){
                   }
        }else{
                Log.i("0723","movice net");
        	getNetData();
                while(flag_data){
                   }
        }
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
	}



	/**
	 * get dataresult
	 * 
	 * @param jsonArray
	 * @return List<Object>
	 */
	private BufferedInputStream bin;

	public List<Object> getList(JSONArray jsonArray) throws Exception {
		ArrayList<Object> arrList = new ArrayList<Object>();

		JSONObject jsonObj = jsonArray.getJSONObject(0); 
		JSONArray js = jsonObj.getJSONArray("data");

		for (int j = 0; j < js.length(); j++) {
			JSONObject jo = js.getJSONObject(j);
			String imgPath = jo.getString("thumb");
			String desc = jo.getString("desc");
			String url=jo.getString("pcmb");
			desc = URLDecoder.decode(desc, "UTF-8");
			String title = jo.getString("title");
			title = URLDecoder.decode(title, "UTF-8");
			int id = jo.getInt("id");
			Movice movice = new Movice();
			movice.setDesc(desc);
			movice.setId(id);
			movice.setImgPath(imgPath);
			movice.setTitle(title);
			movice.setUrl(url);
			arrList.add(movice);
		}

		return arrList;
	}

	/**
	 * get jsonarray
	 * 
	 * @param inputStream
	 * @return JSONArray
	 * @throws Exception
	 */
	public JSONArray getJsonArray(String httpUrl) throws Exception {
		JSONArray jsonArr = new JSONArray();
		URL url = new URL(httpUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
        	conn.setConnectTimeout(6000);
        	conn.setRequestMethod("GET");
        	String json = "";
        	if (200 == conn.getResponseCode()) {
        		InputStream is = conn.getInputStream(); // get inputstream
        		//create file
        		String file_path=Environment.getExternalStorageDirectory().toString()+"/widget";
        		File  f=new File(file_path);
        		if(!f.exists()){
        			f.mkdirs();
        		}
        		//save local
        		String path=file_path+"/widgetmovice.txt";
        		File  file=new File(path);
        		
        		FileOutputStream  fos=new FileOutputStream(file,false);
        		//change to JSONArray
        		is=conn.getInputStream();
        		ByteArrayOutputStream bout = new ByteArrayOutputStream();
        		byte[] buffer = new byte[1024];
        		int len = 0;
        		
        		while ((len = is.read(buffer)) != -1) {
        			bout.write(buffer, 0, len);
        			fos.write(buffer,0,len);
        		}
        		bout.close();
        		fos.close();
        		is.close();
        		byte[] data = bout.toByteArray(); 
        		json = new String(data); 
        		json = URLDecoder.decode(json, "UTF-8");
        		
        		JSONObject jo = new JSONObject(json);
        		jsonArr.put(jo);
        	} 
		} catch (Exception e) {
			showException(e);
			WidgetHelper.toastException(mContext,"movicewidget");

			return getLocalData();
		}

		return jsonArr;
	}

	/**
	 * get byte[]
	 * 
	 * @param inputStream
	 *            
	 * @return  byte[]
	 * 
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inputStream) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;

		while ((len = inputStream.read(buffer)) != -1) {
			bout.write(buffer, 0, len);
		}
		bout.close();
		inputStream.close();

		return bout.toByteArray();

	}

	/**
	 * get inputStream
	 * 
	 * @param url
	 * @return InputStream
	 */
	private InputStream openConnection(String url,int position) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6000);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return conn.getInputStream();
			}
		} catch (Exception e) {
			showException(e);
			String url_local="";
			if(position>=0){
				url_local=Environment.getExternalStorageDirectory().toString() +"/widget/movie"+ "/" + position + ".jpg";;
			}else{
				url_local=Environment.getExternalStorageDirectory().toString()+"/widget/widgetmovice.txt";
			}
			FileInputStream is=null;
			try {
	        	is=new FileInputStream(new File(url_local));
			} catch (Exception e2) {
				showException(e2);
				WidgetHelper.toastException(mContext,"movicewidget");

			}
			return is;
		}
	return null;
	}

	/**
	 * showexception
	 */
	public void showException(Exception e) {
		Log.i("tag", e.toString());
		StackTraceElement[] ste = e.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			Log.i("tag", ste[i].toString());
		}

	}


  
        
      /**
       * get local data
       */
	public JSONArray getLocalData(){
        String url=Environment.getExternalStorageDirectory().toString()+"/widget/widgetmovice.txt";
        FileInputStream is=null;
        String json="";
        JSONArray jsonArray=null;
        File  file=new File(url);
        if(file.exists()){
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			try {
				while ((len = is.read(buffer)) != -1) {
					bout.write(buffer, 0, len);
				}
				bout.close();
				is.close();
			} catch (Exception e) {
				// TODO: handle exception
				showException(e);
				WidgetHelper.toastException(mContext,"movicewidget");

			}
			byte[] data = bout.toByteArray(); 
			json = new String(data);
      try {
			JSONObject  jsonObject=new JSONObject(json);
			jsonArray=new JSONArray();
			jsonArray.put(jsonObject);
			mWidgetItems=getList(jsonArray);
                        flag_data=false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			showException(e);
			WidgetHelper.toastException(mContext,"movicewidget");

		}
        }else{
        	        RemoteViews  remoteViews=new RemoteViews(mContext.getPackageName(), R.layout.movie_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.VISIBLE);
			remoteViews.setTextViewText(R.id.text_start, mContext.getResources().getString(R.string.reload));
			MoviceWidget.myappWidgetManager.updateAppWidget(MoviceWidget.myappWidgetIds, remoteViews);
                        flag_data=false;
        }
                        return  jsonArray;
	}
	
	
	/**
	 * get net data
	 */
    public void getNetData(){
   	 synchronized (MoviceFactory.this) {

			new Thread(new Runnable() {
				JSONArray jsonArray;

				public void run() {
					// TODO Auto-generated method stub
					try{
					synchronized (MoviceFactory.this) {

						try {
							jsonArray = getJsonArray(mContext.getResources().getString(R.string.inter_movie_url));
							mWidgetItems = getList(jsonArray);
							flag_data=false;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							showException(e);
						}
					}
					}catch (Exception e) {
						// TODO: handle exception
						showException(e);
						WidgetHelper.toastException(mContext,"movicewidget");
					}
				}

			}).start();
   	 }
   
    }
    
   
}

