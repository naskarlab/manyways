package com.naskar.manyways.impl.handlers.redirect;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.manyways.config.Configurable;

public class HttpToHttpsRedirectHandler implements Handler, Configurable {
	
	private String path;
	
	public HttpToHttpsRedirectHandler path(String value) {
		this.path = value;
		return this;
	}
	
	@Override
	public void configureParameters(Map<String, Object> params) {
		path((String)params.get("path"));
	}

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {

		String uri = req.getPathInfo();
        if(uri != null && uri.startsWith(path) && !req.isSecure()) {

			StringBuilder newUrl = new StringBuilder("https://");
			newUrl.append(req.getServerName());
			if (req.getRequestURI() != null) {
				newUrl.append(req.getRequestURI());
			}
			if (req.getQueryString() != null) {
				newUrl.append("?").append(req.getQueryString());
			}

			res.sendRedirect(newUrl.toString());
		} else {
			chain.next();
		}
	}
}
