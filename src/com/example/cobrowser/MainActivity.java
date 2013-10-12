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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
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

public class MainActivity extends ActionBarActivity {

    WebView mWebView;

    private static final String TAG = "MyActivity";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
 	    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 	    final Context context = getApplicationContext();

        connectToWebsocketServer();
        configureActionBar();
        setSearchHooks();
        loadWebView();
    }

    private void configureActionBar(){
    	ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
    }
    
    protected void connectToWebsocketServer(){
        Log.d(TAG, "Initialising websockets...");

//    	List<BasicNameValuePair> extraHeaders = Arrays.asList(
//    	    new BasicNameValuePair("Cookie", "session=abcd")
//    	);
    	
        try{
 	    mConnection.connect("ws://10.0.1.11:7500/", new WebSocketHandler() {
    	    @Override
    	    public void onOpen() {
    	        Log.d(TAG, "Connected!");
    	    
    	        sendLogin("ben", "test");
    	    	sendPageload("http://www.google.com/");
    	    }

    	    public void sendLogin(String name, String password){
    	    	Map<String, String> message = new HashMap<String, String>();
    	    	message.put("action", "login");
    	    	message.put("name", name);
    	    	message.put("password", password);
    	    	
    	    	JSONObject json = null;
    	    	
    	    	try {
					json = (JSONObject) JsonHelper.toJSON(message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    	
    	    	mConnection.sendTextMessage(json.toString());
    	    }
    	    
    	    public void sendPageload(String url){
    	    	Map<String, String> message = new HashMap<String, String>();
    	    
    	    	message.put("action", "pageload");
    	    	message.put("url", url);
    	    	
    	    	JSONObject json = null;
    	    	
    	    	try {
					json = (JSONObject) JsonHelper.toJSON(message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    	
    	    	mConnection.sendTextMessage(json.toString());
    	    }
    	    
    	    @Override
    	    public void onTextMessage(String json) {
    	        Log.d(TAG, String.format("Got string message! %s", json));
    	    
             	try {
					processMessage(JsonHelper.toMap(new JSONObject(json)));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }

    	    @Override
    	    public void onClose(int code, String reason) {
    	        Log.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
    	    }
    	});
        } catch (WebSocketException e) {
        	Log.d(TAG, e.toString());
        }
    	
    }

    public void processMessage(Map<String, Object> message){
 	    final Context context = getApplicationContext();
 	    String action = (String) message.get("action");
    	
		Log.d(TAG, "message action: " + action);
		
		if(action == "message"){
	     	Toast toast = Toast.makeText(context, "[message] " + message.get("content"), Toast.LENGTH_SHORT);
	     	toast.show();   
		}
		
		if(action == "subscribers"){
			
		}
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
    
}
