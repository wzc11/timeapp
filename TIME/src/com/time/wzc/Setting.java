package com.time.wzc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends Activity {
	private int bt1status,bt2status,bt3status; 
	Button bt1,bt2,bt3;
	TextView userchange;
	SharedPreferences.Editor editor;
	SharedPreferences sp;
	Builder builder;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);
        sp = getSharedPreferences("user",MODE_PRIVATE);
        userchange=(TextView)findViewById(R.id.idmanager);
        bt1status=sp.getInt("bt1",1);
        bt2status=sp.getInt("bt2",1);
        bt3status=sp.getInt("bt3",1);
        editor = sp.edit();
        builder = new Builder(Setting.this); 
        
        userchange.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        			final EditText et = new EditText(Setting.this);  
        		    builder.setTitle("更改新用户名").setIcon(
        		    	     android.R.drawable.ic_dialog_info).setView(
        		    	     et).setPositiveButton("确定",  new DialogInterface.OnClickListener() {  
        		                    @Override  
        		                    public void onClick(DialogInterface arg0, int arg1) {  
        		                       
        		                    	String newname = et.getText().toString();  
        		                    	SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
        		                    	String oldname = sp.getString("uid", "");
        		                    	String IMSI = sp.getString("IMSI", "");
        		                    	SharedPreferences.Editor editor = sp.edit();
        		                    	editor.putString("uid", newname);
        		                    	editor.commit();
        		                    	List <NameValuePair> postparams = new ArrayList<NameValuePair>(); 
        		                		postparams.add(new BasicNameValuePair("uid",newname));
        		                		postparams.add(new BasicNameValuePair("old",oldname));
        		                		postparams.add(new BasicNameValuePair("IMSI",IMSI));
        		                    	String url = "android/user";
        		                    	Handler handler = new Handler(){
        		                    		@Override
        		                			public void handleMessage(Message msg) {
        		                				// 如果发送的消息来自子线程
        		                				if(msg.what == 1){
        		                					// 将读取的内容追加显示在文本框中
        		                					Toast.makeText(getApplicationContext(), "更改成功",
        		                						     Toast.LENGTH_SHORT).show();
        		                				} else {
        		                					Toast.makeText(getApplicationContext(), "更改失败",
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
        		                    }  
        		                })
        		    	     .setNegativeButton("取消",new DialogInterface.OnClickListener() {  
     		                    @Override  
     		                    public void onClick(DialogInterface arg0, int arg1) {
     		                        	arg0.dismiss(); 
     		                    }}).show();
        		   
        	}
        });
      
        bt1=(Button)findViewById(R.id.setbt1);
        
        bt1.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
    				if (bt1status == 1)
    				{
    					bt1.setBackgroundResource(R.drawable.setbt1);
    					bt1status = 2;
    					editor.putInt("bt1", bt1status);
    					editor.commit();
    				}
    				else 
    				{
    					bt1.setBackgroundResource(R.drawable.setbtn);
    					bt1status =1;
    					editor.putInt("bt1", bt1status);
    					editor.commit();
    				}
            	}	
        }); 
        bt2=(Button)findViewById(R.id.setbt2);
        
        bt2.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
    				if (bt2status == 1)
    				{
    					bt2.setBackgroundResource(R.drawable.setbt1);
    					bt2status = 2;
    					editor.putInt("bt2", bt2status);
    					editor.commit();
    				}
    				else 
    				{
    					bt2.setBackgroundResource(R.drawable.setbtn);
    					bt2status =1;
    					editor.putInt("bt2", bt2status);
    					editor.commit();
    				}
            	}	
        }); 
        bt3=(Button)findViewById(R.id.setbt3);
        Button backbtn=(Button)findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    				Setting.this.finish();
    			}
    		});
        bt3.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
    				if (bt3status == 1)
    				{
    					bt3.setBackgroundResource(R.drawable.setbt1);
    					bt3status = 2;
    					editor.putInt("bt3", bt3status);
    					editor.commit();
    				}
    				else 
    				{
    					bt3.setBackgroundResource(R.drawable.setbtn);
    					bt3status =1;
    					editor.putInt("bt3", bt3status);
    					editor.commit();
    				}
            	}	
        }); 
	}
	protected void showToast(String string) {
		// TODO Auto-generated method stub
		
	}
	
}
