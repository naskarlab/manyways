package com.naskar.manyways;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ProxyHandler {
	
	void handle(HttpServletRequest req, HttpServletResponse res) throws Exception;

}
