package org.hexleo.webfetch.example;

import org.hexleo.webfetch.WebFetch;

/**
 * A example for WebFetch
 * 
 * It implements a default PageHandler.
 * It just uses System.out.print to display the html code in console
 * Because it also run well in Android
 * 
 * @author hexleo
 *
 */
public class SimpleExample {
	public static void main(String[] args) {
		//import webfetch_vx.x.x.jar to your project
		WebFetch webFetch = new WebFetch();
		//add the begin URL (must) and start
		//WebFetch supports http and https
		//after call start it will not stop until call close();
		//start() is not a blocking method
		webFetch.addBeginTask("http://www.oschina.net").start();
	}
}
