package com.naskar.manyways.impl.handlers.proxy.standard;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.impl.Util;

public class SingleTarget implements HttpURLConnectionFactory {
	
	private String prefix;
	private String target;
	
	public SingleTarget prefix(String value) {
		this.prefix = value;
		return this;
	}

	public SingleTarget target(String value) {
		this.target = value;
		return this;
	}
	
	@Override
	public URLConnectionContext create(HttpServletRequest req, HttpServletResponse res) throws Exception {
		URL url = new URL(Util.rewrite(req, prefix, target));
		return new URLConnectionContext(url, (HttpURLConnection) url.openConnection(), res);
	}

}
