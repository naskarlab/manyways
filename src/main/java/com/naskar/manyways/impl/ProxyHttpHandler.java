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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;

public class ProxyHttpHandler implements Handler {
	
	private String prefix;
	private String target;
	
	public ProxyHttpHandler prefix(String value) {
		this.prefix = value;
		return this;
	}

	public ProxyHttpHandler target(String value) {
		this.target = value;
		return this;
	}

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		HttpUriRequest request = newRequest(rewrite(req, prefix, target), req, res);
		
		copyRequestHeaders(req, request);
		
		HttpClientBuilder builder = HttpClientBuilder.create();
		CloseableHttpClient client = builder.build();
		
		try {
			handleResponse(client.execute(request), res);
		} finally {
			client.close();
		}
		
		/*
		proxyRequest.header(HttpHeader.VIA, "http/1.1 " + getViaHost());
		proxyRequest.header(HttpHeader.X_FORWARDED_FOR, clientRequest.getRemoteAddr());
        proxyRequest.header(HttpHeader.X_FORWARDED_PROTO, clientRequest.getScheme());
        proxyRequest.header(HttpHeader.X_FORWARDED_HOST, clientRequest.getHeader(HttpHeader.HOST.asString()));
        proxyRequest.header(HttpHeader.X_FORWARDED_SERVER, clientRequest.getLocalName());
		*/
		
	}

	private void handleResponse(CloseableHttpResponse response, HttpServletResponse res) throws IOException {
		try {
			response.getEntity().writeTo(res.getOutputStream());
		} finally {
			response.close();
		}
	}
	
	private void copyRequestHeaders(HttpServletRequest req, HttpRequest request) {
		Enumeration<String> names = req.getHeaderNames();
		for(; names.hasMoreElements(); ) {
			String name = names.nextElement();
			
			Enumeration<String> values = req.getHeaders(name);
			for(; values.hasMoreElements(); ) {
				request.addHeader(name, values.nextElement());
			}
		}
	}

	protected HttpUriRequest newRequest(String uri, HttpServletRequest req, HttpServletResponse res) {
		switch (req.getMethod()) {
			case "GET":
				return new HttpGet(uri);
			case "POST":
				return new HttpPost(uri);
			default:
				throw new RuntimeException("NotImplementedYet");
		}
	}
	
	private static String rewrite(HttpServletRequest request, String prefix, String proxyTo) {
		
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

		URI rewrittenURI = URI.create(uri.toString()).normalize();

		/*
		if (!validateDestination(rewrittenURI.getHost(), rewrittenURI.getPort())) {
			return null;
		}
		*/

		return rewrittenURI.toString();
	}
	
}
