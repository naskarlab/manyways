package com.naskar.manyways.impl.ways;

import java.util.List;

import com.naskar.manyways.Handler;
import com.naskar.manyways.Way;

public class DiscoveryWay implements Way {
	
	private String path;
	private String url;
	
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

	@Override
	public List<Handler> resolveHandlers() {
		// TODO:
		return null;
	}

}
