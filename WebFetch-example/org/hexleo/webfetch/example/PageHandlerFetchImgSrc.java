package org.hexleo.webfetch.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hexleo.webfetch.Page;
import org.hexleo.webfetch.Request;
import org.hexleo.webfetch.WebFetch;
import org.hexleo.webfetch.handler.PageHandler;

/**
 * A example to fetch image src url
 * @author hexleo
 *
 */
public class PageHandlerFetchImgSrc {
	
	
	public static void main(String[] args) {
		PageHandler myPageHandler = new PageHandler(){
			@Override
			public void finish(Page page) {
				new ParseImgUrl(page.getUrl() , page.getHtmlContent()).start();
			}

			@Override
			public void close() {
			}
		};
		
		
		WebFetch webFetch = new WebFetch();
		//register the your PageHandler to WebFetch
		webFetch.addBeginTask("http://www.oschina.net") 
				.setPageHandler(myPageHandler)
				.setConnectionTimeout(20*1000)
				.setReadTimeout(20*1000)
				.start();
	}
	
	
	private static class ImgTagUtil{
		private static final String BEGIN = "src=\"";
		private static final int BEGIN_LENGTH = BEGIN.length();
		private static final String END = "\"";

		public static String getImgSrc(String imgTag){
			int begin = imgTag.indexOf(BEGIN);
			if(begin >= 0 ){
				return imgTag.substring(begin+BEGIN_LENGTH , imgTag.indexOf(END, begin+BEGIN_LENGTH+1));
			}else{
				return null;
			}
			
		}
		
		public static void writeToFile(String path , String fileName , String Content){
			if(fileName == null)
				return;
			try {
				//Write in the file system
				File file = new File(path + "/" + fileName);
				if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(Content);
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class ParseImgUrl extends Thread{
		private static final String PREFIX = "mzu_";
		private static final String SUFFIX = ".html";
		private static final String STORE_PATH = "d:/temp/webfetch";
		//>=MAX_IMG_EACH_PAGE pictures will hold together be a page
		private static final int MAX_IMG_EACH_PAGE = 500;
		private static Integer sPageNo = new Integer(1);
		private static Vector<String> sImageVector = new Vector<String>(1000);
		private static Map<String , Integer> sHistoryImgSrc = new Hashtable<String , Integer>();
		private static Pattern sPattern = Pattern.compile("<img(.*?)>");
		
		private String mPageUrl;
		private String mHtmlContent;
		
		
		public ParseImgUrl(String pageUrl, String htmlContent) {
			this.mPageUrl = pageUrl;
			this.mHtmlContent = htmlContent;
			
		}
		
		@Override
		public void run() {
			parse();
		} 
		
		private void parse(){
			Request parentReq = new Request(mPageUrl);
			String imgSrc;
			Matcher m = sPattern.matcher(mHtmlContent);
			while(m.find()){
				imgSrc =ImgTagUtil.getImgSrc(m.group());
				if(imgSrc == null){
					continue;
				}
				if(!imgSrc.contains("http://")){
					Request req = Request.createSub(imgSrc, parentReq);
					if(req == null){
						continue;
					}
					imgSrc = req.getUrl();
					if(imgSrc == null){
						continue;
					}
				}
				if(sHistoryImgSrc.containsKey(imgSrc)){
					continue;
				}
				sImageVector.add(imgSrc);
				sHistoryImgSrc.put(imgSrc, sPageNo);
			}
			if(sImageVector.size() >= MAX_IMG_EACH_PAGE){
				StringBuilder sb = new StringBuilder();
				synchronized (sImageVector) {
					for(int i=0 ; i<sImageVector.size() ; i++){
						sb.append("<img src=\"");
						sb.append(sImageVector.get(i));
						sb.append("\" width=\"50px\" height=\"50px\"\\>");
					}
					sImageVector.clear();
				}
				ImgTagUtil.writeToFile(STORE_PATH , PREFIX + sPageNo + SUFFIX , sb.toString());
				sPageNo++;
			}
		}
		
		
		
		
	}
	
	
}
