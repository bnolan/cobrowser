package com.example.cobrowser;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import android.os.Bundle;
// import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends Activity implements MessageActivity {

    WebView mWebView;

    private static final String TAG = "MyActivity";

    private YourApplication app;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 	    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
 	    final Context context = getApplicationContext();

 	    app = (YourApplication) getApplication();
 	    app.currentActivity = this;

 	    setSearchHooks();
        loadWebView();
    }

    public void switchToChat(View view){
	    Intent intent = new Intent(this, ChatActivity.class);
	    startActivity(intent);
    }
    
    private void setSearchHooks() {
        TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener(){
        	public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
        		mWebView.loadUrl("http://www.google.com/search?q=" + exampleView.getText().toString());
             	
             	//fixme hide keyboard
             	//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
             	
            	if ((actionId == EditorInfo.IME_NULL) && (event.getAction() == KeyEvent.ACTION_DOWN)){
        		}


        		return true;
        	}
        };

        EditText mEditText = (EditText) findViewById(R.id.editText1);
        mEditText.setImeActionLabel("Go", KeyEvent.KEYCODE_ENTER);
        mEditText.setOnEditorActionListener(exampleListener);
	}

	private void loadWebView(){
		mWebView = (WebView) findViewById(R.id.webview);

        mWebView.setInitialScale(1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setUseWideViewPort(true);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl("http://www.google.com");
        
        mWebView.setWebViewClient(new WebViewClient() {
       	   public void onPageFinished(WebView view, String url) {
       		   app.sendPageload(url);
           }
        });
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
            case KeyEvent.KEYCODE_BACK:
                if(mWebView.canGoBack() == true){
                    mWebView.goBack();
                }else{
                    finish();
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void processMessage(Map<String, Object> message) {
		// TODO Auto-generated method stub
		
	}
    
}
