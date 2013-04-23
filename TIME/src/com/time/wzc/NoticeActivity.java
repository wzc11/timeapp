package com.time.wzc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class NoticeActivity extends Activity{
	private ListView lv;
	private int num;
	private int lv_id[];
	MyDataHelper myHelper;
	public ImageButton statusimage;
    private ArrayList<String> list = new ArrayList<String>();
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notice);
        myHelper = new MyDataHelper(this, MyDataHelper.DB_NAME, null, 1);
        statusimage = (ImageButton)findViewById(R.id.imagstatus);
        imagestatus();
        lv_id = new int[15];
        ListView();
       
        bottombtn();
        
        
        
        
	}
	public void bottombtn()
	{
		Button discussButton = (Button)findViewById(R.id.discuss);
		discussButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(NoticeActivity.this, LiveActivity.class );
				startActivity(intent);
			}
		});
		Button readButton = (Button)findViewById(R.id.read);
		readButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(NoticeActivity.this, ReadingActivity.class );
				startActivity(intent);
			}
		});
		Button MessageButton = (Button)findViewById(R.id.comment);
		MessageButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(NoticeActivity.this, MessageActivity.class );
				startActivity(intent);
			}
		});
		Button SettingButton = (Button)findViewById(R.id.setting);
		SettingButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(NoticeActivity.this, Setting.class );
				startActivity(intent);
			}
		});
		
		
	}
	
	private void ListView() {
		// TODO Auto-generated method stub
		lv = (ListView)findViewById(R.id.list);
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	    		R.layout.listtext,R.id.tv,getData());
	   lv.setAdapter(adapter);
	   lv.setOnItemClickListener(new OnItemClickListener() {
		   public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
			 
				Intent intent = new Intent();
				intent.putExtra("_id", lv_id[position]);
				intent.putExtra("index", 0);
				intent.setClass(NoticeActivity.this,NoticeDetailActivity.class);
				startActivity(intent);
			}
	   });
	}
	private ArrayList<String> getData()
    {
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID, MyDataHelper.DATE,
        		MyDataHelper.TOPIC,MyDataHelper.NAME }, MyDataHelper.STATUS+"=?",new String[]{0+" "}, null, null,null );
        if (c.moveToFirst())
        {
        	num = 0;
        	do {
        		String dateString =c.getString(1);
        		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        		String nowtime = sDateFormat.format(System.currentTimeMillis());
        		if (dateString.compareTo(nowtime) >0)
        		{
        			String s= c.getString(1)+" "+c.getString(2)+" "+c.getString(3);
        			list.add(s);
        			lv_id[num] = c.getInt(0);
        			num ++;
        		}
        	}while (c.moveToNext());
        
        }
        c.close();
        db.close();
        return list;
    }
	public  void  imagestatus() {
		
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final int mainStatus = sp.getInt("mainStatus", 1);
        switch (mainStatus) {
		case 1:		
			statusimage.setBackgroundResource(R.drawable.nolec);
			break;
		case 2:
			statusimage.setBackgroundResource(R.drawable.finish);
			break;
		case 3:
			statusimage.setBackgroundResource(R.drawable.starting);
			break;
		case 4:
			statusimage.setBackgroundResource(R.drawable.start);
			break;
		default:
			break;
		}          
      
        statusimage.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		Intent intent = new Intent();
        		switch (mainStatus) {
        		case 1:	
        			
        			SQLiteDatabase db = myHelper.getReadableDatabase();
        			Cursor c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.ID}, MyDataHelper.STATUS+"=?",new String[]{0+" "}, null, null,"_id desc" );
        			int newid =0;
        			if (c.moveToFirst())
        				newid = c.getInt(0);
        			
        			intent.putExtra("_id", newid);
        			intent.putExtra("index", 0);
        			intent.setClass(NoticeActivity.this, NoticeDetailActivity.class );
        		
        			c.close();
        			db.close(); 
				break;
        		case 2:
        	        intent.setClass(NoticeActivity.this, MessageActivity.class );
        	      	
        	    break;
        		case 3:
 	        		intent.setClass(NoticeActivity.this,LiveActivity.class );
 				
 				break;
        		default:
        			break;
        		}  
        		startActivity(intent);  
        	}
        });
        
      
	}   
	
	protected void dialog() {
		  AlertDialog.Builder builder = new Builder(NoticeActivity.this);
		  builder.setMessage("确认退出吗？");
		  builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    
		    NoticeActivity.this.finish();
		    
		   }

		
		  });

		  builder.setNegativeButton("取消",new android.content.DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		   }
		  });

		  builder.create().show();
		 }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		   dialog();
		  }
		  return false;
		 }
}
