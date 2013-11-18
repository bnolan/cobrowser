package com.example.cobrowser;

import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;
import de.svenjacobs.loremipsum.LoremIpsum;

public class ChatActivity extends Activity implements MessageActivity {
	private com.example.cobrowser.DiscussArrayAdapter adapter;
	private ListView lv;
	private LoremIpsum ipsum;
	private EditText editText1;
	private static Random random;
    private YourApplication app;

	private static final String TAG = "ChatActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discuss);

 	    app = (YourApplication) getApplication();
 	    app.currentActivity = this;

 	    Log.d("Chat", "ChatActivity created.");

		random = new Random();
		ipsum = new LoremIpsum();

		lv = (ListView) findViewById(R.id.listView1);
		lv.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);

		lv.setAdapter(adapter);

		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String text = editText1.getText().toString(); 

	    	    	app.sendPageload("http://www.google.com/");
			 	    app.sendMessageToCurrentChannel(text);
					scrollToBottom();

					// Perform action on key press
					adapter.add(new OneComment(false, text));
					editText1.setText("");
					return true;
				}
				return false;
			}
		});

		addItems();
	}

	public void switchToBrowsing(View view){
	    Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	}
	
	private void addItems() {
		adapter.add(new OneComment(true, "Hello bubbles!"));

		for (int i = 0; i < 4; i++) {
			boolean left = getRandomInteger(0, 1) == 0 ? true : false;
			int word = getRandomInteger(1, 10);
			int start = getRandomInteger(1, 40);
			String words = ipsum.getWords(word, start);

			adapter.add(new OneComment(left, words));
		}
	}

	private static int getRandomInteger(int aStart, int aEnd) {
		if (aStart > aEnd) {
			throw new IllegalArgumentException("Start cannot exceed End.");
		}
		long range = (long) aEnd - (long) aStart + 1;
		long fraction = (long) (range * random.nextDouble());
		int randomNumber = (int) (fraction + aStart);
		return randomNumber;
	}

	@Override
	public void processMessage(Map<String, Object> message) {
		// TODO Auto-generated method stub

 	    String action = (String) message.get("action");
    	
		// Log.d(TAG, "message action: " + action);
		
		if(action.equals("message")){
			adapter.add(new OneComment(true, (String) message.get("content")));
			scrollToBottom();
//			Toast toast = Toast.makeText(context, "[message] " + message.get("content"), Toast.LENGTH_SHORT);
//	     	toast.show();   
		}else{
			Log.d(TAG, "unknown action: " + action);
		}
	}
		
	public void scrollToBottom(){
		lv.post(new Runnable() {

	        @Override
	        public void run() {
	        	lv.setSelection(adapter.getCount() - 1);    
	        }
	    });
	}
}