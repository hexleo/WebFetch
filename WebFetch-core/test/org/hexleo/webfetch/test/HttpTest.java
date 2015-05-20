package org.hexleo.webfetch.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import org.hexleo.webfetch.Page;
import org.hexleo.webfetch.Request;
import org.hexleo.webfetch.WebFetch;
import org.hexleo.webfetch.download.URLDownloader;
import org.hexleo.webfetch.handler.DefaultPageHandler;
import org.hexleo.webfetch.http.HttpClient;
import org.hexleo.webfetch.http.HttpClientFactory;
import org.hexleo.webfetch.parser.URLParser;
import org.hexleo.webfetch.parser.WebFetchParser;
import org.hexleo.webfetch.schedule.TaskSchedule;
import org.hexleo.webfetch.util.Log;

//http package test
public class HttpTest {
	private static final String TAG = HttpTest.class.toString();
	public static void main(String[] args){
		HttpTest httpTest = new HttpTest();
		//httpTest.TestHttpClient();
		//httpTest.TestURLDownloader();
		//httpTest.TestPageEntity();
		//httpTest.TestWebFetchParser();
		//httpTest.TestTaskSchedule();
		httpTest.TestWebFetch();
	}
	
	public void TestHttpClient(){
		HttpClient client = HttpClientFactory.create();
		Request page = new Request(null, null , "http://abc.def" , Request.GET , null , null);
		boolean status = client.getPage(page);
		if(status){
			Log.i(TAG, page.getPage().getHtmlContent());
		}else{
			Log.i(TAG, "fail");
		}
	}
	
	public void TestURLDownloader(){
		int taskSize = 1;
		Vector<Request> pages = new Vector<Request>();
		
		URLDownloader urlDownloader = new URLDownloader(new TaskSchedule());
		
		for(int i=0 ; i<taskSize ; i++){
			pages.add(new Request(null, null , "http://localhost" , Request.GET , null , null));
		}
		urlDownloader.addRequest(pages);
		
		//Log.i(TAG, "TestURLDownloader close");
		//urlDownloader.close();
		//TaskSchedule.getInstance().close();
	}
	
	public void TestPageEntity(){
		Request page = new Request(null, null , "http://abc.def" , Request.GET , null , null);
		System.out.println("parent\t" + page.getProtocol() +"\t" + page.getHost() + "\t" + page.getUrl());
		page = Request.createSub("https://c/123.html", page);
		if(page != null)
			System.out.println("sub\t" + page.getProtocol() +"\t" + page.getHost() + "\t" + page.getUrl());
		else
			System.out.println("sub page is null");
	}
	
	
	
	public void TestWebFetchParser(){
		StringBuilder sb = new StringBuilder();
		try {
			Scanner scanner = new Scanner(new File("E:/temp/tempHtml.html"));
			while(scanner.hasNext()){
				sb.append(scanner.next());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Request page=new Request(null, null , "http://localhost" , Request.GET , null , null);
		Page p = new Page();
		p.setHtmlContent(sb.toString());
		page.setPage(p);
		URLParser parser = new WebFetchParser();
		Vector<Request> pages = parser.parse(page);
		for(Request r : pages)
			Log.i(TAG, "TestWebFetchParser url:"+r.getUrl());
	}
	
	public void TestTaskSchedule(){
		//TaskSchedule schedule = TaskSchedule.getInstance();
		TaskSchedule schedule = new TaskSchedule();
		schedule.addMatchRule("http://localhost.*");
		Request page1 = new Request(null, null , "http://localhost" , Request.GET , null , null);
		Vector<Request> pages = new Vector<Request>();
		pages.add(page1);
		schedule.addRequest(pages);
		try {
			while(true){
				Thread.sleep(3000);
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		schedule.close();
		
	}
	
	public void TestWebFetch(){
		WebFetch wf = new WebFetch();
		wf.addRule(".*")
			//.addBeginTask("http://localhost/webfetch/a.html" , Request.GET , "UTF-8")
			.addBeginTask(new Request("http://localhost/webfetch/a.html"))
			.setMaxPageLayer(-1)
			.setConnectionTimeout(10*1000)
			.setReadTimeout(20*1000)
			.setThreadSize(2)
			.setMaxTaskSize(-1)
			.setPageHandler(new DefaultPageHandler())
			.setRetryTimes(0)
			.start();
		try {
			while(true){
				Thread.sleep(3*1000);
				//Log.i(TAG, "TestWebFetch \tstatusCode:"+page.getStatusCode()
				//		+"\turl:"+page.getUrl() +" \tcontent:"+page.getHtmlContent());
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		wf.close();
		
	}
	
	
}
