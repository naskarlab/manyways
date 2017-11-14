package com.naskar.manyways.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.manyways.config.Configurable;

public class ProxyHttpHandler implements Handler, Configurable {
	
	private String prefix;
	private String target;
	
	private HttpRequestFactory factory;
	
	public ProxyHttpHandler() {
		this.factory = new HttpRequestFactory();
	}
	
	public ProxyHttpHandler prefix(String value) {
		this.prefix = value;
		return this;
	}

	public ProxyHttpHandler target(String value) {
		this.target = value;
		return this;
	}
	
	@Override
	public void configureParameters(Map<String, Object> params) {
		prefix((String)params.get("prefix"));
		target((String)params.get("target"));
	}

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		HttpClientBuilder builder = HttpClientBuilder.create();
		CloseableHttpClient client = builder.build();
		
		try {
			handleResponse(client.execute(factory.create(req, prefix, target)), res);
		} finally {
			client.close();
		}
		
		chain.next();
	}

	protected void handleResponse(CloseableHttpResponse response, HttpServletResponse res) throws IOException {
		try {
			response.getEntity().writeTo(res.getOutputStream());
		} finally {
			response.close();
		}
	}
		
}
