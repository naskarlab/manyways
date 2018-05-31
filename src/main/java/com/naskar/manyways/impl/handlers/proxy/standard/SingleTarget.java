package com.naskar.manyways.impl.handlers.proxy.standard;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

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
	public FactoryContext create(HttpServletRequest req) throws Exception {
		URL url = new URL(Util.rewrite(req, prefix, target));
		return new FactoryContext(url, (HttpURLConnection) url.openConnection());
	}

}
