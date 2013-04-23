package com.time.wzc;

import java.io.File;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadingDetailActivity extends Activity {
	private Button like,share;
	MyDataHelper myHelper;
	private int islike;
	private int ID;
	private Cursor  c;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.readdetail);
        ID  = (int)(this.getIntent().getExtras().getInt("_id"));
        myHelper = new MyDataHelper(this, MyDataHelper.DB_NAME, null, 1);
        SQLiteDatabase db = myHelper.getWritableDatabase();
        c = db.query(MyDataHelper.TABLE_NAME, new String[]{MyDataHelper.DATE,
        		MyDataHelper.TOPIC,MyDataHelper.NAME,MyDataHelper.TOPIC_TNTRO
        		,MyDataHelper.LIKE,MyDataHelper.DOWNLOAD,
        		MyDataHelper.TEXT_NAME,MyDataHelper.PIC,MyDataHelper.LECTURER}, 
        		MyDataHelper.ID+"=?",new String[]{ID+" "}, null, null,null);
        c.moveToFirst();
        
        like = (Button)findViewById(R.id.like);
        islike = c.getInt(4);
        
        if (islike ==1 )
        {
        	like.setBackgroundResource(R.drawable.like2);
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
        
        TextView topicTextView = (TextView)findViewById(R.id.topic);
        topicTextView.setText(c.getString(0).substring(5)+" "+c.getString(1)+" "+c.getString(2));
        TextView lecturerTextView = (TextView)findViewById(R.id.lecturer);
        lecturerTextView.setText("嘉宾简介："+c.getString(8));
        TextView detilTextView = (TextView)findViewById(R.id.detail);
        detilTextView.setText("讲座简介："+c.getString(3));
     /*   Button downButton = (Button)findViewById(R.id.download);
        downButton.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				String downString = c.getString(5);
				
				
			}
        });   
        */
        Button backbtn=(Button)findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    				ReadingDetailActivity.this.finish();
    			}
    		});
        String picpath = c.getString(7);
        String ppath = c.getString(0);
        String pic1,pic2,pic3;
        String path = Environment.getExternalStorageDirectory().toString()+"/Time/" ;
        pic1 = path+ppath+"1.jpg";
        pic2 = path+ppath+"2.jpg";
        pic3 = path+ppath+"3.jpg";
        Bitmap bitmap1 = null,bitmap2=null,bitmap3=null;
        if (picpath.charAt(0) != '0')
        {
            File file = new File(pic1);
            if(file.exists())
            {
            	bitmap1= BitmapFactory.decodeFile(pic1);
            }
        }
        else {
        	bitmap1 = Bitmap.createBitmap(280,150, Config.ARGB_8888);
        }
        if (picpath.charAt(1) != '0')
        {
        	File file = new File(pic2);
            if(file.exists())
            {
            	bitmap2= BitmapFactory.decodeFile(pic2);
            }
        }
        else {
        	bitmap2 = Bitmap.createBitmap(280,150, Config.ARGB_8888);
        }	
        
        if (picpath.charAt(2) != '0')
        {
        	File file = new File(pic3);
            if(file.exists())
            {
            	bitmap3= BitmapFactory.decodeFile(pic3);
            }
        }
        else {
        	bitmap3 = Bitmap.createBitmap(280,150, Config.ARGB_8888);
        }
        Gallery gallery = (Gallery)findViewById(R.id.gallery);
        gallery.setMinimumHeight(getWindowManager().getDefaultDisplay().getHeight()/4);
        Bitmap[] img = {bitmap1,bitmap2,bitmap3}; 

        gallery.setAdapter(new ImageAdapter(this, img));   
        gallery.setSelection(1);
        c.close();
        db.close();  
	}
	
	private   void updatelike(int lecid, int flag ) {
		SQLiteDatabase db = myHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(MyDataHelper.LIKE, flag);
		db.update(MyDataHelper.TABLE_NAME, values, MyDataHelper.ID+"=?", new String[]{lecid+" "});
        db.close();	
	}
	
	public class  ImageAdapter extends BaseAdapter {
		private Context mContext; 
		private Bitmap[] mImageIds;
		int mGalleryItemBackGround; 
		public ImageAdapter(Context c, Bitmap[] img) { 
			//声明 ImageAdapter  
				mContext = c;  
				mImageIds = img;
				TypedArray a = mContext.obtainStyledAttributes(R.styleable.Gallery1); 
		        mGalleryItemBackGround = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0); 
		        a.recycle(); 
			}
		public int getCount() { //获取图片的个数  
			return mImageIds.length;  
		}  
		public Object getItem(int position) {
			//获取图片在库中的位置  
			return position;  
		}  
		public long getItemId(int position) {
			//获取图片在库中的位置  
			return position;  
		}
		public View getView(int position, View convertView,
				 ViewGroup parent) {  
				ImageView i = new ImageView(mContext);  
				//i.setBackgroundDrawable(new BitmapDrawable(mImageIds[position]));
				i.setImageBitmap(mImageIds[position]);
				//给ImageView设置资源  
				i.setLayoutParams(new Gallery.LayoutParams(280, 150));
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				
				//设置比例类型  
				return i;  
		} 

	}

}
