package com.naskar.manyways.impl.handlers.metrics;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.naskar.manyways.Chain;
import com.naskar.manyways.Handler;

public class SimpleTimeRequestLoggerHandler implements Handler {
	
	private static final Logger LOGGER = Logger.getLogger(SimpleTimeRequestLoggerHandler.class.getName());

	@Override
	public void handle(Chain chain, HttpServletRequest req, HttpServletResponse res) throws Exception {
		long time = System.nanoTime();
		
		chain.next();
		
		time = System.nanoTime() - time;
		
		LOGGER.info("Request: [" + req.getRequestURI() + "], Time: [" + time + "] nanos.");
	}

}
