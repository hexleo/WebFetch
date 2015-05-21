package org.hexleo.webfetch.util;

public class Log {
	private static final boolean DEBUG = false;
	public static void i(String tag, String msg){
		if(DEBUG)
			System.out.println("info \t TAG:"+tag+"\nMSG:("+msg+")");
	}
	
	public static void i(boolean debug , String tag, String msg){
		if(DEBUG && debug)
			i(tag,msg);
	}
	
	public static void e(String tag, String msg){
		System.out.println("error \t TAG:"+tag+"\nMSG:("+msg+")");
	}
	public static void e(boolean debug , String tag, String msg){
		if(DEBUG && debug)
			e(tag,msg);
	}

}
