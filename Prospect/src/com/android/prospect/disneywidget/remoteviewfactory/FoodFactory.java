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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.android.prospect.R;
import com.android.prospect.disneywidget.provider.FoodWidget;
import com.android.prospect.disneywidget.provider.MoviceWidget;
import com.android.prospect.entity.Food;
import com.android.prospect.util.WidgetHelper;
import android.net.Uri;


public class FoodFactory implements RemoteViewsService.RemoteViewsFactory {

        public SharedPreferences foodFactoryPreferences;
	private List<Object> mWidgetItems;
	private Context mContext;
	private int mAppWidgetId;
	private String[] arr;
	private RemoteViews rv = null;
	private boolean flag = true;
        private boolean  flag_ln=false;
	private boolean flag_count = true;
	private boolean flag_out = true;
	private boolean flag_conn = true;
	private boolean flag_first=true;
        private boolean flag_hide=true;
        private boolean flag_data=true;
	static int j=0;

	private final static String TAG = "ListRemote";

	public FoodFactory(Context context, Intent intent) {
		mContext = context;
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
	}

	public int getCount() {
		
		arr = new String[mWidgetItems.size()];
		if(mWidgetItems.size()>0&&!flag_out){
			WidgetHelper.hideToast(mContext,"foodwidget");	
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
                 if(position==mWidgetItems.size()){
                    rv = new RemoteViews(mContext.getPackageName(),
							R.layout.item_more);
                    rv.setTextViewText(R.id.more,mContext.getResources().getString(R.string.more_food));
                    Intent intent = new Intent();
		    String url="http://m.baby.dol.cn/food/index-m.shtml";
		    Uri content_url = Uri.parse(url);   
                    intent.setData(content_url);
	            rv.setOnClickFillInIntent(R.id.more, intent);
                    return rv;
                 
                }else{
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			public void run() {
				try{
				synchronized (FoodFactory.this) {
					// refresh UI
                                    
					rv = new RemoteViews(mContext.getPackageName(),
							R.layout.food_listadapter);
                                              
					String reqirment = ((Food) mWidgetItems.get(position))
							.getReqirment();
					reqirment = reqirment.replaceAll("<p>", "");
					reqirment = reqirment.replaceAll("</p>",
							"&nbsp&nbsp&nbsp&nbsp");
					rv.setTextViewText(R.id.reqirment, Html.fromHtml(reqirment));
					String content = ((Food) mWidgetItems.get(position))
							.getContent();
					content = content.replaceAll("</li>", "</li><br/>");
					rv.setTextViewText(R.id.content, Html.fromHtml(content));
					rv.setTextViewText(R.id.title,
							((Food) mWidgetItems.get(position)).getTitle());
					arr[position] = ((Food) mWidgetItems.get(position))
							.getThumb();
					// redriection
					Intent intent = new Intent();
					//  the  url
					String url = ((Food) mWidgetItems.get(position)).getUrl();
					Uri content_url = Uri.parse(url);   
                                        intent.setData(content_url);
					rv.setOnClickFillInIntent(R.id.reqirment, intent);
					rv.setOnClickFillInIntent(R.id.img, intent);
					rv.setOnClickFillInIntent(R.id.title, intent);
                                        rv.setOnClickFillInIntent(R.id.content,intent);
					// set picture
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

		flag = true;
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				try{
				String url = "";
				InputStream in = null;
                                InputStream in_show = null;
				NetworkInfo network = ((ConnectivityManager) mContext
						.getSystemService(mContext.CONNECTIVITY_SERVICE))
						.getActiveNetworkInfo();


                                if (flag_ln&&network != null && network.isAvailable()) {
                                        url = ((Food) mWidgetItems.get(position)).getThumb();
					in = openConnection(url, position);
					String file_path = Environment
							.getExternalStorageDirectory().toString()
							+ "/widget";
					File f = new File(file_path);
					if (!f.exists()) {
						f.mkdirs();
					}
					String file_movie = file_path + "/food";
					File f_movie = new File(file_movie);
					if (!f_movie.exists()) {
						f_movie.mkdirs();
					}
					File file = new File(file_movie + "/" + position + ".jpg");
					byte[] b = new byte[1024];
					int count = 0;
					try {
						FileOutputStream fos = new FileOutputStream(file, false);
						while ((count = in.read(b)) != -1) {
							fos.write(b, 0, count);
						}
						fos.close();
						in.close();
					} catch (Exception e) {
						showException(e);
					}
				}

					url = Environment.getExternalStorageDirectory().toString()
							+ "/widget/food" + "/" + position + ".jpg";
					try {
						in_show = new FileInputStream(new File(url));
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
                                WidgetHelper.hideToast(mContext,"foodwidget");
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
				WidgetHelper.hideToast(mContext,"foodwidget");
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
        foodFactoryPreferences=mContext.getSharedPreferences("foodwidget",0);
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

			public void uncaughtException(Thread arg0, Throwable arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void onDataSetChanged() {
		// TODO Auto-generated method stub
		String json = "";
		NetworkInfo network = ((ConnectivityManager) mContext
				.getSystemService(mContext.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
                flag_ln=foodFactoryPreferences.getBoolean("foodBoolean",false);
                String url=Environment.getExternalStorageDirectory().toString()+"/widget/widgetfood.txt";
                File file=new File(url);
                if(!file.exists()){
                flag_ln=true;
                }

                String url_img=null;
                File file_img=null;
                for(int i=0;i<=9;i++){
                url_img=Environment.getExternalStorageDirectory().toString()+"/widget/food/"+i+".jpg";
                file_img=new File(url_img);
                if(!file_img.exists()){
                flag_ln=true;
                break;
                }  
                }
                
		if (!flag_ln||network == null || !network.isAvailable()) {
                         Log.i("0723","food local");
                        flag_data=true;
			getLocalData();
                        while(flag_data){
                          }
		} else {
                        Log.i("0723","food net");
                        flag_data=true;
			getNetData();
                        while(flag_data){
                          }
		}
	}

	/**
	 * get net data
	 */
	private void getNetData() {
		// TODO Auto-generated method stub


		synchronized (FoodFactory.this) {


			new Thread(new Runnable() {
				JSONArray jsonArray;

				public void run() {
					// TODO Auto-generated method stub
					try{
					synchronized (FoodFactory.this) {

						try {
							jsonArray = getJsonArray(mContext.getResources().getString(R.string.inter_food_url));
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
						WidgetHelper.toastException(mContext,"foodwidget");
					}
				}

			}).start();
		}
	}

	/**
	 * get local data
	 * 
	 * @return
	 */
	private JSONArray getLocalData() {
		// TODO Auto-generated method stub

		String url = Environment.getExternalStorageDirectory().toString()
				+ "/widget/widgetfood.txt";
		FileInputStream is = null;
		String json = "";
		JSONArray jsonArray = null;
		File file=new File(url);
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
			}
			byte[] data = bout.toByteArray(); 
			json = new String(data);
			try {
				
				jsonArray = new JSONArray(json);
				
				mWidgetItems = getList(jsonArray);
                                flag_data=false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				showException(e);
			}
		}else{
			RemoteViews  remoteViews=new RemoteViews(mContext.getPackageName(), R.layout.food_main);
			remoteViews.setViewVisibility(R.id.progress_bar, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.text_start, View.VISIBLE);
			remoteViews.setTextViewText(R.id.text_start,mContext.getResources().getString(R.string.reload));
			FoodWidget.myappWidgetManager.updateAppWidget(FoodWidget.myappWidgetIds, remoteViews);
                        flag_data=false;
		}
		
		return jsonArray;
	}

	public void onDestroy() {
		// TODO Auto-generated method stub
	}

	/**
	 *get dataresulet
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
			String desc = jo.getString("desc");
			desc = URLDecoder.decode(desc, "UTF-8");
			String title = jo.getString("title");
			title = URLDecoder.decode(title, "UTF-8");
			String id = jo.getString("id");
			id = URLDecoder.decode(id, "UTF-8");
			String reqirement = jo.getString("reqirement");
			reqirement = URLDecoder.decode(reqirement, "UTF-8");
			String context = jo.getString("content");
			context = URLDecoder.decode(context, "UTF-8");
			String url = jo.getString("pcmb");
			Food food = new Food();
			food.setReqirment(reqirement);
			food.setId(id);
			food.setUrl(url);
			food.setThumb(imgPath);
			food.setTitle(title);
			food.setReqirment(reqirement);
			food.setContent(context);
			arrList.add(food);
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
		JSONArray jsonArr = null;
		URL url = new URL(httpUrl);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {

			conn.setConnectTimeout(6000);
			conn.setRequestMethod("GET");
			String json = "";
			if (200 == conn.getResponseCode()) {
				InputStream is = conn.getInputStream(); // get inputstream
				// create file
				String file_path = Environment.getExternalStorageDirectory()
						.toString() + "/widget";
				File f = new File(file_path);
				if (!f.exists()) {
					f.mkdirs();
				}
				// to save local
				String path = file_path + "/widgetfood.txt";
				File file = new File(path);
				FileOutputStream fos = new FileOutputStream(file, false);
				// to change JSONArray
				is=conn.getInputStream();
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;

				while ((len = is.read(buffer)) != -1) {
					bout.write(buffer, 0, len);
					fos.write(buffer, 0, len);
				}
				bout.close();
				fos.close();
				is.close();

				byte[] data = bout.toByteArray();

				json = new String(data); 
				
				
				jsonArr = new JSONArray(json);
			}
		} catch (Exception e) {
			// TODO: handle exception
			showException(e);
			return getLocalData();
		}

		return jsonArr;
	}

	/**
	 * to get byte[]
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
	 * to get inputstream
	 * 
	 * @param url
	 * @return InputStream
	 */
	private InputStream openConnection(String url, int position) {
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
			String url_local = "";
			if (position >= 0) {
				url_local = Environment.getExternalStorageDirectory()
						.toString() + "/widget/food" + "/" + position + ".jpg";
				;
			} else {
				url_local = Environment.getExternalStorageDirectory()
						.toString() + "/widget/widgetfood.txt";
			}
			FileInputStream is = null;
			try {
				is = new FileInputStream(new File(url_local));
			} catch (Exception e2) {
				WidgetHelper.toastException(mContext,"foodwidget");
			}
			return is;
		}
		return null;
	}

	/**
	 * show exception
	 */
	public void showException(Exception e) {
		Log.i("tag", e.toString());
		StackTraceElement[] ste = e.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			Log.i("tag", ste[i].toString());
		}

	}

	
}
