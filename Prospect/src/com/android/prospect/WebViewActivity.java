package com.android.prospect;

import com.android.prospect.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
        WebView  webView=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
	    webView=(WebView)findViewById(R.id.webview);
	    webView.getSettings().setJavaScriptEnabled(true);
	    Intent  intent=getIntent();
	    String url=(String) intent.getCharSequenceExtra("URL");
            webView.loadUrl(url);
            /*Intent intent_1= new Intent();        
            intent_1.setAction("android.intent.action.VIEW");    
            Uri uri= Uri.parse(url);    
            intent_1.setData(uri);           
            intent_1.setClassName("com.android.browser","com.android.browser.BrowserActivity");   
            startActivity(intent_1);*/
            webView.setWebViewClient(new WebViewClient(){
	    		
	    	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    		                webView.loadUrl(url);
					return true;
					}
	    			
	    		}
	    		);
	    
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
