package com.trx.yanr;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.mail.Header;
import javax.mail.MessagingException;

import com.sun.mail.dsn.MessageHeaders;

public class NNTPMessageHeader {

	private HashMap<String, String> headers = new HashMap<String, String>();
	
	// return nntpHeader from text string
    public static NNTPMessageHeader cvrtTxt (String headerText) throws MessagingException {
        NNTPMessageHeader nntpHeader = new NNTPMessageHeader ();
        InputStream stream = new ByteArrayInputStream(headerText.getBytes(StandardCharsets.UTF_8));
        MessageHeaders headers = new MessageHeaders(stream);
        Enumeration <Header> en = headers.getAllHeaders();
        while (en.hasMoreElements()) {
            Header h = (Header) en.nextElement();
            nntpHeader.setHeader(h.getName(), h.getValue()); // set all headers of one single message 
        }
        return nntpHeader;        
    }
	
	public String getFrom() {
		return getHeader("From");
	}
	
	public String getSubject () {
		return getHeader("Subject");
	}
	
    public String getDate () {
        return getHeader("Date");
    }
    
    public String getContentType() {
        return getHeader ("Content-Type");
    }
    
	public String getHeader (String Key) {
		if (headers.containsKey(Key)) {
			return headers.get(Key);
		}
		return "";
	}
	
	public void setHeader(String Key, String Value) {
		headers.put(Key, Value);
	}

    public String getAllHeaderString () {
        String result = "";        
        for (Entry <String, String> header : headers.entrySet ()) {
            result += header.getKey () + ":" + header.getValue () + "\r\r\n";
        }
        return result;
    }


}
