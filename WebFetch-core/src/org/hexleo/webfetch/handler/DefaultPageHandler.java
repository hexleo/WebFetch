package org.hexleo.webfetch.handler;

import org.hexleo.webfetch.Page;

/**
 * memory based database
 * @author hexleo
 *
 */
public class DefaultPageHandler implements PageHandler {
	private static final String TAG = DefaultPageHandler.class.toString();
	
	@Override
	public void finish(Page page) {
		if(page == null)
			return;
		System.out.println("TAG:"+ TAG+"\tfinishPage \turl:"+page.getUrl()+"\tstatusCode:"+page.getStatusCode()
				+"\tcontent:"+page.getHtmlContent());
	}

	@Override
	public void close() {
	}
	
}
