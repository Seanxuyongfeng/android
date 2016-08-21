package sean.com.sean.com.server;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.sean.Log.DebugUtils;

/**
 * Created by Sean on 2016/7/20.
 */
public class HttpOperation {

    private static final String TAG = "HttpOperation";

    public static String postRequest(String url, Map<String, String> rawParams){
        try{
            HttpPost post = new HttpPost(url);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            for(String key : rawParams.keySet()){
                params.add(new BasicNameValuePair(key, rawParams.get(key)));
            }

            UrlEncodedFormEntity urlEncodedFormEntity  = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            post.setEntity(urlEncodedFormEntity);
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10 * 1000);
            HttpResponse httpResponse = httpClient.execute(post);
            if(DebugUtils.DEBUG){
                DebugUtils.i(TAG, "request resultCode : " + httpResponse.getStatusLine().getStatusCode());
            }
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                String result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
                return result;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{

        }
        return null;
    }
}
