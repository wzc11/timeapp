package com.time.wzc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class ReadingActivity extends TabActivity implements OnCheckedChangeListener{
	public ListView listview1,listview2,listview3,listview4,listview5,searchlist;
	MyDataHelper myHelper;
	private RadioGroup radioderGroup;
	private List<Map<String, Object>> data1,data2,data3,data4,data5 ;
    public TabHost tabhost;
    private int  flag ;
    public Myadapter[] adapter = new Myadapter[5];
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.read);
	        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	        myHelper = new MyDataHelper(this, MyDataHelper.DB_NAME, null, 1);
	        bottombtn();
	        
	        flag = 0;
	        tabhost= getTabHost();
	        tabhost.addTab(tabhost.newTabSpec("checkbox").setIndicator("",getResources().getDrawable(R.drawable.tab11)).setContent(R.id.list1));
	        tabhost.addTab(tabhost.newTabSpec("checkbox").setIndicator("",getResources().getDrawable(R.drawable.tab22)).setContent(R.id.list2));
	        tabhost.addTab(tabhost.newTabSpec("checkbox").setIndicator("",getResources().getDrawable(R.drawable.tab33)).setContent(R.id.list3));
	        tabhost.addTab(tabhost.newTabSpec("checkbox").setIndicator("",getResources().getDrawable(R.drawable.tab44)).setContent(R.id.list4));
	        tabhost.addTab(tabhost.newTabSpec("checkbox").setIndicator("",getResources().getDrawable(R.drawable.tab55)).setContent(R.id.list5));
	        tabhost.setCurrentTab(0);
	        
	        listview1 = (ListView)findViewById(R.id.listview1);
	        data1 = getData(1);
	        adapter[0] = new Myadapter(this, data1);
	        listview1.setAdapter(adapter[0]);
	        
	        listview2 = (ListView)findViewById(R.id.listview2);
	        data2 = getData(2);
	        adapter[1] = new Myadapter(this, data2);
	        listview2.setAdapter(adapter[1]);
	        
	        listview3 = (ListView)findViewById(R.id.listview3);
	        data3 = getData(3);
	        adapter[2] = new Myadapter(this, data3);
	        listview3.setAdapter(adapter[2]);
	        
	        listview4 = (ListView)findViewById(R.id.listview4);
	        data4 = getData(4);
	        adapter[3] = new Myadapter(this, data4);
	        listview4.setAdapter(adapter[3]);
	        
	        listview5 = (ListView)findViewById(R.id.listview5);
	        data5 = getData(5);
	        adapter[4] = new Myadapter(this, data5);
	        listview5.setAdapter(adapter[4]);
	       
	        Button backbtn=(Button)findViewById(R.id.backbtn);
	        backbtn.setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View v) {
	    				ReadingActivity.this.finish();
	    			}
	    		});
	        
	        searchlist = (ListView)findViewById(R.id.searchlist);
	        final TextView searchTextView = (TextView)findViewById(R.id.searchtext);
	        searchTextView.setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View v) {
	    			searchTextView.setCursorVisible(true);     		
	    		}	
	    		});
	        
	        Button search = (Button)findViewById(R.id.search);
	        search.setOnClickListener(new View.OnClickListener() {
	    		public void onClick(View v) {
					if (! searchTextView.getText().equals(""))
					{
						flag  = 1;
						search(searchTextView.getText().toString());
						searchTextView.setText("");
						
					}
				}
			});   
	       
	      radioderGroup = (RadioGroup) findViewById(R.id.main_radio);
	      radioderGroup.setOnCheckedChangeListener(this);
	      RadioButton radioButton =(RadioButton)findViewById(R.id.radio_button1);
	      radioButton.setChecked(true);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && flag == 1){
			tabhost.setVisibility(View.VISIBLE);
			searchlist.setVisibility(View.GONE);
			flag = 0;
		} else {
			super.onKeyDown(keyCode, event);
		}
		return true;
	}
	private void search(String text)
	{
		tabhost.setVisibility(View.GONE);
		searchlist.setVisibility(View.VISIBLE);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SQLiteDatabase db = myHelper.getReadableDatabase();
		Cursor c = null;
		c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID,
        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.STATUS }, MyDataHelper.TOPIC+" like ?",new String[]{"%"+text+"%"}, null, null,null);
		if (c.moveToFirst())
    	{
        	do {
        		if (c.getInt(3)== 1)
        		{
        			String s;
        			s= c.getString(1)+" "+c.getString(2);
        			Map<String, Object> map = new HashMap<String, Object>();
        			map.put("content",s);
        			map.put("id", c.getInt(0));
        			list.add(map);      
        		}
        	}while (c.moveToNext());
    	}
		c.close();
		db.close();
		searchlist.setAdapter(new Myadapter(this, list));			
	}
	
	
	private void bottombtn()
	{
		Button discussButton = (Button)findViewById(R.id.discuss);
		discussButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ReadingActivity.this, LiveActivity.class );
				startActivity(intent);
			}
		});
		Button readButton = (Button)findViewById(R.id.notice);
		readButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				/*Intent intent = new Intent();
				intent.setClass(ReadingActivity.this, NoticeActivity.class );
				ReadingActivity.this.finish();
				startActivity(intent);*/
				ReadingActivity.this.finish();
			}
		});
		Button MessageButton = (Button)findViewById(R.id.comment);
		MessageButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ReadingActivity.this, MessageActivity.class );
				startActivity(intent);
			}
		});
		Button SettingButton = (Button)findViewById(R.id.setting);
		SettingButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ReadingActivity.this, Setting.class );
				startActivity(intent);
			}
		});
		
		
	}
	
	private List<Map<String, Object>> getData(int k) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		SQLiteDatabase db = myHelper.getReadableDatabase();
		Cursor c = null;
		switch (k) {
		case 1:
			 c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID,
	        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.STATUS }, MyDataHelper.RECOMMEND+"=?",new String[]{1+" "}, null, null,null );
			
			 break;
		case 2:
			c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID,
	        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.STATUS }, MyDataHelper.CATEGORY+"=?",new String[]{1+" "}, null, null,null );
			break;
		case 3:
			c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID,
	        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.STATUS }, MyDataHelper.CATEGORY+"=?",new String[]{2+" "}, null, null,null );
			break;
		case 4:
			c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID,
	        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.STATUS }, MyDataHelper.CATEGORY+"=?",new String[]{3+" "}, null, null,null );
			break;
		case 5:
			c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID,
	        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.STATUS }, MyDataHelper.LIKE+"=?",new String[]{1+" "}, null, null,null );
			break;
		default:
			break;
		}
		
		if (c.moveToFirst())
	    	{
	        	do { 
	        		if (c.getInt(3) == 1)
	        		{
	        		String s;
	        		s= c.getString(1)+" "+c.getString(2);
	        		Map<String, Object> map = new HashMap<String, Object>();
	        		map.put("content",s);
	        		map.put("id", c.getInt(0));
	        		list.add(map);
	        		}
	        	}while (c.moveToNext());
	        
	        }
		c.close();
        db.close();
		return list;
	}
	
	public final class ViewHolder{
		
		public Button  like;
		public TextView textView;
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
				convertView = mInflater.inflate(R.layout.readlist, null);
				holder.like = (Button)convertView.findViewById(R.id.listlike);
				holder.textView = (TextView)convertView.findViewById(R.id.listtext);
				convertView.setTag(holder);
				
			}else {
				
				holder = (ViewHolder)convertView.getTag();
			}
			holder.textView.setText((String)data.get(position).get("content"));
			final int listid =(Integer) data.get(position).get("id");
			
			SQLiteDatabase db = myHelper.getWritableDatabase();
			Cursor cursor = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.LIKE}
			, MyDataHelper.ID+"=?",new String[]{listid+" "}, null, null,null);
			cursor.moveToFirst();
			int islike = cursor.getInt(0);
			if (islike == 0)
				holder.like.setBackgroundResource(R.drawable.listlike);
			else 
				holder.like.setBackgroundResource(R.drawable.listlove1);	
			db.close();
			holder.like.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					SQLiteDatabase db = myHelper.getWritableDatabase();
					Cursor cursor = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.LIKE}
					, MyDataHelper.ID+"=?",new String[]{listid+" "}, null, null,null);
					cursor.moveToFirst();
					int islike = cursor.getInt(0);
					if (islike == 0)
					{
						ContentValues values = new ContentValues();
						values.put(MyDataHelper.LIKE, 1);
						db.update(MyDataHelper.TABLE_NAME, values, MyDataHelper.ID+"=?", new String[]{listid+" "});
						v.setBackgroundResource(R.drawable.listlike);
					}else
					{
						ContentValues values = new ContentValues();
						values.put(MyDataHelper.LIKE, 0);
						db.update(MyDataHelper.TABLE_NAME, values, MyDataHelper.ID+"=?", new String[]{listid+" "});
						v.setBackgroundResource(R.drawable.listlove1);							
					}	
					db.close();	
					for (int i =0; i<5; i++)
					{
						adapter[i].data = getData(i+1);
						adapter[i].notifyDataSetChanged();
					}
				}
			});
			
			holder.textView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.putExtra("_id", listid);
					intent.setClass(ReadingActivity.this,ReadingDetailActivity.class);
					startActivity(intent);
				}
			});
			return convertView;
		}
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.radio_button1:
				tabhost.setCurrentTab(0);
			break;
		case R.id.radio_button2:
			tabhost.setCurrentTab(1);
			break;
		case R.id.radio_button3:
			tabhost.setCurrentTab(2);
		break;
		case R.id.radio_button4:
			tabhost.setCurrentTab(3);
		break;
		case R.id.radio_button5:
			tabhost.setCurrentTab(4);
		break;
		default:
			break;
		}

	}
	
	

}
