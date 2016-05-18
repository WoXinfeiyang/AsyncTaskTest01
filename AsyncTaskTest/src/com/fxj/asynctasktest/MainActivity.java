package com.fxj.asynctasktest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button executeBtn;
	Button cancelBtn;
	TextView textView;
	ProgressBar progressBar;
	MyAsyncTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.executeBtn=(Button) findViewById(R.id.executeBtn);
		this.cancelBtn=(Button) findViewById(R.id.cancelBtn);
		this.textView=(TextView) findViewById(R.id.textView);
		this.progressBar=(ProgressBar) findViewById(R.id.progressBar);
		
		this.cancelBtn.setEnabled(false);
		
		this.executeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*AsyncTask异步任务只能执行一次，为了每次点击该按钮生效可在此创建AsyncTask对象*/
				task=new MyAsyncTask();
				task.execute("http://www.ifeng.com/");
				executeBtn.setEnabled(false);
				cancelBtn.setEnabled(true);
			}
		});
		
		this.cancelBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				task.cancel(true);
			}
		});
	}

	public class MyAsyncTask extends AsyncTask<String,Integer,String>
	{
		private static final String tag="MyAsyncTask";
		
		/*在主线程中执行，在异步任务执行之前此方法会被调用,一般用于做一些准备工作*/
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Log.i(tag,"onPreExecute");
			textView.setText("loading……");
			super.onPreExecute();
		}
		/*在线程池中执行异步任务,注意该方法传入参数类型为AsyncTash泛型Params,返回结果为AsyncTash泛型Result，
		 * 在此方法中可以调用publishProgress来更新进度，而publishProgress会通过Handler对象发送消息调用
		 * onProgressUpdate方法
		 * */
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.i(tag,"doInBackground");

			
			try {
				/*创建HttpClient对象*/
				HttpClient client=new DefaultHttpClient();
				/*创建HttpGet请求*/
				HttpGet get=new HttpGet(params[0]);
				/*调用HttpClient.execute发送请求，HttpClient.execute返回一个HttpResponse对象*/
				HttpResponse response=client.execute(get);
				
				if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					
					/*从HttpResponse对象中调用HttpResponse.getEntity方法返回HttpEntity
					 * 对象，HttpEntity对象包含了服务器响应的内容
					 * */
					HttpEntity entity=response.getEntity();
					/*拿到服务器响应内容HttpEntity对象对象的InputStream输入流*/
					InputStream is=entity.getContent();
					
					long total=entity.getContentLength();
					Log.i(tag,"doInBackground,服务器响应内容输入流总的长度total="+total);
					
					byte[] buffer=new byte[1024];
					ByteArrayOutputStream byteArrayOS=new ByteArrayOutputStream();
					int length=-1;
					long count=0;
					while((length=is.read(buffer))!=-1){
						byteArrayOS.write(buffer,0,length);
						count+=length;
						/*更新进度*/
						int value=(int)(count*100/total);
						Log.i(tag,"doInBackground,进度值value="+value);
						publishProgress(value);
//						/*线程休眠0.5秒*/
//						Thread.sleep(500);
					}
					return new String(byteArrayOS.toByteArray(),"utf-8");
				}
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (/*Interrupted*/Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		/*在主线程中执行,后台异步任务执行进度发生变化时回调该方法*/
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			Log.i(tag,"onProgressUpdate,当前进度值:"+values[0]);
			progressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}
		
		/*在主线程中执行,当异步任务执行完成后此方法会被回调*/
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			Log.i(tag,"onPostExecute");
			textView.setText(result);
			executeBtn.setEnabled(true);
			cancelBtn.setEnabled(false);
			super.onPostExecute(result);
		}
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			textView.setText(null);
			progressBar.setProgress(0);
			executeBtn.setEnabled(true);
			cancelBtn.setEnabled(false);
			super.onCancelled();
		}		
	}

}
