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
import com.android.prospect.disneywidget.provider.PictureWidget;
import com.android.prospect.disneywidget.provider.ShoppingWidget;
import com.android.prospect.entity.Shopping;
import com.android.prospect.util.WidgetHelper;


public class ShoppingFactory implements RemoteViewsService.RemoteViewsFactory {


        public SharedPreferences shoppingFactoryPreferences;
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
        private boolean  flag_ln=false;
        public  static boolean flag_refresh_completed=false;
        private boolean flag_data=true;
        private ShoppingWidget sw;
	private final static String TAG = "ListRemote";
        static int j=0;

	public ShoppingFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public int getCount() {
	
		int i=0;
		
		arr = new String[mWidgetItems.size()];
		if(mWidgetItems.size()>0&&!flag_out){
			WidgetHelper.hideToast(mContext,"shoppingwidget");
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
                    rv.setTextViewText(R.id.more,mContext.getResources().getString(R.string.more));
                    Intent intent = new Intent();
		    String url="http://m.store.dol.cn/index-m.shtml";
		    Uri content_url = Uri.parse(url);   
                    intent.setData(content_url);
	            rv.setOnClickFillInIntent(R.id.more, intent);
                    flag_refresh_completed=true;
                    return rv;
                 
                }else{
                 
		new Thread(new Runnable() {
		
			public void run() {
				try{
				synchronized (ShoppingFactory.this) {
					rv = new RemoteViews(mContext.getPackageName(),
							R.layout.shopping_listadapter);
					rv.setTextViewText(R.id.title,
							((Shopping) mWidgetItems.get(position)).getTitle());
					arr[position] = ((Shopping) mWidgetItems.get(position))
							.getThumb();
					
					Intent intent = new Intent();
					
					String url=((Shopping)mWidgetItems.get(position)).getUrl();
					Uri content_url = Uri.parse(url);   
                                        intent.setData(content_url);
					
					rv.setOnClickFillInIntent(R.id.img, intent);
					rv.setOnClickFillInIntent(R.id.title, intent);
					
					flag_out = true;
					flag = false;
				}
				}catch (Exception e) {
					// TODO: handle exception
					showException(e);
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
                                                InputStream in_show =null;
						NetworkInfo network = ((ConnectivityManager) mContext.getSystemService                              (mContext.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                                       



                                               if(flag_ln&&network!=null&&network.isAvailable()){
                                                        url=((Shopping) mWidgetItems.get(position)).getThumb();
							in=openConnection(url,position);
							String file_path=Environment.getExternalStorageDirectory()
									.toString()+"/widget";
							File  f=new File(file_path);
							if(!f.exists()){
								f.mkdirs();
							}
							String file_movie=file_path+"/shopping";
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









				       
				        	 url=Environment.getExternalStorageDirectory().toString() +"/widget/shopping"+ "/" + position + ".jpg";
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
                                                                   WidgetHelper.hideToast(mContext,"shoppingwidget");
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
				WidgetHelper.hideToast(mContext,"shoppingwidget");
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
        shoppingFactoryPreferences=mContext.getSharedPreferences("shoppingwidget",0);
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


                flag_ln=shoppingFactoryPreferences.getBoolean("shoppingBoolean",false);
                String url=Environment.getExternalStorageDirectory().toString()+"/widget/widgetshopping.txt";
                File file=new File(url);
                if(!file.exists()){
                flag_ln=true;
                }

                String url_img=null;
                File file_img=null;
                for(int i=0;i<=9;i++){
                url_img=Environment.getExternalStorageDirectory().toString()+"/widget/shopping/"+i+".jpg";
                file_img=new File(url_img);
                if(!file_img.exists()){
                flag_ln=true;
                break;
                }  
                }

               
        if(!flag_ln||network==null||!network.isAvailable()){
                Log.i("0723","shopping local");
        	getLocalData();
                while(flag_data){
                   }
        }else{
                Log.i("0723","shopping net");
        	getNetData();
                while(flag_data){
                   }
        }
	}

	private void getNetData() {
		// TODO Auto-generated method stub

   	 
   	 synchronized (ShoppingFactory.this) {

			new Thread(new Runnable() {
				JSONArray jsonArray;

				public void run() {
					// TODO Auto-generated method stub
					try{
					synchronized (ShoppingFactory.this) {

						try {
							jsonArray = getJsonArray(mContext.getResources().getString(R.string.inter_shopping_url));
							mWidgetItems = getList(jsonArray);
                                                        flag_data=false;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							showException(e);
                                                        getLocalData();
						}

					}
					}catch (Exception e) {
						// TODO: handle exception
						showException(e);
						WidgetHelper.toastException(mContext,"shoppingwidget");
					}
				}

			}).start();
   	 }
   
	}
/**
 *  get local data
 * @return JSONArray
 */
	private JSONArray getLocalData() {
		// TODO Auto-generated method stub

        String url=Environment.getExternalStorageDirectory().toString()+"/widget/widgetshopping.txt";
        FileInputStream is=null;
        String json="";
        JSONArray jsonArray=null;
        File file=new File(url);
        if(file.exists()){
        	try {
    			is = new FileInputStream(new File(url));
    		} catch (FileNotFoundException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
				WidgetHelper.toastException(mContext,"shoppingwidget");
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
    				WidgetHelper.toastException(mContext,"shoppingwidget");
    			}
    			byte[] data = bout.toByteArray(); 
    			json = new String(data);
          try {
    			
    			 jsonArray=new JSONArray(json);
    			mWidgetItems=getList(jsonArray);
                        flag_data=false;
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			showException(e);
				//WidgetHelper.toastException(mContext,"shoppingwidget");
    		}
        }else{

                Log.i("tanxizhuo","delete the files");
        	RemoteViews  remoteViews=new RemoteViews(mContext.getPackageName(), R.layout.shopping_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.VISIBLE);
			remoteViews.setTextViewText(R.id.text_start,mContext.getResources().getString(R.string.reload));
			ShoppingWidget.myappWidgetManager.updateAppWidget(ShoppingWidget.myappWidgetIds, remoteViews);
                        flag_data=false;
                       
        }
		
      return jsonArray;
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
		JSONArray js = jsonArray;

		for (int j = 0; j < js.length(); j++) {
			JSONObject jo = js.getJSONObject(j);
			String imgPath = jo.getString("thumb");
			
			String url=jo.getString("pcmb");
			
			String title = jo.getString("title");
			title = URLDecoder.decode(title, "UTF-8");
			String id = jo.getString("id");
			Shopping shopping = new Shopping();
			
			shopping.setId(id);
			shopping.setThumb(imgPath);
			shopping.setTitle(title);
			shopping.setUrl(url);
			
			arrList.add(shopping);
		}

		return arrList;
	}

	/**
	 *get jsonarray
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
        		InputStream is = conn.getInputStream();
        		
        		String file_path=Environment.getExternalStorageDirectory().toString()+"/widget";
        		File  f=new File(file_path);
        		if(!f.exists()){
        			f.mkdirs();
        		}
        		
        		String path=file_path+"/widgetshopping.txt";
        		File  file=new File(path);
        		FileOutputStream  fos=new FileOutputStream(file,false);
        		
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
        		
        		
        		jsonArr=new JSONArray(json);
        	} 
		} catch (Exception e) {
			// TODO: handle exception
			showException(e);
			return getLocalData();
		}
		return jsonArr;
	}

	/**
	 * get byte[]
	 * 
	 * @param inputStream
	 *            
	 * @return byte[]
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
	 * get inputstream
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
				url_local=Environment.getExternalStorageDirectory().toString() +"/widget/shopping"+ "/" + position + ".jpg";;
			}else{
				url_local=Environment.getExternalStorageDirectory().toString()+"/widget/widgetshopping.txt";
			}
			FileInputStream is=null;
			try {
	        	is=new FileInputStream(new File(url_local));
			} catch (Exception e2) {
				showException(e2);
				WidgetHelper.toastException(mContext,"shoppingwidget");

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



}

