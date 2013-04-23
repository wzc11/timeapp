package com.time.wzc;
/* 
 * author:汪至诚
 * 主界面
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.PrivateCredentialPermission;

import com.time.wzc.MyPost;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class IndexActivity extends Activity {
    /** Called when the activity is first created. */
	private int newid= 0;
    EditText username;
    private String uid;
    private String IMSI;
    private int flag;
    private TelephonyManager telephonyManager;
    MyDataHelper myHelper;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.index);
		UpdateVer updateVer= new UpdateVer(IndexActivity.this);
		updateVer.check();
        myHelper = new MyDataHelper(this, MyDataHelper.DB_NAME, null, 1);
       
		
        telephonyManager= (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
      
        
        Intent timeService = new Intent(IndexActivity.this,MyService.class);  
        this.startService(timeService);  
        Intent shortintent = new Intent(IndexActivity.this, ShortService.class);  
        bindService(shortintent, connection, Context.BIND_AUTO_CREATE);
        
        
        
        username = (EditText)findViewById(R.id.usertext);
        uid = "";
        Button login = (Button)findViewById(R.id.login);
        if ( !check().equals("") )
        {
        	uid = check();
        	username.setVisibility(View.GONE);
        	
        	
        
        
        	ImageView indextop = (ImageView)findViewById(R.id.indextop);
        	indextop.setVisibility(View.GONE);
        	login.setBackgroundResource(R.drawable.loginbtn);
        }
     
        login.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flag =0;
				uid = check();
				if ((uid.equals("")) &&(username.getText().toString().equals(""))) 
				{
					Toast.makeText(getApplicationContext(), "请输入用户名",
						     Toast.LENGTH_SHORT).show();
					return;
				}
				if ( uid.equals(""))
				{
					try {
						register(username.getText().toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (flag ==0)
					{
						rememberme(username.getText().toString());
					}
				}
				
				if ( flag == 0)
				{
					Intent intent = new Intent();
					intent.setClass(IndexActivity.this, NoticeActivity.class );
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
					IndexActivity.this.finish();
					startActivity(intent);
				}
			}
		});
        
        
        
        
        
       
        
   
    }
	
	private ServiceConnection connection = new ServiceConnection() {  
	@Override  
    	public void onServiceDisconnected(ComponentName name) {  
    	}  
		@Override  
		public void onServiceConnected(ComponentName name, IBinder service) {  
			((ShortService.MyBinder) service).getService();  
		}  
	};  
	
	private void updatesoft() {
		List <NameValuePair> postparams = new ArrayList<NameValuePair>(); 
		try {
			int curVersion = getPackageManager().getPackageInfo("com.time.wzc", 0).versionCode;
			postparams.add(new BasicNameValuePair("virsion",String.valueOf(curVersion)));
			
		
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		
	}
	
    public void register(String name) throws IOException
    {
    	List <NameValuePair> postparams = new ArrayList<NameValuePair>(); 
    	postparams.add(new BasicNameValuePair("uid",name));
    	postparams.add(new BasicNameValuePair("IMSI",IMSI));
    	String url = "android/user";
    	HttpPost httpPost = new HttpPost("http://113.11.199.135:8000/"+url);
    	HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000);
		DefaultHttpClient httpClient = new DefaultHttpClient(params); 
		try{
			httpPost.setEntity(new UrlEncodedFormEntity(postparams, "UTF-8"));
			HttpResponse httpResponse=httpClient.execute(httpPost);  
			if (httpResponse.getStatusLine().getStatusCode() == 200)
			{		
				String result = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				if (result.equals("False"))
				{
					flag = 1;
					Toast.makeText(getApplicationContext(), "用户名已被注册",
						     Toast.LENGTH_SHORT).show();
					username.setText("");
					username.setHint("换一个用户名");
				} else
					flag = 0;
			} 
		}	catch(Exception e){
					e.printStackTrace();
				}
    	
    }
    public void rememberme (String uid) {
    	SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
    	SharedPreferences.Editor editor = sp.edit();
    	editor.putString("uid", uid);
    	IMSI = telephonyManager.getDeviceId();
    	String nweString =IMSI;
    	editor.putString("IMSI", IMSI);
    	editor.commit();
	}
    public String  check() {
		SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
		String uid = sp.getString("uid", "");
		return uid;
	}
    protected void onDestroy() {  
        super.onDestroy();  
        unbindService(connection);  
    }; 
}