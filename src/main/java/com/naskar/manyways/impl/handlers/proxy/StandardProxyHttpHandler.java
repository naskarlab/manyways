package com.naskar.manyways.impl.handlers.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.manyways.impl.handlers.proxy.standard.HttpURLConnectionFactory;
import com.naskar.manyways.impl.handlers.proxy.standard.URLConnectionContext;

public class StandardProxyHttpHandler implements Handler {
	
	protected static final Set<String> noBodyMethodCopy = new HashSet<String>(Arrays.asList(
		"GET", "HEAD", "DELETE", "TRACE"
	));
	
	protected static final Set<String> hopByHopheaders = new HashSet<String>(Arrays.asList(
        "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization",
        "TE", "Trailers", "Transfer-Encoding", "Upgrade"
	));

	private String path;
	private HttpURLConnectionFactory factory;
	
	public StandardProxyHttpHandler factory(HttpURLConnectionFactory factory) {
		this.factory = factory;
		return this;
	}
	
	public StandardProxyHttpHandler path(String value) {
		this.path = value;
		return this;
	}

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		// TODO: trocar por way
		String uri = req.getRequestURI();
        if(uri != null && uri.startsWith(path)) {

			URLConnectionContext ctx = factory.create(req, res);
			HttpURLConnection con = ctx.getConnection();
			URL url = ctx.getUrl();
	
			// TODO: timeout
			con.setConnectTimeout(30 * 1000);
			con.setReadTimeout(30 * 1000);
	
			con.setRequestMethod(req.getMethod());
			debug("Method: " + req.getMethod());
	
			copyRequestHeaders(req, con);
			
			con.setRequestProperty("Host", url.getHost());
			debug("Host:" + url.getHost());
			
			copyRequestBody(req, con);
			
			res.setStatus(con.getResponseCode());
			debug("Status:" + con.getResponseCode());
			
			copyResponseHeader(con, res);
			ctx.fireEndHeaderResponse();
			
			copyResponseBody(con, res);
			ctx.fireEndBodyResponse();
			
        } else {
        	
        	chain.next();
        }
	}
	
	private void copyRequestHeaders(HttpServletRequest req, HttpURLConnection con) {
		Enumeration<String> headers = req.getHeaderNames();
		while (headers.hasMoreElements()) {
			String name = headers.nextElement();
			
			if(hopByHopheaders.contains(name)) {
				continue;
			}
			
			Enumeration<String> values = req.getHeaders(name);
			while (values.hasMoreElements()) {
				String value = values.nextElement();
				con.addRequestProperty(name, value);
				debug(name + ":" + value);
			}

		}
	}
	
	private void copyRequestBody(HttpServletRequest req, HttpURLConnection con) throws IOException {
		if(noBodyMethodCopy.contains(req.getMethod().toUpperCase())) {
			return;
		}
		
		con.setDoOutput(true);
		OutputStream out = con.getOutputStream();
		copy(req.getInputStream(), out);
		out.close();
	}

	private void copyResponseHeader(HttpURLConnection con, HttpServletResponse res) {
		for (Map.Entry<String, List<String>> e : con.getHeaderFields().entrySet()) {
			for (String v : e.getValue()) {
				res.addHeader(e.getKey(), v);
				debug(e.getKey() + ":" + v);
			}
		}
	}

	private void copyResponseBody(HttpURLConnection con, HttpServletResponse res) throws IOException {
		InputStream in = con.getInputStream();
		OutputStream out = res.getOutputStream();
		copy(in, out);
		in.close();
	}

	private void copy(final InputStream in, final OutputStream out) throws IOException {
		final byte[] b = new byte[8192];
		for (int r; (r = in.read(b)) != -1;) {
			out.write(b, 0, r);
		}
		out.flush();
	}
	
	private void debug(String msg) {
		//System.out.println(msg);
	}
}
