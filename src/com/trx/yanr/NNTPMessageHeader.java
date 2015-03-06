package com.trx.yanr;

import java.util.HashMap;

public class NNTPMessageHeader {

	private HashMap<String, String> header = new HashMap<String, String>();
	public String body;
	
	public String getFrom() {
		return getHeader("From");
	}
	
	public String getSubject () {
		return getHeader("Subject");
	}
	
	public String getHeader (String Key) {
		if (header.containsKey(Key)) {
			return header.get(Key);
		}
		return "";
	}
	
	public void setHeader(String Key, String Value) {
		header.put(Key, Value);
	}
}
