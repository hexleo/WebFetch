package org.hexleo.webfetch.handler;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.hexleo.webfetch.download.Page;

/**
 * memory based database
 * @author hexleo
 *
 */
public class HashtableMemoryDB implements PageHandler {
	
	private static HashtableMemoryDB mDB; 
	
	private Hashtable<String,Page> mPages;
	
	private HashtableMemoryDB() {
		mPages = new Hashtable<String , Page>();
	}
	
	public static synchronized HashtableMemoryDB getInstance(){
		if(mDB == null){
			mDB = new HashtableMemoryDB();
		}
		return mDB;
	}

	@Override
	public boolean finish(Page page) {
		if(page == null || page.getUrl() == null)
			return false;
		mPages.put(page.getUrl(), page);
		return true;
	}

	public Page select(String key) {
		return mPages.get(key);
	}

	public boolean update(Page page) {
		if(page == null || page.getUrl() == null)
			return false;
		mPages.put(page.getUrl(), page); //hashtable update code is the same as insert
		return true;
	}

	@Override
	public void close() {
		mPages.clear();
	}
	
	public Vector<Page> displayAll(){
		Vector<Page> pages = new Vector<Page>();
		
		Enumeration<Page> e = mPages.elements();
		while(e.hasMoreElements()){
			pages.add(e.nextElement());
		}
		return pages;
	}
	
}
