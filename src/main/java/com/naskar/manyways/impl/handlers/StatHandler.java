package com.naskar.manyways.impl.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;

public class StatHandler implements Handler {

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
		long time = System.nanoTime();
		
		chain.next();
		
		time = System.nanoTime() - time;
		
		System.out.println(req.getPathInfo() + " Time: [" + time + "] nanos.");
	}

}
