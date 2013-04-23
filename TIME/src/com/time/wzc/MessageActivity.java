package com.time.wzc;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.time.wzc.ReadingActivity.Myadapter;
import com.time.wzc.ReadingActivity.ViewHolder;

import android.app.Activity;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MessageActivity extends Activity{
	private ListView exlist,forelist ;
	private Myadapter exadapter, foreadapter;
	private List<Map<String, Object>> exdata,foredata;
	private Timer timer;
	private EditText exmsgText,foremsgText;
	private String exlasttime, forelasttime;
	LinearLayout layout1,layout2;
	private PopupWindow popupWindow;
	private ListView listView;
	private String title[] = { "对上期讲座", "对未来讲座" };
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        layout1 =(LinearLayout)findViewById(R.id.list1);
        layout2 =(LinearLayout)findViewById(R.id.list2);
        layout2.setVisibility(View.INVISIBLE);
        exdata = new ArrayList<Map<String, Object>>();
        foredata = new ArrayList<Map<String, Object>>();
        TextView extop = (TextView)findViewById(R.id.oldtop);
        TextView foretop = (TextView)findViewById(R.id.furtop);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        extop.setText(sp.getString("oldmsgtop", null));
        foretop.setText(sp.getString("furmsgtop", null));          
        
        exlasttime ="2000-11-11 11:11:11";
        forelasttime ="2000-11-11 11:11:11";
        final Button liuButton=(Button)findViewById(R.id.liubtn);
        liuButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				liuButton.getTop();
				int y = liuButton.getBottom() *2;
				int x = liuButton.getLeft();
				showPopupWindow(x, y);
			}
		});
        
        Button backbtn=(Button)findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    				MessageActivity.this.finish();
    			}
    		});
        
        exmsgText = (EditText)findViewById(R.id.exmessagetext);
        Button exsend = (Button)findViewById(R.id.exsubmit);
        exsend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (! exmsgText.getText().equals(""))
				{
					Toast.makeText(getApplicationContext(), "留言已提交",
						     Toast.LENGTH_SHORT).show();
					sendmsg(exmsgText.getText().toString(),0);
					exmsgText.setText("");
				}
				
			}
        });
        foremsgText = (EditText)findViewById(R.id.foremessagetext);
        Button foresend = (Button)findViewById(R.id.foresubmit);
        foresend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (! foremsgText.getText().equals(""))
				{
					Toast.makeText(getApplicationContext(), "留言已提交",
						     Toast.LENGTH_SHORT).show();
					sendmsg(foremsgText.getText().toString(),1);
					foremsgText.setText("");
				}
				
			}
        });
        
        
        
        
        
        exlist =(ListView)findViewById(R.id.listview1);
        exadapter = new Myadapter(this, exdata);
        exlist.setAdapter(exadapter);
        forelist = (ListView)findViewById(R.id.listview2);
        foreadapter = new Myadapter(this, foredata);
        forelist.setAdapter(foreadapter);        
       
        final Handler Handler= new Handler(){
			public void handleMessage(Message msg) {
			// 如果发送的消息来自子线程
			if(msg.what == 1){
				msgqry();					
					}
				}
		    };
        TimerTask timeTask = new TimerTask() {			
    		@Override
    		public void run() {
    		// TODO Auto-generated method stub
    			Message msg = Handler.obtainMessage();
    			msg.what = 1;
    		 	Handler.sendMessage(msg);
    			}
    		};
        timer = new Timer(true);
        timer.schedule(timeTask, 0,1000*60*5);
    }
	
	public void showPopupWindow(int x, int y) {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(MessageActivity.this).inflate(
				R.layout.dialog, null);
		listView = (ListView) layout.findViewById(R.id.lv_dialog);
		listView.setAdapter(new ArrayAdapter<String>(MessageActivity.this,
				R.layout.text, R.id.tv_text, title));

		popupWindow = new PopupWindow(MessageActivity.this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow
				.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		// popupWindow.showAsDropDown(findViewById(R.id.tv_title), x, 10);
		popupWindow.showAtLocation(findViewById(R.id.main), Gravity.LEFT
				| Gravity.TOP, x, y);//需要指定Gravity，默认情况是center.

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0)
				{
					layout1.setVisibility(View.VISIBLE);
					layout2.setVisibility(View.INVISIBLE);
				} else {
					layout2.setVisibility(View.VISIBLE);
					layout1.setVisibility(View.INVISIBLE);
				}
				popupWindow.dismiss();
				popupWindow = null;
			}
		});
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
			url = "interact/comment/say";  
			postparams.add(new BasicNameValuePair("last",exlasttime));
			
		}	else 
		{		
			url = "interact/future/say";
			postparams.add(new BasicNameValuePair("last",forelasttime));
		}
		Handler handler = new Handler(){
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
							map.put("time", time1.substring(5,16));
							map.put("what", temp.getString("what"));
							if (flag == 0)
							{
								exdata.add(map);
								exlasttime = time1;
							} else {
								foredata.add(map);
								forelasttime = time1;
							}
						}
						if (flag == 0)
							exadapter.notifyDataSetChanged();
						else 
							foreadapter.notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
    		}
		};
		try {
			ThreadPoolUtils.execute(new MyPost(url,handler,postparams));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void msgqry() {
		List <NameValuePair> expostparams = new ArrayList<NameValuePair>(); 
		expostparams.add(new BasicNameValuePair("last",exlasttime));
    	String exurl = "interact/comment/listen";
    	
    	List <NameValuePair> forepostparams = new ArrayList<NameValuePair>(); 
		forepostparams.add(new BasicNameValuePair("last",forelasttime));
    	String foreurl ="interact/future/listen";
    	
    	Handler exhandler = new Handler(){
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
							map.put("time", time1.substring(5,16));
							map.put("what", temp.getString("what"));
							exdata.add(map);
							exlasttime = time1;
						}
						exadapter.notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
    		}
    	};
		Handler forehandler = new Handler(){
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
						map.put("time", time1.substring(5,16));
						map.put("what", temp.getString("what"));
						foredata.add(map);
						forelasttime = time1;
					}
					foreadapter.notifyDataSetChanged();
				} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}			
				}
			}
        };
        try{
			ThreadPoolUtils.execute(new MyPost(exurl,exhandler,expostparams));
			ThreadPoolUtils.execute(new MyPost(foreurl,forehandler,forepostparams));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public final class ViewHolder{
		public TextView  uid;
		public TextView time;
		public TextView what;
	}
	
	public class Myadapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private List<Map<String, Object>> data;
		
		public Myadapter(Context context,List<Map<String, Object>> data){
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
				convertView = mInflater.inflate(R.layout.msglist, null);
				holder.uid = (TextView)convertView.findViewById(R.id.uid);
				holder.time = (TextView)convertView.findViewById(R.id.time);
				holder.what = (TextView)convertView.findViewById(R.id.what);
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
			holder.uid.setText((String)data.get(position).get("uid"));
			holder.time.setText("  "+(String)data.get(position).get("time")+" :");
			holder.what.setText((String)data.get(position).get("what"));	
			return convertView;
		}
	}
	
	protected void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
	
	
}
