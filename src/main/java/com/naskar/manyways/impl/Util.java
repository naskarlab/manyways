package com.naskar.manyways.impl;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

public abstract class Util {
	
	private Util() { }
	
	public static String rewrite(HttpServletRequest request, String prefix, String proxyTo) {
		
		String path = request.getRequestURI();

		StringBuilder uri = new StringBuilder(proxyTo);

		String rest = path.substring((request.getServletPath() + prefix).length());
		if(!rest.isEmpty()) {
			uri.append(rest);
		}

		String query = request.getQueryString();
		if (query != null) {
			String separator = "://";
			if (uri.indexOf("/", uri.indexOf(separator) + separator.length()) < 0)
				uri.append("/");
			uri.append("?").append(query);
		}

		return URI.create(uri.toString()).normalize().toString();
	}

}
