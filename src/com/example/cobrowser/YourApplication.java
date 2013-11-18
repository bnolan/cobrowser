package com.example.cobrowser;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class YourApplication extends Application {
	
	private static final String TAG = "YourApplication";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    
    public MessageActivity currentActivity;
    
    public void onCreate(){
        Log.d(TAG, "Application created.");

        super.onCreate();
        connectToWebsocketServer();
    }
    
    protected void connectToWebsocketServer(){
        Log.d(TAG, "Initialising websockets...");

        try{
 	    mConnection.connect("ws://beta.bennolan.com:7500/", new WebSocketHandler() {
    	    @Override
    	    public void onOpen() {
    	        Log.d(TAG, "Connected!");
    	        // sendLogin("ben", "test");
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

    public void sendMessageToCurrentChannel(String content){
	    	Map<String, String> message = new HashMap<String, String>();
    	    
	    	message.put("action", "message");
	    	message.put("content", content);
	    	
	    	JSONObject json = null;
	    	
	    	try {
				json = (JSONObject) JsonHelper.toJSON(message);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	mConnection.sendTextMessage(json.toString());
    }
    
    public void processMessage(Map<String, Object> message){
 	    final Context context = getApplicationContext();
 	    String action = (String) message.get("action");
    	
		Log.d(TAG, "message action: " + action);
		
		if(currentActivity != null){
			currentActivity.processMessage(message);
		}
		
		if(action == "message"){
	     	Toast toast = Toast.makeText(context, "[message] " + message.get("content"), Toast.LENGTH_SHORT);
	     	toast.show();   
		}
		
		if(action == "subscribers"){
			
		}
    }
    
    
}
