package com.time.wzc;

import java.io.File;

import com.time.wzc.ReadingDetailActivity.ImageAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.style.SuperscriptSpan;
import android.text.style.UpdateAppearance;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticeDetailActivity extends Activity {
	private Button like,remind;
	MyDataHelper myHelper;
	private int islike;
	private int ID,index;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.noticedetail);
        ID  = (int)(this.getIntent().getExtras().getInt("_id"));
        index  = (int)(this.getIntent().getExtras().getInt("index"));
        myHelper = new MyDataHelper(this, MyDataHelper.DB_NAME, null, 1);
        SQLiteDatabase db = myHelper.getReadableDatabase();
        Cursor c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.DATE,
        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.PLACE,MyDataHelper.TOPIC_TNTRO,MyDataHelper.LECTURER
        		,MyDataHelper.LIKE,MyDataHelper.ALERT,MyDataHelper.POSTER,MyDataHelper.TIME}, 
        		MyDataHelper.ID+"=?",new String[]{ID+" "}, null, null,null);
        c.moveToFirst();
        
        
        ImageView posterImageView = (ImageView)findViewById(R.id.poster);
        String pathString = c.getString(8);

        File file = new File(pathString);
        if(file.exists())
        {
        	Bitmap bitmap = BitmapFactory.decodeFile(pathString);
        	posterImageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }
        like = (Button)findViewById(R.id.like);
         islike = c.getInt(6);
        int isalert = c.getInt(7);
        
        if (islike ==1 )
        {
        	like.setBackgroundResource(R.drawable.love1);
        } else {
        	like.setBackgroundResource(R.drawable.likebtn);
        }
        
        like.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
			if (islike == 1)
			{
				updatelike(ID,0);
				like.setBackgroundResource(R.drawable.likebtn);	
				islike = 0;
			} else {
				updatelike(ID,1);
				like.setBackgroundResource(R.drawable.like2);
				islike = 1;
			}
        	}	
		});
        
        remind = (Button)findViewById(R.id.remind);
        if (isalert >=1 )
        {
        	remind.setClickable(false);
        	remind.setBackgroundResource(R.drawable.remind);
        } else {
			remind.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v) {
	        	updatealart(ID);
	        	remind.setBackgroundResource(R.drawable.remind);	
				remind.setClickable(false);
	        	}	
			});
        }
        String timeString = c.getString(0).substring(5);
        if (timeString.substring(0, 1).equals("0"))
        	timeString.substring(1);
        TextView topicTextView = (TextView )findViewById(R.id.topic);
        topicTextView.setText(" "+timeString+" "+c.getString(2)+" "+c.getString(1));
        timeString = c.getString(9).substring(0,5);
        if (timeString.substring(0, 1).equals("0"))
        	timeString.substring(1);
        
        TextView placeTextView = (TextView)findViewById(R.id.place);
        placeTextView.setText(" "+timeString+"  "+c.getString(3));
        
        TextView lectureTextView =(TextView)findViewById(R.id.lecture);
        lectureTextView.setText(" ½²×ù¼ò½é: "+c.getString(4));
        
        TextView lecturer = (TextView)findViewById(R.id.lecturer);
        lecturer.setText(" ¼Î±ö¼ò½é: "+c.getString(5));
        
        
        Button backbtn=(Button)findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    				NoticeDetailActivity.this.finish();
    			}
    		});
        c.close();
        db.close();
        
	}
	
	private   void updatelike(int lecid, int flag) {
		SQLiteDatabase db = myHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MyDataHelper.LIKE, flag);
		db.update(MyDataHelper.TABLE_NAME, values, MyDataHelper.ID+"=?", new String[]{lecid+" "});
        db.close();	
	}
	private   void updatealart(int lecid) {
		SQLiteDatabase db = myHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MyDataHelper.ALERT, 1);
		db.update(MyDataHelper.TABLE_NAME, values, MyDataHelper.ID+"=?", new String[]{lecid+" "});
        db.close();	
	}
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && index ==1) {
			  Intent intent = new Intent();
			  intent.setClass(NoticeDetailActivity.this, NoticeActivity.class );
			  NoticeDetailActivity.this.finish();
			  startActivity(intent);
		  } else
		  super.onKeyDown(keyCode, event);
		   return true;
		  
		 }
	 
}
