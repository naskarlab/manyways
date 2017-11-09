package com.naskar.manyways.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;

public class ChainList implements Chain {
	
	private int i = -1;
	private List<Handler> handlers;
	
	private HttpServletRequest req; 
	private HttpServletResponse res;
	
	public ChainList(List<Handler> handlers, HttpServletRequest req, HttpServletResponse res) {
		this.handlers = handlers;
		this.req = req;
		this.res = res;
	}

	@Override
	public void next() {
		if(i < (handlers.size()-1)) {
			i++;
			try {
				handlers.get(i).handle(this, req, res);
			} catch(Exception e) {
				// TODO: create exception global
				throw new RuntimeException(e);
			}
		}
	}
	
}
