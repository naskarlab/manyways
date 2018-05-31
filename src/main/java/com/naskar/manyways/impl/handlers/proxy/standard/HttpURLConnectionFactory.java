package com.naskar.manyways.impl.handlers.proxy.standard;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface HttpURLConnectionFactory {
	FactoryContext create(HttpServletRequest req) throws Exception;
}