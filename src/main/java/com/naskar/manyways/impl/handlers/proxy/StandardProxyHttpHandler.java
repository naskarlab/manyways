package com.naskar.manyways.impl.handlers.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.manyways.config.Configurable;
import com.naskar.manyways.impl.HttpRequestFactory;

public class StandardProxyHttpHandler implements Handler, Configurable {

	private String prefix;
	private String target;

	public StandardProxyHttpHandler prefix(String value) {
		this.prefix = value;
		return this;
	}

	public StandardProxyHttpHandler target(String value) {
		this.target = value;
		return this;
	}

	@Override
	public void configureParameters(Map<String, Object> params) {
		prefix((String) params.get("prefix"));
		target((String) params.get("target"));
	}

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {

		URL url = new URL(HttpRequestFactory.rewrite(req, prefix, target));
		HttpURLConnection con = (HttpURLConnection) url.openConnection();

		con.setDoInput(true);
		con.setDoOutput(true);
		
		// TODO: timeout
		con.setConnectTimeout(10000);
		con.setReadTimeout(10000);

		con.setRequestMethod(req.getMethod());

		copyRequestHeaders(req, con);
		copyRequestBody(req, con);

		res.setStatus(con.getResponseCode());

		copyResponseHeader(con, res);
		copyResponseBody(con, res);

		con.disconnect();

		chain.next();
	}
	
	private void copyRequestHeaders(HttpServletRequest req, HttpURLConnection con) {
		Enumeration<String> headers = req.getHeaderNames();
		while (headers.hasMoreElements()) {
			String name = headers.nextElement();

			Enumeration<String> values = req.getHeaders(headers.nextElement());
			while (values.hasMoreElements()) {
				con.addRequestProperty(name, values.nextElement());
			}

		}
	}
	
	private void copyRequestBody(HttpServletRequest req, HttpURLConnection con) throws IOException {
		copy(req.getInputStream(), con.getOutputStream());
	}

	private void copyResponseHeader(HttpURLConnection con, HttpServletResponse res) {
		for (Map.Entry<String, List<String>> e : con.getHeaderFields().entrySet()) {
			for (String v : e.getValue()) {
				res.addHeader(e.getKey(), v);
			}
		}
	}

	private void copyResponseBody(HttpURLConnection con, HttpServletResponse res) throws IOException {
		copy(con.getInputStream(), res.getOutputStream());
	}

	private void copy(final InputStream in, final OutputStream out) throws IOException {
		final byte[] b = new byte[8192];
		for (int r; (r = in.read(b)) != -1;) {
			out.write(b, 0, r);
			out.flush();
		}
	}
}
