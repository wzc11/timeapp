package com.time.wzc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyPost implements Runnable{
	
	private Handler handler;
	private String urlname;
	private List <NameValuePair> postparams;
	public  MyPost(String url, Handler handler, List <NameValuePair> postparams)throws IOException {
		this.handler = handler;
		this.urlname = url;
		this.postparams = postparams;
	}
	
	public void run() {
	// TODO Auto-generated method stub
		
		HttpPost httpPost = new HttpPost("http://113.11.199.135:8000/"+urlname);
		//HttpPost httpPost = new HttpPost("http://59.66.138.26:8000/"+urlname);
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000);
		DefaultHttpClient httpClient = new DefaultHttpClient(params); 
		try{
			httpPost.setEntity(new UrlEncodedFormEntity(postparams, "UTF-8"));
			HttpResponse httpResponse=httpClient.execute(httpPost);  
			if (httpResponse.getStatusLine().getStatusCode() == 200)
			{
				
				Message msg = handler.obtainMessage();
				String result = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				msg.what = 1;
			 	msg.obj = result;
			 	Log.d("result",result);
			 	handler.sendMessage(msg);
			} 
			}	catch(Exception e){
					e.printStackTrace();
				}
		}
}