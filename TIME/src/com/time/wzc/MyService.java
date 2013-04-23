package com.time.wzc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.R.string;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.MaskFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyService extends Service {
	Timer longtimer;
	SQLiteDatabase  db;
	MyDataHelper myHelper;
	String nowtime  ;
	int lastid, maxid;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onDestroy() {  
        // TODO Auto-generated method stub   
        super.onDestroy();  
    }  
	public int onStartCommand(Intent intent, int flags, int startId) {  
        // TODO Auto-generated method stub  
        return super.onStartCommand(intent, flags, startId);  
    }  
	public void onCreate() {  
		super.onCreate();  
		myHelper = new MyDataHelper(this, MyDataHelper.DB_NAME, null, 1);
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowtime = sDateFormat.format(System.currentTimeMillis());
		
		final Handler longHandler= new Handler(){
			public void handleMessage(Message msg) {
			// 如果发送的消息来自子线程
			if(msg.what == 1){
				try {
					
					longupdate();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
					}
				}
		    };
		TimerTask longTask = new TimerTask() {			
		@Override
		public void run() {
		// TODO Auto-generated method stub
			Message msg = longHandler.obtainMessage();
			msg.what = 1;
		 	longHandler.sendMessage(msg);
				}
			};
		longtimer = new Timer(true);
		longtimer.schedule(longTask,0,1000*60*60*24);
		
		
		final Handler tHandler= new Handler(){
			public void handleMessage(Message msg) {
			// 如果发送的消息来自子线程
			if(msg.what == 1){
				try {
				
					timeupdate();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
					}
				}
		    };
		
		TimerTask pushTask = new TimerTask() {			
			@Override
			public void run() {
				Message msg = tHandler.obtainMessage();
				msg.what = 1;
			 	tHandler.sendMessage(msg);
				
			}
		};
		Timer pushTimer  = new Timer(true);
		pushTimer.schedule(pushTask, 0,1000*60*60);
		    		
	}
	
	public void timeupdate() throws IOException
	{
	
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		nowtime = sDateFormat.format(System.currentTimeMillis());
		Log.d("nowtime",nowtime);
		db = myHelper.getWritableDatabase();
		Cursor cursor = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.TIME,
				MyDataHelper.ALERT,MyDataHelper.TOPIC,MyDataHelper.ID}
			, MyDataHelper.DATE+" like ?",new String[]{"%"+nowtime.substring(0, 10)+"%"}, null, null, null);
	
		if (cursor.moveToFirst())
		{
		do {
			if (cursor.getInt(1) == 1 )
			if (Integer.parseInt(cursor.getString(0).substring(0,2)) - Integer.parseInt(nowtime.substring(11, 13)) <=1  )
			{
				NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);        
				Notification nf = new Notification(R.drawable.ic_launcher,"讲座开始——"+cursor.getString(2), System.currentTimeMillis());
				nf.flags = Notification.FLAG_AUTO_CANCEL;  
				Context ctx = getApplicationContext();
				Intent i = new Intent(ctx, IndexActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);   
				PendingIntent contentIntent = PendingIntent.getActivity( ctx, R.string.app_name, i, 
				PendingIntent.FLAG_UPDATE_CURRENT);
				nf.setLatestEventInfo(ctx, "讲座即将开始", cursor.getString(2), contentIntent);
				notificationManager.notify(R.string.app_name, nf);	
				int id = cursor.getInt(3);
				ContentValues values = new ContentValues();
				values.put(MyDataHelper.ALERT, 2);
				db.update(MyDataHelper.TABLE_NAME, values, MyDataHelper.ID+"=?", new String[]{id+" "});
			}	
		}while (cursor.moveToNext());
		}	
		cursor.close();
		db.close();
	}
	public void longupdate() throws IOException
	{
		
		Context ctx = getApplicationContext();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("servicestatus", 1);
		editor.commit();
		db = myHelper.getWritableDatabase();
		Cursor cursor = db.query(MyDataHelper.TABLE_NAME, new String[]{ "_id"}
				, null,null, null, null, "_id desc");
		int ID;
		if (cursor.getCount() == 0)
		{
			ID = 0;
		} else {
			cursor.moveToFirst();
			ID = cursor.getInt(0);
		}
		maxid = ID;
		cursor = db.query(MyDataHelper.TABLE_NAME, new String[]{ "lastID"}
		, null,null, null, null, "lastID desc");
		if (cursor.getCount() == 0)
		{
			ID = 0;
		} else {
			cursor.moveToFirst();
			ID = cursor.getInt(0);
		}
		lastid =ID;
		Log.d("ID",String.valueOf(ID));
		cursor.close();
		db.close();
		List <NameValuePair> postparams = new ArrayList<NameValuePair>(); 
		postparams.add(new BasicNameValuePair("last",String.valueOf(ID)));
    	String url = "android/lecture";
    	Handler lhandler = new Handler(){
    		@Override
			public void handleMessage(Message msg) {
				// 如果发送的消息来自子线程
				if(msg.what == 1){
					// 将读取的内容追加显示在文本框中
					parsejson(msg.obj.toString());
					Context ctx = getApplicationContext();
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
					SharedPreferences.Editor editor = sp.edit();
					editor.putInt("servicestatus", 0);
					editor.commit();
				}
			}
        };
        ThreadPoolUtils.execute(new MyPost(url,lhandler,postparams));
        
        
        
        
	}
	
	public void parsejson(String jsonData)
	{
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			db = myHelper.getWritableDatabase();
			for (int i =0; i<jsonArray.length();i++)
			{
				JSONObject temp = (JSONObject) jsonArray.get(i);  
				ContentValues values = new ContentValues();
				values.put(MyDataHelper.ID, temp.getInt("pk"));
				values.put(MyDataHelper.DATE, temp.getString(MyDataHelper.DATE));
				values.put(MyDataHelper.TIME, temp.getString(MyDataHelper.TIME));
				values.put(MyDataHelper.TOPIC, temp.getString(MyDataHelper.TOPIC));
				values.put(MyDataHelper.NAME, temp.getString(MyDataHelper.NAME));
				values.put(MyDataHelper.STATUS, temp.getInt(MyDataHelper.STATUS));
				values.put(MyDataHelper.PLACE, temp.getString(MyDataHelper.PLACE));
				values.put(MyDataHelper.CATEGORY, temp.getInt(MyDataHelper.CATEGORY));
				values.put(MyDataHelper.TOPIC_TNTRO, temp.getString(MyDataHelper.TOPIC_TNTRO));
				values.put(MyDataHelper.LECTURER, temp.getString(MyDataHelper.LECTURER));
				
				String posterString = temp.getString(MyDataHelper.POSTER);
				if (! posterString.equals(""))
				{
				download(posterString,temp.getString(MyDataHelper.DATE));
				values.put(MyDataHelper.POSTER,
						Environment.getExternalStorageDirectory().toString()+"/Time/"+temp.getString(MyDataHelper.DATE)+".jpg");
				}    else
				{
					values.put(MyDataHelper.POSTER,"");
				}
				if (temp.getInt(MyDataHelper.STATUS) == 1)
				{
				String picString = temp.getString(MyDataHelper.PIC);
				int x= picString.indexOf("@");
				String pic1 = picString.substring(0, x);
				Log.d("pic1", pic1);
				picString = picString.substring(x+1);
				x = picString.indexOf("@");
				String pic2 = picString.substring(0,x);
				Log.d("pic2", pic2);
				String pic3 = picString.substring(x+1);
				Log.d("pic3", pic3);
				//values.put(MyDataHelper.PIC, );
				String ppic = "";
				if (! pic1.equals(""))
				{
					download(pic1, temp.getString(MyDataHelper.DATE)+"1");
					ppic+="1";
				} else {
					ppic +="0";
				}
				if (! pic2.equals(""))
				{
					download(pic2, temp.getString(MyDataHelper.DATE)+"2");
					ppic+="2";
				} else {
					ppic +="0";
				}
				if (! pic3.equals(""))
				{
					download(pic3, temp.getString(MyDataHelper.DATE)+"3");
					ppic+="3";
				} else {
					ppic +="0";
				}
				Log.d("pic", ppic);
				values.put(MyDataHelper.PIC, ppic);
				
				}
				values.put(MyDataHelper.ALERT,0);
				values.put(MyDataHelper.LIKE,0);
				values.put(MyDataHelper.DOWNLOAD, 0);
				values.put(MyDataHelper.TEXT_NAME, "");
				values.put(MyDataHelper.RECOMMEND, temp.getInt(MyDataHelper.RECOMMEND));
				values.put(MyDataHelper.LASTID, temp.getInt(MyDataHelper.LASTID));
				int id = temp.getInt("pk");
				if (id>maxid)
				{
					db.insert(MyDataHelper.TABLE_NAME, null, values);
				} else
				{
					db.update(MyDataHelper.TABLE_NAME, values, MyDataHelper.ID+"=?", new String[]{id+" "});
				}
				Log.d("db", String.valueOf(values));
			}
			db.close();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public void download(final String mediaurl, final String name) {
		
		Log.d("poster1",mediaurl);
		
		ThreadPoolUtils.execute(new Runnable(){
                @Override
           public void run() { 
                     //这里下载数据
                 try{
                	 	String value  = "http://113.11.199.135:8000";
                        URL  url = new URL(value+mediaurl);
                        HttpURLConnection conn  = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect(); 
                        InputStream inputStream=conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream); 
                        Log.d("poster","true");
                        saveMyBitmap(name, bitmap);
                     
                    } catch (MalformedURLException e1) { 
                        e1.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }  
                }
            });
		
		
	}
	public void saveMyBitmap(String bitName,Bitmap mBitmap){
		  String path = Environment.getExternalStorageDirectory().toString()+"/Time/" ;	
		  File file = new File(path);  
	        if(!file.exists()){  
	            file.mkdir();  
	        }  
	      File f = new File(path+bitName+".jpg");
		  try {
			  f.createNewFile();
			  FileOutputStream fos = new FileOutputStream(f);
			  mBitmap.compress(CompressFormat.JPEG, 100, fos);
			  fos.flush();
			  fos.close();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
		  
		  
		  
	}
	
}
