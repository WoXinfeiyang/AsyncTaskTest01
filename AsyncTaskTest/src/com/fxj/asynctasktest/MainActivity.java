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
				/*AsyncTask�첽����ֻ��ִ��һ�Σ�Ϊ��ÿ�ε���ð�ť��Ч���ڴ˴���AsyncTask����*/
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
		
		/*�����߳���ִ�У����첽����ִ��֮ǰ�˷����ᱻ����,һ��������һЩ׼������*/
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Log.i(tag,"onPreExecute");
			textView.setText("loading����");
			super.onPreExecute();
		}
		/*���̳߳���ִ���첽����,ע��÷��������������ΪAsyncTash����Params,���ؽ��ΪAsyncTash����Result��
		 * �ڴ˷����п��Ե���publishProgress�����½��ȣ���publishProgress��ͨ��Handler��������Ϣ����
		 * onProgressUpdate����
		 * */
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.i(tag,"doInBackground");

			
			try {
				/*����HttpClient����*/
				HttpClient client=new DefaultHttpClient();
				/*����HttpGet����*/
				HttpGet get=new HttpGet(params[0]);
				/*����HttpClient.execute��������HttpClient.execute����һ��HttpResponse����*/
				HttpResponse response=client.execute(get);
				
				if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					
					/*��HttpResponse�����е���HttpResponse.getEntity��������HttpEntity
					 * ����HttpEntity��������˷�������Ӧ������
					 * */
					HttpEntity entity=response.getEntity();
					/*�õ���������Ӧ����HttpEntity��������InputStream������*/
					InputStream is=entity.getContent();
					
					long total=entity.getContentLength();
					Log.i(tag,"doInBackground,��������Ӧ�����������ܵĳ���total="+total);
					
					byte[] buffer=new byte[1024];
					ByteArrayOutputStream byteArrayOS=new ByteArrayOutputStream();
					int length=-1;
					long count=0;
					while((length=is.read(buffer))!=-1){
						byteArrayOS.write(buffer,0,length);
						count+=length;
						/*���½���*/
						int value=(int)(count*100/total);
						Log.i(tag,"doInBackground,����ֵvalue="+value);
						publishProgress(value);
//						/*�߳�����0.5��*/
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
		
		/*�����߳���ִ��,��̨�첽����ִ�н��ȷ����仯ʱ�ص��÷���*/
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			Log.i(tag,"onProgressUpdate,��ǰ����ֵ:"+values[0]);
			progressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}
		
		/*�����߳���ִ��,���첽����ִ����ɺ�˷����ᱻ�ص�*/
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
