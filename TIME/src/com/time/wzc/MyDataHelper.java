package com.time.wzc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataHelper extends  SQLiteOpenHelper {

	public static final String DB_NAME = "timeData";
	public static final String TABLE_NAME ="lecture";
	public static final String ID ="_id";
	public static final String TOPIC ="lectureTopic";
	public static final String NAME = "lecturerName";
	public static final String DATE = "lectureDate";
	public static final String TIME = "lectureTime";
	public static final String STATUS = "lectureStatus";
	public static final String PLACE ="lecturePlace";
	public static final String CATEGORY="lectureCategory";
	public static final String TOPIC_TNTRO="lectureTopicIntro";
	public static final String LECTURER="lecturerIntro";
	public static final String POSTER="lecturePoster";
	public static final String PIC = "lecturePic" ;
	public static final String ALERT = "isAlert" ;
	public static final String LIKE ="isLike" ;
	public static final String DOWNLOAD = "isDownloaded";
	public static final String TEXT_NAME="textName"; 
	public static final String RECOMMEND ="isRecommended";
	public static final String LASTID ="lastID";
	
	public MyDataHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists "+TABLE_NAME+" ("
				+ ID +" integer primary key,"
				+ TOPIC +" nvarchar,"
				+ NAME +" nvarchar,"
			 	+ DATE +" TEXT,"
				+ TIME +" TEXT,"
				+ STATUS +" integer,"
				+ PLACE +" nvarchar,"
				+ CATEGORY +" integer,"
				+ TOPIC_TNTRO +" nvarchar,"
				+ LECTURER +" nvarchar,"
				+ POSTER +" nvarchar,"
				+ PIC +" nvarchar,"
				+ ALERT +" integer,"
				+ LIKE +" integer,"
				+ DOWNLOAD +" integer,"
				+ TEXT_NAME +" nvarchar,"
				+ RECOMMEND +" integer,"
				+ LASTID +" integer)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
