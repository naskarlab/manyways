package com.naskar.manyways.impl.ways;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Handler;
import com.naskar.manyways.Way;

public class MappingWay implements Way {
	
	private String path;
	private List<Handler> handlers;
	
	public MappingWay path(String value) {
		this.path = value;
		return this;
	}
	
	public MappingWay handlers(List<Handler> value) {
		this.handlers = value;
		return this;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public List<Handler> resolveHandlers(HttpServletRequest req, HttpServletResponse res) {
		return this.handlers;
	}

}
