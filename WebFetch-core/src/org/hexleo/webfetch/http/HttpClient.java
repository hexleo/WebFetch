package org.hexleo.webfetch.http;

import org.hexleo.webfetch.Request;

/**
 * blocking method get http page
 * each thread must has it own HttpClient instance
 * @author hexleo
 *
 */
public interface HttpClient {
	public static final int HTTP_OK = 200;
	//a blocking method
	//task thread manage fail request
	public boolean getPage(Request page);
}
