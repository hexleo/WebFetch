package org.hexleo.webfetch.schedule;

import java.util.Hashtable;

import org.hexleo.webfetch.download.Request;
import org.hexleo.webfetch.http.HttpClient;

public class VisitedURLManager {
	private static final int DEFAULT_RETRYTIMES = 3;
	private int mRetry;
	//Hashtable is thread safe
	private Hashtable<String , ShortPage> mVisitedURL;
	
	public VisitedURLManager(){
		this.mVisitedURL = new Hashtable<String, ShortPage>();
		mRetry = DEFAULT_RETRYTIMES;
	}
	/**
	 * 
	 * @param mRetry 0 or <0 means no retry
	 */
	public void setRetryTimes(int mRetry) {
		this.mRetry = mRetry > 0 ? mRetry : 0;
	}

	/**
	 * call VisitedURLManager.add behind pattern match
	 * to add a new page before call http download
	 * @param request
	 */
	public void add(Request request){
		if(request != null)
			mVisitedURL.put(request.getUrl(), new ShortPage(request.getUrl() ,
					0,
					false));
	}
	
	/**
	 * to find the visited history
	 * @param url
	 * @return
	 */
	public ShortPage find(String url){
		return mVisitedURL.get(url);
	}
	/**
	 * to update the exited page, after finish download
	 * @param page
	 */
	public void update(Request request){
		if(request == null || request.getPage() == null)
			return;
		ShortPage tmpPage= find(request.getUrl());
		if(tmpPage != null){
			tmpPage.setStatusCode(request.getPage().getStatusCode());
			if(tmpPage.getStatusCode() != HttpClient.HTTP_OK)
				tmpPage.decRetryTimes();
			tmpPage.setIsVisited(true);
		}
	}
	
	public class ShortPage{
		private String mUrl;
		private int mStatusCode;
		private boolean mIsVisited;
		private int mRetryTimes;
		public ShortPage(String mUrl, int mStatusCode, boolean mIsVisited) {
			super();
			this.mUrl = mUrl;
			this.mStatusCode = mStatusCode;
			this.mIsVisited = mIsVisited;
			this.mRetryTimes = mRetry; //only have DEFAULT_RETRYTIMES times to retry
		}
		public String getUrl() {
			return mUrl;
		}
		public int getStatusCode() {
			return mStatusCode;
		}
		public void setStatusCode(int mStatusCode) {
			this.mStatusCode = mStatusCode;
		}
		public boolean isVisited() {
			return mIsVisited;
		}
		public void setIsVisited(boolean mIsVisited) {
			this.mIsVisited = mIsVisited;
		}
		public void decRetryTimes(){
			if(this.mRetryTimes >= 0)
				this.mRetryTimes--;
		}
		public boolean isCanRetry(){
			return this.mRetryTimes>=0?true : false;
		}
		
	}
	
}
