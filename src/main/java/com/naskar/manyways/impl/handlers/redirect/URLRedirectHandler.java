package com.naskar.manyways.impl.handlers.redirect;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.manyways.config.Configurable;
import com.naskar.manyways.impl.Util;

public class URLRedirectHandler implements Handler, Configurable {
	
	private String path;
	private String target;
	
	public URLRedirectHandler path(String value) {
		this.path = value;
		return this;
	}

	public URLRedirectHandler target(String value) {
		this.target = value;
		return this;
	}
	
	@Override
	public void configureParameters(Map<String, Object> params) {
		path((String)params.get("path"));
		target((String)params.get("target"));
	}
	
	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
        
        String uri = req.getRequestURI();
        if(uri != null && uri.startsWith(path)) {
			res.sendRedirect(Util.rewrite(req, path, target));
			
        } else {
        	chain.next();
        	
        }
	}

}
