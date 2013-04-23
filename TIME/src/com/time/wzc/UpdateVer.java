package com.time.wzc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.R.bool;
import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

public class UpdateVer  {
	
	private String verIntro; 
	private String apkUrl;
	private Context mContext; 
	private int verCode;
	private int build;
	private String verson;
	private ProgressBar progressBar;
	private int progress = 0;  
	
	public  UpdateVer(Context context) {
		this.mContext = context;
		verCode = getVerCode();
	}
	public  int getVerCode() {  
        int verCode = -1;  
        try {  
            verCode = mContext.getPackageManager().getPackageInfo(  
                    "com.time.wzc", 0).versionCode;  
        } catch (NameNotFoundException e) {  
        }  
        return verCode;  
    }  
	
	private void notNewVersionShow() {  
	    StringBuffer sb = new StringBuffer();  
	    sb.append("当前版本:");  
	    sb.append(verson);  
	    sb.append(",/n已是最新版,无需更新!");  
	    Builder builder = new Builder(mContext);  
	    builder.setMessage(sb.toString())// 设置内容  
	            .setPositiveButton("确定",// 设置确定按钮  
	                    new DialogInterface.OnClickListener() {  
	                        @Override  
	                        public void onClick(DialogInterface dialog,  
	                                int which) {  
	                        	 dialog.dismiss(); 
	                        }  
	                    }).create();// 创建  
	    // 显示对话框  
	    builder.show();  
	}  
	
	private void doNewVersionUpdate() {  
	    StringBuffer sb = new StringBuffer();  
	    sb.append("当前版本:");  
	    sb.append(verson);    
	    sb.append(", 发现新版本:");  
	    sb.append(verIntro);   
	    sb.append(", 是否更新?");  
	    Builder builder = new Builder(mContext);  
	    builder.setTitle("软件更新")  
	            .setMessage(sb.toString())  
	            // 设置内容  
	            .setPositiveButton("更新",// 设置确定按钮  
	                    new DialogInterface.OnClickListener() {  
	                        @Override  
	                        public void onClick(DialogInterface dialog,  
	                                int which) {  
	                             showDownloadDialog();
	                        }  
	                    })  
	            .setNegativeButton("暂不更新",  
	                    new DialogInterface.OnClickListener() {  
	                        public void onClick(DialogInterface dialog,  
	                                int whichButton) {  
	                            // 点击"取消"按钮之后退出程序  
	                        	dialog.dismiss(); 
	                        }  
	                    });// 创建  
	    builder.create().show();
	}  
	
	private void downloadApk(){  
        //开启另一线程下载  
		ThreadPoolUtils.execute(downApkRunnable);
    }  
    private String getName()
    {
    	return ("Time"+verson+".apk");
    	
    }
    private Runnable downApkRunnable = new Runnable(){  
        @Override  
        public void run() {  
            if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {  
                //如果没有SD卡  
            	Builder builder = new Builder(mContext);   
	            builder.setTitle("提示").setMessage("当前设备无SD卡，数据无法下载").setNegativeButton("取消",  
	                    new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog,  
                            int whichButton) {  
                        // 点击"取消"按钮之后退出程序  
                    	dialog.dismiss(); 
                    }  
                });
                builder.create().show();  
                return;  
            }else{  
                try {  
                    //服务器上新版apk地址  
                    URL url = new URL("http://113.11.199.135:8000"+apkUrl);  
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                    conn.connect();  
                    int length = conn.getContentLength();  
                    InputStream is = conn.getInputStream();  
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/updateApkFile/");  
                    if(!file.exists()){  
                        //如果文件夹不存在,则创建  
                        file.mkdir();  
                    }  
                    //下载服务器中新版本软件（写文件）  
                    String apkFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/updateApkFile/" +getName() ;  
                    File ApkFile = new File(apkFile);  
                    FileOutputStream fos = new FileOutputStream(ApkFile);  
                    int count = 0;  
                    byte buf[] = new byte[1024];   
                    do{
                    	int numRead = is.read(buf);  
                        count += numRead;  
                        //更新进度条  
                        progress = (int) (((float) count / length) * 100);  
                        barhandler.sendEmptyMessage(1);  
                        if(numRead <= 0){  
                            //下载完成通知安装  
                            barhandler.sendEmptyMessage(0);  
                            break;  
                        }  
                        fos.write(buf,0,numRead);  
                    }while(1 > 0);  
                    
                } catch (MalformedURLException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    };  
    
    private Handler barhandler = new Handler() {  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case 1:  
                // 更新进度情况  
                progressBar.setProgress(progress);  
                break;  
            case 0:  
                progressBar.setVisibility(View.INVISIBLE);  
                // 安装apk文件  
                installApk();  
                break;  
            default:  
                break;  
            }  
        };  
    };  
    
    private void installApk() {  
        // 获取当前sdcard存储路径  
        File apkfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/updateApkFile/" +getName());  
        if (!apkfile.exists()) {  
            return;  
        }  
        Intent i = new Intent(Intent.ACTION_VIEW);  
        // 安装，如果签名不一致，可能出现程序未安装提示  
        i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())), "application/vnd.android.package-archive");   
        mContext.startActivity(i);  
    }  
    
	private void showDownloadDialog() {  
	    Builder builder = new Builder(mContext);  
	    builder.setTitle("版本更新中...");  
	    final LayoutInflater inflater = LayoutInflater.from(mContext);  
	    View v = inflater.inflate(R.layout.update_prgress, null);  
	    progressBar = (ProgressBar) v.findViewById(R.id.pb_update_progress);  
	    builder.setView(v);  
	    builder.create().show();  
	    //下载apk  
	    downloadApk();  
	   }
	
	public void check() {
		List <NameValuePair> postparams = new ArrayList<NameValuePair>(); 
		postparams.add(new BasicNameValuePair("last",String.valueOf(verCode)));
    	String url = "android/version";
    	Handler handler = new Handler(){
    		@Override
			public void handleMessage(Message msg) {
				// 如果发送的消息来自子线程
    			if(msg.what == 1){
    				String jsonData = msg.obj.toString();
    				if (jsonData.equals("0"))
    					return;
    				try {
						JSONObject jsonObj = new JSONObject(jsonData);
						build = jsonObj.getInt("build");
						verson = jsonObj.getString("version");
						apkUrl = jsonObj.getString("apk");
						verIntro = jsonObj.getString("description");
						if (build > verCode)
						{
							doNewVersionUpdate();
						} else {
							notNewVersionShow();
						}
						
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
	


}
