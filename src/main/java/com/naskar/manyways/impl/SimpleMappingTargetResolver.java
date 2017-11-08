package com.naskar.manyways.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.TargetResolver;

public class SimpleMappingTargetResolver implements TargetResolver {
	
	private class Entry {
		
		private String path;
		private String target;

		public Entry(String path, String target) {
			this.path = path;
			this.target = target;
		}
		
	}

	private List<Entry> paths;
	
	public SimpleMappingTargetResolver() {
		this.paths = new ArrayList<Entry>();
	}

	public SimpleMappingTargetResolver add(String path, String target) {
		paths.add(new Entry(path, target));
		return this;
	}
	
	@Override
	public String resolve(HttpServletRequest req, HttpServletResponse res) {
		return rewriteTarget(req);
	}
	
	protected String rewriteTarget(HttpServletRequest request) {
		String path = request.getRequestURI();
		for (Entry e : paths) {
			if (path.startsWith(e.path)) {
				return rewrite(request, e.path, e.target);
			}
		}

		return null;
	}

	private String rewrite(HttpServletRequest request, String _prefix, String _proxyTo) {
		String path = request.getRequestURI();

		StringBuilder uri = new StringBuilder(_proxyTo);

		String rest = path.substring(_prefix.length());
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

		/* TODO: validate
		if (!validateDestination(rewrittenURI.getHost(), rewrittenURI.getPort())) {
			return null;
		}
		*/

		return rewrittenURI.toString();
	}

}
