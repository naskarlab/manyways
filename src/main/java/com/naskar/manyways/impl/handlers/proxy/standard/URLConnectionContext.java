package com.naskar.manyways.impl.handlers.proxy.standard;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletResponse;

public class URLConnectionContext {

	private URL url;
	private HttpURLConnection connection;
	
	private HttpServletResponse response;
	
	private Consumer<URLConnectionContext> endHeaderResponse;
	private Consumer<URLConnectionContext> endBodyResponse;

	public URLConnectionContext(URL url, HttpURLConnection connection, HttpServletResponse response) {
		this.url = url;
		this.connection = connection;
		this.response = response;
	}

	public URL getUrl() {
		return url;
	}

	public HttpURLConnection getConnection() {
		return connection;
	}

	public HttpServletResponse getResponse() {
		return response;
	}
	
	public void setEndHeaderResponse(Consumer<URLConnectionContext> endHeaderResponse) {
		this.endHeaderResponse = endHeaderResponse;
	}
	
	public void setEndBodyResponse(Consumer<URLConnectionContext> endBodyResponse) {
		this.endBodyResponse = endBodyResponse;
	}
	
	public void fireEndHeaderResponse() {
		if(endHeaderResponse != null) {
			endHeaderResponse.accept(this);
		}		
	}

	public void fireEndBodyResponse() {
		if(endBodyResponse != null) {
			endBodyResponse.accept(this);
		}
	}

}
