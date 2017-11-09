package com.naskar.manyways;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Chain {
	
	HttpServletRequest request();
	
	HttpServletResponse response();
	
	void next();

}
