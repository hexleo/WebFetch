package org.hexleo.webfetch.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.hexleo.webfetch.Page;
import org.hexleo.webfetch.Request;
import org.hexleo.webfetch.util.Log;

/**
 * HttpClient default implement
 * depend: java 1.6 api
 * @author hexleo
 *
 */
public class HttpClientImpl implements HttpClient {
	private static final String TAG = HttpClientImpl.class.toString();
	private static final boolean DEBUG = false;
	private static final int CONN_TIMEOUT = 8000; // connection timeout 8s
	private static final int READ_TIMEOUT = 10000; // read inputstream timeout 10s
	private static final String USER_AGENT = "webfetch";
	private Proxy mProxy;
	private int mConnTimeout;
	private int mReadTimeout;
	private String mUserAgent;
	public HttpClientImpl(){
		this.mConnTimeout = CONN_TIMEOUT;
		this.mReadTimeout = READ_TIMEOUT;
		this.mUserAgent = USER_AGENT;
	}
	public HttpClientImpl(int mConnTimeout, int mReadTimeout , String userAgent , Proxy mProxy) {
		super();
		this.mConnTimeout = mConnTimeout;
		this.mReadTimeout = mReadTimeout;
		this.mUserAgent = userAgent;
		this.mProxy = mProxy;
	}
	@Override
	public boolean getPage(Request request) {
		if(request == null)
			return false;
		HttpURLConnection connection;
		BufferedReader reader;
		StringBuilder pageContent;
		boolean status = false;
		try {
			URL url = new URL(request.getUrl());
			if(mProxy == null)
				connection = (HttpURLConnection)url.openConnection();
			else
				connection = (HttpURLConnection)url.openConnection(mProxy);
			connection.setRequestMethod(request.getMethod());
			connection.setConnectTimeout(this.mConnTimeout);
			connection.setReadTimeout(this.mReadTimeout);
			connection.setRequestProperty("User-Agent", mUserAgent);
			if(request.getCookie() != null)
				connection.setRequestProperty("Cookie", request.getCookie());
			connection.connect();
			//insert to request
			Page page = new Page();
			page.setUrl(request.getUrl());
			page.setStatusCode(connection.getResponseCode());
			page.setContextType(connection.getContentType());
			String tmpCharset = connection.getContentEncoding();
			page.setCharset(tmpCharset==null?request.getCharset() : tmpCharset);
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), request.getCharset()));
			pageContent = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null){
				pageContent.append(line);
			}
			page.setHtmlContent(pageContent.toString());
			page.setFetchTime(System.currentTimeMillis());
			request.setPage(page);
			status = true;
			reader.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			Log.e(DEBUG , TAG, "URL MalformedURLException");
		} catch(SocketTimeoutException e){
			Log.e(DEBUG , TAG, "HttpURLConnection SocketTimeoutException");
		} catch (IOException e) {
			Log.e(DEBUG , TAG, "HttpURLConnection error "+e.getMessage());
		}
		return status;
	}
}
