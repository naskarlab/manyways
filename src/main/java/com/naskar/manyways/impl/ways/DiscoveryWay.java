package com.naskar.manyways.impl.ways;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.naskar.manyways.Handler;
import com.naskar.manyways.Way;
import com.naskar.manyways.config.HandlersConfig;
import com.naskar.manyways.impl.HttpRequestFactory;

public class DiscoveryWay implements Way {
	
	private String path;
	private String url;
	
	private HttpRequestFactory factory;
	private Gson gson;
	private Type mapType;
	
	public DiscoveryWay() {
		this.factory = new HttpRequestFactory();
		this.gson = new GsonBuilder().create();
		this.mapType = new TypeToken<Map<String, Object>>() { }.getType();
	}
	
	public DiscoveryWay path(String value) {
		this.path = value;
		return this;
	}
	
	public DiscoveryWay url(String value) {
		this.url = value;
		return this;
	}

	@Override
	public String getPath() {
		return path;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Handler> resolveHandlers(HttpServletRequest req, HttpServletResponse res) {
		
		try {
			HttpClientBuilder builder = HttpClientBuilder.create();
			CloseableHttpClient client = builder.build();
			
			String json = null;
			try {
				json = handleResponse(client.execute(factory.create(req, "", url)));
			} finally {
				client.close();
			}
			
			Map<String, Object> map = gson.fromJson(json, mapType);
			List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("handlers");
			
			return new HandlersConfig().create(list);
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String handleResponse(CloseableHttpResponse response) throws IOException {
		try {
			return EntityUtils.toString(response.getEntity());
		} finally {
			response.close();
		}
	}

}
