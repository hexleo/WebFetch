package org.hexleo.webfetch.http;

import java.net.Proxy;

/**
 * create a new HttpClient
 * @author hexleo
 *
 */
public class HttpClientFactory {
	
	public static final int HC_DEFAULT = 0;
	private static int mConnTimeout = 8000; // connection timeout 8s
	private static int mReadTimeout = 10000; // read inputstream timeout 10s
	private static Proxy mProxy = null;
	
	public static HttpClient create(){
		return create(HC_DEFAULT);
	}
	
	public static HttpClient create(int type){
		HttpClient client = null;
		switch(type){
		case HC_DEFAULT:
			client = new HttpClientImpl(mConnTimeout , mReadTimeout , mProxy);
			break;
		}
		return client;
	}
	
	public static void setConnectTimeout(int timeout){
		mConnTimeout = timeout>0? timeout: 8000;
	}
	public static void setReadTimeout(int timeout){
		mReadTimeout = timeout>0? timeout: 10000;
	}
	public static void setProxy(Proxy proxy) {
		mProxy = proxy;
	}
}
