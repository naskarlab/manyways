package com.naskar.manyways.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.naskar.manyways.Handler;
import com.naskar.manyways.impl.handlers.AuthCheckHandler;
import com.naskar.manyways.impl.handlers.ProxyHttpHandler;

public class HandlersConfig {
	
	public List<Handler> create(List<Map<String, Object>> config) {
		List<Handler> handlers = new ArrayList<Handler>();
		
		if(config != null && !config.isEmpty()) {
			for(Map<String, Object> m : config) {
				handlers.add(createHandler(m));
			}
		}
		
		return handlers;
	}

	@SuppressWarnings("unchecked")
	private Handler createHandler(Map<String, Object> m) {
		
		// TODO: criar mapping handlers
		Handler h = null;
		
		switch((String)m.get("type")) {
			case "proxy_http":
				h = new ProxyHttpHandler();
				break;
			case "auth_check":
				h = new AuthCheckHandler();
				break;
			default:
				throw new RuntimeException("OperationNotSupportedException");
		}
		
		if(h instanceof Configurable) {
			((Configurable)h).configureParameters((Map<String, Object>)m.get("params"));
		}
		
		return h;
	}

}
