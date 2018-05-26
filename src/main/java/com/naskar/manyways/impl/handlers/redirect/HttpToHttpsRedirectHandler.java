package com.naskar.manyways.impl.handlers.redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;

public class HttpToHttpsRedirectHandler implements Handler {

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {

		if (!req.isSecure()) {

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
