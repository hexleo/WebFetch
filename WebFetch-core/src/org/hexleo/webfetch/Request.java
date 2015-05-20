package org.hexleo.webfetch;

public class Request {
	public static final String GET = "GET";
	public static final String POST = "POST";
	private static final String DEFAULT_CHARSET="UTF-8"; 
	
	private String mProtocol;
	private String mHost;
	private String mUrl;
	private String mMethod;
	private String mCharset;
	private String mCookie;
	private Page mPage;
	//TODO private String mPostData; //for post
	private int mLayer; // the layer of the original page 
	
	private Request(String mUrl , Request parentRequest){ // to create sub page
		this(parentRequest.getProtocol() , parentRequest.getHost() , mUrl , parentRequest.getMethod() , parentRequest.getCharset() , parentRequest.getCookie());
		this.mLayer = parentRequest.getLayer() + 1;
	}
	
	public static Request createSub(String mUrl ,  Request parentRequest){
		//only sub page can use "./"pattern 
		if(mUrl == null || mUrl.length() == 0 || parentRequest == null)
			return null;
		mUrl = mUrl.trim();
		if(mUrl.indexOf("://") < 0 && mUrl.charAt(0) != '/'){ //match ./123.html  123.html .123.html(x)
			//test parentPage.url= http://abc.def/second/index.php  sub.url = ./test.php(.test.php)   =>  http://abc.def/second/test.php
			int tmpSpace=0;
			if(mUrl.length() >= 2 && mUrl.charAt(0) == '.'){ // match ./123.html 
				if(mUrl.charAt(1) != '/')
					return  null; //test: .abc.def  error form
				tmpSpace=2;
				
			}
			String pUrl = parentRequest.getUrl();
			int lastSlash = pUrl.lastIndexOf('/');
			if(lastSlash > 0){
				if(pUrl.charAt(lastSlash-1) == '/'){ //  parentPage.url = http://abc.def   sub.url = ./123.html or 123.html
					mUrl = pUrl + "/" + mUrl.substring(tmpSpace);
				}else{ 
					mUrl = pUrl.substring(0, lastSlash+1) + mUrl.substring(tmpSpace);  //parentPage.url = http://abc.def/second/ sub.url = ./123.html or 123.html 
				}
			}else{
				return null; //parent page url is error form
			}
		}
		return new Request(mUrl , parentRequest);
	}
	
	public Request(String mUrl){
		this(null,  null , mUrl , GET , DEFAULT_CHARSET , null);
	}

	
	//even mProtocol and mHost was set , mUrl is the only one true
	public Request(String mProtocol, String mHost , String mUrl , String mMethod , String mCharset ,String mCookie) {
		super();
		if(mUrl == null || mUrl.length() == 0) //mUrl must has value
			return;
		mUrl = mUrl.trim();
		int index1 = mUrl.indexOf("://");
		if(index1 >= 0){
			this.mProtocol = mUrl.substring(0, index1);
			int index2 = mUrl.indexOf('/', index1+3);
			if(index2 >= 0 )
				this.mHost = mUrl.substring(index1+3, index2); //test: http://abc.def/
			else
				this.mHost = mUrl.substring(index1+3); //test: http://abc.def
			//this mUrl is the true one, parameter mProtocol and mHost will be omitted
			this.mUrl = mUrl;
		}else{
			this.mProtocol = mProtocol == null ? "http" : mProtocol;
			if(mHost == null){
				int index2 = mUrl.indexOf('/');
				// the first char is '/' in mUrl
				if(index2 == 0){
					//error form for url
					int index3 = mUrl.indexOf('/', 1);
					if(index3 > 0)
						this.mHost = mUrl.substring(index2+1, index3); //test: mHost=null /abc.def/
					else
						this.mHost = mUrl.substring(index2+1, mUrl.length()); //test: mHost=null /abc.def
					this.mUrl = this.mProtocol + ":/" + mUrl; 
				}else if(index2 > 0 ){
					if(mUrl.charAt(0) == '.'){ 
						int index3 = mUrl.indexOf('/', index2+1);
						if(index3 > 0)
							this.mHost = mUrl.substring(index2+1, index3); //test: mHost=null ./abc.def/
						else
							this.mHost = mUrl.substring(index2+1, mUrl.length()); //test: mHost=null ./abc.def
						this.mUrl = this.mProtocol + "://" + mUrl.substring(index2+1);
					}else{
						this.mHost = mUrl.substring(0, index2); 
						this.mUrl = this.mProtocol + "://" + mUrl; //test: mHost=null abc.def/
					}
					
					
				}else{
					this.mHost = mUrl;
					//need to think the first char is '/' in the mUrl
					this.mUrl = this.mProtocol + "://" + mUrl; //test: mHost=null abc.def
				}
			}else{
				this.mHost = mHost;
				if(mUrl.charAt(0) == '/')
					this.mUrl = this.mProtocol + "://"+ this.mHost + mUrl; //test: mHost=hexleo  /abc.def
				else if(mUrl.charAt(0) == '.')
					this.mUrl = this.mProtocol + "://"+ this.mHost + mUrl.substring(1);//test: mHost=hexleo ./abc.def
				else
					this.mUrl = this.mProtocol + "://"+ this.mHost + "/" + mUrl; //test: mHost=hexleo  abc.def
			}
			
		}
		this.mMethod = mMethod==null? GET : mMethod; 
		this.mCharset = mCharset==null? DEFAULT_CHARSET : mCharset;
		this.mLayer = 0; //default layer number
		this.mCookie = mCookie;
	}

	public String getProtocol() {
		return mProtocol;
	}

	public void setProtocol(String mProtocol) {
		this.mProtocol = mProtocol;
	}

	public String getHost() {
		return mHost;
	}

	public void setHost(String mHost) {
		this.mHost = mHost;
	}

	public String getUrl() {
		return mUrl;
	}
	public String getMethod() {
		return mMethod;
	}

	public void setMethod(String mMethod) {
		this.mMethod = mMethod;
	}
	public String getCharset() {
		return mCharset;
	}
	public void setCharset(String mCharset) {
		this.mCharset = mCharset;
	}

	public int getLayer() {
		return mLayer;
	}

	public String getCookie() {
		return mCookie;
	}

	public void setCookie(String mCookie) {
		this.mCookie = mCookie;
	}

	public Page getPage() {
		return mPage;
	}

	public void setPage(Page mPage) {
		this.mPage = mPage;
	}
	
}
