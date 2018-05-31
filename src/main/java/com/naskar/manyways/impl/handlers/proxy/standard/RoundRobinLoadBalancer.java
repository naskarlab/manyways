package com.naskar.manyways.impl.handlers.proxy.standard;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.impl.Util;

public class RoundRobinLoadBalancer implements HttpURLConnectionFactory {
	
	private String prefix;
	private List<String> targets;
	private AtomicInteger index;
	
	public RoundRobinLoadBalancer() {
		this.targets = new ArrayList<String>();
		this.index = new AtomicInteger(-1);
	}
	
	public RoundRobinLoadBalancer prefix(String value) {
		this.prefix = value;
		return this;
	}

	public RoundRobinLoadBalancer addTarget(String value) {
		this.targets.add(value);
		return this;
	}
	
	@Override
	public URLConnectionContext create(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		URL url = null;
		HttpURLConnection connection = null;
		boolean found = false;
		Exception lastException = null;
		
		for(int count = 0; !found && count < targets.size(); count++) {
			
			int targetIndex = index.updateAndGet((i) -> { ++i; return i % targets.size(); });
			String target = targets.get(targetIndex);
			
			url = new URL(Util.rewrite(req, prefix, target));
			try {
				url.openStream().close();
				connection = (HttpURLConnection) url.openConnection();
				found = true;
			} catch(IOException e) {
				// TODO: logger
				e.printStackTrace();
				lastException = e;
			}
			
		}
		
		if(!found) {
			throw lastException;
		}
		
		return new URLConnectionContext(url, connection);
	}
	
}
