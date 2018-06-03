package com.naskar.manyways.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;
import com.naskar.manyways.ManyWay;
import com.naskar.manyways.ManyWayExecutor;
import com.naskar.manyways.Way;

public class DefaultManyWayExecutor implements ManyWayExecutor {
	
	private ManyWay manyWay;
	
	public DefaultManyWayExecutor(ManyWay manyWay) {
		this.manyWay = manyWay;
	}
	
	public void execute(HttpServletRequest req, HttpServletResponse res) {
		
		List<Handler> handlers = new ArrayList<Handler>(manyWay.resolveHandlers());
		
		String uri = req.getRequestURI();
		if(uri == null) {
			uri = "/";
		}
		List<Way> ways = manyWay.resolveWays();
		for (Way w : ways) {
			if (uri.startsWith(w.getPath())) {
				handlers.addAll(w.resolveHandlers(req, res));
			}
		}
		
		if(!handlers.isEmpty()) {
			Chain chain = new ChainList(handlers, req, res);
			chain.next();
		} else {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
