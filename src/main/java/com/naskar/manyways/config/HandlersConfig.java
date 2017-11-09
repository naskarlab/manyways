package com.naskar.manyways.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.naskar.manyways.Handler;
import com.naskar.manyways.impl.ProxyHttpHandler;

public class HandlersConfig {
	
	public List<Handler> create(List<Map<String, String>> config) {
		List<Handler> handlers = new ArrayList<Handler>();
		
		for(Map<String, String> m : config) {
			handlers.add(createHandler(m));
		}
		
		return handlers;
	}

	private Handler createHandler(Map<String, String> m) {
		Handler h = null;
		
		switch(m.get("type")) {
			case "proxy_http":
				h = new ProxyHttpHandler();
				break;
			default:
				throw new RuntimeException("OperationNotSupportedException");
		}
		
		if(h instanceof Configurable) {
			((Configurable)h).configureParameters(m);
		}
		
		return h;
	}

}
