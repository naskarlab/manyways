package com.naskar.manyways.impl.handlers.proxy.standard;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.impl.Holder;
import com.naskar.manyways.impl.Util;

public class RoundRobinLoadBalancer implements HttpURLConnectionFactory {
	
	private String prefix;
	private List<String> targets;
	
	private class StickyContext {
		AtomicInteger findIndex;
		String routeId;
		Integer oldIndex;
		Cookie route;
	}
	
	private boolean sticky;
	private String stickyId;
	
	private AtomicInteger index;
	
	public RoundRobinLoadBalancer() {
		this.targets = new ArrayList<String>();
		this.index = new AtomicInteger(-1);
		this.sticky = false;
		this.stickyId = "JSESSIONID";
	}
	
	public RoundRobinLoadBalancer prefix(String value) {
		this.prefix = value;
		return this;
	}

	public RoundRobinLoadBalancer addTarget(String value) {
		this.targets.add(value);
		return this;
	}
	
	public RoundRobinLoadBalancer sticky() {
		this.sticky = true;
		return this;
	}
	
	public RoundRobinLoadBalancer stickyId(String value) {
		this.stickyId = value;
		return this;
	}
	
	@Override
	public URLConnectionContext create(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		AtomicInteger findIndex = index;
		StickyContext stickyContext = findSticky(findIndex, req);
		findIndex = stickyContext.findIndex;
		
		URL url = null;
		HttpURLConnection connection = null;
		boolean found = false;
		Exception lastException = null;
		Holder<Integer> holderTargetIndex = new Holder<Integer>();
		
		for(int count = 0; !found && count < targets.size(); count++) {
			
			int targetIndex = findIndex.updateAndGet((i) -> { ++i; return i % targets.size(); });
			holderTargetIndex.value = targetIndex; 
			String target = targets.get(targetIndex);
			
			url = new URL(Util.rewrite(req, prefix, target));
			try {
				tryRequest(url);
				connection = (HttpURLConnection) url.openConnection();
				found = true;
				
				updateSticky(stickyContext, targetIndex, res);
				
			} catch(IOException e) {
				// TODO: logger
				e.printStackTrace();
				lastException = e;
			}
			
		}
		
		if(!found) {
			throw lastException;
		}
		
		URLConnectionContext ctx = new URLConnectionContext(url, connection, res);
		ctx.setEndHeaderResponse((c) -> updateStickyResponse(c, holderTargetIndex.value));
		
		return ctx;
	}

	private void tryRequest(URL url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.getInputStream();
		int status = con.getResponseCode();
		if(status > 500) {
			throw new IOException("Serviço indisponível.");
		}
	}

	private StickyContext findSticky(AtomicInteger findIndex, HttpServletRequest req) {
		StickyContext ctx = new StickyContext();
		ctx.findIndex = findIndex;
		
		if(!sticky) {
			return ctx;
		}
		
		Cookie[] cs = req.getCookies();
		if(cs == null || cs.length == 0) {
			return ctx;
		}
		
		Cookie session = findCookie(cs, stickyId);
		if(session == null) {
			return ctx;
		}
		
		ctx.routeId = "RID" + session.getValue();
		ctx.route = findCookie(cs, ctx.routeId);
		
		if(ctx.route != null) {
			try {
				Integer tmp = Integer.parseInt(ctx.route.getValue());
				if(tmp > -1 && tmp < targets.size()) {
					ctx.findIndex = new AtomicInteger(tmp - 1);
					ctx.oldIndex = tmp;
				}
			} catch(Exception e) {
				// @ignore
			}
			
		}
		
		return ctx;
	}
	
	private void updateSticky(StickyContext ctx, int targetIndex, HttpServletResponse res) {
		if(!sticky) {
			return;
		}
		
		if(ctx.routeId == null) {
			return;
		}
		
		if(ctx.route == null) {
			ctx.route = new Cookie(ctx.routeId, String.valueOf(targetIndex));
			res.addCookie(ctx.route);
			
		} else {
			if(ctx.oldIndex == null || ctx.oldIndex != targetIndex) {
				ctx.route.setValue(String.valueOf(targetIndex));
				res.addCookie(ctx.route);
			}
		}
	}
	
	private void updateStickyResponse(URLConnectionContext ctx, int targetIndex) {
		
		if(!sticky) {
			return;
		}
		
		String headerName = "Set-Cookie";
		String headerCookie = ctx.getResponse().getHeader(headerName);
		if(headerCookie == null || headerCookie.isEmpty()) {
			headerName = "Cookie";
			headerCookie = ctx.getResponse().getHeader("Cookie");
		}
		
		if(headerCookie == null) {
			return;
		}
		
		String value = extractCookieValue(headerCookie, stickyId);
		if(value == null || value.isEmpty()) {
			return;
		}
		
		String routeId = "RID" + value;
		String routeValue = extractCookieValue(headerCookie, routeId);
		
		if(routeValue == null) {
			Cookie c = new Cookie(routeId, String.valueOf(targetIndex));
			ctx.getResponse().addCookie(c);
			
		} else {
			
			String current = String.valueOf(targetIndex);
			if(!current.equals(routeValue)) {
				String newCookie = replaceCookieValue(headerCookie, routeId, routeValue, current);
				ctx.getResponse().setHeader(headerName, newCookie);
			}
		}
	}
	
	private String replaceCookieValue(String cookie, String name, String oldValue, String newValue) {
		return cookie.replace(name + "=" + oldValue + ";", name + "=" + newValue + ";");
	}

	private String extractCookieValue(String cookie, String name) {
		String value = null;
		
		String key = name + "=";
		int start = cookie.indexOf(key);
		if(start > -1) {
			start += key.length();
			int end = cookie.indexOf(";", start);
			if(end > -1) {
				value = cookie.substring(start, end);
			}
		}
		
		return value;
	}

	private static Cookie findCookie(Cookie[] cs, String name) {
		for(Cookie c : cs) {
			if(name.equals(c.getName())) {
				return c;
			}
		}
		return null;
	}
	
}
