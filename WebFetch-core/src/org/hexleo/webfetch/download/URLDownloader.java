package org.hexleo.webfetch.download;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.hexleo.webfetch.Request;
import org.hexleo.webfetch.http.HttpClient;
import org.hexleo.webfetch.http.HttpClientFactory;
import org.hexleo.webfetch.schedule.TaskSchedule;
import org.hexleo.webfetch.util.Log;

/**
 * use task queue
 * @author hexleo
 *
 */
public class URLDownloader {

	private static final String TAG = URLDownloader.class.toString();
	private static final boolean DEBUG = false;
	private static final int DEFAULT_THREAD_SIZE = 10;
	//pause time for some web site
	public static int PAUSE_TIME = -1;
	private int mTaskThreadSize;
	private int mMaxTaskSize;
	private int mCurrentTaskSize;
	private LinkedList<Request> mRequestQueue;
	private HttpThread[] mHttpThreads;
	private TaskSchedule mTaskSchedule;
	private boolean mIsStart;
	
	//for https to trust all certificates
	private X509TrustManager mTrustManager = new X509TrustManager(){

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};
	//create an class to trust all hosts
	private HostnameVerifier mHostnameVerifier = new HostnameVerifier(){
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
	
	public URLDownloader(TaskSchedule mTaskSchedule) {
		this(mTaskSchedule , -1 , -1);
	}
	
	public URLDownloader(TaskSchedule mTaskSchedule , int mTaskThreadSize, int maxTaskSize) {
		this.mTaskThreadSize = mTaskThreadSize > 0? mTaskThreadSize : DEFAULT_THREAD_SIZE;
		this.mMaxTaskSize = maxTaskSize;
		this.mCurrentTaskSize = 0;
		this.mTaskSchedule = mTaskSchedule;
		this.mRequestQueue = new LinkedList<Request>();
		this.mIsStart = false;
		
		//no matter http or https all start up with https
		//initialize the TLS SSLContext with TrustManager
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			X509TrustManager[] xtms = new X509TrustManager[]{mTrustManager};
			sslContext.init(null, xtms, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			Log.e(DEBUG, TAG, "URLDownloader NoSuchAlgorithmException");
		} catch (KeyManagementException e) {
			Log.e(DEBUG, TAG, "URLDownloader KeyManagementException");
		}
		//set HttpsURLConnection
		if(sslContext != null){
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		}
		HttpsURLConnection.setDefaultHostnameVerifier(mHostnameVerifier);
		
	}
	
	public void setTaskThreadSize(int mTaskThreadSize) {
		if(!mIsStart)
			this.mTaskThreadSize = mTaskThreadSize > 0? mTaskThreadSize : DEFAULT_THREAD_SIZE;
	}

	public void setMaxTaskSize(int mMaxTaskSize) {
		if(!mIsStart)
			this.mMaxTaskSize = mMaxTaskSize;
	}

	public boolean addRequest(Vector<Request> requests) {
		if(!mIsStart || requests == null || (mMaxTaskSize > 0 && mCurrentTaskSize > mMaxTaskSize))
			return false;
		synchronized (mRequestQueue) {
			for(Request page : requests){
				if(mMaxTaskSize > 0 && ++mCurrentTaskSize > mMaxTaskSize)
					break;
				mRequestQueue.add(page);
			}
			mRequestQueue.notifyAll();
		}
		
		return true;
	}
	
	public void start(){
		if(mIsStart)
			return;
		mIsStart = true;
		this.mHttpThreads = new HttpThread[mTaskThreadSize];
		for(int i=0 ; i<this.mHttpThreads.length ; i++){
			this.mHttpThreads[i] = new HttpThread();
			this.mHttpThreads[i].start();
		}
		
	}
	
	public void close(){
		if(!mIsStart)
			return;
		if(mHttpThreads != null){
			for(int i=0 ; i<this.mHttpThreads.length ; i++){
				this.mHttpThreads[i].interrupt();
			}
		}
		mIsStart = false;
	}
	
	/**
	 * task thread
	 * @author hexleo
	 *
	 */
	private class HttpThread extends Thread{
		// each thread has it own http client
		private HttpClient mHttpClient;
		public HttpThread() {
			super();
			this.mHttpClient = HttpClientFactory.create();
		}


		public void run(){
			Log.i(DEBUG , TAG, "Thread id:"+this.toString()+" was started");
			try {
				Request request = null;
				while(mIsStart){
					synchronized (mRequestQueue) {
						while(mRequestQueue.isEmpty()){
							mRequestQueue.wait();
						}
						request = mRequestQueue.removeFirst();
					}
					if(mHttpClient == null){ //prevent mHttpClient is null
						mHttpClient = HttpClientFactory.create();
					}
					if(request != null && mHttpClient != null){
						mHttpClient.getPage(request);
						//TaskSchedule.getInstance().finishTask(page);
						if(mTaskSchedule != null)
							mTaskSchedule.finishRequest(request);
					}
					request = null;
					if(PAUSE_TIME > 0 ){
						Thread.sleep(PAUSE_TIME);
					}
				}
			} catch (InterruptedException e) {
				Log.e(DEBUG , TAG, "TaskThread id:"+this.toString()+"interrupted");
			}
			Log.i(DEBUG , TAG, "TaskThread id:"+this.toString()+" closed");
		}
	}

}
