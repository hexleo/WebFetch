/**
 * Copyright 2015 hexleo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hexleo.webfetch;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Vector;

import org.hexleo.webfetch.handler.DefaultPageHandler;
import org.hexleo.webfetch.handler.PageHandler;
import org.hexleo.webfetch.http.HttpClientFactory;
import org.hexleo.webfetch.parser.URLParser;
import org.hexleo.webfetch.parser.WebFetchParser;
import org.hexleo.webfetch.schedule.TaskSchedule;

public class WebFetch {
	private TaskSchedule mTaskSchedule;
	private Vector<Request> mBeginTask;
	private boolean mIsStart; // this switch is ensure the when WebFetch is processing web page will not change the rule
	private PageHandler mPageHandler;
	private URLParser mURLParser;
	public WebFetch() {
		super();
		mTaskSchedule = new TaskSchedule();
		mBeginTask  = new Vector<Request>();
		mIsStart = false;
	}
	
	public WebFetch addRule(String pattern){
		if(!mIsStart)
			mTaskSchedule.addMatchRule(pattern);
		return this;
	}
	
	public WebFetch addBeginTask(String url){
		return addBeginTask(null, null, url, Request.GET , null , null);
	}
	
	public WebFetch addBeginTask(String url , String method){
		return addBeginTask(null, null, url, method, null, null);
	}
	
	public WebFetch addBeginTask(String url, String method , String charset){
		return addBeginTask(null, null, url, method, charset, null);
	}
	
	public WebFetch addBeginTask(String url , String method ,String charset , String cookie){
		return addBeginTask(null, null, url, method, charset , cookie);
	}
	
	public WebFetch addBeginTask(String proto, String host , String url , String method , String charset , String cookie){
		if(!mIsStart && url != null)
			mBeginTask.add(new Request(proto, host,  url, method , charset, cookie));
		return this;
	}
	
	public WebFetch addBeginTask(Request request){
		if(!mIsStart && request != null)
			mBeginTask.add(request);
		return this;
	}
	public WebFetch addBeginTask(Request[] requests){
		if(!mIsStart && requests != null)
			for(Request req : requests)
				mBeginTask.add(req);
		return this;
	}
	
	/**
	 * the first layer is max=0 
	 * @param max
	 */
	public WebFetch setMaxPageLayer(int max){
		if(!mIsStart)
			mTaskSchedule.setMaxPageLayer(max);
		return this;
	}
	
	/**
	 * it will fetch MaxTaskSize webs, and only StatusCode=200 it will call db finish function 
	 * @param max
	 * @return
	 */
	public WebFetch setMaxTaskSize(int max){
		if(!mIsStart)
			mTaskSchedule.setMaxTaskSize(max);
		return this;
	}
	
	public WebFetch setThreadSize(int max){
		if(!mIsStart)
			mTaskSchedule.setThreadSize(max);
		return this;
	}
	
	/**
	 * @param timeout millisce
	 */
	public WebFetch setConnectionTimeout(int timeout){
		if(!mIsStart)
			HttpClientFactory.setConnectTimeout(timeout);
		return this;
	}
	public WebFetch setReadTimeout(int timeout){
		if(!mIsStart)
			HttpClientFactory.setReadTimeout(timeout);
		return this;
	}
	
	public WebFetch setProxy(String host , int port){
		return setProxy(host , port , Proxy.Type.HTTP); // default HTTP
	}
	public WebFetch setProxy(String host , int port , Proxy.Type type){
		if(!mIsStart)
			HttpClientFactory.setProxy(new Proxy(type , new InetSocketAddress(host , port)));
		return this;
	}
	
	public WebFetch setPageHandler(PageHandler pageHandler){
		if(!mIsStart)
			this.mPageHandler = pageHandler;
		return this;
	}
	public WebFetch setURLParser(URLParser parser){
		if(!mIsStart)
			this.mURLParser = parser;
		return this;
	}
	public WebFetch setRetryTimes(int times){
		if(!mIsStart)
			mTaskSchedule.setRetryTimes(times);
		return this;
	}
	public void start(){
		if(!mIsStart && mBeginTask.size() > 0){
			mIsStart = true;
			if(mPageHandler != null)
				mTaskSchedule.setPageHandler(mPageHandler);
			else //default implement
				mTaskSchedule.setPageHandler(new DefaultPageHandler());
			if(mURLParser != null)
				mTaskSchedule.setURLParser(mURLParser);
			else
				mTaskSchedule.setURLParser(new WebFetchParser());
			mTaskSchedule.start();
			mTaskSchedule.addRequest(mBeginTask);
		}
	}
	
	public void close(){
		if(mIsStart)
			mTaskSchedule.close();
		mIsStart = false;
	}

}
