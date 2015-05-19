package org.hexleo.webfetch.schedule;

import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Pattern;

import org.hexleo.webfetch.db.PageHandler;
import org.hexleo.webfetch.db.HashtableMemoryDB;
import org.hexleo.webfetch.download.Request;
import org.hexleo.webfetch.download.URLDownloader;
import org.hexleo.webfetch.http.HttpClient;
import org.hexleo.webfetch.parser.URLParser;
import org.hexleo.webfetch.parser.WebFetchParser;
import org.hexleo.webfetch.util.Log;

/**
 * task schedule
 * new task queue
 * finish pages task threads queue
 * single pattern
 * @author Administrator
 *
 */
public class TaskSchedule {
	private static final String TAG = TaskSchedule.class.toString();
	//private static final long DB_PAGE_EXPIRE = 3*24*60*60*1000;//a page expire in 3 days  
	private static final boolean DEBUG = false;
	private static int PARSE_TASK_THREAD_SIZE = 5;
	//private static TaskSchedule mTaskSchedule;
	//url downloader
	private URLDownloader mURLDownloader;
	//the pattern of match
	private Vector<String> mUrlPattern;
	//the max page layer number if -1 NaN
	private int mMaxLayler;
	//the page wait for parse
	private LinkedList<Request> mFinishRequestQueue;
	private URLParserThread[] mURLParserThreads;
	//visited manager
	private VisitedURLManager mVisitedURL;
	//db
	private PageHandler mPageHandler;
	//parser
	private URLParser mURLParser;
	//private boolean mIsClosed;
	private boolean mIsStart;

	//---temp test param
	//private int tempCount = 0;
	
	public TaskSchedule(){
		this.mPageHandler = HashtableMemoryDB.getInstance(); // db default implement
		this.mURLParser = new WebFetchParser();// default parser
		this.mFinishRequestQueue = new LinkedList<Request>();
		this.mVisitedURL = new VisitedURLManager();
		this.mIsStart = false;
		this.mUrlPattern = new Vector<String>();
		this.mURLDownloader = new URLDownloader(this);
		this.mMaxLayler = -1;
	}
	
	
	public void setMaxPageLayer(int maxLayler){
		if(!mIsStart)
			this.mMaxLayler = maxLayler;
	}
	
	public void setPageHandler(PageHandler mPageHandler){
		if(!mIsStart && mPageHandler != null)
			this.mPageHandler = mPageHandler;
	}
	public void setURLParser(URLParser mURLParser){
		if(!mIsStart)
			this.mURLParser = mURLParser;
	}
	public void setMaxTaskSize(int maxTaskSize){
		if(this.mURLDownloader != null)
			this.mURLDownloader.setMaxTaskSize(maxTaskSize);
	}
	public void setThreadSize(int threadSize){
		if(this.mURLDownloader != null)
			this.mURLDownloader.setTaskThreadSize(threadSize);
	}
	public void setRetryTimes(int times){
		if(!mIsStart)
			mVisitedURL.setRetryTimes(times);
	}
	
	public void start(){
		if(mIsStart)
			return;
		if(this.mURLDownloader != null)
			this.mURLDownloader.start();
		this.mIsStart = true;
		//mIsStart is one of the parser thread switch
		mURLParserThreads = new URLParserThread[PARSE_TASK_THREAD_SIZE];
		for(int i=0 ; i<PARSE_TASK_THREAD_SIZE ; i++){
			mURLParserThreads[i] = new URLParserThread(mURLParser);
			mURLParserThreads[i].start();
		}
	}
	public void close(){
		if(!mIsStart)
			return;
		if(mURLParserThreads != null){
			for(int i=0 ; i<PARSE_TASK_THREAD_SIZE ; i++)
				mURLParserThreads[i].interrupt();
		}
		if(mPageHandler != null)
			mPageHandler.close();
		if(mURLDownloader != null)
			mURLDownloader.close();
		mIsStart = false;
	}
	/**
	 * add match rule (regular pattern)
	 * @param pattern
	 */
	public void addMatchRule(String pattern){
		mUrlPattern.add(pattern);
	}
	
	/**
	 * 1 match the pattern
	 * 2 never run before (status = 0)
	 * 3 the task is timeout in DB
	 * @param originReq
	 * @return
	 */
	private Vector<Request> urlFilter(Vector<Request> originReq){
		Vector<Request> taskReq = new Vector<Request>();
		if(originReq == null)
			return taskReq;
		VisitedURLManager.ShortPage shortPage;
		boolean patternMatch;
		for(Request req : originReq){
			patternMatch = true;
			//test layer number
			if(mMaxLayler >= 0 && req.getLayer() > mMaxLayler)
				continue;
			//pattern match
			for(String pattern : mUrlPattern)
				if(!Pattern.matches(pattern, req.getUrl())){
					patternMatch = false;
					break;
				}
			if(!patternMatch)
				continue;
			//visited match
			shortPage = mVisitedURL.find(req.getUrl());
			if(shortPage == null){
				taskReq.add(req); //add new page
				mVisitedURL.add(req); // for visited check
			}else if(shortPage.isVisited() 
						&& shortPage.getStatusCode()!= HttpClient.HTTP_OK
						&& shortPage.isCanRetry()){
				taskReq.add(req); //page needs update
				Log.i(DEBUG , TAG, "retry url:"+shortPage.getUrl()+" statusCode:"+shortPage.getStatusCode());
			}
		}
		
		return taskReq;
	}
	
	/**
	 * add new request to URLDownloader
	 * when add a new task, it start immediately
	 * addRequest will be called by different threads
	 */
	public void addRequest(Vector<Request> newReqs){
		if(!mIsStart)
			return;
		for(Request req : newReqs)
			Log.i(DEBUG , TAG, "addNewTask url:"+req.getUrl());
		Vector<Request> rightPages = urlFilter(newReqs);
		if(mURLDownloader != null)
			mURLDownloader.addRequest(rightPages);
	}
	
	
	/**
	 * when URLDownloader is finished, it will parse the html to get more URL
	 */
	public boolean finishRequest(Request finishReq){
		if(!mIsStart || finishReq == null || finishReq.getPage() == null)
			return false;
		synchronized (mFinishRequestQueue) {
			mFinishRequestQueue.add(finishReq);
			mFinishRequestQueue.notifyAll();
			Log.i(DEBUG , TAG, "finishTask url:"+finishReq.getUrl()+" statusCode:"+finishReq.getPage().getStatusCode()
					+" content:"+finishReq.getPage().getHtmlContent());
		}
		return true;
	}
	
	/**
	 * When a page was finished and status is OK (status=200) must do the things below
	 * 1 store in database 
	 * 2 parse the page
	 */
	private class URLParserThread extends Thread{
		private URLParser mParser;
		
		public URLParserThread(URLParser mParser){
			 this.mParser = mParser;
		}
		
		public void run(){
			try {
				Request request;
				while(mIsStart){
					synchronized (mFinishRequestQueue) {
						while(mFinishRequestQueue.isEmpty()){
							mFinishRequestQueue.wait();
						}
						request = mFinishRequestQueue.removeFirst();
					}
					mVisitedURL.update(request);
					if(request.getPage().getStatusCode() == HttpClient.HTTP_OK){
						if(mPageHandler != null)
							mPageHandler.finish(request.getPage());
						if(mParser != null)
							addRequest(mParser.parse(request)); //a blocking method running in this Thread, and add to new task queue manage by TaskSchedule
					}else{
						Vector<Request> retryPage = new Vector<Request>();
						retryPage.add(request);
						addRequest(retryPage);
					}
				}
			} catch (InterruptedException e) {
			}
			
		}
	}

}
