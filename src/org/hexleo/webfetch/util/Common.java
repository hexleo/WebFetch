package org.hexleo.webfetch.util;

import java.io.UnsupportedEncodingException;

public class Common {
	private static final String TAG = Common.class.toString();
	private static final boolean DEBUG = false;
	public static String changeCharset(String str, String oldCharset, String newCharset){
		try {
			if (str != null) 
				return new String(str.getBytes(oldCharset), newCharset);
		} catch (UnsupportedEncodingException e) {
			Log.e(DEBUG , TAG, "changeCharset UnsupportedEncodingException");
		}
		return null;
	}
}
