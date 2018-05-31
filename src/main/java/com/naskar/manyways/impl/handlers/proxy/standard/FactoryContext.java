package com.naskar.manyways.impl.handlers.proxy.standard;

import java.net.HttpURLConnection;
import java.net.URL;

public class FactoryContext {

	private URL url;
	private HttpURLConnection connection;

	public FactoryContext(URL url, HttpURLConnection connection) {
		this.url = url;
		this.connection = connection;
	}

	public URL getUrl() {
		return url;
	}

	public HttpURLConnection getConnection() {
		return connection;
	}

}
