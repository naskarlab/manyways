package com.naskar.manyways.impl.handlers.auth;

import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.manyways.config.Configurable;
import com.naskar.manyways.impl.HttpRequestFactory;

public class AuthCheckHandler implements Handler, Configurable {
	
	private String url;
	
	private HttpRequestFactory factory;
	private Gson gson;
	private Type mapType;
	
	public AuthCheckHandler() {
		this.factory = new HttpRequestFactory();
		this.gson = new GsonBuilder().create();
		this.mapType = new TypeToken<Map<String, Object>>() { }.getType();
	}
	
	public AuthCheckHandler url(String value) {
		this.url = value;
		return this;
	}

	@Override
	public void configureParameters(Map<String, Object> params) {
		url((String)params.get("url"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String json = redirectRequest(chain, req);
		
		Map<String, Object> map = gson.fromJson(json, mapType);
		Map<String, Object> valid = (Map<String, Object>) map.get("valid");
		if(valid != null) {
			
			Map<String, Object> header = (Map<String, Object>) valid.get("header");
			
			if(header != null) {
				chain.getHeaderMap().putAll(header);
			}
			
			chain.next();
			
		} else {
			
			String redirect = null;
					
			Map<String, Object> invalid = (Map<String, Object>) map.get("invalid");
			if(invalid != null) {
				redirect = (String) invalid.get("redirect");
			} 
			
			if(redirect != null) {
				res.sendRedirect(redirect);
				
			} else {
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
				
		}
		
	}

	private String redirectRequest(Chain chain, HttpServletRequest req) {
		
		String json = "{}";
		
		try {
			HttpClientBuilder builder = HttpClientBuilder.create();
			CloseableHttpClient client = builder.build();
						
			try {
				
				HttpUriRequest request = factory.create(chain, req, "", url);
				
				request.addHeader("X-Gateway-URL", req.getRequestURL().toString());
				
				json = EntityUtils.toString(client.execute(request).getEntity());
				
			} finally {
				client.close();
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		return json;
	}

}
