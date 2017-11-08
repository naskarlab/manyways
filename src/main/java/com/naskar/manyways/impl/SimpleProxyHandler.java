package com.naskar.manyways.impl;

import java.io.IOException;
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

import com.naskar.manyways.ProxyHandler;
import com.naskar.manyways.TargetResolver;

public class SimpleProxyHandler implements ProxyHandler {
	
	private TargetResolver targetResolver;

	public SimpleProxyHandler(TargetResolver targetResolver) {
		this.targetResolver = targetResolver;
	}

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String target = targetResolver.resolve(req, res);
		if(target == null) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		HttpUriRequest request = newRequest(target, req, res);
		
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
	
}
