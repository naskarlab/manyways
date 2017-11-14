package com.naskar.manyways.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpRequestFactory {
	
	public HttpUriRequest create(HttpServletRequest req, String prefix, String target) throws Exception {
		
		HttpUriRequest request = newRequest(rewrite(req, prefix, target), req);
		
		copyRequestHeaders(req, request);
				
		/* TODO: proxy
		proxyRequest.header(HttpHeader.VIA, "http/1.1 " + getViaHost());
		proxyRequest.header(HttpHeader.X_FORWARDED_FOR, clientRequest.getRemoteAddr());
        proxyRequest.header(HttpHeader.X_FORWARDED_PROTO, clientRequest.getScheme());
        proxyRequest.header(HttpHeader.X_FORWARDED_HOST, clientRequest.getHeader(HttpHeader.HOST.asString()));
        proxyRequest.header(HttpHeader.X_FORWARDED_SERVER, clientRequest.getLocalName());
		*/
		
		return request;
	}

	protected void handleResponse(CloseableHttpResponse response, HttpServletResponse res) throws IOException {
		try {
			response.getEntity().writeTo(res.getOutputStream());
		} finally {
			response.close();
		}
	}
	
	protected void copyRequestHeaders(HttpServletRequest req, HttpRequest request) {
		Enumeration<String> names = req.getHeaderNames();
		for(;names.hasMoreElements();) {
			String name = names.nextElement();
			
			Enumeration<String> values = req.getHeaders(name);
			for(; values.hasMoreElements(); ) {
				request.addHeader(name, values.nextElement());
			}
		}
	}

	protected HttpUriRequest newRequest(String uri, HttpServletRequest req) {
		switch (req.getMethod()) {
			case "GET":
				return new HttpGet(uri);
			case "POST":
				return new HttpPost(uri);
			default:
				throw new RuntimeException("NotImplementedYet");
		}
	}
	
	private String rewrite(HttpServletRequest request, String prefix, String proxyTo) {
		
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
