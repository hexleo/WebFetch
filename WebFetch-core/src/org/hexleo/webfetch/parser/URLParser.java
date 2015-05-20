package org.hexleo.webfetch.parser;

import java.util.Vector;

import org.hexleo.webfetch.Request;

/**
 * parse the url in html page
 * return the whole of page correct url form
 * provide syn method
 * each thread must has it own URLParser instance
 * @author hexleo
 *
 */
public interface URLParser {
	//blocking method
	public Vector<Request> parse(Request parentPage); 
}
