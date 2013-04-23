package com.time.wzc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;

public class ShortService extends Service {
	private MyBinder myBinder = new MyBinder();
	Timer shorttimer;
	private int status;
	private String livetop,asktop,oldmsgtop,furmsgtop;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return myBinder;
	}
	public class MyBinder extends Binder{
		public ShortService getService(){
            return ShortService.this;
        }
    }
	public  void onCreate() {
		super.onCreate(); 
		
		final Handler shortHandler= new Handler(){
			public void handleMessage(Message msg) {
			// 如果发送的消息来自子线程
			if(msg.what == 1){
			// 将读取的内容追加显示在文本框中
				try{
					shortupdate();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
					}
				}
		    };
		TimerTask shortTask = new TimerTask() {			
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg = shortHandler.obtainMessage();
			msg.what = 1;
			shortHandler.sendMessage(msg);
				}
			};
		shorttimer = new Timer(true);
		shorttimer.schedule(shortTask, 0,1000*60*30);    
	}
	public void  shortupdate() throws IOException
	{
		List <NameValuePair> postparams = new ArrayList<NameValuePair>(); 
		postparams.add(new BasicNameValuePair("status","what"));
		String url = "android/status";
		Handler shandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// 如果发送的消息来自子线程
				if(msg.what == 1){
					String jsonData = msg.obj.toString();
					try {
						JSONObject jsonObj = new JSONObject(jsonData); 
						status = jsonObj.getInt("status");
						livetop = jsonObj.getString("livetop");
						asktop = jsonObj.getString("asktop");
						oldmsgtop = jsonObj.getString("oldmsgtop");
						furmsgtop = jsonObj.getString("furmsgtop");
						Context ctx = getApplicationContext();
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt("mainStatus", status);
						editor.putString("livetop", livetop);
						editor.putString("asktop", asktop);
						editor.putString("oldmsgtop", oldmsgtop);
						editor.putString("furmsgtop", furmsgtop);
						editor.commit();
	
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}
			}
        };
        ThreadPoolUtils.execute(new MyPost(url,shandler,postparams));   
	}
	
}
