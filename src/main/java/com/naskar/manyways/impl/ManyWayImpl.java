package com.naskar.manyways.impl;

import java.util.ArrayList;
import java.util.List;

import com.naskar.manyways.Handler;
import com.naskar.manyways.ManyWay;
import com.naskar.manyways.Way;

public class ManyWayImpl implements ManyWay {
	
	private List<Handler> handlers;
	private List<Way> ways;
	
	public ManyWayImpl() {
		this.handlers = new ArrayList<Handler>();
		this.ways = new ArrayList<Way>();
	}
	
	public ManyWayImpl addHandler(Handler value) {
		this.handlers.add(value);
		return this;
	}
	
	public ManyWayImpl addWay(Way value) {
		this.ways.add(value);
		return this;
	}
	
	@Override
	public List<Handler> resolveHandlers() {
		return this.handlers;
	}
	
	@Override
	public List<Way> resolveWays() {
		return this.ways;
	}
	
}
