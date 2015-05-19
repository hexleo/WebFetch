package org.hexleo.webfetch.handler;


import org.hexleo.webfetch.download.Page;


/**
 * who implement must block method, but it will be called by different threads
 * it must be thread safe
 * @author hexleo
 *
 */
public interface PageHandler {
	
	//insert to db
	public boolean finish(Page page);
	public void close();
	
}
