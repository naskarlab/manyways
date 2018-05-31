package com.naskar.manyways.impl.handlers.proxy.standard;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface HttpURLConnectionFactory {
	URLConnectionContext create(HttpServletRequest req, HttpServletResponse res) throws Exception;
}