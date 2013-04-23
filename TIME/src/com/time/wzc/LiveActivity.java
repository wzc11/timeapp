package com.time.wzc;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.time.wzc.MessageActivity.Myadapter;
import com.time.wzc.MessageActivity.ViewHolder;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class LiveActivity extends TabActivity implements OnCheckedChangeListener{
	private RadioGroup radioderGroup;
	private TabHost tabhost;
	private ListView chatlist,asklist ;
	private Chatadapter chatadapter;
	private Askadapter askadapter;
	private List<Map<String, Object>> chatdata,askdata,newchat,newask;
	private Timer qrytimer,updatetimer;
	private EditText chatmsgText,askmsgText;
	private String chatlasttime , asklasttime;
	private int asknum=0,chatnum=0;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.live);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        TextView topchat = (TextView)findViewById(R.id.chattop);
        TextView topask = (TextView)findViewById(R.id.asktop);
        
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        chatlasttime = sDateFormat.format(System.currentTimeMillis());
        asklasttime = "2000-11-11 11:11:11";
        Log.d("chattime",chatlasttime);
        List <NameValuePair> timepostparams = new ArrayList<NameValuePair>();
        String url = "android/";
        Handler timehandler= new Handler(){
			public void handleMessage(Message msg) {
			// 如果发送的消息来自子线程
			if(msg.what == 1){
				chatlasttime =msg.obj.toString();   
		        asklasttime = msg.obj.toString();				
					}
				}
		    };
		try {
			ThreadPoolUtils.execute(new MyPost(url,timehandler,timepostparams));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        chatdata =new ArrayList<Map<String, Object>>();
        askdata = new ArrayList<Map<String, Object>>();
        newchat = new ArrayList<Map<String, Object>>();
        newask = new ArrayList<Map<String, Object>>();
        
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        topchat.setText(sp.getString("livetop", null));
        topask.setText(sp.getString("asktop", null));
        
        
        chatmsgText = (EditText)findViewById(R.id.chatmessagetext);
        chatmsgText.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			chatmsgText.setCursorVisible(true);     		
    		}	
    		});
        Button chatbutton = (Button)findViewById(R.id.chatsubmit);
        chatbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (! chatmsgText.getText().equals(""))
				{
					sendmsg(chatmsgText.getText().toString(),0);
					chatmsgText.setText("");
				}
				
			}
        });
        askmsgText = (EditText )findViewById(R.id.askmessagetext);
        askmsgText.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			askmsgText.setCursorVisible(true);     		
    		}	
    		});
        Button askbutton = (Button)findViewById(R.id.asksubmit);
        askbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (! askmsgText.getText().equals(""))
				{
					Toast.makeText(getApplicationContext(), "提问已提交  正在审核",
						     Toast.LENGTH_SHORT).show();
					sendmsg(askmsgText.getText().toString(),1);
					askmsgText.setText("");
				}
				
			}
        });
        
        tabhost = getTabHost();
        tabhost.addTab(tabhost.newTabSpec("checkbox").setIndicator("说话").setContent(R.id.list1));
        tabhost.addTab(tabhost.newTabSpec("checkbox").setIndicator("提问").setContent(R.id.list2));
        
        radioderGroup = (RadioGroup) findViewById(R.id.main_radio);
	    radioderGroup.setOnCheckedChangeListener(this);
	    RadioButton radioButton =(RadioButton)findViewById(R.id.radio_button1);
	    radioButton.setChecked(true);
        
	    Button backbtn=(Button)findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    				LiveActivity.this.finish();
    			}
    		});
        
        
        chatlist = (ListView)findViewById(R.id.listview1);
        chatadapter = new Chatadapter(this, newchat);
        chatlist.setAdapter(chatadapter);
        
        asklist = (ListView)findViewById(R.id.listview2);
        askadapter = new Askadapter(this, newask);
        asklist.setAdapter(askadapter);
        
        final Handler qryhandler= new Handler(){
			public void handleMessage(Message msg) {
			// 如果发送的消息来自子线程
			if(msg.what == 1){
				msgqry();					
					}
				}
		    };
		 TimerTask qrytimeTask = new TimerTask() {			
	    		@Override
	    	public void run() {
	    	// TODO Auto-generated method stub
	    		Message msg = qryhandler.obtainMessage();
	    		msg.what = 1;
	    		qryhandler.sendMessage(msg);
	    		}
	    	};
	     qrytimer = new Timer(true);
	     qrytimer.schedule(qrytimeTask, 0,1000*15);
	     
	     final Handler updatehandler= new Handler(){
				public void handleMessage(Message msg) {
				// 如果发送的消息来自子线程
				if(msg.what == 1){
					if (chatnum <chatdata.size())
					{
						newchat.add(chatdata.get(chatnum));
						chatnum++;
						chatadapter.notifyDataSetChanged();
					}
					if (asknum <askdata.size())
					{
						newask.add(askdata.get(asknum));
						asknum++;
						askadapter.notifyDataSetChanged();
					}

						}
					}
			    };
		  TimerTask updatetimeTask = new TimerTask() {			
		    @Override
		  public void run() {
		    	// TODO Auto-generated method stub
		    	Message msg = updatehandler.obtainMessage();
		    	msg.what = 1;
		    	updatehandler.sendMessage(msg);
		    	}
		    };
		 updatetimer = new Timer(true);
		 updatetimer.schedule(updatetimeTask, 0,500);  
        
	}
	
	private void msgqry() {
		List <NameValuePair> chatpostparams = new ArrayList<NameValuePair>(); 
		chatpostparams.add(new BasicNameValuePair("last",chatlasttime));
    	String chaturl = "interact/talking/listen";
    	
    	List <NameValuePair> askpostparams = new ArrayList<NameValuePair>(); 
		askpostparams.add(new BasicNameValuePair("last",asklasttime));
    	String askurl ="interact/question/listen";
    	
    	Handler chathandler = new Handler(){
    		@Override
			public void handleMessage(Message msg) {
				// 如果发送的消息来自子线程
				if(msg.what == 1){
					String jsonData = msg.obj.toString();
					try {
						JSONArray jsonArray = new JSONArray(jsonData);
						for (int i =0; i<jsonArray.length();i++)
						{
							JSONObject temp1 = (JSONObject) jsonArray.get(i);
							JSONObject temp =temp1.getJSONObject("fields");
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("uid", temp.getString("uid"));
							String time = temp.getString("time");
							String time1= time.replace('T', ' ');
							map.put("time",time1.substring(11,16));
							map.put("what", temp.getString("what"));
							map.put("flag", temp.getString("flag"));
							chatdata.add(map);
							chatlasttime = time1;
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
    		}
    	};
		Handler askhandler = new Handler(){
			  @Override
			public void handleMessage(Message msg) {
			// 如果发送的消息来自子线程
			if(msg.what == 1){
				String jsonData = msg.obj.toString();
				try {
					JSONArray jsonArray = new JSONArray(jsonData);
					for (int i =0; i<jsonArray.length();i++)
					{
						JSONObject temp1 = (JSONObject) jsonArray.get(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", temp1.getInt("pk"));
						JSONObject temp =temp1.getJSONObject("fields");
						String time = temp.getString("time");
						String time1= time.replace('T', ' ');
						map.put("uid", temp.getString("uid"));
						map.put("time", time1.substring(11,16));
						map.put("what", temp.getString("what"));
						map.put("flag", temp.getString("flag"));
						askdata.add(map);
						asklasttime = time1;
					}
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}			
				}
			}
        };
        try{
			ThreadPoolUtils.execute(new MyPost(chaturl,chathandler,chatpostparams));
			ThreadPoolUtils.execute(new MyPost(askurl,askhandler,askpostparams));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
	
	
	private void sendmsg(String content ,final int flag)
	{
		List <NameValuePair> postparams = new ArrayList<NameValuePair>(); 
		
		String url ="";
		postparams.add(new BasicNameValuePair("what", content));
		SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
		String uid = sp.getString("uid", null);
		
		postparams.add(new BasicNameValuePair("uid", uid));
		if (flag == 0 )
		{
			url = "interact/talking/say";  
			postparams.add(new BasicNameValuePair("last",chatlasttime));
			
		}	else 
		{		
			url = "interact/question/say";
			postparams.add(new BasicNameValuePair("last",asklasttime));
		}
		Handler handler = new Handler(){
    		@Override
			public void handleMessage(Message msg) {
				// 如果发送的消息来自子线程
			/*	if(msg.what == 1){
					String jsonData = msg.obj.toString();
					try {
						JSONArray jsonArray = new JSONArray(jsonData);
						for (int i =0; i<jsonArray.length();i++)
						{
							JSONObject temp1 = (JSONObject) jsonArray.get(i);  
							JSONObject temp =temp1.getJSONObject("fields");
							Map<String, Object> map = new HashMap<String, Object>();
							String time = temp.getString("time");
							String time1= time.replace('T', ' ');
							map.put("uid", temp.getString("uid"));
							map.put("time", time1.substring(11,16));
							map.put("what", temp.getString("what"));
							map.put("flag", temp.getString("flag"));
							if (flag == 0)
							{
								chatdata.add(map);
								chatlasttime = time1;
							} else {
								askdata.add(map);
								asklasttime = time1;
							}
						}   
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	*/				
				}   
		};
		try {
			ThreadPoolUtils.execute(new MyPost(url,handler,postparams));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public final class ViewHolder{
		public TextView  uid;
		public TextView what;
		public Button love;
	}
	public class Askadapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private List<Map<String, Object>> data;
		
		public Askadapter(Context context,List<Map<String, Object>> data){
			this.mInflater = LayoutInflater.from(context);
			this.data = data;
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			notifyDataSetChanged();
			ViewHolder holder = null;
			final int pos = position;
			if (convertView == null) {				
				holder=new ViewHolder();  
				convertView = mInflater.inflate(R.layout.asklist, null);
				holder.uid = (TextView)convertView.findViewById(R.id.uid);
				holder.what = (TextView)convertView.findViewById(R.id.what);
				holder.love = (Button)convertView.findViewById(R.id.love);
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
			holder.uid.setText((String)data.get(position).get("uid")+"提问： ");
			holder.what.setText((String)data.get(position).get("what"));
			holder.love.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					String url ="interact/question/love";
					List <NameValuePair> postparams = new ArrayList<NameValuePair>();
					postparams.add(new BasicNameValuePair("love",String.valueOf((Integer)data.get(pos).get("id"))));
					Log.d("love", String.valueOf((Integer)data.get(pos).get("id")));
					Handler handler= new Handler(){
						public void handleMessage(Message msg) {
						// 如果发送的消息来自子线程
						if(msg.what == 1){
							Toast.makeText(getApplicationContext(),"有"+msg.obj.toString()+"个人喜欢这个提问",
								     Toast.LENGTH_SHORT).show();			
								}
							}
					    };
					try {
						ThreadPoolUtils.execute(new MyPost(url,handler,postparams));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					v.setBackgroundResource(R.drawable.listlove1);
					v.setClickable(false);				}
			});

			return convertView;
		}
	}	
	
	
	
	public final class ChatViewHolder{
		public TextView  uid;
		public TextView time;
		public TextView what;
	}
	
	public class Chatadapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private List<Map<String, Object>> data;
		
		public Chatadapter(Context context,List<Map<String, Object>> data){
			this.mInflater = LayoutInflater.from(context);
			this.data = data;
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return data.get(arg0);
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			notifyDataSetChanged();
			ChatViewHolder holder = null;
			final int pos = position;
			if (convertView == null) {				
				holder=new ChatViewHolder();  
				convertView = mInflater.inflate(R.layout.msglist, null);
				holder.uid = (TextView)convertView.findViewById(R.id.uid);
				holder.time = (TextView)convertView.findViewById(R.id.time);
				holder.what = (TextView)convertView.findViewById(R.id.what);
				convertView.setTag(holder);
				
			}else {
				
				holder = (ChatViewHolder)convertView.getTag();
			}
			holder.uid.setText((String)data.get(position).get("uid"));
			holder.time.setText("  "+(String)data.get(position).get("time")+" :");
			holder.what.setText((String)data.get(position).get("what"));
			
			if (((String)data.get(position).get("flag")).equals("1"))
			{
				holder.uid.setTextColor(Color.RED);
				holder.what.setTextColor(Color.RED);
			}  
			
			return convertView;
		}
	}
	
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.radio_button1:
				tabhost.setCurrentTab(0);
			break;
		case R.id.radio_button2:
			tabhost.setCurrentTab(1);
			break;
		default:
			break;
		}
	}
	protected void onDestroy() {
		qrytimer.cancel();
		updatetimer.cancel();
		super.onDestroy();
	}

}
