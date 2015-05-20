package org.hexleo.webfetch.parser;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hexleo.webfetch.Request;

public class WebFetchParser implements URLParser {

	@Override
	public Vector<Request> parse(Request finishReq) {
		if(finishReq == null)
			return null;
		Vector<Request> pages = new Vector<Request>();
		Pattern p = Pattern.compile("<\\s*a(.*?)href\\s*=\\s*[\'\"](.*?)[\'\"]");
		Matcher m = p.matcher(finishReq.getPage().getHtmlContent());
		Request subPage;
		while(m.find()){
			String url = getURLFromHref(m.group());
			subPage = Request.createSub(url, finishReq);
			if(subPage != null)
				pages.add(subPage);
		}
		return pages;
	}
	
	private String getURLFromHref(String href){
		if(href == null)
			return null;
		href = href.substring(href.indexOf("href")); //except <a 
		String url = null;
		char sQuote = '\''; //single quota
		char dQuote = '\"'; //double quota
		char p;
		char nop; //if p=' then nop=", prevent js make the href
		int indexStart = href.indexOf(dQuote);
		if(indexStart >= 0){
			p = dQuote;
			nop = sQuote;
		}else{
			indexStart = href.indexOf('\'');
			if(indexStart >= 0){
				p=sQuote;
				nop = dQuote;
			}else{
				p = dQuote; //never found ' or " , default "
				nop = sQuote;
			}
		}
		int indexEnd = href.indexOf(p, indexStart+1); // find the second p
		int indexJS = href.indexOf(nop, indexStart +1); // prevent for js
		if(indexStart >= 0 && indexEnd > indexStart && indexJS < 0){
			url = href.substring(indexStart+1, indexEnd);
		}
		if(url != null && Pattern.matches(".*?['\"()]+.*?|.*?javascript:.*?", url)){
			return null;
		}
		return url;
	}

}
