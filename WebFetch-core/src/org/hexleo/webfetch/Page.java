package org.hexleo.webfetch;

public class Page {
	private String mUrl;
	private String mContextType;
	private String mHtmlContent;
	private int mStatusCode; 
	private String mCharset;
	private long mFetchTime;
	public Page() {
		super();
		this.mStatusCode = 0;
	}
	public Page(String mUrl,String mContextType, String mHtmlContent, 
			long mFetchTime,int mStatusCode, String mCharset) {
		super();
		this.mUrl = mUrl;
		this.mContextType = mContextType;
		this.mHtmlContent = mHtmlContent;
		this.mFetchTime = mFetchTime;
		this.mStatusCode = mStatusCode;
		this.mCharset = mCharset;
	}
	
	public String getUrl() {
		return mUrl;
	}
	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	public String getContextType() {
		return mContextType;
	}
	public void setContextType(String mContextType) {
		this.mContextType = mContextType;
	}
	public String getHtmlContent() {
		return mHtmlContent;
	}
	public void setHtmlContent(String mContent) {
		this.mHtmlContent = mContent;
	}
	public long getFetchTime() {
		return mFetchTime;
	}
	public void setFetchTime(long mGetTime) {
		this.mFetchTime = mGetTime;
	}
	public int getStatusCode() {
		return mStatusCode;
	}
	public void setStatusCode(int mStatusCode) {
		this.mStatusCode = mStatusCode;
	}
	public String getCharset() {
		return mCharset;
	}
	public void setCharset(String mCharset) {
		this.mCharset = mCharset;
	}
	
	
	
}
