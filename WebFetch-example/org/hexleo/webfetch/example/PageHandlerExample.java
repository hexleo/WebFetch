package org.hexleo.webfetch.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hexleo.webfetch.Page;
import org.hexleo.webfetch.WebFetch;
import org.hexleo.webfetch.handler.PageHandler;

/**
 * A example for WebFetch
 * 
 * This example implements a customize PageHandler.
 * When WebFetch finish download a page, we write in the file.
 * 
 * @author hexleo
 *
 */
public class PageHandlerExample {
	public static void main(String[] args) {
		PageHandler myPageHandler = new PageHandler(){
			/**
			 * the 'page' is what WebFetch found
			 * and it's what you need to handler in your way.
			 * For example, you can save the page in your database
			 * 
			 * When you implement this interface, you don't need to care about blocking or non-blocking,
			 * WebFetch runs PageHandler in parse thread which locate in parse thread pool.
			 * ps: you can't blocking it forever, when you do this, it will run out of parse thread pool's resources
			 */
			@Override
			public void finish(Page page) {
				//it's create an unique name for an unique URL
				String fileName = MD5(page.getUrl());
				if(fileName == null)
					return;
				try {
					//Write in the file system
					File file = new File("e:/webs/"+fileName+".txt");
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(page.getHtmlContent());
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void close() {
				//this method is leave for database
			}
		};
		
		
		WebFetch webFetch = new WebFetch();
		//register the your PageHandler to WebFetch
		webFetch.addBeginTask("http://www.oschina.net")
				.setPageHandler(myPageHandler)
				.start();
	}
	
	/**
	 * use md5 to named file
	 * @param str
	 * @return
	 */
	public static String MD5(String str){
		if(str == null)
			return null;
		StringBuilder sb = new StringBuilder();;
		byte[] buf = str.getBytes();
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(buf);
			byte[] tmp = md5.digest();
			for (byte b : tmp) {
				sb.append(Integer.toHexString(b & 0xff));
			}
		} catch (NoSuchAlgorithmException e) {
		}
	    return sb.toString();
	}
}
